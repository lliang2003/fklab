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
	private int[] points;

	public void fillSegment(int pstart, int len, int min, int max) {
		log.info("fill segment start=" + pstart + " len=" + len + " min=" + min
				+ " max=" + max);
		int step = (max - min) / len;
		for (int i = 0; i < len; ++i) {
			points[pstart + i] = min + step * (i + 1);
		}
		points[pstart + len - 1] = max;
	}

	/**
	 * Each interval get equal segment. Items in one interval is given the same
	 * weight.
	 */
	public void averageFillPoints(Configuration conf) {
		int reducerNum = DA.getCountReducerNum(conf);
		if (reducerNum == 1)
			return;
		points = new int[reducerNum];

		int[] mins = Util.intArray(DA.getDataStartPoints(conf).split(","));
		int[] maxs = Util.intArray(DA.getDataEndPoints(conf).split(","));

		int icnt = mins.length;
		int ilen = reducerNum / icnt;
		for (int i = 0; i < icnt; ++i) {
			int pstart = ilen * i;
			int pend = ilen * i + ilen;
			if (i == icnt - 1)
				pend = reducerNum;
			fillSegment(pstart, pend - pstart, mins[i], maxs[i]);
		}
		points[reducerNum - 1] = Integer.MAX_VALUE;
		log.info("split points:" + Arrays.toString(points));
	}

	// public void init(Configuration conf) {
	// int reducerNum = DA.getCountReducerNum(conf);
	// if (reducerNum == 1)
	// return;
	//
	// int[] starts = Util.intArray(DA.getDataStartPoints(conf).split(","));
	// int[] ends = Util.intArray(DA.getDataEndPoints(conf).split(","));
	//
	// int itemCount = 0;
	// for (int i = 0; i < starts.length; ++i)
	// itemCount += ends[i] - starts[i];
	//
	// int total = itemCount * (itemCount - 1) / 2;
	// points = new int[reducerNum];
	// int pos = 0;
	// int sum = 0;
	// int n = itemCount;
	//
	// for (int interval = 0; interval < starts.length && pos < reducerNum - 1;
	// ++interval) {
	// for (int item = starts[interval]; item < ends[interval]; ++item) {
	// sum += --n;
	// if (sum > total * (pos + 1) / reducerNum) {
	// points[pos++] = item;
	// if (pos == reducerNum - 1)
	// break;
	// }
	// }
	// }
	// points[reducerNum - 1] = Integer.MAX_VALUE;
	// log.info("split points:" + Arrays.toString(points));
	// }

	public int getPartition(ItemSet key, Object value, int numPartitions) {
		if (points == null)
			return 0;
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
		//		init(conf);
		averageFillPoints(conf);
	}
}
