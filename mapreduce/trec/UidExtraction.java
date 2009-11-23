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

public class UidExtraction {
	public static class UidMapper extends
			Mapper<LongWritable, Text, Text, Text> {
		int errcount = 0;
		public static Log log = LogFactory.getLog(UidExtraction.class);

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			try {
				String[] parts = line.split("\t");
				context.write(new Text(parts[0]), new Text(parts[1]));
			} catch (Exception e) {
				log.info("error " + errcount + ":" + e + ";" + line);
				errcount += 1;
				context.setStatus(errcount + ":" + e.getMessage() + ";" + line);
			}
		}
	}

	public static class UidReducer extends Reducer<Text, Text, Text, Text> {
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "uid extraction");
		job.setMapperClass(UidMapper.class);
		job.setReducerClass(UidReducer.class);
		job.setInputFormatClass(MyInputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(3333);
		FileInputFormat.addInputPath(job, new Path("/trec/urls"));
		FileOutputFormat.setOutputPath(job, new Path("/trec/uids"));
		FileSystem.get(conf).delete(new Path("/trec/uids"), true);
		job.waitForCompletion(true);
	}
}
