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
import org.apache.hadoop.fs.FileStatus;
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

import trec.AnchorTextExtraction2.AnchorMapper;
import trec.AnchorTextExtraction2.AnchorReducer;


public class TrecPageRank {

	public static class ReverseFilterMapper extends
			Mapper<LongWritable, Text, Text, Text> {
		int ecount = 0;
		int mcount = 0;
		int wcount = 0;
		String first_uid = null;
		public static Log log = LogFactory.getLog(TrecPageRank.class);

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			mcount++;
			try {
				String[] lines = value.toString().split("\n");
				String[] parts = lines[0].split(" ");
				Text url = new Text(parts[2].split("#")[0]);
				Text uid = new Text(parts[0].substring(3) + ":" + parts[1]);
				if (mcount % 10000 == 0)
					context.setStatus(mcount + " write " + wcount + " error "
							+ ecount);
				Set<String> url_list = new HashSet<String>();

				for (int i = 1; i < lines.length; ++i) {
					if (!lines[i].startsWith("http"))
						continue;
					int j = 0;
					while (j < lines[i].length() && lines[i].charAt(j) != ' '
							&& lines[i].charAt(j) != '#')
						++j;
					String to_url = lines[i].substring(0, j);
					if (to_url.equals(url.toString())
							|| url_list.contains(to_url))
						continue;
					url_list.add(to_url);
					context.write(new Text(to_url), uid);
					wcount++;
				}
			} catch (Exception e) {
				ecount++;
				log.info("error " + ecount + ":"
						+ value.toString().replace("\n", ",") + ";"
						+ e.toString());
			}
			context.progress();
		}
	}

	public static class ReverseFilterReducer extends
			Reducer<Text, Text, Text, Text> {
		public static Log log = LogFactory.getLog(ReverseFilterReducer.class);
		Map<String, String> urls;

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			if (urls == null)
				init(key, context);
			if (!urls.containsKey(key.toString()))
				return;
			for (Text val : values) {
				context.write(new Text(urls.get(key.toString())), val);
				context.progress();
			}
		}

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

	}

	public static class ReverseInitMapper extends
			Mapper<LongWritable, Text, Text, Text> {
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] parts = value.toString().split("\t");
			context.write(new Text(parts[1]), new Text(parts[0]));
		}
	}

	public static class ReverseInitReducer extends
			Reducer<Text, Text, Text, Text> {
		public static Log log = LogFactory.getLog(ReverseInitReducer.class);
		public Map<String, String> urls;

		public void init(Text key, Context context) {
			try {
				urls = new HashMap<String, String>();
				Formatter f = new Formatter();
				int pid = (key.hashCode() & Integer.MAX_VALUE) % 3333;
				FileSystem fs = FileSystem.get(context.getConfiguration());
				String filename = "/trec/uids/part-r-" + f.format("%05d", pid);
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
				log.info("uid count:" + urls.size());
			} catch (Exception e) {
				log.info(e.toString() + " error: uid count:" + urls.size());
				;
			}
		}

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			if (urls == null)
				init(key, context);
			context.write(new Text(key + "#" + urls.get(key.toString())), null);
			for (Text val : values) {
				context.write(new Text("#"), null);
				context.write(val, null);
			}
			context.write(new Text("\n"), null);
			context.progress();
		}
	}

	public static class PageRankMapper0 extends
			Mapper<LongWritable, Text, Text, Text> {
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] parts = value.toString().split("#");
			String uid = parts[0];
			double pr = 1;
			Text dpr = new Text("" + 1.0 / (parts.length - 2));
			for (int i = 2; i < parts.length; ++i) {
				context.write(new Text(parts[i]), dpr);
			}
		}
	}

	public static class PageRankMapper extends
			Mapper<LongWritable, Text, Text, Text> {
		public Map<String, Double> prs;
		public static Log log = LogFactory.getLog(PageRankMapper.class);

		public void init(Text key, Context context) {
			try {
				prs = new HashMap<String, Double>();
				Formatter f = new Formatter();
				int pid = (key.hashCode() & Integer.MAX_VALUE) % 3333;
				FileSystem fs = FileSystem.get(context.getConfiguration());
				String filename = "/trec/pr/lastpr/part-r-"
						+ f.format("%05d", pid);
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
			if (prs == null)
				init(new Text(uid), context);
			double pr;
			if (!prs.containsKey(uid))
				pr = 0.15;
			else
				pr = prs.get(uid);
			Text dpr = new Text("" + 1.0 / (parts.length - 2));
			for (int i = 2; i < parts.length; ++i) {
				context.write(new Text(parts[i]), dpr);
			}
		}
	}

	public static class PageRankReducer extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			String links = null;
			double pr = 0;
			for (Text val : values) {
				pr += Double.parseDouble(val.toString());
			}
			pr = pr * 0.85 + 0.15;
			context.write(key, new Text("" + pr));
		}
	}

	public static void reverse_filter(Configuration conf, int nr, String pout)
			throws Exception {
		Job job = new Job(conf, "trec reverse & filter");
		job.setMapperClass(ReverseFilterMapper.class);
		job.setReducerClass(ReverseFilterReducer.class);
		job.setNumReduceTasks(nr);
		job.setInputFormatClass(TrecInputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path("/trec/data"));
		FileOutputFormat.setOutputPath(job, new Path(pout));
		FileSystem.get(conf).delete(new Path(pout), true);
		job.waitForCompletion(true);
	}

	public static void reverse_init(Configuration conf, int nr, String pout)
			throws Exception {
		Job job = new Job(conf, "trec reverse & init");
		job.setMapperClass(ReverseInitMapper.class);
		job.setReducerClass(ReverseInitReducer.class);
		job.setNumReduceTasks(nr);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setOutputFormatClass(TrecOutputFormat.class);
		job.setInputFormatClass(MyInputFormat.class);
		FileInputFormat.addInputPath(job, new Path("/trec/inlinks"));
		FileOutputFormat.setOutputPath(job, new Path(pout));
		FileSystem.get(conf).delete(new Path(pout), true);
		job.waitForCompletion(true);
	}

	public static void calc_pagerank0(Configuration conf, int nr, String pin,
			String pout) throws Exception {
		JobPriority jp = JobPriority.LOW;
		Job job = new Job(conf, "trec pagerank round 0");
		job.setMapperClass(PageRankMapper0.class);
		job.setReducerClass(PageRankReducer.class);
		job.setNumReduceTasks(nr);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(pin));
		FileOutputFormat.setOutputPath(job, new Path(pout));
		FileSystem.get(conf).delete(new Path(pout), true);
		job.waitForCompletion(true);
	}

	public static void calc_pagerank(Configuration conf, int nr, String pin,
			String pout, int round) throws Exception {
		JobPriority jp = JobPriority.LOW;
		Job job = new Job(conf, "trec pagerank round " + round);
		job.setMapperClass(PageRankMapper.class);
		job.setReducerClass(PageRankReducer.class);
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
		int nr = 3333;
		reverse_filter(conf, nr, "/trec/inlinks");
		for (FileStatus f : FileSystem.get(conf).listStatus(
				new Path("/trec/inlinks")))
			FileSystem.get(conf).setReplication(f.getPath(), (short) 3);
		
		reverse_init(conf, nr, "/trec/links");
		for (FileStatus f : FileSystem.get(conf).listStatus(
				new Path("/trec/links")))
			FileSystem.get(conf).setReplication(f.getPath(), (short) 3);
		
		calc_pagerank0(conf, nr, "/trec/links", "/trec/pr/0");
		for (int i = 1; i < 10; ++i) {
			hdfs.rename(new Path("/trec/pr/" + (i - 1)), new Path(
					"/trec/pr/lastpr"));
			for (FileStatus f : FileSystem.get(conf).listStatus(
					new Path("/trec/pr/lastpr")))
				FileSystem.get(conf).setReplication(f.getPath(), (short) 3);
			calc_pagerank(conf, nr, "/trec/pr/links", "/trec/pr/" + i, i);
			hdfs.rename(new Path("/trec/pr/lastpr"), new Path("/trec/pr/"
					+ (i - 1)));
		}
	}
}
