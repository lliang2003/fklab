package da;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

/**
 * Generate item sets of limited length from original data.
 * 
 * @author fankai
 */
public class CountMapper extends Mapper<Object, Text, ItemSet, IntWritable> {
	public static Log log = LogFactory.getLog(CountMapper.class);
	private int initItemSetLength;
	private int filterSize;

	public void info(Context context) throws IOException {
		log.info("working dir:" + context.getWorkingDirectory());
		FileSplit split = (FileSplit) context.getInputSplit();
		log.info("file:" + split.getPath());
		log.info("length:" + split.getLength());
		log.info("start:" + split.getStart());
	}

	/**
	 * Generate all item sets with limited length from a transaction.
	 * @param items	The transaction.
	 * @param max	Maximum item set length.
	 */
	public static void gen(int[] items, int[] pos, int index, int max,
			List<Integer> tmp, Context context) throws IOException,
			InterruptedException {
		if (index > 0)
			context.write(new ItemSet(tmp), new IntWritable(1));
		if (index == max || index == items.length) {
			return;
		}
		pos[index] = (index == 0 ? 0 : pos[index - 1]);
		for (; pos[index] < items.length; ++pos[index]) {
			if (index == 0 || pos[index] > pos[index - 1]) {
				tmp.add(items[pos[index]]);
				gen(items, pos, index + 1, max, tmp, context);
				tmp.remove(tmp.size() - 1);
			}
		}
	}

	protected void setup(Context context) throws IOException,
			InterruptedException {
		info(context);
		initItemSetLength = DA.getInitItemSetLength(context.getConfiguration());
		filterSize = DA.getFilterSize(context.getConfiguration());
	}

	/**
	 * Generate item sets from transactions no longer than FilterSize.
	 * Each transaction consists of 1 or more items separated by spaces.
	 * Items are positive integers sorted in numerical order. 
	 */
	protected void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		String[] tokens = value.toString().split(" +");
		if (tokens.length > filterSize) {
			log.info("filter: "+value);
			return;
		}
		int[] items = Util.intArray(tokens);
//		log.info("gen item sets from: "+Arrays.toString(items));
		gen(items, new int[items.length], 0, initItemSetLength,
				new ArrayList<Integer>(), context);
	}
}
