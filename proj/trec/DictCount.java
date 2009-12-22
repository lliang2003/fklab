package trec;

import java.io.IOException;
import java.util.StringTokenizer;

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

public class DictCount {

	public static class WMapper extends Mapper<Object, Text, Text, Text> {

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] parts = value.toString().split("[ \t]", 2);
			context.write(new Text(parts[0]), new Text(parts[1]));
		}
	}

	public static class WReducer extends Reducer<Text, Text, Text, Text> {
		int ecount = 0;

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			long a = 0, b = 0;
			for (Text val : values) {
				try {
					a += Long.parseLong(val.toString().split(" ")[0]);
					b += Long.parseLong(val.toString().split(" ")[1]);
					// context.write(key, val);
				} catch (Exception e) {
					ecount += 1;
					context.setStatus("err(" + ecount + ");" + key.toString()
							+ ":" + val.toString());
				}
			}
			context.write(new Text(key + " " + a + " " + b), null);
			context.progress();
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "dict word count");
		job.setMapperClass(WMapper.class);
		// job.setCombinerClass(WReducer.class);
		job.setReducerClass(WReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		//job.setNumReduceTasks(10);
		FileInputFormat.addInputPath(job, new Path("/trec/dict"));
		FileOutputFormat.setOutputPath(job, new Path("/trec/dictwc2"));
		FileSystem.get(conf).delete(new Path("/trec/dictwc2"), true);
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
