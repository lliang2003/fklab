package auto;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class AutoSplit {

	public static String pathRoot = "hdfs:/apriori3/";
	public static String treePathRoot = pathRoot + "tree";
	public static String fpPathRoot = pathRoot + "fp";
	public static int minsup = 3;

	public static boolean scan(int depth) throws Exception {
		Job job = new Job(new Configuration(),
				"apriori scan depth=" + depth);
		Configuration conf = job.getConfiguration();
		conf.setInt("depth", depth);
		FileSystem fs = FileSystem.get(new Configuration());
		DistributedCache.addCacheFile(
				new URI("hdfs://index10:9000/aers/0/dat"), conf);

		job.setMapperClass(TreeScanMapper.class);
		job.setNumReduceTasks(0);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setInputFormatClass(NTreeInputFormat.class);

		FileStatus[] fss = fs.listStatus(new Path(treePathRoot + depth));
		int cnt = 0;
		for (FileStatus status : fss) {
			System.out.println(status.getPath()+" "+status.getLen());
			if (status.getLen() == 0) {
				fs.delete(status.getPath(), true);
			} else if (!status.isDir())
				cnt++;
		}
		if (cnt == 0) return false;
		System.out.println(cnt);
		System.out.println("scan depth = " + depth);
		FileInputFormat.addInputPath(job, new Path(treePathRoot + (depth)));
		FileSystem.get(conf).delete(new Path(treePathRoot + (depth + 1)), true);
		FileOutputFormat.setOutputPath(job,	new Path(treePathRoot + (depth + 1)));
		job.waitForCompletion(true);
		return true;
	}

	public static void init() throws Exception {
		Job job = new Job(new Configuration(), "apriori init");
		Configuration conf = job.getConfiguration();
		job.setMapperClass(PatternCountMapper.class);
		job.setReducerClass(PatternCountReducer.class);
		job.setNumReduceTasks(30);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setPartitionerClass(FirstItemPartitioner.class);
		
		FileInputFormat.addInputPath(job, new Path("/aers/aers"));
		FileSystem.get(conf).delete(new Path(fpPathRoot + "2"), true);
		FileOutputFormat.setOutputPath(job, new Path(fpPathRoot + "2"));
		job.waitForCompletion(true);
	}

	public static boolean makeTree(int depth) throws Exception {
		System.out.println("make tree depth = "+depth);
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Job job = new Job(conf, "apriori make tree depth="+depth);
		job.getConfiguration().setInt("olddepth", depth-1);

		job.setMapperClass(MakeTreeMapper.class);
		job.setReducerClass(MakeTreeReducer.class);
		job.setNumReduceTasks(30);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(fpPathRoot+(depth-1)));
		FileSystem.get(conf).delete(new Path(treePathRoot+depth), true);
		FileOutputFormat.setOutputPath(job, new Path(treePathRoot+depth));
		job.waitForCompletion(true);
		return true;
	}
	public static void main(String[] args) throws Exception {
		//init();
		//makeTree(3);
		int depth = 3;
		while (scan(depth))
			depth++;
		System.out.println("done");
	}
}
