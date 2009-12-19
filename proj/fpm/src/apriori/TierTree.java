package apriori;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
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

	// depth+1�㣬���ϲ�Ԫ����û���õģ�ʹ�������
	Tier[] tiers;

	public int depth;

	TierTree(int depth) {
		this.depth = depth;
		tiers = new Tier[depth + 1];
		for (int i = 0; i <= depth; ++i) {
			tiers[i] = new Tier();
		}
		tiers[0].add(new Item());

		cpos = new int[depth + 1][];
		for (int d = 0; d <= depth; ++d) {
			cpos[d] = new int[d + 1];
		}
	}

	public int hashCode() {
		StringBuilder sb = new StringBuilder();
		for (int d = 1; d <= depth; ++d) {
			sb.append(get(d, 0).id);
			sb.append(",");
			if (tiers[d].size() > 1) break;
		}
		return sb.toString().hashCode();
	}

	int findInChildren(int tierIndex, int pos, int id) {
		int left = pos == 0 ? 0 : get(tierIndex, pos - 1).num;
		int right = get(tierIndex, pos).num;
		return binarySearch(tiers[tierIndex + 1], left, right, id);
	}

	int binarySearch(List<Item> a, int fromIndex, int toIndex, int key) {
		// println("bs "+fromIndex+" "+toIndex+" "+key);
		int low = fromIndex;
		int high = toIndex - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			int midVal = a.get(mid).id;
			if (midVal < key) low = mid + 1;
			else if (midVal > key) high = mid - 1;
			else return mid; // key found
		}
		return -(low + 1); // key not found.
	}

	public void scan(int[] tr) {
		scan(tr, 0, 1, 0, tiers[0].size());
	}

	public void scan(int[] tr, int start, int d, int fromIndex, int toIndex) {
		//println("scan "+Arrays.toString(tr)+" "+start+" "+d+" "+fromIndex+" "+
		// toIndex);
		if (tr.length - start <= depth - d) return;
		for (int i = start; i < tr.length - depth + d; ++i) {
			int index = binarySearch(tiers[d], fromIndex, toIndex, tr[i]);
			if (index < 0) continue;
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
			// println(dp+" forward " + Arrays.toString(pos));
			if (pos[dp] == tiers[dp].size() - 1) return;
			pos[dp]++;
			assert pos[dp] < tiers[dp].size();
			for (int d = dp; d < depth; ++dp) {
				pos[d + 1] = get(d, pos[d] - 1).num;
			}
			for (int d = dp; d > 0 && pos[d] >= get(d - 1, pos[d - 1]).num; --d) {
				// println(d+" "+pos[d]+" "+pos[d-1]+" "+get(d - 1, pos[d -
				// 1]).num);
				pos[d - 1]++;
			}
			// println("forward done " + Arrays.toString(pos));
		}

		public void forward() {
			forward(depth);
		}

		public Path clone() {
			Path np = new Path();
			System.arraycopy(pos, 0, np.pos, 0, pos.length);
			return np;
		}

		public void clone(Path p) {
			System.arraycopy(p.pos, 0, pos, 0, pos.length);
		}

		public boolean endOfSegment(int d) {
			// System.out.println(get(depth - 2, pos[depth - 2]).num + " "
			// + pos[depth - 1]);
			return get(d - 1, pos[d - 1]).num == pos[d] + 1;
		}
	}

	boolean checkValid(int dp, int id) {
		if (dp >= 0) return true;
		// println("checkValid "+dp+" "+Arrays.toString(cpos[dp-1])+" "+id);
		for (int i = 0; i < cpos[dp - 1].length; ++i) {
			int index = findInChildren(dp - 1, cpos[dp - 1][i], id);
			if (index < 0) return false;
			if (dp < depth) cpos[dp][i + 1] = index;
		}
		return true;
	}

	int threshold = 5;

	ArrayList<Integer> leaves = new ArrayList<Integer>(threshold * 3 / 2);

	int[][] cpos;

	Path left = new Path(), right = new Path();

	public void writeg(Path left, Path right, Writer treeWriter) throws IOException {
		//printTree();
		System.out.println("writeg " + Arrays.toString(left.pos) + Arrays.toString(right.pos) + leaves);
		// left must be valid
		for (int d = 1; d <= depth; ++d) {
			boolean needComma = false;
			for (int i = left.pos[d], pi = left.pos[d - 1]; i <= right.pos[d]; ++i) {
				if (needComma && get(d - 1, pi).num == i) {
					pi++;
					treeWriter.write(", ");
					needComma = false;
				}
				if (get(d, i).id >= 0) {
					treeWriter.write(get(d, i).id + " ");
					needComma = true;
				}
			}
			treeWriter.write("; ");
		}
		for (int i = 0, pi = left.pos[depth]; i < leaves.size(); ++i) {
			//println(get(depth, pi).num + " " + pi + " " + i);
			if (get(depth, pi).num == i) {
				treeWriter.write(", ");
				pi++;
			}
			while (get(depth, pi).id < 0)
				pi++;
			treeWriter.write(leaves.get(i) + " ");
			// println(i+" "+pi+" "+get(depth,pi).num);
		}
		treeWriter.write("\n");
	}

	public void grow(Writer treeWriter) throws IOException {
		Path left = new Path(), right = new Path();
		grow(0, left, right, treeWriter);
		if (!leaves.isEmpty()) writeg(left, right, treeWriter);
	}

	public void grow(int dp, Path left, Path right, Writer treeWriter) throws IOException {
		Item item = get(right, dp);
		// println("grow "+dp+" "+Arrays.toString(left.pos)+" "+Arrays.toString(
		// right.pos)+" "+ccnt+" "+item.id);
		if (dp < depth) {
			cpos[dp][0] = right.pos[dp]; // current position
			int start = right.pos[dp] > 0 ? get(dp, right.pos[dp] - 1).num : 0;
			int end = item.num;
			int cnt = 0;
			for (int p = start; p < end; ++p) {
				Item child = get(dp + 1, p);
				if (item.id == -2 || !checkValid(dp, child.id)) child.id = -2;
				grow(dp + 1, left, right, treeWriter);
				if (child.id >= 0) cnt++;
			}
			if (cnt == 0) item.id = -2;
		} else {
			// last tier
			item.num = leaves.size(); // use as candidate count
			int start = right.pos[dp] + 1;
			int end = get(right, dp - 1).num;
			for (int p = start; p < end; ++p) {
				Item sib = get(dp, p);
				if (item.id != -2 && checkValid(dp, sib.id)) {
					leaves.add(sib.id);
				}
			}
			if (item.num == leaves.size()) item.id = -2;
			else item.num = leaves.size();
			if (leaves.size() > threshold) {
				writeg(left, right, treeWriter);
				left.clone(right);
				leaves.clear();
			}
			if (leaves.isEmpty()) left.forward();
			right.forward();
		}
	}

	public void check(int minsup, Writer fpWriter) throws IOException {
		Path path = new Path();
		int[] vpos = new int[depth + 1];
		for (int pos = 0; pos < tiers[depth].size(); ++pos, path.forward()) {
			// println("pos=" + pos);
			Item item = get(depth, pos);
			if (item.num >= minsup) {
				// output frequent pattern
				fpWriter.write(Arrays.toString(path.pos) + ")\t");
				for (int i = 0; i < depth; ++i)
					fpWriter.write(get(i, path.pos[i]).id + " ");
				fpWriter.write("\t" + get(depth, pos).num + "\n");
				set(depth, vpos[depth]++, item);
			}
			// set segments
			for (int d = depth; d > 0 && path.endOfSegment(d); --d) {
				// println("end of seg " + d);
				get(path, d - 1).num = vpos[d];
				int left = vpos[d - 1] > 0 ? get(d - 1, vpos[d - 1] - 1).num : 0;
				if (get(path, d - 1).num - left > 0) set(d - 1, vpos[d - 1]++, get(path, d - 1));
			}
			// println(Arrays.toString(vpos));
			// printTree();
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
			Scanner sc = new Scanner(new FileReader("f:/share/dataset/chess.dat"));
			while (sc.hasNext()) {
				String line = sc.nextLine();
				String[] parts = line.split(" +");
				for (int i = 0; i < parts.length; ++i) {
					int n = Integer.parseInt(parts[i]);
					int c = 0;
					if (count.containsKey(n)) c = count.get(n);
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
		t.expand(new StringReader("0 ; 1 2 ; 2 3 5 7 , 3 4 : 7 6 5 8 7 6"));
		// t.printTree();
		// println(t.hashCode());
		t.expand(new StringReader("1 ; 2 4 5 ; 3 6 , 5 8 , 7 8 9 : 5 6 9 3 6 5 9"));
		t.printTree();
		// println(t.hashCode());
		t.scan(new int[] { 0, 1, 2 });
		t.scan(new int[] { 0, 2, 5 });
		System.out.println("after scan:");
		t.printTree();

		Writer treeWriter = new StringWriter();
		Writer fpWriter = new StringWriter();
		t.check(5, fpWriter);
		System.out.println("after check:");
		t.printTree();
		t.grow(treeWriter);
		print(treeWriter.toString());
		// print(fpWriter.toString());
		// t.serialize(new OutputStreamWriter(System.out));
	}

	public static void main(String[] args) throws IOException {
		test2();
		// test1();
	}

}
