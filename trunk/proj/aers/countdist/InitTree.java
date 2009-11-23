package apriori;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class InitTree {

	static class CountMapper extends Mapper<Object, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text pattern = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] tokens = value.toString().split("[ \t]+");
			// if (tokens.length > 16) return; // filter out long transactions
			Arrays.sort(tokens);
			for (String item : tokens) {
				pattern.set(item);
				context.write(pattern, one);
			}
			for (int i = 0; i < tokens.length; ++i) {
				for (int j = i + 1; j < tokens.length; ++j) {
					pattern.set(tokens[i] + " " + tokens[j]);
					context.write(pattern, one);
				}
			}
		}
	}

	static class CountReducer extends Reducer<Text, IntWritable, Text, Text> {
		Tree tree = new Tree();
		protected void setup(Context context) throws IOException,
				InterruptedException {
			
		}

		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			Iterator<IntWritable> it = values.iterator();
			int count = 0;
			while (it.hasNext()) {
				count += 1;
				it.next();
			}
			
			if (count < Apriori.minsup) return;
			context.write(key, new Text("" + count));

			String[] tokens = key.toString().split(" +");
			if (tokens.length == 1) {
				tree.root.addChild(tokens[0]);
				tree.root.getChild(tokens[0]).count = count;
			} else if (tokens.length == 2) {
				tree.root.getChild(tokens[0]).addChild(tokens[1]);
				tree.root.getChild(tokens[0]).getChild(tokens[1]).count = count;
			} 
		}
		
		  protected void cleanup(Context context
          ) throws IOException, InterruptedException {
			  context.write(new Text(tree.toString()), null);
		  }

		
		
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "apriori");
		job.setMapperClass(CountMapper.class);
		job.setReducerClass(CountReducer.class);
		job.setNumReduceTasks(1);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path("/aers"));
		FileSystem.get(conf).delete(new Path("/apriori/init"), true);
		FileOutputFormat.setOutputPath(job, new Path("/apriori/init"));
		job.waitForCompletion(true);
	}
}
