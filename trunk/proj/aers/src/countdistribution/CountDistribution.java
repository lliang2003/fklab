package countdistribution;

import java.net.URI;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class CountDistribution {

	public static int minsup = 10;

	public static boolean runRound(int round) throws Exception {
		//Configuration conf = new Configuration();
		Job job = new Job(new Configuration(),
				"apriori count distribution round " + round);
		Configuration conf = job.getConfiguration();
		conf.set("round", ""+round);
		System.out.println(conf);


		String treePath = "hdfs:/apriori/round" + (round - 1) + "/part-r-00000";
		FileStatus file = FileSystem.get(conf)
				.getFileStatus(new Path(treePath));
		Scanner sc = new Scanner(FileSystem.get(conf).open(file.getPath()));
		int num = sc.nextInt();
		System.out.println("candidate number:" + num);
		System.out.println("file length:" + file.getLen());
		if (num == 0) return false;
		System.out.println("start round:" + round);
		
		
		job.setMapperClass(CountDistributionMapper.class);
		job.setReducerClass(CountDistributioniReducer.class);
		job.setNumReduceTasks(1);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		DistributedCache.addCacheFile(new URI(treePath), conf);

		FileInputFormat.addInputPath(job, new Path("/aers"));
		FileSystem.get(conf).delete(new Path("/apriori/round" + round), true);
		FileOutputFormat.setOutputPath(job, new Path("/apriori/round" + round));
		job.waitForCompletion(true);

		return true;
	}

	public static void init() throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "apriori init");
		job.setMapperClass(PatternCountMapper.class);
		job.setReducerClass(PatternCountReducer.class);
		job.setNumReduceTasks(1);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path("/aers"));
		FileSystem.get(conf).delete(new Path("/apriori/round1"), true);
		FileOutputFormat.setOutputPath(job, new Path("/apriori/round1"));
		job.waitForCompletion(true);
	}

	public static void main(String[] args) throws Exception {
		init();
		int round = 2;
		while (round < 9 && runRound(round))
			round ++;
	}
}
