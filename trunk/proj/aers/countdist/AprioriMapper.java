package apriori;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class AprioriMapper extends Mapper<Object, Text, Text, Text> {
	protected void setup(Context context) throws IOException,
			InterruptedException {

		URI[] us = DistributedCache.getCacheFiles(context.getConfiguration());
		Configuration conf = context.getConfiguration();
		Text xxx = new Text("xxx");
		Text t1 = new Text(DistributedCache.getFileTimestamps(conf)[0]);
		Text t2 = new Text(DistributedCache.getCacheFiles(conf)[0].toString());
		Text t3 = new Text(DistributedCache.getLocalCacheFiles(conf)[0].toString());
		//Text t3 = new Text(DistributedCache.releaseCache(cache, conf);
		context.write(t1,xxx);
		context.write(t2,xxx);
		context.write(t2,xxx);
	}

	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {

		// context.write(value, new Text("1"));
	}
}
