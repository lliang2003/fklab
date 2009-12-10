package test;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Test {
	public static Log log = LogFactory.getLog(Test.class);

	public static class TestMapper extends Mapper<Object, Text, Text, Text> {
		protected void setup(Context context) throws IOException,
				InterruptedException {
			log.info(context.getTaskAttemptID());
			log.info(((FileSplit) context.getInputSplit()).getPath());
			FileSplit split = (FileSplit) context.getInputSplit();
			context.write(new Text(context.getTaskAttemptID().toString()),
					new Text(split.getPath().toString() + " "
							+ split.getStart()));
		}

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
		}
	}

	public static class TestReducer extends
			Reducer<IntWritable, Text, Text, Text> {
		// public void reduce(IntWritable key, Iterable<Text> values,
		// Context context) throws IOException, InterruptedException {
		// for (Text value : values)
		// context.write(value, null);
		// }
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "test");
		job.setMapperClass(TestMapper.class);
		job.setJarByClass(Test.class);
		job.setReducerClass(TestReducer.class);
		job.setNumReduceTasks(1);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path("/tmp/fankai"));
		FileInputFormat.addInputPath(job, new Path("/tmp/fankai"));
		FileInputFormat.addInputPath(job, new Path("/tmp/fankai"));
		FileInputFormat.addInputPath(job, new Path("/tmp/fankai"));
		FileInputFormat.addInputPath(job, new Path("/tmp/fankai"));
		FileInputFormat.addInputPath(job, new Path("/tmp/fankai"));
		FileSystem.get(conf).delete(new Path("/output"), true);
		FileOutputFormat.setOutputPath(job, new Path("/output"));
		job.waitForCompletion(true);
	}
}
