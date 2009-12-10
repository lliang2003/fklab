package fulldistribution;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import apriori.Tree;

public class MakeTreeReducer extends Reducer<Text, Text, Text, Text> {
	Tree tree = new Tree();
	protected void setup(Context context) throws IOException,
			InterruptedException {
		tree.treeDepth = context.getConfiguration().getInt("old tree depth", -1);
	}

	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		Iterator<Text> it = values.iterator();
		while (it.hasNext()) {
			Text value = it.next();
			String[] tokens = value.toString().split("[ \t]+");
			if (tokens.length < 2) return;
			tree.root.add(tokens, 0, tokens.length-1);
			//context.write(value, null);
		}
	}

	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		tree.grow();
		StringWriter sw = new StringWriter();
		tree.write(sw);
		context.write(new Text(sw.toString()), null);
		sw.close();
	}
}