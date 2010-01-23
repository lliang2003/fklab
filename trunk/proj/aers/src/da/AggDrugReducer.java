package da;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/** Aggregate drug pattern count. */
public class AggDrugReducer extends Reducer<ItemSet, Text, ItemSet, Text> {
	public static Log log = LogFactory.getLog(AggAndCalcReducer.class);
	HashMap<String, Integer> drugCount = new HashMap<String, Integer>();

	public void reduce(ItemSet key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		String val = values.iterator().next().toString();
		int count = Integer.parseInt(val);
		if (!key.hasReaction()) {
			// This is a drug pattern, save its count in hashmap.
			log.info("add " + drugCount.size() + "th drug pattern:" + key);
			drugCount.put(key.toString(), count);
		} else if (!key.hasDrug()) {
			// This is a reaction pattern, simply write its count.
			context.write(key, new Text("" + count));
		} else {
			ItemSet dset = key.drugSet();
			int dcnt = drugCount.get(dset.toString());
			context.write(key, new Text(count + " " + dcnt));
		}
	}
}
