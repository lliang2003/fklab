package fulldistribution;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MakeTreeMapper extends Mapper<Object, Text, Text, Text> {

	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		String firstitem = value.toString().split("[ \t]+")[0];
		context.write(new Text(firstitem), value);
	}
}