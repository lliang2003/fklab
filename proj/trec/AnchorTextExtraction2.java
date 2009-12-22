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
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import trec.UrlExtraction.AnchorMapper;
import trec.UrlExtraction.AnchorReducer;


public class AnchorTextExtraction2 {
	public static class AnchorMapper extends
			Mapper<LongWritable, Text, Text, Text> {
		int errcount = 0;
		public static Log log = LogFactory.getLog(AnchorTextExtraction2.class);

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			try {
				if (line.startsWith("###")) {
					String[] parts = line.split(" +");
					context.write(new Text(parts[2]), new Text(parts[0] + ":"
							+ parts[1]));
				} else if (line.startsWith("http://") && line.contains(" ")) {
					String[] parts = line.split(" +", 2);
					String at = parts[1].replace("#", " ").trim();
					Text to_url = new Text(parts[0].split("#")[0]);
					if (at.length() > 0)
						context.write(to_url, new Text(at));
				}
				context.progress();
			} catch (Exception e) {
				log.info("error " + errcount + ":" + e + ";" + line);
				errcount += 1;
				context.setStatus(errcount + ":" + e.getMessage() + ";" + line);
			}
		}
	}

	public static class AnchorReducer extends Reducer<Text, Text, Text, Text> {
		public static Log log = LogFactory.getLog(AnchorTextExtraction2.class);

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			Map<String, Integer> atexts = new HashMap<String, Integer>();
			String uid = null;
			for (Text val : values) {
				if (val.toString().startsWith("###"))
					uid = val.toString().substring(3);
				else if (!atexts.containsKey(val.toString()))
					atexts.put(val.toString(), 1);
				else
					atexts.put(val.toString(), atexts.get(val.toString()) + 1);
			}
			if (uid == null) return;
			context.write(new Text(uid + "#" + key), null);
			for (String s : atexts.keySet())
				context.write(new Text("#" + atexts.get(s) + ":" + s), null);
			context.write(new Text("\n"), null);
			context.progress();
		}
	}

	
	public static void getatexts() throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "trec anchor text extraction 2");
		job.setMapperClass(AnchorMapper.class);
		job.setReducerClass(AnchorReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(33);
		job.setOutputFormatClass(TrecOutputFormat.class);
		job.setInputFormatClass(MyInputFormat.class);
		FileInputFormat.addInputPath(job, new Path("/trec/data2"));
		FileOutputFormat.setOutputPath(job, new Path("/trec/atext_maze"));
		FileSystem.get(conf).delete(new Path("/trec/atext_maze"), true);
		job.waitForCompletion(true);
	}
	public static void main(String[] args) throws Exception {
		getatexts();
	}
}
