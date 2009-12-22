package trec;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Apriori {

	public static String nextCombination(ArrayList<String> list, int[] pointers) {
		for (int i = pointers.length - 1; i >= 0; --i) {
			if (pointers[i] < list.size() - pointers.length + i) {
				pointers[i]++;
				for (int j = i + 1; j < pointers.length; ++j)
					pointers[j] = pointers[i] + (j - i);
				StringBuilder sb = new StringBuilder(list.get(pointers[0]));
				for (int j = 1; j < pointers.length; ++j)
					sb.append(" " + list.get(pointers[j]));
				return sb.toString();
			}
		}
		return null;
	}

	public static String[] combinations(ArrayList<String> items, int num) {
		ArrayList<String> as = new ArrayList<String>();
		int[] p = new int[num];
		p[0] = -1;
		for (int i = 1; i < num; ++i)
			p[i] = items.size() - num + i;
		String s;
		while ((s = nextCombination(items, p)) != null) {
			as.add(s);
		}
		return as.toArray(new String[0]);
	}

	public static String join(ArrayList<String> as) {
		StringBuilder sb = new StringBuilder(as.get(0));
		for (int j = 1; j < as.size(); ++j)
			sb.append(as.get(j));
		return sb.toString();
	}

	public static class MyMapper extends
			Mapper<Object, Text, Text, IntWritable> {
		IntWritable one = new IntWritable(1);

		@Override
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			for (String s : value.toString().split(" ")) {
				context.write(new Text(s), one);
			}
		}
	}

	public static class MyMapper2 extends
			Mapper<Object, Text, Text, IntWritable> {
		IntWritable one = new IntWritable(1);
		Set<String> frequent_itemsets = new HashSet<String>();
		Set<String> vitems = new HashSet<String>();
		int round;
		int icount = 0;
		int vcount = 0;
		int scount = 0;

		protected void setup(Context context) throws IOException,
				InterruptedException {
			round = Integer.parseInt(context.getJobName().split("-")[1]);
			Path[] paths = DistributedCache.getLocalCacheFiles(context
					.getConfiguration());
			FileSystem fs = FileSystem.get(context.getConfiguration());
			for (Path p : paths) {
				BufferedReader reader = new BufferedReader(new FileReader(p
						.toString()));
				String line;
				while ((line = reader.readLine()) != null) {
					frequent_itemsets.add(line);
					for (String s : line.split(" "))
						vitems.add(s);
					scount++;
					context.setStatus(round + " " + scount);
				}
			}
		}

		public boolean valid(String items) {
			ArrayList<String> as = new ArrayList<String>();
			for (String s : items.split(" "))
				as.add(s);
			for (String subset : combinations(as, as.size() - 1)) {
				if (!frequent_itemsets.contains(subset))
					return false;
			}
			return true;
		}

		@Override
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			ArrayList<String> items = new ArrayList<String>();
			for (String s : value.toString().split(" ")) {
				if (vitems.contains(s))
					items.add(s);
			}
			// context.write(key, new Text(join(items)));
			for (String s : combinations(items, round)) {
				icount += 1;
				if (valid(s)) {
					context.write(new Text(s), one);
					vcount += 1;
				}
			}
			context.setStatus(vitems.size() + " "+scount+" " + vcount + " " + icount);
			context.progress();
		}
	}

	public static class MyReducer extends
			Reducer<Text, IntWritable, Text, Text> {
		int icount = 0;
		int fcount = 0;

		@Override
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int count = 0;
			for (IntWritable val : values) {
				count += val.get();
			}
			icount++;
			if (count > 3) {
				fcount++;
				context.write(key, null);
			}
			context.setStatus(fcount + " in " + icount);
		}
	}

	public static void newjob(Configuration conf, String name,
			Class<? extends Mapper> mc, Class<? extends Reducer> rc, int nr,
			String pin, String pout) throws Exception {
		Job job = new Job(conf, name);
		job.setMapperClass(mc);
		job.setReducerClass(rc);
		job.setNumReduceTasks(nr);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		// job.setOutputFormatClass(SequenceFileOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(pin));
		FileOutputFormat.setOutputPath(job, new Path(pout));
		FileSystem.get(conf).delete(new Path(pout), true);
		job.waitForCompletion(true);
	}

	public static void loop(int round, Configuration conf) throws Exception {
		DistributedCache.purgeCache(conf);
		DistributedCache.addCacheFile(new Path("/test/apr-" + (round - 1)
				+ "/part-r-00000").toUri(), conf);
		newjob(conf, "apr-" + round, MyMapper2.class, MyReducer.class, 1,
				"/test/input", "/test/apr-" + round);
	}

	public static void apriori() throws Exception {
		Configuration conf = new Configuration();
		newjob(conf, "apr-1", MyMapper.class, MyReducer.class, 1,
				"/test/input", "/test/apr-1");
		for (int r = 2; r < 10; ++r)
			loop(r, conf);
	}

	public static boolean valid(String items, HashSet<String> vitems) {
		ArrayList<String> as = new ArrayList<String>();
		for (String s : items.split(" "))
			as.add(s);
		for (String subset : combinations(as, as.size() - 1)) {
			if (!vitems.contains(subset))
				return false;
		}
		return true;
	}

	public static void lapriori() throws Exception {
		HashMap<String, Integer> itemcount = new HashMap<String, Integer>();
		HashSet<String> vitems = new HashSet<String>();
		BufferedReader reader = new BufferedReader(new FileReader(
				"F:\\Desktop\\apr.data"));
		ArrayList<String> lines = new ArrayList<String>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			lines.add(line);
			for (String s : line.split(" ")) {
				if (itemcount.containsKey(s))
					itemcount.put(s, itemcount.get(s) + 1);
				else
					itemcount.put(s, 1);
			}
		}
		for (String s : itemcount.keySet()) {
			if (itemcount.get(s) > 10) {
				// System.out.println(s+" "+itemcount.get(s));
				vitems.add(s);
			}
		}
		System.out.println(vitems.toString());

		// round2
		HashSet<String> vitems2 = new HashSet<String>();
		HashMap<String, Integer> icount2 = new HashMap<String, Integer>();
		for (String str : lines) {
			System.out.println("-----------------");
			System.out.println(str);
			ArrayList<String> as = new ArrayList<String>();
			for (String s : str.split(" "))
				if (vitems.contains(s))
					as.add(s);
			for (String s : combinations(as, 2)) {
				if (valid(s, vitems)) {
					System.out.printf(s + ';');
					if (icount2.containsKey(s))
						icount2.put(s, icount2.get(s) + 1);
					else
						icount2.put(s, 1);
				}
			}
			System.out.println();
		}
		for (String s : icount2.keySet()) {
			if (icount2.get(s) > 5) {
				// System.out.println(s+" "+itemcount.get(s));
				vitems2.add(s);
			}
		}
		System.out.println(vitems2.toString());

		// round3
		HashSet<String> vitems3 = new HashSet<String>();
		for (String str : lines) {
			System.out.println("==================");
			System.out.println(str);
			ArrayList<String> as = new ArrayList<String>();
			for (String s : str.split(" "))
				if (vitems.contains(s))
					as.add(s);
			for (String s : combinations(as, 3)) {
				if (valid(s, vitems2))
					System.out.printf(s + ';');
			}
			System.out.println();

		}

	}

	public static void main(String[] args) throws Exception {
		apriori();
	}
}
