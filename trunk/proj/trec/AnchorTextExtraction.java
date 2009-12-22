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

public class AnchorTextExtraction {
	public static class AnchorMapper extends
			Mapper<LongWritable, Text, Text, Text> {
		int errcount = 0;
		public static Log log = LogFactory.getLog(AnchorTextExtraction.class);

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			try {
				if (line.startsWith("###")) {
					String[] parts = line.split(" +");
					context.write(new Text(parts[2]), new Text(parts[0] + ":"
							+ parts[1] + "###"));
				} else if (line.startsWith("http://") && line.contains(" ")) {
					String[] parts = line.split(" +", 2);
					String at = parts[1].replace("#", " ").trim();
					Text to_url = new Text(parts[0].split("#")[0]);
					if (at.length()>0)
						context.write(to_url, new Text(at));
				}
				context.progress();
			} catch (Exception e) {
				log.info("error "+errcount+":"+e+";"+line);
				errcount += 1;
				context.setStatus(errcount + ":" + e.getMessage() + ";" + line);
			}
		}
	}

	public static class AnchorReducer extends Reducer<Text, Text, Text, Text> {
		public static Log log = LogFactory.getLog(AnchorTextExtraction.class);
		int count = 0;
		int rcount = 0;
		int wcount = 0;
		int rrcount = 0;
		int max = 0;
		Text atext = new Text();

		protected void setup(Context context) throws IOException,
				InterruptedException {
		}

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			rcount++;
			rrcount = 0;
			atext.clear();
			String id = null;
			for (Text val : values) {
				count++;
				rrcount ++;
//				log.info(rcount + " " + wcount + " " + count + " " + max);
				if (count % 1000 == 0) {
					context.progress();
					log.info(key.toString());
					log.info(rcount + " " + wcount + " " + count + " "+rrcount+" " + max);
				}
				if (val.toString().startsWith("###")
						&& val.toString().endsWith("###"))
					id = val.toString();
				else {
//					log.info(atext.getLength() + " " + val);
					atext.append("#".getBytes(), 0, 1);
					atext.append(val.getBytes(), 0, val.getLength());
				}
				if (atext.getLength() > max) {
					max = atext.getLength();
				}
			}
			if (id != null) {
				wcount++;
				context.write(new Text(id+key), atext);
			}
			context.progress();
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "trec anchor text extraction");
		job.setMapperClass(AnchorMapper.class);
		job.setReducerClass(AnchorReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(3);
		FileInputFormat.addInputPath(job, new Path("/trec/test"));
		FileOutputFormat.setOutputPath(job, new Path("/trec/atext2"));
		FileSystem.get(conf).delete(new Path("/trec/atext2"), true);
		job.waitForCompletion(true);
	}
}
