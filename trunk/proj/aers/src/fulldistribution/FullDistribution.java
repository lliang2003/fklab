package fulldistribution;

import java.net.URI;
import java.util.Scanner;

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

public class FullDistribution {

	public static String pathRoot = "hdfs:/apriori2/";
	public static int minsup = 10;

	public static boolean scan(int depth) throws Exception {
		//Configuration conf = new Configuration();
		Job job = new Job(new Configuration(),
				"apriori count distribution scan depth=" + depth);
		Configuration conf = job.getConfiguration();
		conf.set("round", ""+depth);
		FileSystem fs = FileSystem.get(new Configuration());
		FileStatus[] fss = fs.listStatus(new Path(pathRoot+"tree"+depth));
		int treeSplitCount = 0;
		for (FileStatus status: fss) {
			if (!status.isDir()) {
				DistributedCache.addCacheFile(status.getPath().toUri(), conf);
				treeSplitCount += 1;
			}
		}

		job.setMapperClass(ScanMapper.class);
		job.setReducerClass(ScanReducer.class);
		job.setNumReduceTasks(treeSplitCount);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);

		for (int i = 0; i < treeSplitCount; ++i) {
			FileInputFormat.addInputPath(job, new Path("/aers/"+i));
		}
		FileSystem.get(conf).delete(new Path(pathRoot+"fp" + depth), true);
		FileOutputFormat.setOutputPath(job, new Path(pathRoot +"fp"+ depth));
		job.waitForCompletion(true);

		return true;
	}

	public static void init() throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "apriori count distribution init");
		job.setMapperClass(PatternCountMapper.class);
		job.setReducerClass(PatternCountReducer.class);
		job.setNumReduceTasks(10);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path("/aers/0"));
		FileSystem.get(conf).delete(new Path(pathRoot+"fp2"), true);
		FileOutputFormat.setOutputPath(job, new Path(pathRoot+"fp2"));
		job.waitForCompletion(true);
	}
	

	public static void makeTree(int depth, int nr) throws Exception {
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		int space = 0;
		FileStatus[] fss = fs.listStatus(new Path(pathRoot+"fp"+(depth-1)));
		for (FileStatus status:fss)
			space += status.getLen();
		System.out.println(space);
		
		Job job = new Job(conf, "apriori count distribution make tree depth="+depth);
		job.getConfiguration().setInt("old tree depth", depth-1);
		job.setMapperClass(MakeTreeMapper.class);
		job.setReducerClass(MakeTreeReducer.class);
		job.setNumReduceTasks(nr);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(pathRoot+"fp"+(depth-1)));
		FileSystem.get(conf).delete(new Path(pathRoot+"tree"+depth), true);
		FileOutputFormat.setOutputPath(job, new Path(pathRoot+"tree"+depth));
		job.waitForCompletion(true);
	}

	public static void main(String[] args) throws Exception {
		//init();
		//makeTree(3, 10);
		scan(3);
	}
}
