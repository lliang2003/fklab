package da;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

// Distributed Apriori.
public class DA {
	public static Log log = LogFactory.getLog(DA.class);

	public static String getInputPath(Configuration conf) {
		return conf.get("input", "/dat/tmp");
	}

	public static String getOutputPath(Configuration conf) {
		return conf.get("output", "/apriori");
	}

	public static String getPatternPath(Configuration conf, int depth) {
		return getOutputPath(conf) + "/fp/" + depth;
	}

	public static String getTreePath(Configuration conf, int depth) {
		return getOutputPath(conf) + "/tree/" + depth;
	}

	public static int getMinSupport(Configuration conf) {
		return conf.getInt("minsup", 9);
	}

	public static int getInitItemSetLength(Configuration conf) {
		return conf.getInt("init", 2);
	}

	public static int getMaxItemSetLength(Configuration conf) {
		return conf.getInt("max", 9);
	}

	public static int getFilterSize(Configuration conf) {
		return conf.getInt("filter", 9);
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

	public static void memInfo() {
		long fm = Runtime.getRuntime().freeMemory() / 1024 / 1024;
		long tm = Runtime.getRuntime().totalMemory() / 1024 / 1024;
		long mm = Runtime.getRuntime().maxMemory() / 1024 / 1024;
		log.info("Memory Info:" + (tm - fm) + "MB Used \t" + fm + "MB Free  \t"
				+ tm + "MB Total\t" + mm + "MB Max");
	}

	/**
	 * Count and find item sets with limited length and generate candidate trees if needed.
	 * @param conf	
	 * @throws Exception
	 */
	public static void count(Configuration conf) throws Exception {
		System.out.println("Count and initialization.");

		int initLen = getInitItemSetLength(conf);
		String input = getInputPath(conf);
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
		job.setNumReduceTasks(1);

		FileInputFormat.addInputPath(job, new Path(input));
		FileSystem.get(conf).delete(new Path(getOutputPath(conf)), true);
		FileOutputFormat.setOutputPath(job, new Path(output));
		job.waitForCompletion(true);
	}

	public static boolean iterate(Configuration conf, int depth)
			throws Exception {
		System.out.println("Iteration depth = " + depth);
		FileSystem fs = FileSystem.get(new Configuration());
		String input = getTreePath(conf, depth);
		String output = getPatternPath(conf, depth);
		int tsplit = getTreeSplitSize(conf);
		conf.setInt("depth", depth);

		if (fs.exists(new Path(input))) {
			for (FileStatus status : fs.listStatus(new Path(input))) {
				if (status.getLen() == 0
						|| status.getPath().getName().toString().startsWith(
								"attempt")) {
					fs.delete(status.getPath(), true);
				}
			}
		}
		if (!fs.exists(new Path(input))
				|| fs.listStatus(new Path(input)).length == 0) {
			System.out.println("No more candidates.");
			return false;
		}

		Job job = new Job(conf, "DA: Iteration depth = " + depth);
		job.setJarByClass(DA.class);
		job.setOutputKeyClass(ItemSet.class);
		job.setOutputValueClass(IntWritable.class);
		job.setInputFormatClass(MaxInputFormat.class);
		job.setMapperClass(IterationMapper.class);
		job.setNumReduceTasks(0);

		MaxInputFormat.setMaxInputSplitSize(job, tsplit * 1024 * 1024);

		DistributedCache.addCacheFile(new Path(getInputPath(conf)).toUri(),
				conf);

		FileInputFormat.addInputPath(job, new Path(input));
		FileSystem.get(conf).delete(new Path(output), true);
		FileOutputFormat.setOutputPath(job, new Path(output));
		return job.waitForCompletion(true);
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		new GenericOptionsParser(conf, args);
		
		System.out.println("Distritubted Apriori");
		System.out.println("input path:\t" + getInputPath(conf));
		System.out.println("output path:\t" + getOutputPath(conf));
		System.out.println("min support:\t" + getMinSupport(conf));
		System.out.println("init itemset length:\t"
				+ getInitItemSetLength(conf));
		System.out.println("max itemset length:\t" + getMaxItemSetLength(conf));
		System.out.println("filter size:\t" + getFilterSize(conf));
		System.out.println("tree size threshold:\t" + getTreeThreshold(conf));
		System.out.println("count split size(MB):\t" + getCountSplitSize(conf));
		System.out.println("tree split size(MB):\t" + getTreeSplitSize(conf));
		System.out.println();

		long start = new Date().getTime();
		count(conf);
		System.out.println("cost " + (new Date().getTime() - start) + " ms");
		int depth = getInitItemSetLength(conf) + 1;
		for (; depth <= getMaxItemSetLength(conf); ++depth) {
			start = new Date().getTime();
			boolean success = iterate(conf, depth);
			System.out.println("cost " + (new Date().getTime() - start) + " ms");
			if (!success)
				break;
		}
	}
}
