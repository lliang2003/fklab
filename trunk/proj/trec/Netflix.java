package trec;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Netflix {

	public static class AggregateMapper extends
			Mapper<LongWritable, Text, Text, Text> {
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] parts = value.toString().split(",");
			String mid = parts[0];
			String uid = parts[1];
			String rate = parts[2];
			context.write(new Text(mid), new Text(uid + ":" + rate));
		}
	}

	public static class AggregateReducer extends
			Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			TreeMap<Integer, Integer> tm = new TreeMap<Integer, Integer>();
			StringBuilder sb = new StringBuilder();
			for (Text val : values) {
				int uid = Integer.parseInt(val.toString().split(":")[0]);
				int rating = Integer.parseInt(val.toString().split(":")[1]);
				tm.put(uid, rating);
			}
			for (int k : tm.keySet())
				sb.append("," + k + "," + tm.get(k));
			context.write(new Text(key + sb.toString()), null);
		}
	}

	public static class Movie {
		public Integer[] uids;
		public Byte[] ratings;
		public int mid;
		public ArrayList<Integer> canopies = new ArrayList<Integer>();
		public int size;

		public Movie(String line) {
			String mstr = line.split("#")[0];
			String[] parts = mstr.split(",");
			size = parts.length / 2;
			mid = Integer.parseInt(parts[0]);
			uids = new Integer[size];
			ratings = new Byte[size];
			for (int i = 1; i < parts.length; i += 2) {
				uids[(i - 1) / 2] = Integer.parseInt(parts[i]);
				ratings[(i - 1) / 2] = Byte.parseByte(parts[i + 1]);
			}

			if (line.contains("#")) {
				String cstr = line.split("#")[1];
				String[] cs = cstr.split(",");
				for (int i = 0; i < cs.length; ++i)
					canopies.add(Integer.parseInt(cs[i]));
			}
		}

		public String toString() {
			StringBuilder sb = new StringBuilder("" + mid);
			for (int i = 0; i < uids.length; ++i) {
				sb.append("," + uids[i] + "," + ratings[i]);
			}
			sb.append("#");
			for (int c : canopies)
				sb.append(c + ",");
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		}

		public boolean near(Movie m, int threshold) {
			int count = 0;
			for (int i = 0, j = 0; i < size && j < m.size;) {
				if (uids[i] > m.uids[j]) {
					++j;
				} else if (uids[i] < m.uids[j]) {
					++i;
				} else {
					++i;
					++j;
					++count;
					if (count >= threshold) return true;
				}
			}
			return false;
		}

		
		public double cosine(Movie m) {
			int a = 0, b = 0, c = 0;
			int count = 0;
			for (int i = 0, j = 0; i < size && j < m.size;) {
				if (uids[i] > m.uids[j]) {
					++j;
				} else if (uids[i] < m.uids[j]) {
					++i;
				} else {
					a += ratings[i] * ratings[i];
					b += m.ratings[j] * m.ratings[j];
					c += ratings[i] * ratings[j];
					++i;
					++j;

				}
			}
			if (c == 0)
				return 0;
			return (double) c * c / a / b;
		}

	}

	public static class ChooserMapper extends
			Mapper<LongWritable, Text, Text, Text> {
		ArrayList<Movie> canopies = new ArrayList<Movie>();

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			try {
				Movie m = new Movie(value.toString());
				for (Movie c : canopies) {
					if (m.near(c, 10))
						return;
				}
				canopies.add(m);
				context.write(new Text(m.toString()), new Text(m.toString()));
			} catch (Exception e) {
				context.setStatus(key + ";" + value + ";" + e);
			}
		}
	}

	public static class ChooserReducer extends Reducer<Text, Text, Text, Text> {
		ArrayList<Movie> canopies = new ArrayList<Movie>();

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			try {
				Movie m = new Movie(key.toString());
				for (Movie c : canopies) {
					if (c.uids.equals(m.uids) && c.ratings.equals(m.ratings))
						return;
				}
				m.canopies.add(m.mid);
				canopies.add(m);
				context.write(new Text(m.toString()), null);
				context.progress();
			} catch (Exception e) {
				context.setStatus(key + ";" + values.iterator().next() + ";"
						+ e);
			}
		}
	}

	public static class AssignMapper extends
			Mapper<LongWritable, Text, Text, Text> {
		ArrayList<Movie> canopies = new ArrayList<Movie>();

		protected void setup(Context context) throws IOException,
				InterruptedException {
			String line;
			int count = 0;
			BufferedReader fis = new BufferedReader(new FileReader(
					"/trec/netflix/canopies/part-r-00000"));
			while ((line = fis.readLine()) != null) {
				canopies.add(new Movie(line));
				++count;
			}
			context.setStatus("read " + count + "canopies");

		}

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			Movie m = new Movie(value.toString());
			for (Movie c : canopies) {
				if (m.near(c, 10))
					m.canopies.add(c.mid);
			}
			context.write(new Text(m.toString()), null);

		}
	}

	public static class AssignReducer extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
		}
	}

	public static class KmeansMapper extends
			Mapper<LongWritable, Text, Text, Text> {
		HashMap<Integer, Movie> centers = new HashMap<Integer, Movie>();

		protected void setup(Context context) throws IOException,
				InterruptedException {
			String line;
			int count = 0;
			for (int i = 0; i < 9; ++i) {
				BufferedReader fis = new BufferedReader(new FileReader(
						"/trec/netflix/last-centers/part-r-0000" + i));
				while ((line = fis.readLine()) != null) {
					Movie m = new Movie(line);
					centers.put(m.canopies.get(0), m);
					++count;
				}
			}
			context.setStatus("read " + count + "centers");

		}

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			try {
				Movie m = new Movie(value.toString());
				int nearest = -1;
				double max = 0;
				for (int canopy : m.canopies) {
					Movie center = centers.get(m);
					double cos = m.cosine(center);
					if (cos > max) {
						max = cos;
						nearest = center.mid;
					}

				}
				context.write(new Text("" + nearest), value);
			} catch (Exception e) {
				context.setStatus(key + ";" + value + ";" + e);
			}
		}
	}

	public static class KmeansReducer extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			HashMap<Integer, Integer> cpos = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> ucount = new HashMap<Integer, Integer>();
			ArrayList<Movie> movies = new ArrayList<Movie>();
			for (Text val : values) {
				Movie m = new Movie(val.toString());
				movies.add(m);
				for (int i = 0; i < m.uids.length; ++i) {
					if (!cpos.containsKey(m.uids[i])) {
						cpos.put(m.uids[i], (int) m.ratings[i]);
						ucount.put(m.uids[i], 1);
					} else {
						cpos.put(m.uids[i], cpos.get(m.uids[i]) + m.ratings[i]);
						ucount.put(m.uids[i], ucount.get(m.uids[i]) + 1);
					}

				}
			}
			Movie new_center = null;
			double mindiff = Double.MAX_VALUE;
			for (Movie m : movies) {
				double diff = 0;
				for (int i = 0; i < m.uids.length; ++i) {
					double avg = (double) cpos.get(m.uids[i])
							/ ucount.get(m.uids[i]);
					diff += Math.abs(avg - m.ratings[i]);
					if (diff < mindiff) {
						mindiff = diff;
						new_center = m;
					}
				}
			}
			new_center.canopies.clear();
			new_center.canopies = new Movie(key.toString()).canopies;
			context.write(new Text(new_center.toString()), null);
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
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(pin));
		FileOutputFormat.setOutputPath(job, new Path(pout));
		FileSystem.get(conf).delete(new Path(pout), true);
		job.waitForCompletion(true);
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		int nr = 6;
		// newjob(conf, "netflix aggregate", AggregateMapper.class,
		// AggregateReducer.class, nr, "/smallnetflix",
		// "/trec/netflix/aggregate");
//		newjob(conf, "netflix choose canopies", ChooserMapper.class,
//				ChooserReducer.class, 1, "/trec/netflix/aggregate",
//				"/trec/netflix/canopies");
//		 newjob(conf, "netflix assign movies", AssignMapper.class,
//		 AssignReducer.class, 0, "/trec/netflix/aggregate",
//		 "/trec/netflix/assigned");

	}
}
