package apriori2;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import apriori.Tree;

public class CountDistributioniReducer extends Reducer<Text, Text, Text, Text> {
	Tree tree;
	OutputStreamWriter patternWriter;

	protected void setup(Context context) throws IOException,
			InterruptedException {
		FileSystem fs = FileSystem.get(context.getConfiguration());
		OutputStream os = fs.create(new Path("/apriori/pf-"+context.getConfiguration().get("round")
				+ "-"+(new Date().getTime())));
		patternWriter = new OutputStreamWriter(os);
	}

	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
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

		tree.checkFrequent(CountDistribution.minsup, patternWriter);
		patternWriter.flush();
		patternWriter.close();
		tree.grow();

		StringWriter sw = new StringWriter();
		tree.write(sw);
		context.write(new Text(sw.toString()), null);
		sw.close();

	}
}
