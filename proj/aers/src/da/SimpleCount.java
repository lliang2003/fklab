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
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * This class calculate PRR for a data set using simple count methods.
 * The first map-reduce round count patterns and aggregate them by drugs.
 * The second map-reduce round aggregate them by reactions and calculate PRR values. 

 * @author fankai
 */
public class SimpleCount {
	public static Log log = LogFactory.getLog(SimpleCount.class);

	/** Count occurrence and aggregate drug pattern count. */  
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

	public static void initAndDrugAgg() throws Exception {
		Configuration conf = new Configuration();
		conf.setInt("filterSize", 10);
		conf.setInt("maxLength", 5);
		conf.setInt("minSupport", 3);
		Job job = new Job(conf, "Data Initialization And Drug Aggregation");
		job.setJarByClass(SimpleCount.class);
		job.setOutputKeyClass(ItemSet.class);
		job.setOutputValueClass(IntWritable.class);
		job.setInputFormatClass(TextInputFormat.class);
		TextInputFormat.setMaxInputSplitSize(job, 1024*1024);
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
