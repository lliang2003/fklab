package auto;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class PatternCountReducer extends Reducer<Text, IntWritable, Text, Text> {

	public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		Iterator<IntWritable> it = values.iterator();
		int count = 0;
		while (it.hasNext()) {
			count += 1;
			it.next();
		}
		if (count < AutoSplit.minsup)
			return;
		context.write(key, new Text(""+count));
	}

}
