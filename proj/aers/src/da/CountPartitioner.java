package da;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Partitioner;

/**
 * Partition item sets with a total order. Only the first item is considered.
 * 
 * @author fankai
 */
public class CountPartitioner extends Partitioner<ItemSet, Object> implements
		Configurable {
	public static Log log = LogFactory.getLog(CountPartitioner.class);
	private int[] points = null;

	public int getPartition(ItemSet key, Object value, int numPartitions) {
		if (points == null) return 0;
		int pos = Arrays.binarySearch(points, 0, points.length, key._items[0]);
		if (pos < 0)
			pos = -pos - 1;
		return pos;
	}

	@Override
	public Configuration getConf() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConf(Configuration conf) {
		if (conf.get("count.points") != null)
			points = new ItemSet(conf.get("count.points"))._items;
	}

}
