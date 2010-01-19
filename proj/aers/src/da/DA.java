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
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

// Distributed Apriori.
public class DA {
	public static Log log = LogFactory.getLog(DA.class);

	public static String defaultInputPath = "/dat/tmp";
	public static String defaultOutputPath = "/apriori";
	public static String treePathRoot = "/apriori/tree/";
	public static String fpPathRoot = "/apriori/fp/";
	public static int defaultMinSupport = 29;
	public static int defaultFilterSize = 99;
	public static int defaultCountSplitSizeMB = 1;
	public static int defaultTreeSplitSizeMB = 16;
	public static int defaultTreeSize = 1000;
	
	public static int minsup(Configuration conf) {
		return conf.getInt("minsup", 99);
	}

	public static void memInfo() {
		long fm = Runtime.getRuntime().freeMemory() / 1024 / 1024;
		long tm = Runtime.getRuntime().totalMemory() / 1024 / 1024;
		long mm = Runtime.getRuntime().maxMemory() / 1024 / 1024;
		log.info("Memory Info:" + (tm - fm) + "MB Used \t" + fm + "MB Free  \t"
				+ tm + "MB Total\t" + mm + "MB Max");
	}

	public static void count(Configuration conf) throws Exception {
		System.out.println("Count and initialization.");

		String input = conf.get("input", DA.defaultInputPath);
		String outputRoot = conf.get("output", DA.defaultOutputPath);
		int initItemSetLength = conf.getInt("init", 1);
		int countSplitSize = conf.getInt("csplit",
				DA.defaultCountSplitSizeMB);

		Job job = new Job(conf, "DA: Count And Initialization");
		job.setJarByClass(DA.class);
		job.setOutputKeyClass(ItemSet.class);
		job.setOutputValueClass(IntWritable.class);
		job.setInputFormatClass(MaxInputFormat.class);
		TextInputFormat.setMaxInputSplitSize(job, countSplitSize * 1024 * 1024);
		job.setMapperClass(CountMapper.class);
		job.setReducerClass(CountReducer.class);
		job.setNumReduceTasks(1);

		FileInputFormat.addInputPath(job, new Path(input));
			String output = outputRoot + "/fp/" + initItemSetLength;
			FileSystem.get(conf).delete(new Path(outputRoot), true);
			FileOutputFormat.setOutputPath(job, new Path(output));
		job.waitForCompletion(true);
	}

	public static boolean iterate(Configuration conf, int depth)
			throws Exception {
		System.out.println("Iteration depth = " + depth);
		FileSystem fs = FileSystem.get(new Configuration());
		for (FileStatus status : fs.listStatus(new Path(treePathRoot + depth))) {
			if (status.getLen() == 0
					|| status.getPath().getName().toString().startsWith(
							"attempt")) {
				fs.delete(status.getPath(), true);
			}
		}
		if (fs.listStatus(new Path(treePathRoot + depth)).length == 0) {
			System.out.println("No more candidates.");
			return false;
		}
		conf.setInt("depth", depth);
		conf.setInt("minSupport", defaultMinSupport);
		Job job = new Job(conf, "DA: Iteration depth = " + depth);
		job.setJarByClass(DA.class);
		job.setOutputKeyClass(ItemSet.class);
		job.setOutputValueClass(IntWritable.class);
		job.setInputFormatClass(MaxInputFormat.class);
		job.setMapperClass(IterationMapper.class);
		job.setNumReduceTasks(0);
		DistributedCache.addCacheFile(new Path(DA.defaultInputPath).toUri(),
				conf);

		FileInputFormat.addInputPath(job, new Path(treePathRoot + depth));

		FileSystem.get(conf).delete(new Path(fpPathRoot + depth), true);
		FileOutputFormat.setOutputPath(job, new Path(fpPathRoot + depth));
		return job.waitForCompletion(true);
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		new GenericOptionsParser(conf, args);

		String inputPath = conf.get("input", DA.defaultInputPath);
		String outputPath = conf.get("output", DA.defaultOutputPath);
		int initItemSetLength = conf.getInt("init", 2);
		int maxItemSetLength = conf.getInt("max", 99);
		int filterSize = conf.getInt("filter", DA.defaultFilterSize);
		int minSupport = conf.getInt("minsup", DA.defaultMinSupport);
		int treeSize = conf.getInt("tree", DA.defaultTreeSize);
		int countSplitSize = conf.getInt("csplit", DA.defaultCountSplitSizeMB);
		int treeSplitSize = conf.getInt("tsplit", DA.defaultTreeSplitSizeMB);

		System.out.println("Distritubted Apriori");
		System.out.println("input path:" + inputPath);
		System.out.println("output path:" + outputPath);
		System.out.println("min support:" + minSupport);
		System.out.println("init itemset length:" + initItemSetLength);
		System.out.println("max itemset length:" + maxItemSetLength);
		System.out.println("filter size:" + filterSize);
		System.out.println("tree size threshold:" + treeSize);
		System.out.println("count split size(MB):" + countSplitSize);
		System.out.println("tree split size(MB):" + treeSplitSize);

		long start = new Date().getTime();
		count(conf);
		for (int depth = 3; depth <= maxItemSetLength; ++depth) {
			boolean success = iterate(conf, depth);
			if (!success)
				break;
		}
		System.out.println("cost " + (new Date().getTime() - start) + " ms");
	}
}
