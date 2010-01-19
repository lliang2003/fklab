package apriori4;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Distribution {
	public static String datPath = "/validaers";
	public static String pathRoot = "/apriori4/";
	public static String treePathRoot = pathRoot + "tree";
	public static String fpPathRoot = pathRoot + "fp";
	public static int minsup = 3;
	public static int maxLength = 12;

	public static void init() throws Exception {
//		Job job = new Job(new Configuration(), "apriori init");
//		Configuration conf = job.getConfiguration();
//		conf.setInt("apriori.maxLength", maxLength);
//		job.setMapperClass(CountMapper.class);
//		job.setReducerClass(CountReducer.class);
//		job.setNumReduceTasks(30);
//		job.setOutputKeyClass(Text.class);
//		job.setOutputValueClass(Text.class);
//		job.setPartitionerClass(FirstItemPartitioner.class);
//		
//		FileInputFormat.addInputPath(job, new Path(datPath));
//		FileSystem.get(conf).delete(new Path(fpPathRoot + "2"), true);
//		FileOutputFormat.setOutputPath(job, new Path(fpPathRoot + "2"));
//		job.waitForCompletion(true);
	}


	public static void main(String[] args) throws Exception {
		init();
//		makeTree(3);
//		int depth = 3;
//		while (scan(depth))
//			depth++;
//		System.out.println("done");

	}
}
