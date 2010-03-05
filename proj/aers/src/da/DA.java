package da;

import java.util.Arrays;
import java.util.Formatter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * Main class of Distributed Apriori algorithm.
 * 
 * @author fankai
 */
public class DA {
	public static Log log = LogFactory.getLog(DA.class);

	public static String getDataPath(Configuration conf) {
		return conf.get("input", "/dat/T40.dat");
	}

	public static String getOutputPath(Configuration conf) {
		String root = conf.get("output", "/da");
		int minsup = getMinSupport(conf), filter = getFilterSize(conf);
		int nr = getCountReducerNum(conf);
		Formatter fmt = new Formatter();
		return root + fmt.format("-%d-%d-%d", minsup, filter, nr);
	}

	public static String getPatternPath(Configuration conf, int depth) {
		Formatter fmt = new Formatter();
		return getOutputPath(conf) + fmt.format("/fp-%02d", depth);
	}

	public static String getTreePath(Configuration conf, int depth) {
		Formatter fmt = new Formatter();
		return getOutputPath(conf) + fmt.format("/tree-%02d", depth);
	}

	public static int getMachineNum(Configuration conf) {
		return conf.getInt("machine", 999);
	}

	public static int getMinSupport(Configuration conf) {
		return conf.getInt("minsup", 5);
	}

	public static int getInitItemSetLength(Configuration conf) {
		return conf.getInt("init", 2);
	}

	public static int getMaxItemSetLength(Configuration conf) {
		return conf.getInt("maxlen", 3);
	}

	public static int getCountReducerNum(Configuration conf) {
		return conf.getInt("ncr", 49);
	}

	public static int getFilterSize(Configuration conf) {
		return conf.getInt("filter", 999);
	}

	public static int getTreeThreshold(Configuration conf) {
		return conf.getInt("tree", 1000);
	}

	public static int getCountSplitSize(Configuration conf) {
		return conf.getInt("csplit", 1);
	}

	public static int getTreeSplitSize(Configuration conf) {
		return conf.getInt("tsplit", 16);
	}

	public static int getStartRound(Configuration conf) {
		return conf.getInt("round", 0);
	}

	/**
	 * Log information of memory.
	 */
	public static void memInfo() {
		long fm = Runtime.getRuntime().freeMemory() / 1024 / 1024;
		long tm = Runtime.getRuntime().totalMemory() / 1024 / 1024;
		long mm = Runtime.getRuntime().maxMemory() / 1024 / 1024;
		log.info("Memory Info:" + (tm - fm) + "MB Used \t" + fm + "MB Free  \t"
				+ tm + "MB Total\t" + mm + "MB Max");
	}

	/**
	 * Count and find item sets with limited length and generate candidate trees
	 * if needed.
	 * 
	 * @param conf
	 * @throws Exception
	 */
	public static void count(Configuration conf) throws Exception {
		System.out.println("Count and initialization.");

		if (getCountReducerNum(conf) > 1) {
			int[] points = Sample.sample(conf);
			conf.set("count.points", new ItemSet(points).toString());
			System.out.println("Sample done, points:");
			System.out.println(Arrays.toString(points));
		} 

		int initLen = getInitItemSetLength(conf);
		String input = getDataPath(conf);
		String output = getPatternPath(conf, initLen);
		int csplit = getCountSplitSize(conf);

		Job job = new Job(conf, "DA: Count And Initialization");
		job.setJarByClass(DA.class);
		job.setOutputKeyClass(ItemSet.class);
		job.setOutputValueClass(IntWritable.class);
		job.setInputFormatClass(MaxInputFormat.class);
		MaxInputFormat.setMaxInputSplitSize(job, csplit * 1024 * 1024);
		job.setMapperClass(CountMapper.class);
		job.setReducerClass(CountReducer.class);
		job.setPartitionerClass(CountPartitioner.class);
		job.setNumReduceTasks(getCountReducerNum(conf));

		FileInputFormat.addInputPath(job, new Path(input));
		FileSystem.get(conf).delete(new Path(output), true);
		FileOutputFormat.setOutputPath(job, new Path(output));
		job.waitForCompletion(true);
	}

	/**
	 * Do Apriori iteration at certain depth, generate frequent patterns of
	 * length (depth) and candidate trees with depth of (depth+1).
	 * 
	 * Use map tasks only, no reduce task needed.
	 * 
	 * @param conf
	 * @param depth
	 * @return
	 * @throws Exception
	 */
	public static boolean iterate(Configuration conf, int depth)
			throws Exception {
		System.out.println("Iteration depth = " + depth);
		FileSystem fs = FileSystem.get(new Configuration());
		String input = getTreePath(conf, depth);
		String output = getPatternPath(conf, depth);
		int tsplit = getTreeSplitSize(conf);
		conf.setInt("depth", depth);

		Job job = new Job(conf, "DA: Iteration depth = " + depth);
		job.setJarByClass(DA.class);
		job.setOutputKeyClass(ItemSet.class);
		job.setOutputValueClass(IntWritable.class);
		job.setInputFormatClass(MaxInputFormat.class);
		job.setMapperClass(IterationMapper.class);
		job.setNumReduceTasks(0);

		MaxInputFormat.setMaxInputSplitSize(job, tsplit * 1024 * 1024);

		if (fs.isFile(new Path(getDataPath(conf)))) {
			DistributedCache.addCacheFile(new Path(getDataPath(conf)).toUri(),
					job.getConfiguration());
		} else {
			for (FileStatus status : fs.listStatus(new Path(getDataPath(conf))))
				DistributedCache.addCacheFile(status.getPath().toUri(), job
						.getConfiguration());
		}

		FileInputFormat.addInputPath(job, new Path(input));
		FileSystem.get(conf).delete(new Path(output), true);
		FileSystem.get(conf).delete(new Path(getTreePath(conf, depth + 1)),
				true);
		FileOutputFormat.setOutputPath(job, new Path(output));
		return job.waitForCompletion(true);
	}

	/**
	 * Reorganize tree directory to reduce file numbers.
	 */
	public static boolean reorganize(Configuration conf, int depth)
			throws Exception {
		FileSystem fs = FileSystem.get(conf);
		Path input = new Path(getTreePath(conf, depth));
		System.out.println("Check tree directory:" + input);
		long totalSize = 0;

		for (FileStatus status : fs.listStatus(input)) {
			if (!status.getPath().getName().toString().startsWith("tree-")
					|| status.getLen() == 0) {
				fs.delete(status.getPath(), true);
			} else {
				fs.setReplication(status.getPath(), (short) 1);
				totalSize += status.getLen();
			}
		}
		if (fs.listStatus(input).length == 0) {
			System.out.println("No more candidates.");
			return false;
		}
		int fcnt = fs.listStatus(input).length;
		System.out.println("file count:" + fcnt + "\tavg size:" + totalSize
				/ fcnt + "\ttotal size:" + totalSize);

		if (fcnt > getMachineNum(conf) * 4
				&& totalSize / fcnt < getTreeSplitSize(conf) * 1024 * 1024 / 4) {

			FileStatus[] fss = fs.listStatus(input);
			StringBuilder sb = new StringBuilder(fss[0].getPath().getName());
			long sum = fss[0].getLen();
			for (int i = 1; i < fss.length; ++i) {
				if (sum + fss[i].getLen() < getTreeSplitSize(conf) * 1024 * 1024) {
					sum += fss[i].getLen();
					sb.append(",");
				} else {
					sb.append(";");
				}
				sb.append(fss[i].getPath().getName());
			}
			conf.set("treefiles", sb.toString());

			System.out.println("Reorganize tree directory");
			Job job = new Job(conf, "DA: Reorganize tree directory depth = "
					+ depth);
			job.setJarByClass(DA.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			job.setInputFormatClass(TextInputFormat.class);
			job.setMapperClass(ReorgnizeMapper.class);
			job.setNumReduceTasks(0);

			Path old = new Path(input + ".old");
			fs.rename(input, old);
			fs.mkdirs(input);
			job.getConfiguration().set("oldtreedir", old.toString());
			job.getConfiguration().set("newtreedir", input.toString());

			FileInputFormat.addInputPath(job, old);
			FileOutputFormat.setOutputPath(job, new Path(getOutputPath(conf)
					+ "/tmp"));

			return job.waitForCompletion(true);
		}
		return true;
	}

	public static void da(Configuration conf) throws Exception {

		System.out.println("Distritubted Apriori " + getOutputPath(conf));
		System.out.println("input path:\t" + getDataPath(conf));
		System.out.println("output path:\t" + getOutputPath(conf));
		System.out.println("machine count:\t" + getMachineNum(conf));
		System.out.println("min support:\t" + getMinSupport(conf));
		System.out.println("init itemset length:\t"
				+ getInitItemSetLength(conf));
		System.out.println("max itemset length:\t" + getMaxItemSetLength(conf));
		System.out.println("filter transaction size:\t" + getFilterSize(conf));
		System.out.println("tree size threshold:\t" + getTreeThreshold(conf));
		System.out.println("tree split size(MB):\t" + getTreeSplitSize(conf));
		System.out.println("count split size(MB):\t" + getCountSplitSize(conf));
		System.out
				.println("count reducer number:\t" + getCountReducerNum(conf));
		System.out.println("start round:\t" + getStartRound(conf));
		System.out.println();

		long start = System.currentTimeMillis();
		if (getStartRound(conf) == 0) {
			FileSystem.get(conf).delete(new Path(getOutputPath(conf)), true);
			count(conf);
		}
		System.out.printf("cost %d ms\n", System.currentTimeMillis() - start);

		int depth = Math.max(getInitItemSetLength(conf) + 1,
				getStartRound(conf));
		for (; depth <= getMaxItemSetLength(conf); ++depth) {
			long istart = System.currentTimeMillis();
			boolean success = reorganize(conf, depth) && iterate(conf, depth);
			long iend = System.currentTimeMillis();
			System.out.printf("cost %d ms\n", iend - istart);
			if (!success)
				break;
		}
		long end = System.currentTimeMillis();
		System.out.printf("cost %f minutes in frequent pattern mining\n",
				(end - start) / 60000.0);

	}

	public static void main(String[] args) {
		Configuration conf = new Configuration();
		new GenericOptionsParser(conf, args);
		try {
			da(conf);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
