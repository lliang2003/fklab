package auto;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ScanMapper extends Mapper<Object, Text, Text, Text> {
	private static final Log logger = LogFactory.getLog(ScanMapper.class);
	PTree tree ;
	private int maxLength;

	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		if (tree == null) tree = new PTree();
		tree.addPath(value.toString());
	}

	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		maxLength = context.getConfiguration().getInt("apriori.maxLength", 8);
		FileSystem fs = FileSystem.get(context.getConfiguration());
		String treeFile = Distribution.treePathRoot + tree.depth()+"/"
				+ context.getTaskAttemptID();
		Writer treeWriter = new OutputStreamWriter(fs.create(new Path(treeFile)));
		
		for (Path path:DistributedCache.getLocalCacheFiles(context
				.getConfiguration())) {
			Scanner sc = new Scanner(new FileReader(path.toString()));
			while (sc.hasNext()) {
				String[] items = sc.nextLine().split(" +");
				if (items.length > maxLength)
					continue;
				tree.scan(items);
			}
			logger.info("scaned "+path);
			context.progress();
		}
		
		
	}
}
