package apriori3;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import apriori.Tree;

public class ScanMapper extends Mapper<Object, Text, IntWritable, Text> {
	private static final Log logger = LogFactory.getLog(ScanMapper.class);
	Tree tree;
	int part;

	protected void setup(Context context) throws IOException,
			InterruptedException {
		Date start = new Date();
		FileSplit split = (FileSplit) context.getInputSplit();
		part = Integer.parseInt(split.getPath().toString().split("/+")[3]);
		String path = DistributedCache.getLocalCacheFiles(context.getConfiguration())[part].toString();
		logger.info("part="+part);
		logger.info(path);
		Reader reader = new FileReader(path);
		logger.info("begin construct tree:"+new Date());
		tree = new Tree(reader);
		reader.close();
		logger.info("cost:" + (new Date().getTime() - start.getTime()));
	}

	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		String[] items = value.toString().split("[ \t]+");
		Arrays.sort(items);
		tree.scan(items);
	}

	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		StringWriter sw = new StringWriter();
		tree.write(sw);
		sw.close();
		context.write(new IntWritable(part), new Text(sw.toString()));
	}
}
