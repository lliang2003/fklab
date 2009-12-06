package auto;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class MakeTreeReducer extends Reducer<Text, Text, Text, Text> {
	NTree tree = new NTree();

	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		Iterator<Text> it = values.iterator();
		while (it.hasNext()) {
			Text value = it.next();
			String[] tokens = value.toString().split("[ \t]+");
			tree.root.add(tokens, 0, tokens.length);
			// context.write(value, null);
		}
	}

	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		tree.treeDepth = context.getConfiguration().getInt("olddepth", 99);
		List<NTree> trees = tree.grow(null);
		for (NTree t : trees) {
			if (t.root.children.size() > 0)
				context.write(new Text(t.toString()), null);
		}
	}
}