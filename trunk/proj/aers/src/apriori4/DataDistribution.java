package apriori4;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class DataDistribution {
	//public static String datPath = "/validaers";
	public static String datPath = "/aers/aers";
	public static String pathRoot = "/apriori3/";
	public static String treePathRoot = pathRoot + "tree";
	public static String fpPathRoot = pathRoot + "fp";
	public static int minsup = 3;
	public static int maxLength = 12;

	public static boolean scan(int depth) throws Exception {
		Job job = new Job(new Configuration(), "apriori scan depth=" + depth);
		Configuration conf = job.getConfiguration();
		conf.setInt("depth", depth);
		FileSystem fs = FileSystem.get(new Configuration());
		for (FileStatus status : fs.listStatus(new Path(datPath))) {
			DistributedCache.addCacheFile(status.getPath().toUri(), conf);
		}

		conf.setInt("apriori.maxLength", maxLength);
		job.setMapperClass(TreeScanMapper.class);
		job.setNumReduceTasks(0);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setInputFormatClass(NTreeInputFormat.class);

		if (!fs.exists(new Path(treePathRoot + depth)))
			return false;
		for (FileStatus status : fs.listStatus(new Path(treePathRoot + depth))) {
			// System.out.println(status.getPath()+" "+status.getLen());
			if (status.getLen() == 0
					|| !status.getPath().getName().toString()
							.startsWith("tree")) {
				fs.delete(status.getPath(), true);
			}

		}
		// System.out.println(cnt);
		System.out.println("scan depth = " + depth);
		FileInputFormat.addInputPath(job, new Path(treePathRoot + (depth)));
		fs.delete(new Path(treePathRoot + (depth + 1)), true);
		fs.delete(new Path(fpPathRoot + depth), true);
		FileOutputFormat.setOutputPath(job, new Path(fpPathRoot + depth));
		return job.waitForCompletion(true);
	}

	public static void main(String[] args) throws Exception {
		Init.init(2);
		int depth = 3;
		while (scan(depth))
			depth++;
		System.out.println("done");
	}
}
