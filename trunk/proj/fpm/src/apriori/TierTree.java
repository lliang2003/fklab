package apriori;

import java.io.FileReader;
import java.io.FileWriter;
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
		public Item(int id, int num) {
			this.id = id;
			this.num = num;
		}

		public Item(int id) {
			this(id, 0);
		}

		public int compareTo(Item item) {
			return id - item.id;
		}

		public int id;
		public int num; // for leaves indicate count, for other nodes indicate
		// the tail index of children
	}

	public static class Tier extends ArrayList<Item> {
		private static final long serialVersionUID = 8683452581122892189L;

		Item last() {
			return get(size() - 1);
		}
	}

	public class Path {
		public int[] pos = new int[depth + 1];

		public void forward(int dp) {
			// println(dp+" forward " + Arrays.toString(pos));
			if (pos[dp] == tiers[dp].size() - 1)
				return;
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
			// System.out.printf("%d %s\n", d, Arrays.toString(pos));
			// System.out.println(get(depth - 2, pos[depth - 2]).num + " " +
			// pos[depth - 1]);
			return get(d - 1, pos[d - 1]).num == pos[d] + 1;
		}
	}

	boolean checkValid(int dp, int childId) {
		if (dp >= 0)
			return true;
		if (dp == 0)
			return true;
		// println("checkValid "+dp+" "+Arrays.toString(cpos[dp-1])+" "+id);
		for (int i = 0; i < dp; ++i) {
			if (cpos[dp - 1][i] == -1) {
				cpos[dp][i + 1] = -1;
				return true;
			}
			int index = findInChildren(dp - 1, cpos[dp - 1][i], childId);
			if (index > 0) {
				cpos[dp][i + 1] = index;
			} else if ((i == dp - 1 || cpos[dp - 1][i + 1] == -1)
					&& childId > get(dp, get(dp - 1, cpos[dp - 1][i]).num - 1).id) {
				cpos[dp][i + 1] = -1;
				return true;
			} else
				return false;
		}
		return true;
	}

	public int depth; // depth of the tree
	int threshold = 10; // child trees with more candidates is outputed
	ArrayList<Integer> leaves = new ArrayList<Integer>(threshold * 3 / 2);
	int[][] cpos; // the check list for candidate
	Tier[] tiers; // has depth+1 tiers, the top one is not used

	TierTree(int depth) {
		this.depth = depth;
		tiers = new Tier[depth + 1];
		for (int i = 0; i <= depth; ++i) {
			tiers[i] = new Tier();
			tiers[i].add(new Item(-1, 1));
		}
		tiers[0].add(new Item(-1));
		cpos = new int[depth + 1][];
		for (int d = 0; d <= depth; ++d) {
			cpos[d] = new int[d + 1];
		}
	}

	// unique for all partial trees, defined by the left border
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
		int left = get(tierIndex, pos - 1).num;
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
		// println("scan: "+Arrays.toString(tr));
		scan(tr, 0, 1, 1, tiers[1].size());
	}

	public void scan(int[] tr, int start, int d, int fromIndex, int toIndex) {
		// System.out.printf("scan %s start=%d depth=%d from:%d to:%d\n",
		// Arrays.toString(tr), start, d, fromIndex, toIndex);
		if (tr.length - start <= depth - d)
			return;
		for (int i = start; i < tr.length - depth + d; ++i) {
			int index = binarySearch(tiers[d], fromIndex, toIndex, tr[i]);
			if (index < 0)
				continue;
			Item item = get(d, index);
			if (d == depth) {
				item.num += 1;
			} else {
				scan(tr, i + 1, d + 1, get(d, index - 1).num, item.num);
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

	public void writeg(Path left, Path right, Writer treeWriter)
			throws IOException {
		// printTree();
		// System.out.println("writeg " + Arrays.toString(left.pos) +
		// Arrays.toString(right.pos) + leaves);
		// left must be valid
		for (int d = 1; d <= depth; ++d) {
			boolean newSegment = false;
			for (int i = left.pos[d], pi = left.pos[d - 1]; i <= right.pos[d]; ++i) {
				if (get(d - 1, pi).num == i) {
					pi++;
					newSegment = true;
				}
				if (get(d, i).id >= 0) {
					if (newSegment) {
						treeWriter.write(", ");
						newSegment = false;
					}
					treeWriter.write(get(d, i).id + " ");
				}
			}
			treeWriter.write("; ");
		}
		for (int i = 0, pi = left.pos[depth]; i < leaves.size(); ++i) {
			// println(get(depth, pi).num + " " + pi + " " + i);
			if (get(depth, pi).num == i) {
				treeWriter.write(", ");
				pi++;
			}
			while (get(depth, pi).id < 0)
				pi++;
			treeWriter.write(leaves.get(i) + " ");
			// println(i+" "+pi+" "+get(depth,pi).num);
		}
		treeWriter.write(";\n");
	}

	public void grow(Writer treeWriter) throws IOException {
		Path left = new Path(), right = new Path();
		left.forward();
		right.forward();
		grow(0, left, right, treeWriter);
		if (!leaves.isEmpty())
			writeg(left, right, treeWriter);
	}

	public void grow(int dp, Path left, Path right, Writer treeWriter)
			throws IOException {
		Item item = get(right, dp);
		// println("grow "+dp+" "+Arrays.toString(left.pos)+" "+Arrays.toString(
		// right.pos)+" "+ccnt+" "+item.id);
		if (dp < depth) {
			cpos[dp][0] = right.pos[dp]; // current position
			int start = get(dp, right.pos[dp] - 1).num;
			int end = item.num;
			int cnt = 0;
			// process children
			for (int p = start; p < end; ++p) {
				Item child = get(dp + 1, p);
				if (item.id == -2 || !checkValid(dp, child.id))
					child.id = -2;
				grow(dp + 1, left, right, treeWriter);
				if (child.id >= 0)
					cnt++;
			}
			if (cnt == 0)
				item.id = -2;
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
			if (item.num == leaves.size())
				item.id = -2;
			else
				item.num = leaves.size();
			if (leaves.size() > threshold) {
				writeg(left, right, treeWriter);
				left.clone(right);
				leaves.clear();
			}
			if (leaves.isEmpty())
				left.forward();
			right.forward();
		}
	}

	public int check(int minsup, Writer fpWriter) throws IOException {
		Path path = new Path();
		int cnt = 0;
		int[] lastValidPos = new int[depth + 1];
		for (int pos = 1; pos < tiers[depth].size(); ++pos) {
			path.forward();
			// println("pos=" + pos+" "+tiers[depth].size());
			Item item = get(depth, pos);
			if (item.num >= minsup) {
				cnt++;
				// output frequent pattern
				fpWriter.write(Arrays.toString(path.pos) + ")\titemset=[");
				for (int i = 1; i <= depth; ++i)
					fpWriter.write(get(i, path.pos[i]).id + " ");
				fpWriter.write("]\tcount=" + get(depth, pos).num + "\n");
				set(depth, ++lastValidPos[depth], item);
			}
			// set segments
			for (int d = depth; d > 0 && path.endOfSegment(d); --d) {
				// println("end of seg " + d);
				get(path, d - 1).num = lastValidPos[d] + 1;
				// System.out.println(Arrays.toString(path.pos));
				if (get(path, d - 1).num > get(d - 1, lastValidPos[d - 1]).num)
					set(d - 1, ++lastValidPos[d - 1], get(path, d - 1));
			}
			// println(Arrays.toString(vpos));
			// printTree();
		}
		// trim
		for (int d = 1; d <= depth; ++d)
			while (tiers[d].size() > lastValidPos[d] + 1)
				tiers[d].remove(lastValidPos[d] + 1);
		return cnt;
	}

	void expand(Reader reader) throws IOException {
		Scanner sc = new Scanner(reader);
		int startIndex = 1; // used for the first tier
		boolean separated = false;
		for (int d = 1; d <= depth; ++d) {
			Tier ptier = tiers[d - 1];
			Tier tier = tiers[d];
			int parentIndex = startIndex;
			startIndex = tier.size();
			if (!separated && startIndex > 1) {
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
			ptier.get(parentIndex).num = tier.size();
		}
	}

	void addPath(int[] items, int count) {
		for (int d = 1; d <= depth; ++d) {
			if (items[d - 1] != tiers[d].last().id) {
				tiers[d].add(new Item(items[d - 1], count));
				tiers[d - 1].last().num = tiers[d].size();
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
		long start = System.currentTimeMillis();
		try {
			int minsup = 2500;
			String db = "f:/share/dataset/chess.dat";
			Map<Integer, Integer> count = new TreeMap<Integer, Integer>();
			TierTree t = new TierTree(1);
			Scanner sc = new Scanner(new FileReader(db));
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
			// t.printTree();
			Writer fpWriter = new FileWriter("tmpfp.txt");
			t.check(minsup, fpWriter);
			// t.printTree();
			FileWriter fw = new FileWriter("tmptree2.txt");
			t.grow(fw);
			fw.close();

			for (int d = 2; d < 15; ++d) {
				println("depth=" + d);
				FileReader fr = new FileReader("tmptree" + d + ".txt");
				fw = new FileWriter("tmptree" + (d + 1) + ".txt");
				Scanner treeScanner = new Scanner(fr);
				int ncandidate = 0, nfp = 0;
				int nparts = 0;
				while (treeScanner.hasNext()) {
					nparts++;
					t = new TierTree(d);
					int n = 0;
					while (n < 5 && treeScanner.hasNext()) {
						++n;
						t.expand(new StringReader(treeScanner.nextLine()));
					}
					sc = new Scanner(new FileReader(db));
					while (sc.hasNext()) {
						String[] parts = sc.nextLine().split(" +");
						int[] tr = new int[parts.length];
						for (int i = 0; i < tr.length; ++i)
							tr[i] = Integer.parseInt(parts[i]);
						t.scan(tr);
					}
					ncandidate += t.tiers[d].size();
					int nf = t.check(minsup, fpWriter);
					nfp += nf;
					if (nf > 0) t.grow(fw);
				}
				println(nparts + " " + ncandidate + " " + nfp);
				if (nfp == 0)
					break;
				fw.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		println("cost:" + (System.currentTimeMillis() - start));
	}

	public static void test2() throws IOException {
		TierTree t = new TierTree(3);
		// t.print();
		t.expand(new StringReader("0 ; 1 2 ; 2 3 5 7 , 3 5 : 7 6 5 8 7 6"));
		t.printTree();
		// println(t.hashCode());
		t.expand(new StringReader(
				"1 ; 2 4 5 ; 3 5 , 5 8 , 7 8 9 : 5 6 9 3 6 5 9"));
		t.printTree();
		// println(t.hashCode());
		t.scan(new int[] { 0, 1, 2 });
		t.scan(new int[] { 0, 2, 5 });
		System.out.println("after scan:");
		t.printTree();

		Writer treeWriter = new StringWriter();
		Writer fpWriter = new StringWriter();
		t.check(5, fpWriter);
		// print(fpWriter.toString());
		System.out.println("after check:");
		t.printTree();
		t.grow(treeWriter);
		print(treeWriter.toString());
		// t.serialize(new OutputStreamWriter(System.out));
	}

	public static void main(String[] args) throws IOException {
		// test2();
		test1();
	}

}
