package auto;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class CountReducer extends Reducer<Text, Text, Text, IntWritable> {
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
	
	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		PTree tree = new PTree();
		logger.info("scan patterns");
		for (Entry<String, Integer> entry : patternCount.entrySet()) {
			String pattern = entry.getKey();
			int count = entry.getValue();
			if (count < 3)
				continue;
			context.write(new Text(pattern), new IntWritable(
					count));
			String[] items = pattern.split(" +");
			if (items.length == 1)
				continue;
			tree.root.addChild(items[0]);
			tree.root.getChild(items[0]).addChild(items[1]);
			tree.root.getChild(items[0]).getChild(items[1]).count = count;
		}
		logger.info("begin check and grow");
		tree.root.checkAndGrow(Distribution.minsup, treeWriter, null);
	}

}
