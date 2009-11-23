package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Count {
	public static class PatternMapper extends
			Mapper<Object, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text pattern = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] tokens = value.toString().split("[ \t]+");
			if (tokens.length > 16) return;  // filter out long transactions
			Arrays.sort(tokens);
			int n = (int)Math.pow(2, tokens.length);
			for (int i=1; i < n; ++i) {
				String pat = "";
				for (int j = 0; j < tokens.length; ++j) {
					if (((i>>j)&2) > 0) pat += tokens[j]+" ";
				}
				pattern.set(pat);
				context.write(pattern, one);
			}
		}
	}

	public static class IntSumReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			if (sum < 3) return;
			result.set(sum);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "count");
		job.setMapperClass(PatternMapper.class);
		//job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setNumReduceTasks(40);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
	    FileInputFormat.addInputPath(job, new Path("/aers"));
		FileSystem.get(conf).delete(new Path("/prr/count"), true);
	    FileOutputFormat.setOutputPath(job, new Path("/prr/count"));
		job.waitForCompletion(true);
	}
}
