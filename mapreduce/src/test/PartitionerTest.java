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

public class PartitionerTest {
	public static Log log = LogFactory.getLog(PartitionerTest.class);

	public static class TestMapper extends
			Mapper<LongWritable, Text, Text, Text> {
	}

	public static class TestReducer extends
			Reducer<LongWritable, Text, Text, Text> {
	}

	public static class TensDivPart extends Partitioner<LongWritable, Text> {
		public int getPartition(LongWritable key, Text value, int numPartitions) {
			return (int) key.get() / 10 % numPartitions;
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "test partitioner");
		job.setMapperClass(TestMapper.class);
		job.setJarByClass(PartitionerTest.class);
		job.setReducerClass(TestReducer.class);
		job.setPartitionerClass(TensDivPart.class);
		job.setNumReduceTasks(10);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(Text.class);
		String inputPath = "/tmpinput";
		String outputPath = "/tmpoutput";
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileSystem.get(conf).delete(new Path(outputPath), true);
		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		job.waitForCompletion(true);
	}
}
