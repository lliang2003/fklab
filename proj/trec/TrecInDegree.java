package trec;

import java.io.IOException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import trec.TrecPageRank.ReverseFilterReducer;


public class TrecInDegree {
	public static class MyMapper extends
			Mapper<LongWritable, Text, Text, IntWritable> {

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			byte[] bytes = value.getBytes();
			if (bytes[0] != 'h') return;
			for (int i = 0; i < bytes.length; ++i) {
				if (bytes[i] == ' ' || bytes[i] == '#') {
					value.set(bytes, 0, i);
					context.write(value, new IntWritable(1));
				}
			}
		}
	}

	public static class MyReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		public static Log log = LogFactory.getLog(ReverseFilterReducer.class);
		Map<String, String> urls;
		Map<String, Integer> ucount;
		public void init(Text key, Context context) {
			try {
				urls = new HashMap<String, String>();
				Formatter f = new Formatter();
				int pid = (key.hashCode() & Integer.MAX_VALUE) % 3333;
				FileSystem fs = FileSystem.get(context.getConfiguration());
				String filename = "/trec/urls/part-r-" + f.format("%05d", pid);
				FSDataInputStream is = fs.open(new Path(filename));
				log.info("read urls from " + filename);
				while (true) {
					String line = is.readLine();
					if (line == null)
						break;
					String[] parts = line.split("\t");
					urls.put(parts[0], parts[1]);
				}
				is.close();
				log.info("url count:" + urls.size());
			} catch (Exception e) {
				;
			}
		}
		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			if (urls == null) init(key, context);
			if (!urls.containsKey(key.toString())) return;
			int cnt = 0;
			for (IntWritable val:values) {
				cnt += val.get();
			}
			context.write(new Text(urls.get(key)+"\t"+key), new IntWritable(cnt));
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "trec in degree");
		job.setMapperClass(MyMapper.class);
		job.setReducerClass(MyReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setNumReduceTasks(3);
		FileInputFormat.addInputPath(job, new Path("/trec/test"));
		FileOutputFormat.setOutputPath(job, new Path("/trec/indegree"));
		FileSystem.get(conf).delete(new Path("/trec/indegree"), true);
		job.waitForCompletion(true);
	}
}
