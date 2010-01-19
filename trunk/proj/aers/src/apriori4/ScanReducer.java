package apriori4;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class ScanReducer extends Reducer<Text, Text, Text, IntWritable> {
	private static final Log logger = LogFactory.getLog(Distribution.class);
	private Map<String, Integer> patternCount = new HashMap<String, Integer>();
	private Writer treeWriter;

	protected void setup(Context context) throws IOException,
			InterruptedException {
		FileSystem fs = FileSystem.get(context.getConfiguration());
		String treeFile = Distribution.treePathRoot + "3/"
				+ context.getTaskAttemptID();
		treeWriter = new OutputStreamWriter(fs.create(new Path(treeFile)));
	}

	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		Iterator<Text> it = values.iterator();
		while (it.hasNext()) {
			String pattern = it.next().toString();
			Integer cnt = patternCount.get(pattern);
			if (cnt == null)
				cnt = 1;
			else
				cnt += 1;
			patternCount.put(pattern, cnt);
		}
		logger.info("done process key:" + key);
	}

	protected void cleanup(Context context) throws IOException,
			InterruptedException {
//		maxLength = context.getConfiguration().getInt("apriori.maxLength", 8);
//		FileSystem fs = FileSystem.get(context.getConfiguration());
//		String treeFile = Distribution.treePathRoot + tree.depth() + "/"
//				+ context.getTaskAttemptID();
//		Writer treeWriter = new OutputStreamWriter(fs
//				.create(new Path(treeFile)));
//
//		for (Path path : DistributedCache.getLocalCacheFiles(context
//				.getConfiguration())) {
//			Scanner sc = new Scanner(new FileReader(path.toString()));
//			while (sc.hasNext()) {
//				String[] items = sc.nextLine().split(" +");
//				if (items.length > maxLength)
//					continue;
//				tree.scan(items);
//			}
//			logger.info("scaned " + path);
//			context.progress();
//		}

//		tree.root.checkAndGrow(Distribution.minsup, treeWriter, context);
	}
}
