package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class Calculation {

	public class MyPartitioner extends Partitioner<Text, Text> {

		@Override
		public int getPartition(Text key, Text value, int numReduceTasks) {
			String kk = key.toString();
			if (!kk.endsWith(",")) kk += ",";
			return (kk.hashCode() & Integer.MAX_VALUE) % numReduceTasks;
		}
	}

	public static class AggregatorMapper extends
			Mapper<Object, Text, Text, Text> {
		private final static IntWritable one = new IntWritable(1);
		private Text pattern = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] tokens = value.toString().split("[ \t]+");
			StringBuilder drugs = new StringBuilder();
			StringBuilder events = new StringBuilder();
			for (int i = 0; i < tokens.length-1; ++i) {
				if (tokens[i].startsWith("D")) drugs.append(tokens[i] + ",");
				else events.append(tokens[i] + ",");
			}
			String count = tokens[tokens.length-1];
			if (drugs.length() > 0)drugs.deleteCharAt(drugs.length()-1);
			if (events.length() > 0)events.deleteCharAt(events.length()-1);
			if (drugs.length() == 0)
				context.write(new Text(events.toString()), new Text(count));
			else if (events.length() == 0) 
				context.write(new Text(drugs.toString()), new Text(count));
			else 
				context.write(new Text(drugs.toString()+","), new Text(events.toString()+" "+count));
		}
	}

	public static class AggregatorReducer extends
			Reducer<Text, Text, Text, Text> {
		public String drugs = null;
		public String count = null;
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			if (key.toString().startsWith("R")) {
				context.write(key, values.iterator().next());
			} else if (key.toString().endsWith(",")) {
				if (key.toString().equals(drugs+",")) {
					for (Text val: values)
						context.write(new Text(drugs+" "+val.toString()+" "+count), null);
				} else
					context.write(key, new Text("error"));
				
			} else {
				drugs = key.toString();
				count = values.iterator().next().toString();
				context.write(key, new Text(count));				
			}
			
		}
	}

	public static class CalcMapper extends Mapper<Object, Text, Text, Text> {
		private final static IntWritable one = new IntWritable(1);
		private Text pattern = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] tokens = value.toString().split("[ \t]+");
			if (tokens.length == 2)
				context.write(new Text(tokens[0]), new Text(tokens[1]));
			else
				context.write(new Text(tokens[1]+","), value);
		}
	}

	public static class CalcReducer extends Reducer<Text, Text, Text, Text> {
		public String drugs = null;
		public String count = null;
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			if (key.toString().endsWith(",")) {
				if (key.toString().equals(drugs+",")) {
					for (Text val: values) {
						String[] tokens = val.toString().split("[ \t]+");
						int a = Integer.parseInt(tokens[2]);
						int b = Integer.parseInt(tokens[3]);
						int c = Integer.parseInt(count);
						int d = 30000;
						context.write(new Text(val + " " + count), new Text(""
								+ ((double) a * d / b / c) + " "
								+ ((double) a * a * d / b / c)));
					}
				} else
					context.write(key, new Text("error"));
			} else {
				drugs = key.toString();
				count = values.iterator().next().toString();
				context.write(key, new Text(count));				
			}
			
//			List<String> vals = new ArrayList<String>();
//			String count = null;
//			for (Text val : values) {
//				String s = val.toString();
//				if (s.charAt(0) != 'D')
//					count = s;
//				else
//					vals.add(s);
//			}
//			if (count == null)
//				return;
//			for (String val : vals) {
//				String[] tokens = val.toString().split("[ \t]+");
//				int a = Integer.parseInt(tokens[2]);
//				int b = Integer.parseInt(tokens[3]);
//				int c = Integer.parseInt(count);
//				int d = 30000;
//				context.write(new Text(val + " " + count), new Text(""
//						+ ((double) a * d / b / c) + " "
//						+ ((double) a * a * d / b / c)));
//			}
		}
	}

	public static void aggregate() throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "aggregate");
		job.setJarByClass(Calculation.class);
		job.setPartitionerClass(MyPartitioner.class);
		job.setMapperClass(AggregatorMapper.class);
		//job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(AggregatorReducer.class);
		job.setNumReduceTasks(4);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
	    FileInputFormat.addInputPath(job, new Path("/prr/count"));
		FileSystem.get(conf).delete(new Path("/prr/agg"), true);
	    FileOutputFormat.setOutputPath(job, new Path("/prr/agg"));
		job.waitForCompletion(true);

	}

	public static void calc() throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "calc");
		job.setMapperClass(CalcMapper.class);
		// job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(CalcReducer.class);
		job.setPartitionerClass(MyPartitioner.class);
		job.setNumReduceTasks(40);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path("/prr/agg"));
		FileSystem.get(conf).delete(new Path("/prr/calc"), true);
		FileOutputFormat.setOutputPath(job, new Path("/prr/calc"));
		job.waitForCompletion(true);
	}

	public static void main(String[] args) throws Exception {
		aggregate();
		//calc();
	}
}
