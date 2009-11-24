package fulldistribution;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import apriori.Tree;

public class ScanReducer extends Reducer<IntWritable, Text, Text, Text> {
	Tree tree;
	OutputStreamWriter patternWriter;
	boolean inited = false;
	int part;
	
	void init(Context context, IntWritable key) throws IOException {
		part = key.get();
		FileSystem fs = FileSystem.get(context.getConfiguration());
		OutputStream os = fs.create(new Path("/apriori/pf"+context.getConfiguration().get("round")
				+ "-part-"+part+"-"+(new Date().getTime())));
		patternWriter = new OutputStreamWriter(os);	
		inited = true;
	}

	protected void reduce(IntWritable key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		if (!inited) {
			init(context, key);
		}
		Iterator<Text> iter = values.iterator();
		while (iter.hasNext()) {
			StringReader sr = new StringReader(iter.next().toString());
			Tree nt = new Tree(sr);
			sr.close();
			if (tree == null)
				tree = nt;
			else
				tree.mergeCount(nt);
		}
	}

	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		StringWriter sw = new StringWriter();
		tree.checkFrequent(FullDistribution.minsup, sw );
		context.write(new Text(sw.toString()), null);

//		StringWriter sw = new StringWriter();
//		tree.write(sw);
//		context.write(new Text(sw.toString()), null);
//		sw.close();


	}
}
