package fulldistribution;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ShuffleMapper extends Mapper<Object, Text, Text, Text> {
	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		String[] tokens = value.toString().split("[ \t]+");
		if (tokens.length == 2) return;
		context.write(new Text(tokens[0]), value);
	}
}
