package da;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class SimpleCount {
	public static Log log = LogFactory.getLog(SimpleCount.class);

	public static class GenInputFormat extends TextInputFormat {
		protected long computeSplitSize(long blockSize, long minSize, long maxSize) {
			return 1024*1024;
		}
	}
	
	public static class DrugPartitioner extends
			Partitioner<ItemSet, Object> {
		public int getPartition(ItemSet key, Object value,
				int numPartitions) {
			int h = key.drugHashCode();
			if (h == 0)
				h = key.hashCode();
			return (h & Integer.MAX_VALUE) % numPartitions;
		}
	}

	public static class ReactionPartitioner extends
			Partitioner<ItemSet, Object> {
		public int getPartition(ItemSet key, Object value,
				int numPartitions) {
			int h = key.reactionHashCode();
			if (h == 0)
				h = key.hashCode();
			return (h & Integer.MAX_VALUE) % numPartitions;
		}
	}

	// Count occurence and aggregate drug pattern count.  
	public static class DrugAggReducer extends
			Reducer<ItemSet, IntWritable, ItemSet, Text> {
		int maxLength;
		int minSupport;
		HashMap<String, Integer> drugCount = new HashMap<String, Integer>();

		protected void setup(Context context) throws IOException,
				InterruptedException {
			maxLength = context.getConfiguration().getInt("maxLength", 1);
			minSupport = context.getConfiguration().getInt("minSupport", 3);
		}

		public void reduce(ItemSet key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int count = 0;
			for (IntWritable cnt : values) {
				count += cnt.get();
			}
			if (count < minSupport)
				return;
			if (!key.hasReaction()) {
				// This is a drug pattern, save its count in hashmap.
				log.info("add " + drugCount.size() + "th drug pattern:" + key);
				drugCount.put(key.toString(), count);
			} else if (!key.hasDrug()) {
				// This is a reaction pattern, simply write its count.
				context.write(key, new Text("" + count));
			} else {
				ItemSet dset = key.drugSet();
				int dcnt = drugCount.get(dset.toString());
				context.write(key, new Text(count + " " + dcnt));
			}
		}
	}

	public static class AggMapper extends
			Mapper<Object, Text, ItemSet, Text> {
		protected void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] tokens = value.toString().split("\t");
			context.write(new ItemSet(tokens[0]), new Text(tokens[1]));
		}
	}

	// Aggregate reaction pattern count and calculate PRR value.
	public static class AggAndCalcReducer extends
			Reducer<ItemSet, Text, ItemSet, Text> {
		int total;
		HashMap<String, String> reactionCount = new HashMap<String, String>();

		protected void setup(Context context) throws IOException,
				InterruptedException {
			total = context.getConfiguration().getInt("total", 1);
		}

		public void reduce(ItemSet key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			String val = values.iterator().next().toString();
			if (!key.hasDrug()) {
				// This is a reaction pattern, save its count in hashmap.
				log.info("add " + reactionCount.size() + "th reaction pattern:" + key);
				reactionCount.put(key.toString(), val);
			} else {
				ItemSet rset = key.reactionSet();
				int rcnt = Integer.parseInt(reactionCount.get(rset.toString()));
				int pcnt = Integer.parseInt(val.split(" ")[0]);
				int dcnt = Integer.parseInt(val.split(" ")[1]);
				double prr = (double)total*pcnt/dcnt/rcnt;
				context.write(key, new Text(val+" "+rcnt+" "+prr));
			}
		}
	}

	public static void initAndDrugAgg() throws Exception {
		Configuration conf = new Configuration();
		conf.setInt("filterSize", 10);
		conf.setInt("maxLength", 5);
		conf.setInt("minSupport", 3);
		Job job = new Job(conf, "Data Initialization And Drug Aggregation");
		job.setJarByClass(SimpleCount.class);
		job.setOutputKeyClass(ItemSet.class);
		job.setOutputValueClass(IntWritable.class);
		job.setInputFormatClass(GenInputFormat.class);
		job.setMapperClass(CountMapper.class);
		job.setReducerClass(DrugAggReducer.class);
		job.setPartitionerClass(DrugPartitioner.class);
		job.setNumReduceTasks(30);
		FileInputFormat.addInputPath(job, new Path("/dat/tmp"));
		FileSystem.get(conf).delete(new Path("/simple/agg"), true);
		FileOutputFormat.setOutputPath(job, new Path("/simple/agg"));
		job.waitForCompletion(true);
	}
	
	public static void aggAndCalc() throws Exception {
		Configuration conf = new Configuration();
		conf.setInt("total", 10000);
		Job job = new Job(conf, "Reaction Aggregation And PRR Calculation");
		job.setJarByClass(SimpleCount.class);
		job.setOutputKeyClass(ItemSet.class);
		job.setOutputValueClass(Text.class);
//		job.setInputFormatClass(MyInputFormat.class);
		job.setMapperClass(AggMapper.class);
		job.setReducerClass(AggAndCalcReducer.class);
		job.setPartitionerClass(ReactionPartitioner.class);
		job.setNumReduceTasks(30);
		FileInputFormat.addInputPath(job, new Path("/simple/agg"));
		FileSystem.get(conf).delete(new Path("/simple/prr"), true);
		FileOutputFormat.setOutputPath(job, new Path("/simple/prr"));
		job.waitForCompletion(true);
	}
	
	public static void main(String[] args) throws Exception {
		initAndDrugAgg();
		aggAndCalc();
	}
}
