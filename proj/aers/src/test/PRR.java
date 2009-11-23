package test;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Partitioner;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class PRR extends Configured implements Tool {

	public static class AggregationPartitioner implements Partitioner<Text, Text> {
		public int getPartition(Text key, Text value, int numReduceTasks) {
			String kk = key.toString();
			if (!kk.endsWith(","))
				kk += ",";
			return (kk.hashCode() & Integer.MAX_VALUE) % numReduceTasks;
		}
		public void configure(JobConf job) {
		}
	}

	public static class AggregationMapper extends MapReduceBase implements
			Mapper<Object, Text, Text, Text> {
		public void map(Object key, Text value,
				OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {
			String[] tokens = value.toString().split("[ \t]+");
			StringBuilder drugs = new StringBuilder();
			StringBuilder events = new StringBuilder();
			for (int i = 0; i < tokens.length - 1; ++i) {
				if (tokens[i].startsWith("D"))
					drugs.append(tokens[i] + ",");
				else
					events.append(tokens[i] + ",");
			}
			String count = tokens[tokens.length - 1];
			if (drugs.length() > 0)
				drugs.deleteCharAt(drugs.length() - 1);
			if (events.length() > 0)
				events.deleteCharAt(events.length() - 1);
			if (drugs.length() == 0)
				output.collect(new Text(events.toString()), new Text(count));
			else if (events.length() == 0)
				output.collect(new Text(drugs.toString()), new Text(count));
			else
				output.collect(new Text(drugs.toString() + ","), new Text(
						events.toString() + " " + count));
		}

	}

	public static class AggregateReducer extends MapReduceBase implements
			Reducer<Text, Text, Text, Text> {
		public String drugs = null;
		public String count = null;

		public void reduce(Text key, Iterator<Text> values,
				OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {
			if (key.toString().startsWith("R")) {
				output.collect(key, values.next());
			} else if (key.toString().endsWith(",")) {
				if (key.toString().equals(drugs + ",")) {
					while (values.hasNext()) {
						Text t = values.next();
						output.collect(new Text(drugs + " " + t.toString()
								+ " " + count), new Text());
					}
				} else {
					//output.collect(key, new Text("error"));
					throw new IOException(key.toString());
				}
			} else {
				drugs = key.toString();
				count = values.next().toString();
				//output.collect(key, new Text(count));
			}
		}
	}

	public static class CalculationMapper extends MapReduceBase implements
			Mapper<Object, Text, Text, Text> {
		private final static IntWritable one = new IntWritable(1);
		private Text pattern = new Text();

		public void map(Object key, Text value,
				OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {
			String[] tokens = value.toString().split("[ \t]+");
			if (tokens.length == 2)
				output.collect(new Text(tokens[0]), new Text(tokens[1]));
			else
				output.collect(new Text(tokens[1]+","), value);
		}

	}

	public static class CalculationReducer extends MapReduceBase implements
			Reducer<Text, Text, Text, Text> {
		public String events = null;
		public String count = null;

		public void reduce(Text key, Iterator<Text> values,
				OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {
			if (key.toString().endsWith(",")) {
				if (key.toString().equals(events+",")) {
					while (values.hasNext()){
						Text val = values.next();
						String[] tokens = val.toString().split("[ \t]+");
						int a = Integer.parseInt(tokens[2]);
						int b = Integer.parseInt(tokens[3]);
						int c = Integer.parseInt(count);
						int d = 30000;
						output.collect(new Text(val + " " + count), new Text(""
								+ ((double) a * d / b / c) + " "
								+ ((double) a * a * d / b / c)));
					}
				} else {
					//output.collect(key, new Text("error"));
					throw new IOException(key.toString());
				}
			} else {
				events = key.toString();
				count = values.next().toString();
				//output.collect(key, new Text(count));				
			}

		}
	}

	public void aggregate() throws IOException {
		JobConf conf = new JobConf(getConf(), PRR.class);
		conf.setJobName("aggregation");

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);

		conf.setMapperClass(AggregationMapper.class);
		conf.setReducerClass(AggregateReducer.class);
		conf.setNumReduceTasks(40);
		conf.setPartitionerClass(AggregationPartitioner.class);

		// Make sure there are exactly 2 parameters left.
		FileInputFormat.setInputPaths(conf, "/prr/count");
		FileSystem.get(conf).delete(new Path("/prr/agg"), true);
		FileOutputFormat.setOutputPath(conf, new Path("/prr/agg"));

		JobClient.runJob(conf);
	}

	public void calc() throws IOException {
		JobConf conf = new JobConf(getConf(), PRR.class);
		conf.setJobName("calculation");

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);

		conf.setMapperClass(CalculationMapper.class);
		conf.setReducerClass(CalculationReducer.class);
		conf.setNumReduceTasks(40);
		conf.setPartitionerClass(AggregationPartitioner.class);

		// Make sure there are exactly 2 parameters left.
		FileInputFormat.setInputPaths(conf, "/prr/agg");
		FileSystem.get(conf).delete(new Path("/prr/calc"), true);
		FileOutputFormat.setOutputPath(conf, new Path("/prr/calc"));

		JobClient.runJob(conf);
	}
	
	public int run(String[] args) throws Exception {
		aggregate();
		calc();
		return 0;
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new PRR(), args);
		System.exit(res);
	}

}
