package da;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Simply map out a item set.
 *
 * @author fankai
 */
public class AggMapper extends Mapper<Object, Text, ItemSet, Text> {
	protected void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		String[] tokens = value.toString().split("\t");
		context.write(new ItemSet(tokens[0]), new Text(tokens[1]));
	}
}
