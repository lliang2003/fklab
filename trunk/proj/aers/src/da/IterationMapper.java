package da;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

/**
 * Apriori iteration. 1. build partial candidate tree in memory, each map
 * represent a part of the tree 2. scan data file, which is managed by
 * DistributedCache 3. check candidates and output frequent patterns using
 * context 4. output new candidate trees directly to HDFS
 * 
 * @author fankai
 * 
 */
public class IterationMapper extends Mapper<Object, Text, ItemSet, IntWritable> {
	public static Log log = LogFactory.getLog(IterationMapper.class);
	TierTree tree;
	int filterSize;
	int depth;

	/**
	 * Output basic info of this map task.
	 * 
	 * @param context
	 * @throws IOException
	 */
	public void info(Context context) throws IOException {
		log.info("working dir:" + context.getWorkingDirectory());
		FileSplit split = (FileSplit) context.getInputSplit();
		log.info("file:" + split.getPath());
		log.info("length:" + split.getLength());
		log.info("start:" + split.getStart());
		for (URI uri : DistributedCache.getCacheFiles(context
				.getConfiguration())) {
			log.info("cached:" + uri);
		}
	}

	protected void setup(Context context) throws IOException,
			InterruptedException {
		info(context);
		depth = context.getConfiguration().getInt("depth", -1);
		tree = new TierTree(depth);
		filterSize = DA.getFilterSize(context.getConfiguration());
	}

	protected void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		// log.info("expand: " + value);
		tree.expand(new StringReader(value.toString()));
	}

	void scan(Reader reader, Context context) {
		Scanner sc = new Scanner(reader);
		int n = 0;
		while (sc.hasNext()) {
			String[] items = sc.nextLine().split(" +");
			if (items.length > filterSize)
				continue;
			// log.info(n + "\tscan: " + Arrays.toString(items));
			tree.scan(Util.intArray(items));
			n++;
		}
		context.progress();
	}

	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		Configuration conf = context.getConfiguration();
		int minsup = DA.getMinSupport(conf);
		FileSystem fs = FileSystem.get(conf);

		for (Path path : DistributedCache.getLocalCacheFiles(context
				.getConfiguration())) {
			log.info("scan " + path);
			scan(new FileReader(path.toString()), context);
		}
		log.info("check and output frequent patterns");
		tree.check(minsup, context);
		context.progress();
		if (depth >= DA.getMaxItemSetLength(conf))
			return;
		String treeDir = DA.getTreePath(conf, depth + 1);
		String tmpTreeFile = treeDir + "/" + context.getTaskAttemptID();
		String finalTreeFile = treeDir + "/tree" + tree.getId() + "-"
				+ tmpTreeFile.split("_")[4];
		PrintWriter treeWriter = new PrintWriter(new OutputStreamWriter(fs
				.create(new Path(tmpTreeFile))));
		log.info("begin growing ...");
		tree.grow(treeWriter, context);
		// tree.printTree(treeWriter);
		treeWriter.close();
		fs.rename(new Path(tmpTreeFile), new Path(finalTreeFile));
	}
}
