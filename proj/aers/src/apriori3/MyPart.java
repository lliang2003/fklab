package apriori3;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class MyPart extends Partitioner<Text, Text> {

	@Override
	public int getPartition(Text key, Text value, int numReduceTasks){
		String first = key.toString().split(" +")[0];
		
		return first.hashCode() % numReduceTasks;
	}
}
