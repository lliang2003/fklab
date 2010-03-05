package da;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class Sample {
	private static Set<ItemSet> samples = new TreeSet<ItemSet>();
	public static void gen(int[] items, int[] pos, int index, int max,
			List<Integer> tmp) throws IOException, InterruptedException {
		if (index == max || index == items.length) {
			samples.add(new ItemSet(tmp));
			return;
		}
		pos[index] = (index == 0 ? 0 : pos[index - 1]);
		for (; pos[index] < items.length; ++pos[index]) {
			if (index == 0 || pos[index] > pos[index - 1]) {
				tmp.add(items[pos[index]]);
				gen(items, pos, index + 1, max, tmp);
				tmp.remove(tmp.size() - 1);
			}
		}
	}
	
	public static int[] sample(Configuration conf) throws Exception {
		long start = System.currentTimeMillis();
		int nr = DA.getCountReducerNum(conf);
		int[] points = new int[nr-1];
		FileSystem fs = FileSystem.get(conf);
		Scanner sc = new Scanner(new InputStreamReader(fs.open(new Path(DA.getDataPath(conf)))));
		int line_cnt = 0;
		int step = 100;
		int initItemSetLength = DA.getInitItemSetLength(conf);
		int filterSize = DA.getFilterSize(conf);

		while (sc.hasNext()) {
			String line = sc.nextLine();
			line_cnt ++;
//			if (line_cnt % step != 0) continue;
			if (line_cnt > 20000) break;
			String[] tokens = line.split(" +");
			if (tokens.length > filterSize) {
				continue;
			}
			int[] items = Util.intArray(tokens);
			gen(items, new int[items.length], 0, initItemSetLength,
					new ArrayList<Integer>());
		//	System.out.print(".");
		}
		
		System.out.println("scan "+line_cnt+" lines");
		System.out.println("size of itemsets:" + samples.size());
		
		int count = samples.size();
		Iterator<ItemSet> iter = samples.iterator();
		for (int i = 0; i < nr-1; ++i) {
			for (int j = 0; j < count/nr-1; ++j) {
				iter.next();
			}
			ItemSet iset = iter.next();
			points[i] = iset._items[0];
		}
		
		long end = System.currentTimeMillis();
		System.out.println("sample cost "+ (end-start) + " ms");
	
		return points;
	}

	public static void main(String[] args) throws Exception {
		sample(new Configuration());
	}

}
