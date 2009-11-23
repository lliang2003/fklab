package test;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class MyPart extends Partitioner<Text, Text> {

	@Override
	public int getPartition(Text key, Text value, int numReduceTasks) {
//		return Integer.parseInt(key.toString()) % numReduceTasks;
		return 3;
	}
}
