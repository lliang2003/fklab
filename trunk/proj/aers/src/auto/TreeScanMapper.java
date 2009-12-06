package auto;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class TreeScanMapper extends Mapper<Object, Text, Text, Text> {
	private static final Log logger = LogFactory.getLog(TreeScanMapper.class);
	NTree tree;
	int part;

	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		if (value.toString().length() == 0)
			return;
		logger.info(value.toString().length());
		if (tree == null)
			tree = new NTree(new StringReader(value.toString()));
		else
			tree.merge(new NTree(new StringReader(value.toString())));
		logger.info("done");
	}

	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		if (tree == null)return;
		Path db = DistributedCache.getLocalCacheFiles(context
				.getConfiguration())[0];
		Scanner sc = new Scanner(new FileReader(db.toString()));
		while (sc.hasNext()) {
			String[] items = sc.nextLine().split(" +");
			if (items.length > 16)
				continue;
			tree.scan(items);
		}
		logger.info("scan done");
		FileSystem fs = FileSystem.get(context.getConfiguration());
		Writer wr = new OutputStreamWriter(fs.create(new Path("/apriori3/fp"
				+ context.getConfiguration().getInt("depth", 99) + "/"
				+ tree.hashCode() + (new Date()).getTime())));
		tree.checkFrequent(AutoSplit.minsup, wr);
		wr.close();
		logger.info("check done");
		List<NTree> trees = tree.grow(context);
		for (NTree t:trees)
			if (t.root.children.size() > 0)
				context.write(new Text(t.toString()), new Text(""));
		logger.info("grow done");
	}
}
