package tier;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class Init {

	public static class FirstItemPartitioner extends
			Partitioner<Text, IntWritable> {
		public int getPartition(Text key, IntWritable value, int numPartitions) {
			String firstItem = key.toString().split(" +")[0];
			return (firstItem.hashCode() & Integer.MAX_VALUE) % numPartitions;
		}
	}
	
	
}
