package apriori;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class TierTree extends util.Util {
	public static class Item implements Comparable<Item> {
		public Item() {
			this(-1);
		}

		public Item(int id) {
			this.id = id;
		}

		public int compareTo(Item item) {
			return id - item.id;
		}

		public int id;
		public int num;
	}

	public static class Tier extends ArrayList<Item> {
		private static final long serialVersionUID = 8683452581122892189L;

		Item last() {
			return get(size() - 1);
		}
	}

	// depth+1层，最上层元素是没有用的，使代码更简洁
	Tier[] tiers;
	public int depth;

	TierTree(int depth) {
		this.depth = depth;
		tiers = new Tier[depth + 1];
		for (int i = 0; i <= depth; ++i) {
			tiers[i] = new Tier();
		}
		tiers[0].add(new Item());

		for (int d = 0; d <= depth; ++d) {
			cpos[d] = new int[d + 1];
		}
	}

	public int hashCode() {
		StringBuilder sb = new StringBuilder();
		for (int d = 1; d <= depth; ++d) {
			sb.append(get(d, 0).id);
			sb.append(",");
			if (tiers[d].size() > 1)
				break;
		}
		return sb.toString().hashCode();
	}

	int findInChildren(int tierIndex, int pos, int id) {
		int left = pos == 0 ? 0 : get(tierIndex, pos - 1).num;
		int right = get(tierIndex, pos).num;
		return binarySearch(tiers[tierIndex], left, right, id);
	}

	int binarySearch(List<Item> a, int fromIndex, int toIndex, int key) {
		int low = fromIndex;
		int high = toIndex - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			int midVal = a.get(mid).id;
			if (midVal < key)
				low = mid + 1;
			else if (midVal > key)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1); // key not found.
	}

	public void scan(int[] tr) {
		scan(tr, 0, 0, 1, tiers[0].size());
	}

	public void scan(int[] tr, int start, int d, int fromIndex, int toIndex) {
		if (tr.length - start < depth - d + 1)
			return;
		for (int i = start; i <= tr.length - d + d; ++i) {
			int index = binarySearch(tiers[d], fromIndex, toIndex, tr[i]);
			if (index < 0)
				continue;
			Item item = get(d, index);
			if (d == depth) {
				item.num += 1;
			} else {
				fromIndex = index == 0 ? 0 : get(d, index - 1).num;
				scan(tr, i + 1, d + 1, fromIndex, item.num);
			}
		}
	}

	public Item get(int tierIndex, int pos) {
		// println(tierIndex+" "+pos+" "+tiers[tierIndex].size());
		return tiers[tierIndex].get(pos);
	}

	public Item get(Path path, int tierIndex) {
		return tiers[tierIndex].get(path.pos[tierIndex]);
	}

	public void set(int tierIndex, int pos, Item item) {
		tiers[tierIndex].set(pos, item);
	}

	public class Path {
		public int[] pos = new int[depth + 1];

		public void forward(int dp) {
			// println("forward");
			pos[dp]++;
			assert pos[dp] < tiers[dp].size();
			for (int d = dp; d < depth; ++dp) {
				pos[d + 1] = get(d, pos[d] - 1).num;
			}
			for (int d = dp; d > 0 && pos[d] >= get(d - 1, pos[d - 1]).num; --d) {
				pos[d - 1]++;
			}
		}

		public void forward() {
			forward(depth);
		}

		public Path clone() {
			Path np = new Path();
			System.arraycopy(pos, 0, np.pos, 0, pos.length);
			return np;
		}

		public boolean endOfSegment(int d) {
			// System.out.println(get(depth - 2, pos[depth - 2]).num + " "
			// + pos[depth - 1]);
			return get(d - 1, pos[d - 1]).num == pos[d] + 1;
		}
	}

	// output [left, right]
	public void writeGrow(Path left, Path right, Path seg, int minsup,
			Writer treeWriter, int[][] cc) throws IOException {
		println("grow part:" + join(left.pos, " ") + "," + join(right.pos, " ")
				+ "," + join(seg.pos, " "));
		for (int i = 0; i < depth; ++i) {
			System.out.println(join(cc[i], " "));
		}
		// treeWriter.write((depth + 1) + " ");
		for (int p = left.pos[0]; p <= right.pos[0]; ++p) {
			if (cc[0][p] == 0)
				continue;
			treeWriter.write(get(0, p).id + " ");
		}
		treeWriter.write("; ");
		for (int d = 1; d < depth; ++d) {
			for (int p = left.pos[d], pp = left.pos[d - 1]; p <= right.pos[d]; ++p) {
				treeWriter.write("(" + p + cc[d][p] + ") ");
				if (get(d - 1, pp).num == p) {
					pp++;
					if (cc[d - 1][pp] > 0)
						treeWriter.write("* ");

				}

				if (cc[d][p] == 0)
					continue;
				treeWriter.write(get(d, p).id + " ");
			}
			treeWriter.write("; ");
		}
		for (int p = left.pos[depth - 1]; p <= right.pos[depth - 1]; ++p) {
			if (cc[depth - 1][p] == 0)
				continue;
			for (int pos = p, cnt = 0; cnt < cc[depth - 1][p]; ++pos) {
				if (get(depth - 1, pos).num < minsup)
					continue;
				treeWriter.write(get(depth - 1, pos).id + " ");
				cnt++;
			}
			if (p < right.pos[depth - 1])
				treeWriter.write(", ");
		}
		treeWriter.write("\n");
		treeWriter.flush();
		for (int i = 0; i < depth; ++i) {
			java.util.Arrays.fill(cc[i], 0);
		}
	}

	public void trim() {
		Path left = new Path();
		Path right = new Path();
		int vpos = 0;
		for (int i = 0; i < tiers[depth - 1].size(); ++i) {
			if ((i == 0 && get(depth - 1, i).num == 0)
					|| get(depth - 1, i).num < get(depth - 1, i - 1).num + 2) {

			}
		}

	}

	boolean checkValid(int dp, int id) {
		for (int i = 0; i < cpos[dp - 1].length; ++i) {
			int index = findInChildren(dp - 1, cpos[dp - 1][i], id);
			if (index < 0)
				return false;
			if (dp < depth)
				cpos[dp][i + 1] = index;
		}
		return true;
	}

	int threshold = 10;
	int ccnt = 0;
	int[] leaves = new int[threshold * 3 / 2];
	int[][] cpos = new int[depth][];
	Path left = new Path(), right = new Path();

	public void writeg(Path left, Path right, Writer treeWriter) throws IOException {
		for (int d = 0; d <=depth; ++d) {
			for (int i = left.pos[d]; i <= right.pos[d]; ++i) {
				if (get(d, i).id == -1) continue;
			}
			treeWriter.write("; ");
		}
		for (int i = 0; i < leaves.length; ++i) {
			if (leaves[i]< 0)
				treeWriter.write(", ");
		}
			
	}

	public void grow(int dp, Path left, Path right, Writer treeWriter) throws IOException {
		Item item = get(right, dp);
		if (dp < depth) {
			cpos[dp][0] = right.pos[dp]; // current position
			int start = right.pos[dp] > 0 ? get(dp, right.pos[dp] - 1).num : 0;
			int end = item.num;
			item.num = 0;
			for (int p = start; p < end; ++p) {
				Item child = get(dp + 1, p);
				if (item.id == -1 || !checkValid(dp, child.id))
					child.id = -1;
				grow(dp + 1, left, right, treeWriter);
			}
		} else {
			// last tier
			item.num = 0; // use as candidate count
			int start = right.pos[dp] + 1;
			int end = get(right, dp - 1).num;
			for (int p = start; p < end; ++p) {
				Item sib = get(dp, p);
				if (item.id != -1 && checkValid(dp, sib.id)) {
					item.num++;
					leaves[ccnt++] = sib.id;
				}
			}
			if (item.num == 0)
				item.id = -1;
			if (ccnt > threshold) {
				writeg(left, right, treeWriter);
				left = right.clone();
			}
			right.forward();
		}
		
	}

	public void check(int minsup, Writer fpWriter) throws IOException {
		Path path = new Path();
		int[] vpos = new int[depth + 1];
		for (int pos = 0; pos < tiers[depth].size(); ++pos, path.forward()) {
			println("pos=" + pos);
			Item item = get(depth, pos);
			if (item.num >= minsup) {
				// output frequent pattern
				fpWriter.write(join(path.pos, " ") + ")\t");
				for (int i = 0; i < depth; ++i)
					fpWriter.write(get(i, path.pos[i]).id + " ");
				fpWriter.write("\t" + get(depth, pos).num + "\n");
				set(depth, vpos[depth]++, item);
			}
			// set segments
			for (int d = depth; d > 0 && path.endOfSegment(d); --d) {
				println("end of seg " + d);
				get(path, d - 1).num = vpos[d];
				int left = vpos[d - 1] > 0 ? get(d - 1, vpos[d - 1] - 1).num
						: 0;
				if (get(path, d - 1).num - left > 0)
					set(d - 1, vpos[d - 1]++, get(path, d - 1));
			}
			println(join(vpos, " "));
			printTree();
		}
		// trim
		for (int d = 0; d <= depth; ++d)
			while (tiers[d].size() > vpos[d])
				tiers[d].remove(vpos[d]);
	}

	void expand(Reader reader) throws IOException {
		Scanner sc = new Scanner(reader);
		int startIndex = 0;
		boolean separated = false;
		for (int d = 1; d <= depth; ++d) {
			Tier ptier = tiers[d - 1];
			Tier tier = tiers[d];
			int parentIndex = startIndex;
			startIndex = tier.size();
			if (!separated && startIndex > 0) {
				// possible merge
				int id = sc.nextInt();
				if (id == tier.last().id) {
					startIndex -= 1;
				} else {
					separated = true;
					tier.add(new Item(id));
				}
			}
			String token;
			while (!(token = sc.next()).equals(";")) {
				if (token.equals(",")) {
					ptier.get(parentIndex).num = tier.size();
					parentIndex += 1;
				} else if (token.equals(":")) {
					for (int i = startIndex; i < tier.size(); ++i) {
						tier.get(i).num = sc.nextInt();
					}
					break;
				} else {
					tier.add(new Item(Integer.parseInt(token)));
				}
			}
			ptier.last().num = tier.size();
		}
	}

	void addPath(int[] items, int count) {
		for (int d = 1; d <= depth; ++d) {
			if (tiers[d].size() == 0 || items[d - 1] != tiers[d].last().id) {
				tiers[d].add(new Item(items[d - 1]));
				tiers[d - 1].last().num += 1;
			}
		}
	}

	void printTree() {
		for (List<Item> tier : tiers) {
			print("*) ");
			for (Item item : tier) {
				print(item.id + "[" + item.num + "] ");
			}
			println();
		}
	}

	public static void test1() {
		try {
			Map<Integer, Integer> count = new TreeMap<Integer, Integer>();
			TierTree t = new TierTree(1);
			Scanner sc = new Scanner(new FileReader(
					"f:/share/dataset/chess.dat"));
			while (sc.hasNext()) {
				String line = sc.nextLine();
				String[] parts = line.split(" +");
				for (int i = 0; i < parts.length; ++i) {
					int n = Integer.parseInt(parts[i]);
					int c = 0;
					if (count.containsKey(n))
						c = count.get(n);
					count.put(n, c + 1);
				}
			}
			int[] items = new int[1];
			for (int i : count.keySet()) {
				items[0] = i;
				t.addPath(items, count.get(i));
			}
			t.printTree();
			Writer treeWriter = new StringWriter();
			Writer fpWriter = new StringWriter();
			t.check(3000, fpWriter);
			print(treeWriter.toString());
			// print(fpWriter.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test2() throws IOException {
		TierTree t = new TierTree(3);
		// t.print();
		t.expand(new StringReader("0 ; 1 2 ; 2 3 5 7 , 3 4 : 3 4 4 2 7 3"));
		t.printTree();
		// println(t.hashCode());
		t.expand(new StringReader(
				"0 ; 2 4 5 ; 5 6 , 5 8 , 7 8 9 : 1 2 9 3 6 5 9"));
		t.printTree();
		// println(t.hashCode());
		t.scan(new int[] { 0, 1, 2 });
		t.scan(new int[] { 0, 2, 5 });
		t.printTree();

		Writer treeWriter = new StringWriter();
		Writer fpWriter = new StringWriter();
		t.check(5, fpWriter);
		t.printTree();
		// print("joke");
		// print(treeWriter.toString());
		print(fpWriter.toString());
		// t.serialize(new OutputStreamWriter(System.out));
	}

	public static void main(String[] args) throws IOException {
		test2();
		// test1();
	}

}
