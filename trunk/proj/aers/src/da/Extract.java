package da;

import java.io.IOException;
import java.util.PriorityQueue;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/*
 * Extract top prr result.
 */
public class Extract {
	public static int filter = 99;
	public static class EMapper extends
			Mapper<Object, Text, DoubleWritable, Text> {
		PriorityQueue<Double> pq = new PriorityQueue<Double>();
		public void map(Object o, Text value, Context context)
				throws IOException, InterruptedException {
			String[] parts = value.toString().split("\t");
//			if (value.toString().split(",").length > 4) return;
//			double prr = Double.parseDouble(parts[4]);
			double chi = Double.parseDouble(parts[5]);
//			double pcnt = Double.parseDouble(parts[1]);
//			double dcnt = Double.parseDouble(parts[2]);
//			double rcnt = Double.parseDouble(parts[3]);
//			double a = pcnt, b = rcnt - pcnt, c = dcnt - pcnt, d = 1236591 - a
//			- b - c;
			
			if (pq.size()<100) {
				pq.add(chi);
				context.write(new DoubleWritable(chi), value);
				System.out.println(chi);
			} else if (pq.peek() < chi) {
				pq.add(chi);
				context.write(new DoubleWritable(chi), value);
				System.out.println(chi);
				pq.remove();
			}
			
//			if (chi > 500000){
//				context.write(new DoubleWritable(chi), value);
//				System.out.println(chi);
//			}
		}
	}

	public static class EReducer extends
			Reducer<DoubleWritable, Text, Text, Text> {
		PriorityQueue<Double> pq = new PriorityQueue<Double>();
		public void reduce(DoubleWritable num, Iterable<Text> val,
				Context context) throws IOException, InterruptedException {
			for (Text is: val) {
				context.write(is, null);
			}
		}
	}

	public static void extract(String input, String output) throws Exception {
		System.out.println("PRR: Extract top prr results");
		Configuration conf = new Configuration();
		Job job = new Job(conf, "PRR: Extract top prr results");
		job.setJarByClass(Extract.class);
		job.setOutputKeyClass(DoubleWritable.class);
		job.setOutputValueClass(Text.class);
		job.setMapperClass(EMapper.class);
		job.setReducerClass(EReducer.class);
		job.setNumReduceTasks(1);
		FileInputFormat.addInputPath(job, new Path(input));
		FileSystem.get(conf).delete(new Path(output), true);
		FileOutputFormat.setOutputPath(job, new Path(output));
		job.waitForCompletion(true);
	}

	public static void main(String[] args) throws Exception {
		extract("/user/fankai/va-15-3-20-45/prr", "/top7");
		extract("/user/fankai/aa-15-3-20-45/prr", "/top8");
	}

}
