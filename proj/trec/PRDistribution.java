package trec;

import java.io.IOException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobPriority;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PRDistribution {

	public static class MyMapper extends Mapper<LongWritable, Text, Text, Text> {
		public Map<String, Double> prs;
		public static Log log = LogFactory.getLog("");
		Formatter fmt = new Formatter();

		public void init(Text key, Context context) {
			try {
				prs = new HashMap<String, Double>();
				int pid = (key.hashCode() & Integer.MAX_VALUE) % 3333;
				FileSystem fs = FileSystem.get(context.getConfiguration());
				String filename = "/trec/pr/lastpr/part-r-"
						+ fmt.format("%05d", pid);
				FSDataInputStream is = fs.open(new Path(filename));
				log.info("read prs from " + filename);
				while (true) {
					String line = is.readLine();
					if (line == null)
						break;
					String[] parts = line.split("\t");
					prs.put(parts[0], Double.parseDouble(parts[1]));
				}
				is.close();
				log.info("pr count:" + prs.size());
			} catch (Exception e) {
				;
			}
		}

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] parts = value.toString().split("#");
			String uid = parts[0];
			String url = parts[1];
			if (prs == null)
				init(new Text(uid), context);
			double pr;
			if (!prs.containsKey(uid))
				pr = 0.15;
			else
				pr = prs.get(uid);
			context.write(new Text(""+pr), new Text(uid + "#" + url));
		}
	}

	public static class MyReducer extends Reducer<Text, Text, Text, Text> {
	}

	public static void dist_pagerank(Configuration conf, int nr, String pin,
			String pout) throws Exception {
		Job job = new Job(conf, "trec pagerank distribution");
		job.setMapperClass(MyMapper.class);
		job.setReducerClass(MyReducer.class);
		job.setNumReduceTasks(nr);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(pin));
		FileOutputFormat.setOutputPath(job, new Path(pout));
		FileSystem.get(conf).delete(new Path(pout), true);
		job.waitForCompletion(true);
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		FileSystem hdfs = FileSystem.get(conf);
		int nr = 0;
		dist_pagerank(conf, nr, "/trec/pr/links/part-r-00000", "/trec/pr/final");
	}
}
