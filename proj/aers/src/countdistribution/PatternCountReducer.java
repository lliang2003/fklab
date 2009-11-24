package countdistribution;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Iterator;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import apriori.Tree;

public class PatternCountReducer extends Reducer<Text, IntWritable, Text, Text> {
	Tree tree = new Tree();
	OutputStreamWriter patternWriter;

	protected void setup(Context context) throws IOException,
			InterruptedException {
		FileSystem fs = FileSystem.get(context.getConfiguration());
		OutputStream os = fs.create(new Path("/apriori/pf"));
		patternWriter = new OutputStreamWriter(os);
	}

	public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		Iterator<IntWritable> it = values.iterator();
		int count = 0;
		while (it.hasNext()) {
			count += 1;
			it.next();
		}

		if (count < CountDistribution.minsup)
			return;
		// context.write(key, new Text("" + count));

		String[] tokens = key.toString().split(" +");
		patternWriter.write(key+" "+count+"\n");
		if (tokens.length == 1) {
			tree.root.addChild(tokens[0]);
			// tree.root.getChild(tokens[0]).count = count;
		} else if (tokens.length == 2) {
			tree.root.getChild(tokens[0]).addChild(tokens[1]);
			tree.root.getChild(tokens[0]).getChild(tokens[1]).count = count;
			tree.activeNodeCount += 1;
		}
	}

	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		patternWriter.close();
		tree.treeDepth = 2;
		tree.grow();
		StringWriter sw = new StringWriter();
		tree.write(sw);
		context.write(new Text(sw.toString()), null);
	}

}
