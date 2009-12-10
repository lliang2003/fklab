package count;

import java.io.IOException;

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
import org.apache.hadoop.util.GenericOptionsParser;

public class Count {
	private static int maxLength;
	public static class CountMapper extends
			Mapper<Object, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text pattern = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] tokens = value.toString().split("[ \t]+");
			// filter out long transactions
			if (tokens.length > maxLength)
				return;
			// Arrays.sort(tokens);
			int n = (int) Math.pow(2, tokens.length);
			for (int i = 1; i < n; ++i) {
				String pat = "";
				for (int j = 0; j < tokens.length; ++j) {
					if (((i >> j) & 1) > 0)
						pat += tokens[j] + " ";
				}
				pattern.set(pat);
				context.write(pattern, one);
			}
		}
	}

	public static class CountCombiner extends
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

	public static class CountReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			if (sum < 3)
				return;
			result.set(sum);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		new GenericOptionsParser(conf, args);
		String inputPath = conf.get("count.inputPath", "/allaers");
		String outputPath = conf.get("count.outputPath", "/prr/count");
		int reduceNum = conf.getInt("count.reduceNum", 30);
		maxLength = conf.getInt("count.maxLength", 12);
		System.out.println("input path:"+inputPath);
		System.out.println("output path:"+outputPath);
		System.out.println("reduce task number:"+reduceNum);
		System.out.println("max transaction length:"+maxLength);

		Job job = new Job(conf, "count");
		job.setMapperClass(CountMapper.class);
		//job.setCombinerClass(CountCombiner.class);
		job.setReducerClass(CountReducer.class);
		job.setNumReduceTasks(reduceNum);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setJarByClass(Count.class);
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileSystem.get(conf).delete(new Path(outputPath), true);
		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		job.waitForCompletion(true);
	}
}
