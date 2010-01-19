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

/**
 * Count occurence of item sets and output frequent ones, then 
 * generate candidate trees if needed(initLen < maxLen).
 * @author fankai
 */
public class CountReducer extends
		Reducer<ItemSet, IntWritable, ItemSet, IntWritable> {
	public static Log log = LogFactory.getLog(CountReducer.class);
	
	private int initLen;
	private int maxLen;
	private int minSupport;
	private TierTree tree;

	protected void setup(Context context) throws IOException,
			InterruptedException {
		initLen = DA.getInitItemSetLength(context.getConfiguration());
		maxLen = DA.getMaxItemSetLength(context.getConfiguration());
		minSupport = DA.getMinSupport(context.getConfiguration());
		if (maxLen > initLen)
			tree = new TierTree(initLen);
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

		if (key._items.length < initLen)
			return;
		if (maxLen > initLen) {
			tree.addPath(key, count);
			log.info("add path " + key);
		}
	}

	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		if (maxLen <= initLen)
			return;
		FileSystem fs = FileSystem.get(context.getConfiguration());
		String treeDir = DA.getTreePath(context.getConfiguration(),
				initLen + 1);
		String tmpTreeFile = treeDir + "/" + context.getTaskAttemptID();
		String finalTreeFile = treeDir + "/" + tmpTreeFile.split("_")[4];
		PrintWriter treeWriter = new PrintWriter(new OutputStreamWriter(fs
				.create(new Path(tmpTreeFile))));
		log.info("gen candidate trees ...");
		tree.grow(treeWriter, context);
		treeWriter.close();
		log.info("rename " + tmpTreeFile + " to " + finalTreeFile);
		fs.rename(new Path(tmpTreeFile), new Path(finalTreeFile));
	}
}