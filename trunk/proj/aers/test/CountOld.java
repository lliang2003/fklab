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

public class CountOld {
	public static class PatternMapper extends
			Mapper<Object, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text pattern = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] tokens = value.toString().split("[ \t]+");
			Arrays.sort(tokens);
			
			List<String> drug_patterns = new ArrayList<String>(); 
			List<String> event_patterns = new ArrayList<String>(); 
			
			for (int i = 0; i < tokens.length; ++i) {
				pattern.set(tokens[i]);
				context.write(pattern, one);
				for (int j = i+1; j < tokens.length; ++j) {
					if (tokens[i].charAt(0) == tokens[j].charAt(0)) {
						pattern.set(tokens[i] + "," + tokens[j]);
						if (tokens[i].charAt(0)=='R') event_patterns.add(tokens[i] + "," + tokens[j]);
						if (tokens[j].charAt(0)=='D') drug_patterns.add(tokens[i] + "," + tokens[j]);
					}else
						pattern.set(tokens[i] + " " + tokens[j]);
					context.write(pattern, one);
				}
			}
			for (int i = 0; i < drug_patterns.size(); ++i) {
				for (int j = 0; j < event_patterns.size(); ++j) {
					pattern.set(drug_patterns.get(i) + " " + event_patterns.get(j));
					context.write(pattern, one);
				}
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
			result.set(sum);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "pattern count");
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
