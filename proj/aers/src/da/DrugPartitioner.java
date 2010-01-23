package da;

import org.apache.hadoop.mapreduce.Partitioner;

/**
 * Partition a pattern according to the drugs. 
 *
 * @author fankai
 */
public class DrugPartitioner extends Partitioner<ItemSet, Object> {
	@Override
	public int getPartition(ItemSet key, Object value, int numPartitions) {
		int h = key.drugHashCode();
		if (h == 0)
			h = key.hashCode();
		return (h & Integer.MAX_VALUE) % numPartitions;
	}
}