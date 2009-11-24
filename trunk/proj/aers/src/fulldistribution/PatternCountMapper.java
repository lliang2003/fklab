package fulldistribution;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PatternCountMapper extends Mapper<Object, Text, Text, IntWritable> {
	private final static IntWritable one = new IntWritable(1);
	private Text pattern = new Text();

	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		String[] tokens = value.toString().split("[ \t]+");
		// if (tokens.length > 16) return; // filter out long transactions
		Arrays.sort(tokens);
		for (String item : tokens) {
			pattern.set(item);
			context.write(pattern, one);
			context.write(new Text("word"), one);
		}
		for (int i = 0; i < tokens.length; ++i) {
			for (int j = i + 1; j < tokens.length; ++j) {
				pattern.set(tokens[i] + " " + tokens[j]);
				context.write(pattern, one);
			}
		}
	}
}
