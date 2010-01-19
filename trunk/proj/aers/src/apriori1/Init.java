package apriori1;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.UUID;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Partitioner;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import test.PRR;

public class Init {

	public static class FirstItemPartitioner implements
			Partitioner<Text, IntWritable> {
		// 只比较第一个项目
		public int getPartition(Text key, IntWritable value, int numReduceTasks) {
			String firstItem = key.toString().split(" +")[0];
			return (firstItem.hashCode() & Integer.MAX_VALUE) % numReduceTasks;
		}

		public void configure(JobConf job) {
		}
	}

	// 生成所有长度小于max的子项集
	public static void gen(String[] items, int[] pos, int index, int max,
			StringBuilder sb, OutputCollector<Text, IntWritable> output)
			throws IOException {
		if (index > 0)
			output.collect(new Text(sb.toString()), new IntWritable(1));
		if (index == max || index == items.length) {
			return;
		}
		if (index == 0)
			pos[index] = 0;
		else
			pos[index] = pos[index - 1];
		for (; pos[index] < items.length; ++pos[index]) {
			int len = sb.length();
			if (index == 0 || pos[index] > pos[index - 1]) {
				sb.append(items[pos[index]] + " ");
				gen(items, pos, index + 1, max, sb, output);
				sb.setLength(len);
			}
		}
	}

	public static class InitMapper extends MapReduceBase implements
			Mapper<Object, Text, Text, IntWritable> {
		public int maxLength;

		public void configure(JobConf job) {
			maxLength = job.getInt("max length", -1);
		}

		public void map(Object key, Text value,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			// 生成子项集
			String[] items = value.toString().split(" +");
			gen(items, new int[items.length], 0, maxLength, new StringBuilder(), output);
		}

	}

	public static class InitReducer extends MapReduceBase implements
			Reducer<Text, IntWritable, Text, IntWritable> {
		public MTree tree = new MTree();
		public int maxLength;
		public FileSystem fs;
		public JobConf conf;
		public int reduceIndex = -1;
		public int reduceNum;

		public void configure(JobConf job) {
			maxLength = job.getInt("max length", 2);
			conf = job;
			reduceNum = job.getInt("mapred.reduce.tasks", 0);
		}

		public void reduce(Text key, Iterator<IntWritable> values,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			// 第一次获取该reducer的index
			if (reduceIndex < 0) {
				String firstItem = key.toString().split(" +")[0];
				reduceIndex = (firstItem.hashCode() & Integer.MAX_VALUE)%reduceNum;
			}
			int count = 0;
			while (values.hasNext())
				count += values.next().get();
			if (count < DataDistribution.minsup)
				return;
			// 输出频繁项集
			output.collect(key, new IntWritable(count));
			// 初始化count tree
			String[] items = key.toString().split(" +");
			if (items.length != maxLength)
				return;
			tree.root.addPath(items, 0, items.length);
		}

		public void close() throws IOException {
			if (tree.root.children == null)
				return;
			// 向HDFS写入candidate tree
			FileSystem fs = FileSystem.get(conf);
			String tmpTreeFile = DataDistribution.treePathRoot + (maxLength + 1) + "/"
					+ reduceIndex+"-"+UUID.randomUUID();
			String finalTreeFile = DataDistribution.treePathRoot + (maxLength + 1) + "/"
					+ "/tree-" + reduceIndex;
			Writer treeWriter = new OutputStreamWriter(fs.create(new Path(
					tmpTreeFile)));
			tree.growWrite(null, treeWriter);
			if (tree.root.count > 0)
				tree.writeg(treeWriter);
			treeWriter.flush();
			treeWriter.close();
			fs.rename(new Path(tmpTreeFile), new Path(finalTreeFile));
		}
	}

	public static void init(int maxLength) throws IOException {
		JobConf conf = new JobConf(PRR.class);
		conf.setJobName("data distribution init");
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);

		conf.setMapperClass(InitMapper.class);
		conf.setReducerClass(InitReducer.class);
		conf.setPartitionerClass(FirstItemPartitioner.class);
		conf.setNumReduceTasks(40);

		conf.setInt("max length", maxLength);
		FileInputFormat.setInputPaths(conf, DataDistribution.datPath);
		FileSystem.get(conf).delete(new Path(DataDistribution.pathRoot), true);
		FileOutputFormat.setOutputPath(conf, new Path(DataDistribution.fpPathRoot+"2"));
		JobClient.runJob(conf);
	}

	public static void main(String[] args) throws Exception {
		init(2);
	}

}
