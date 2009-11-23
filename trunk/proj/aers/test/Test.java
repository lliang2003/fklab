package test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;

public class Test{
	public static Log log = LogFactory.getLog(Test.class);


	public static class PatternMapper extends Mapper<Object, Text, LongWritable, Text> {
	}

	public static class IntSumReducer extends Reducer<LongWritable, Text, LongWritable, Text> {

	}
	
	public static class MyPart extends Partitioner<LongWritable, Text> {

		@Override
		public int getPartition(LongWritable key, Text value, int numReduceTasks) {
			return Integer.parseInt(key.toString()) % numReduceTasks;
		}
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();

		Job job = new Job(conf, "pattern count");
		job.setMapperClass(PatternMapper.class);
		job.setJarByClass(Test.class);
		job.setReducerClass(IntSumReducer.class);
		job.setPartitionerClass(MyPart.class);
		job.setNumReduceTasks(10);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path("/aers"));
		FileSystem.get(conf).delete(new Path("/output"), true);
		FileOutputFormat.setOutputPath(job, new Path("/output"));
		job.waitForCompletion(true);
	}
}
