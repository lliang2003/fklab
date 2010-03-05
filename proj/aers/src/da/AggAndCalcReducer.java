package da;

import java.io.IOException;
import java.util.Formatter;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/** Aggregate reaction pattern count and calculate PRR value. */
public class AggAndCalcReducer extends Reducer<ItemSet, Text, ItemSet, Text> {
	public static Log log = LogFactory.getLog(AggAndCalcReducer.class);
	private int total;
	HashMap<String, String> reactionCount = new HashMap<String, String>();

	protected void setup(Context context) throws IOException,
			InterruptedException {
		total = PRR.getTotalCount(context.getConfiguration());
	}

	public void reduce(ItemSet key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		String val = values.iterator().next().toString();
		if (!key.hasDrug()) {
			// This is a reaction pattern, save its count in hashmap.
//			log.info("add " + reactionCount.size() + "th reaction pattern:"
//					+ key);
			reactionCount.put(key.toString(), val);
		} else {
			ItemSet rset = key.reactionSet();
			int rcnt = Integer.parseInt(reactionCount.get(rset.toString()));
			int pcnt = Integer.parseInt(val.split(" ")[0]);
			int dcnt = Integer.parseInt(val.split(" ")[1]);
			double a = pcnt, b = rcnt - pcnt, c = dcnt - pcnt, d = total - a
					- b - c;
			double prr = a * (b + d) / b / (a + c);
			double chi = total * Math.pow((Math.abs(a * d - b * c) - total / 2), 2)
					/ (a + b) / (a + c) / (b + d) / (c + d);
			if (prr < 2.0)
				return;
			if (chi < 4.0)
				return;
			log.info(key+"pcnt="+pcnt+"rcnt="+rcnt+" dcnt="+dcnt);
			log.info("a="+a+" b="+b+ " c="+c+" d="+d);
			log.info("prr="+prr+ "\tchi="+chi);
			context.write(key, new Text(new Formatter().format(
					"%d\t%d\t%d\t%.2f\t%.2f", pcnt, dcnt, rcnt, prr, chi).toString()));
		}
	}
}
