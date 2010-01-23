package da;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * Calculate PRR value, using Distributed Apriori to get pattern counts.
 * @author fankai
 */
public class PRR {
	
	public static String getAggPath(Configuration conf) {
		return DA.getOutputPath(conf)+"/drugagg";
	}
	public static String getPRRPath(Configuration conf) {
		return DA.getOutputPath(conf)+"/prr";
	}	
	public static int getReducerNum(Configuration conf) {
		return conf.getInt("npr", 99);
	}
	public static void aggDrug(Configuration conf) throws Exception {
		System.out.println("PRR: Drug Aggregation");
		Job job = new Job(conf, "PRR: Drug Aggregation");
		job.setJarByClass(PRR.class);
		job.setOutputKeyClass(ItemSet.class);
		job.setOutputValueClass(Text.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setMapperClass(AggMapper.class);
		job.setReducerClass(AggDrugReducer.class);
		job.setPartitionerClass(DrugPartitioner.class);
		job.setNumReduceTasks(getReducerNum(conf));
		FileSystem fs = FileSystem.get(conf);
		String output = getAggPath(conf);
		
		for (FileStatus status: fs.listStatus(new Path(DA.getOutputPath(conf)))) {
			if (status.getPath().getName().startsWith("fp-"))
				FileInputFormat.addInputPath(job, status.getPath());
		}
		FileSystem.get(conf).delete(new Path(output), true);
		FileOutputFormat.setOutputPath(job, new Path(output));
		job.waitForCompletion(true);
	}
	
	public static void aggReactionAndCalc(Configuration conf) throws Exception {
		System.out.println("PRR: Reaction Aggregation And PRR Calculation");
		Job job = new Job(conf, "PRR: Reaction Aggregation And PRR Calculation");
		job.setJarByClass(PRR.class);
		job.setOutputKeyClass(ItemSet.class);
		job.setOutputValueClass(Text.class);
		job.setMapperClass(AggMapper.class);
		job.setReducerClass(AggAndCalcReducer.class);
		job.setPartitionerClass(ReactionPartitioner.class);
		job.setNumReduceTasks(getReducerNum(conf));
		FileInputFormat.addInputPath(job, new Path(getAggPath(conf)));
		String output = getPRRPath(conf);
		FileSystem.get(conf).delete(new Path(output), true);
		FileOutputFormat.setOutputPath(job, new Path(output));
		job.waitForCompletion(true);
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		new GenericOptionsParser(conf, args);
		DA.da(conf);
		
		System.out.println("Proportional Report Ratio Calculation.");
		System.out.println("total cases:\t"+conf.getInt("total", 1));

		aggDrug(conf);
		aggReactionAndCalc(conf);
	}

}
