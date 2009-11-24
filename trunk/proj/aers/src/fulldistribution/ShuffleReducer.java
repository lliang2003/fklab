package fulldistribution;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import apriori.Tree;

public class ShuffleReducer extends Reducer<Text, Text, Text, Text> {
	Tree tree = new Tree();

	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		tree.root.addChild(key.toString());
		Tree.TreeNode node = tree.root.getChild(key.toString());

		Iterator<Text> iter = values.iterator();
		while (iter.hasNext()) {
			Text value = iter.next();
			//context.write(value, null);
			String[] tokens = value.toString().split("[ \t]+");
			if (tokens.length != 3)
				throw new IOException();
			node.addChild(tokens[1]);
			node.getChild(tokens[1]).count = Integer.parseInt(tokens[2]);
			tree.activeNodeCount += 1;
		}
	}

	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		tree.treeDepth = 2;
		tree.grow();
		StringWriter sw = new StringWriter();
		tree.write(sw);
		context.write(new Text(sw.toString()), null);
	}
}
