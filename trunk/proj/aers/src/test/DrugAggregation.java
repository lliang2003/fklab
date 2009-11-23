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

public class DrugAggregation {
	public static class GMapper extends
			Mapper<Object, Text, Text, Text> {
		private final static IntWritable one = new IntWritable(1);
		private Text pattern = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] tokens = value.toString().split("[ \t]+");
			if (Integer.parseInt(tokens[tokens.length-1]) < 3) return;
			if (tokens.length == 2)
				context.write(new Text(tokens[0]), new Text(tokens[1]));
			else
				context.write(new Text(tokens[0]), value);
		}
	}

	public static class IntSumReducer extends
			Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
//			context.write(new Text("key="+key.toString()), null);
			if (key.toString().charAt(0) != 'D') {
				for (Text val : values)
					context.write(key, val);
			} else {
				List<String> vals = new ArrayList<String>();
				String count = null;
				for (Text val : values) {
					String s = val.toString();
					if (s.charAt(0)!='D') count = s;
					else vals.add(s);
				}
				if (count == null) return;
				for (String val: vals)
					context.write(new Text(val+" "+count), null);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "pattern count");
		job.setMapperClass(GMapper.class);
		//job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setNumReduceTasks(40);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
	    FileInputFormat.addInputPath(job, new Path("/output"));
		FileSystem.get(conf).delete(new Path("/mid"), true);
	    FileOutputFormat.setOutputPath(job, new Path("/mid"));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
