package auto;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MakeTreeMapper extends Mapper<Object, Text, Text, Text> {

	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		String[] tokens = value.toString().split("[ \t]+");
		if (tokens.length < 3)
			return;
		context.write(new Text(tokens[0]), value);
	}
}