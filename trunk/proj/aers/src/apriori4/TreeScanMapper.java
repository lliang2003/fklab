package apriori4;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.Date;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class TreeScanMapper extends Mapper<Object, Text, Text, IntWritable> {
	private static final Log logger = LogFactory.getLog(TreeScanMapper.class);
	MTree tree;
	int part;
	private int maxLength;

	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		if (value.toString().length() == 0)
			return;
		logger.info(value.toString());
		if (tree == null)
			tree = new MTree(new StringReader(value.toString()));
		else
			tree.merge(new MTree(new StringReader(value.toString())));
		logger.info("done");
		context.progress();
	}

	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		maxLength = context.getConfiguration().getInt("apriori.maxLength", 8);
		logger.info("max length="+maxLength);
		if (tree == null)
			return;
		if (tree.root.children == null) return;
		for (Path path : DistributedCache.getLocalCacheFiles(context
				.getConfiguration())) {
			Scanner sc = new Scanner(new FileReader(path.toString()));
			while (sc.hasNext()) {
				String[] items = sc.nextLine().split(" +");
				if (items.length > maxLength)
					continue;
				tree.scan(items);
			}
			logger.info("scaned " + path);
			context.progress();
		} 
		logger.info("tree depth="+tree.depth());
		tree.root.checkFrequency(new String[tree.depth()], AutoSplit.minsup,
				context);
		logger.info("check done");
		if (tree.root.children.size() == 0) return;
		FileSystem fs = FileSystem.get(context.getConfiguration());
		String tmpTreeFile = AutoSplit.treePathRoot + (tree.depth() + 1) + "/"
				+ context.getTaskAttemptID();
		String finalTreeFile = AutoSplit.treePathRoot + (tree.depth() + 1)
		+ "/tree-" + tmpTreeFile.split("_")[4];
		logger.info("tree file="+tmpTreeFile);
		Writer treeWriter = new OutputStreamWriter(fs
				.create(new Path(tmpTreeFile)));
		tree.growWrite(null, treeWriter);
		if (tree.root.count>0)
			tree.writeg(treeWriter);
		treeWriter.close();
		fs.rename(new Path(tmpTreeFile), new Path(finalTreeFile));
		logger.info("grow done");
	}
}
