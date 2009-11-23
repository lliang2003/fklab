package apriori;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Apriori {
	public static int minsup = 200;
	
	public static void run(int round) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "apriori round "+round);
		job.setMapperClass(AprioriMapper.class);
		job.setReducerClass(AprioriReducer.class);
		job.setNumReduceTasks(0);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		DistributedCache.addCacheFile(new URI("hdfs:/apriori/init/part-r-00000"), job.getConfiguration());
		
	    FileInputFormat.addInputPath(job, new Path("/aers"));
		FileSystem.get(conf).delete(new Path("/apriori/round"+round), true);
	    FileOutputFormat.setOutputPath(job, new Path("/apriori/round"+round));
		job.waitForCompletion(true);	
	}
	
	public static void main(String[] args) throws Exception {
		run(2);
	}
}
