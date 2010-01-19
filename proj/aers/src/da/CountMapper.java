package da;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

// Generate patterns from original data.
public class CountMapper extends Mapper<Object, Text, ItemSet, IntWritable> {
	public static Log log = LogFactory.getLog(SimpleCount.class);
	private int maxItemSetLength;
	private int filterSize;

	// Generate all item sets with limited length.
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
		maxItemSetLength = context.getConfiguration().getInt("count.max", 1);
		filterSize = context.getConfiguration().getInt("apriori.filter", DA.defaultFilterSize);
	}

	protected void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		String[] tokens = value.toString().split(" +");
		if (tokens.length > filterSize) {
			log.info("filter: "+value);
			return;
		}
		int[] items = new int[tokens.length];
		for (int i = 0; i < tokens.length; ++i)
			items[i] = Integer.parseInt(tokens[i]);
		log.info("gen item sets from: "+Arrays.toString(items));
		gen(items, new int[items.length], 0, maxItemSetLength,
				new ArrayList<Integer>(), context);
	}
}
