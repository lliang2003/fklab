package test;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Test {
	public static Log log = LogFactory.getLog(Test.class);

	public static class TestMapper extends Mapper<Object, Text, Text, Text> {
		protected void setup(Context context) throws IOException,
				InterruptedException {
			FileSplit split = (FileSplit) context.getInputSplit();
//			context.write(new Text(context.getTaskAttemptID().toString()),
//					new Text(split.getPath().toString() + " "
//							+ split.getStart()));
			context.write(new Text(context.getTaskAttemptID().toString()), new Text(context.getInputSplit().toString()));
			for (Path p:DistributedCache.getLocalCacheFiles(context.getConfiguration())){
				log.info("cache:"+p);
			}
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
		FileInputFormat.addInputPath(job, new Path("/dat/tmp"));
		FileInputFormat.addInputPath(job, new Path("/dat/tmp"));
		TextInputFormat.setMaxInputSplitSize(job, 256*1024);
		for (Path p:FileInputFormat.getInputPaths(job))
			System.out.println(p);
		FileInputFormat f = new TextInputFormat();
		List<InputSplit> list = f.getSplits(job);
		for (InputSplit s: list) {
			System.out.println(s);
		}
		
		FileSystem.get(conf).delete(new Path("/test-output"), true);
		FileOutputFormat.setOutputPath(job, new Path("/test-output"));
		DistributedCache.addCacheFile(new Path("/dat/tmp").toUri(), job.getConfiguration());
		DistributedCache.addCacheFile(new Path("/dat").toUri(), job.getConfiguration());
		job.waitForCompletion(true);
	}
}
