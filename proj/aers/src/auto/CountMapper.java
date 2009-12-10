package auto;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class CountMapper extends Mapper<Object, Text, Text, Text> {
	private static final Log logger = LogFactory.getLog(Distribution.class);
	private Text patternKey = new Text();
	private Text patternValue = new Text();
	private int maxLength;

	protected void setup(Context context) throws IOException,
			InterruptedException {
		maxLength = context.getConfiguration().getInt("apriori.maxLength", 8);
		logger.info("max length="+maxLength);
	}

	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		String[] tokens = value.toString().split("[ \t]+");
		if (tokens.length > maxLength)
			return; // filter out long transactions
		Arrays.sort(tokens);
		for (int i = 0; i < tokens.length; ++i) {
			patternKey.set(tokens[i]);
			context.write(patternKey, patternKey);
			for (int j = i + 1; j < tokens.length; ++j) {
				patternValue.set(tokens[i]+" "+tokens[j]);
				context.write(patternKey, patternValue);
			}
		}
	}
}
