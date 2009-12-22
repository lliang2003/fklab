package trec;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TrecDistribution {
	public static class MyMapper extends
			Mapper<LongWritable, Text, LongWritable, Text> {
		public static Log log = LogFactory.getLog(TrecDistribution.class);
		HashMap<String, Integer> nmap = new HashMap<String, Integer>();

		protected void setup(Context context) throws IOException,
				InterruptedException {
			for (int i = 0; i < 10; ++i) {
				nmap.put("index" + (i + 1), i);
				if (i < 5) {
					nmap.put("maze6" + (i + 1), 10 + i);
					nmap.put("jupiter" + i, 15 + i);
				}
			}
			for (int i = 1; i <= 10; ++i)
				nmap.put("index" + i, i - 1);
			for (int i = 0; i < 5; ++i) {
				nmap.put("maze6" + (i + 1), 10 + i);
				nmap.put("jupiter" + i, 15 + i);
			}
			nmap.put("maze4", 13);
			nmap.put("build5", 20);
			nmap.put("build8", 21);
			nmap.put("build9", 22);
			log.info(((FileSplit)context.getInputSplit()).getPath().getName());
			log.info(context.getConfiguration().getInt("io.file.buffer.size", 1024));
		}
		int mcount = 0;
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			try {
//				log.info(++mcount);
				String line = new String(value.getBytes(), 0, 15);
				if (line.startsWith("null")) return;
				int i = nmap.get(line.substring(0, line.indexOf(":")));
				key.set(i * np + key.get() % np);
//				log.info("done "+value.getLength()+ " " + line);
				context.write(key, value);
			} catch (Exception e) {
				log.info(value.toString());
			}
			context.progress();
		}

		protected void cleanup(Context context) throws IOException,
				InterruptedException {
			log.info("map cleanup");
			// NOTHING
		}
	}

	public static class MyReducer extends
			Reducer<LongWritable, Text, Text, Text> {
		public void reduce(LongWritable key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			for (Text val : values)
				context.write(val, null);
			context.progress();
		}
	}

	public static int np = 1;

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "trec distribution");
		job.setMapperClass(MyMapper.class);
		job.setReducerClass(MyReducer.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(Text.class);
//		job.setNumReduceTasks(0);
		job.setInputFormatClass(MyInputFormat.class);
		job.setNumReduceTasks(23 * np);
		FileInputFormat.addInputPath(job, new Path("/trec/atext"));
		FileOutputFormat.setOutputPath(job, new Path("/trec/atext2"));
		FileSystem.get(conf).delete(new Path("/trec/atext2"), true);
		job.waitForCompletion(true);
	}
}
