package da;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class CountReducer extends
		Reducer<ItemSet, IntWritable, ItemSet, IntWritable> {
	public static Log log = LogFactory.getLog(CountReducer.class);

	private int initItemSetLength;
	private int maxItemSetLength;
	private int minSupport;
	private TierTree tree;

	protected void setup(Context context) throws IOException,
			InterruptedException {
		initItemSetLength = context.getConfiguration().getInt("init", 2);
		maxItemSetLength = context.getConfiguration().getInt("max", 99);
		minSupport = context.getConfiguration().getInt("minsup",
				DA.defaultMinSupport);
		if (maxItemSetLength > initItemSetLength)
			tree = new TierTree(initItemSetLength);
	}

	public void reduce(ItemSet key, Iterable<IntWritable> values,
			Context context) throws IOException, InterruptedException {
		int count = 0;
		for (IntWritable cnt : values) {
			count += cnt.get();
		}
		if (count < minSupport)
			return;
		context.write(key, new IntWritable(count));

		if (key._items.length < initItemSetLength)
			return;
		if (maxItemSetLength > initItemSetLength)
			tree.addPath(key, count);
		// log.info("add path " + key);
	}

	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		if (maxItemSetLength <= initItemSetLength)
			return;
		FileSystem fs = FileSystem.get(context.getConfiguration());
		String tmpTreeFile = DA.treePathRoot + (tree.treeDepth + 1) + "/"
				+ context.getTaskAttemptID();
		String finalTreeFile = DA.treePathRoot + (tree.treeDepth + 1) + "/"
				+ tmpTreeFile.split("_")[4];
		PrintWriter treeWriter = new PrintWriter(new OutputStreamWriter(fs
				.create(new Path(tmpTreeFile))));
		log.info("begin growing ...");
		tree.grow(treeWriter, context);
		treeWriter.close();
		log.info("rename " + tmpTreeFile + " to " + finalTreeFile);
		fs.rename(new Path(tmpTreeFile), new Path(finalTreeFile));
	}
}