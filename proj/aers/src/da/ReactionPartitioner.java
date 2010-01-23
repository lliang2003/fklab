package da;

import org.apache.hadoop.mapreduce.Partitioner;

/**
 * Partition a pattern according to the reactions.
 * 
 * @author fankai
 */
public class ReactionPartitioner extends Partitioner<ItemSet, Object> {
	public int getPartition(ItemSet key, Object value, int numPartitions) {
		int h = key.reactionHashCode();
		if (h == 0)
			h = key.hashCode();
		return (h & Integer.MAX_VALUE) % numPartitions;
	}
}