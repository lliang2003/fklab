package apriori;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class AprioriReducer extends Reducer<Text, Text, Text, Text> {
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		Iterator<Text> it = values.iterator(); 
		int count = 0;
		while (it.hasNext()) {
			count += 1;
			it.next();
		}
		context.write(key, new Text(""+count));
	}
}
