package apriori4;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class PatternCountMapper extends Mapper<Object, Text, Text, IntWritable> {
	private final static IntWritable one = new IntWritable(1);
	private Text pattern = new Text();
	private static final Log logger = LogFactory.getLog(TreeScanMapper.class);
	private int maxLength;

	protected void setup(Context context) throws IOException,
			InterruptedException {
		maxLength = context.getConfiguration().getInt("apriori.maxLength", 8);
	}

	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		String[] tokens = value.toString().split("[ \t]+");
		//if (tokens.length > maxLength)			return; // filter out long transactions
		Arrays.sort(tokens);
		for (String item : tokens) {
			pattern.set(item);
			context.write(pattern, one);
		}
		for (int i = 0; i < tokens.length; ++i) {
			for (int j = i + 1; j < tokens.length; ++j) {
				pattern.set(tokens[i] + " " + tokens[j]);
				context.write(pattern, one);
			}
		}
	}
}
