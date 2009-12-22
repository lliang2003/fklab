package trec;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class LineSort {

	public static class MyMapper extends
			Mapper<Object, Text, Text, Text> {

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			Text a = new Text();
			a.append(value.getBytes(), 0, value.getLength());
			context.write(value, new Text());
		}
	}

	public static class MyReducer extends
			Reducer<Text, Text, Text, Text> {

//		public void reduce(Text key, Iterable<Text> values,
//				Context context) throws IOException, InterruptedException {
//		}
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("user.name", "joke");
		Configuration conf = new Configuration();
		Job job = new Job(conf, "line sort");
		job.setMapperClass(MyMapper.class);
//		job.setCombinerClass(MyReducer.class);
		job.setReducerClass(MyReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
//		job.setInputFormatClass(MyInputFormat.class);
		job.setNumReduceTasks(13);
//		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path("/trec/urls/part-r-00000"));
		FileOutputFormat.setOutputPath(job, new Path("/trec/sort"));
		FileSystem.get(conf).delete(new Path("/trec/sort"), true);
		job.waitForCompletion(true) ;
		for (FileStatus f: FileSystem.get(conf).listStatus(new Path("/trec/sort")))
			FileSystem.get(conf).setReplication(f.getPath(), (short)3);
	}
}
