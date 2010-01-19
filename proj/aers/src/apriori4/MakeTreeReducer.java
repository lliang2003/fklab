package apriori4;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class MakeTreeReducer extends Reducer<Object, Text, Text, Text> {
	MTree tree = new MTree();
	private static final Log logger = LogFactory.getLog(TreeScanMapper.class);

	public void reduce(Object key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		Iterator<Text> it = values.iterator();
		while (it.hasNext()) {
			Text value = it.next();
			String[] tokens = value.toString().split("[ \t]+");
			tree.root.addPath(tokens, 0, tokens.length - 1);
			// context.write(value, null);
		}
	}

	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		if (tree.root.children == null)
			return;
		FileSystem fs = FileSystem.get(context.getConfiguration());
		String tmpTreeFile = AutoSplit.treePathRoot + (tree.depth() + 1) + "/"
				+ context.getTaskAttemptID();
		String finalTreeFile = AutoSplit.treePathRoot + (tree.depth() + 1)
				+ "/tree-" + tmpTreeFile.split("_")[4];
		Writer treeWriter = new OutputStreamWriter(fs.create(new Path(
				tmpTreeFile)));
		tree.growWrite(null, treeWriter);
		if (tree.root.count > 0)
			tree.writeg(treeWriter);
		treeWriter.flush();
		treeWriter.close();
		logger.info("write done");
		fs.rename(new Path(tmpTreeFile), new Path(finalTreeFile));
	}
}