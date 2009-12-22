package trec;

import java.io.IOException;

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
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class UrlExtraction {
	public static class AnchorMapper extends
			Mapper<LongWritable, Text, Text, Text> {
		int errcount = 0;
		public static Log log = LogFactory.getLog(UrlExtraction.class);

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line;
			if (value.getLength() > 1000)
				line = new String(value.getBytes(), 0, 1000);
			else
				line = value.toString();
			try {
				String[] parts = line.split("#", 3);
				context.write(new Text(parts[1]), new Text(parts[0]));
			} catch (Exception e) {
				log.info("error " + errcount + ":" + e + ";" + line);
				errcount += 1;
				context.setStatus(errcount + ":" + e.getMessage() + ";" + line);
			}
		}
	}

	public static class AnchorReducer extends Reducer<Text, Text, Text, Text> {

	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "link extraction");
		job.setMapperClass(AnchorMapper.class);
		job.setReducerClass(AnchorReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(3333);
		job.setInputFormatClass(MyInputFormat.class);
		FileInputFormat.addInputPath(job, new Path("/trec/atext2"));
		FileOutputFormat.setOutputPath(job, new Path("/trec/urls"));
		FileSystem.get(conf).delete(new Path("/trec/urls"), true);
		job.waitForCompletion(true);
	}
}
