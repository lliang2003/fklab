package fulldistribution;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PatternCountReducer extends Reducer<Text, IntWritable, Text, Text> {

	public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		Iterator<IntWritable> it = values.iterator();
		int count = 0;
		while (it.hasNext()) {
			count += 1;
			it.next();
		}
		if (count < FullDistribution.minsup)
			return;
		context.write(key, new Text("" + count));
	}

}
