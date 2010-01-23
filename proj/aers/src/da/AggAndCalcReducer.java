package da;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


/** Aggregate reaction pattern count and calculate PRR value. */
public class AggAndCalcReducer extends
		Reducer<ItemSet, Text, ItemSet, Text> {
	public static Log log = LogFactory.getLog(AggAndCalcReducer.class);
	private int total;
	HashMap<String, String> reactionCount = new HashMap<String, String>();

	protected void setup(Context context) throws IOException,
			InterruptedException {
		total = context.getConfiguration().getInt("total", 1);
	}

	public void reduce(ItemSet key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		String val = values.iterator().next().toString();
		if (!key.hasDrug()) {
			// This is a reaction pattern, save its count in hashmap.
			log.info("add " + reactionCount.size() + "th reaction pattern:" + key);
			reactionCount.put(key.toString(), val);
		} else {
			ItemSet rset = key.reactionSet();
			int rcnt = Integer.parseInt(reactionCount.get(rset.toString()));
			int pcnt = Integer.parseInt(val.split(" ")[0]);
			int dcnt = Integer.parseInt(val.split(" ")[1]);
			double prr = (double)total*pcnt/dcnt/rcnt;
			context.write(key, new Text(val+"\t"+rcnt+"\t"+prr));
		}
	}
}
