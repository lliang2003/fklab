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

public class ArrayTree extends util.Util {
	public static class Item implements Comparable<Item> {
		public Item(int id) {
			this.id = id;
		}

		public int compareTo(Item item) {
			return id - item.id;
		}

		public int id;
		public int num;
	}

	List<List<Item>> tiers = new ArrayList<List<Item>>();
	public int depth;

	ArrayTree(int depth) {
		this.depth = depth;
		for (int i = 0; i < depth; ++i) {
			tiers.add(new ArrayList<Item>());
		}
	}

	public void serialize(Writer writer) throws IOException {
		for (int d = 0; d < tiers.size(); ++d) {
			for (int i = 0; i < tiers.get(d).size(); ++i) {
				writer.write(tiers.get(d).get(i).id + ":" + " ");
			}
		}
	}

	public int hashCode() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tiers.size(); ++i) {
			sb.append(tiers.get(i).get(0));
			sb.append(",");
			if (tiers.get(i).size() > 1)
				break;
		}
		return sb.toString().hashCode();
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
		scan(tr, 0, 0, 0, tiers.get(0).size());
	}

	public void scan(int[] tr, int start, int depth, int fromIndex, int toIndex) {
		if (tr.length - start < tiers.size() - depth)
			return;
		for (int i = start; i <= tr.length - tiers.size() + depth; ++i) {
			int index = binarySearch(tiers.get(depth), fromIndex, toIndex,
					tr[i]);
			if (index < 0)
				continue;
			Item item = tiers.get(depth).get(index);
			if (depth == tiers.size() - 1) {
				item.num += 1;
			} else {
				fromIndex = index == 0 ? 0
						: tiers.get(depth).get(index - 1).num;
				scan(tr, i + 1, depth + 1, fromIndex, item.num);
			}
		}
	}

	public Item get(int tierIndex, int pos) {
		// println(tierIndex+" "+pos+" ");
		return tiers.get(tierIndex).get(pos);
	}

	public class Border {
		public int[] pos = new int[tiers.size()];

		public void forward() {
			int depth = tiers.size();
			if (pos[depth - 1] == tiers.get(depth - 1).size() - 1)
				return;
			pos[depth - 1]++;
			for (int i = depth - 1; i > 0
					&& pos[i] >= get(i - 1, pos[i - 1]).num; --i) {
				pos[i - 1]++;
			}
		}

		public Border clone() {
			Border sec = new Border();
			System.arraycopy(pos, 0, sec.pos, 0, pos.length);
			return sec;
		}

		public boolean endSeg() {
			// System.out.println(get(depth - 2, pos[depth - 2]).num + " "
			// + pos[depth - 1]);
			if (depth==1)
				return pos[0] == tiers.get(0).size()-1;
			return get(depth - 2, pos[depth - 2]).num == pos[depth - 1] + 1;
		}
	}

	// output [left, right]
	public void writeGrow(Border left, Border right, Border seg, int minsup,
			Writer treeWriter, int[][] cc) throws IOException {
		println("grow part:" + join(left.pos, " ") + "," + join(right.pos, " ")
				+ "," + join(seg.pos, " "));
		for (int i = 0; i < depth; ++i) {
			System.out.println(join(cc[i], " "));
		}
		//treeWriter.write((depth + 1) + " ");
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

	public void checkAndGrow(int minsup, Writer treeWriter, Writer fpWriter)
			throws IOException {
		int depth = tiers.size();
		Border left = new Border();
		Border right = new Border();
		Border edge = new Border();
		int fcnt = 0;
		int candidateCount = 0;
		final int candidateCountThreshold = 10;
		int[][] cc = new int[depth][];
		for (int i = 0; i < depth; ++i)
			cc[i] = new int[tiers.get(i).size()];
		for (int p = 0; p < tiers.get(depth - 1).size(); ++p, edge.forward()) {
			// println(p);
			Item item = get(depth - 1, p);
			if (item.num >= minsup) {
				fcnt++;
			}
			if (edge.endSeg()) {
				for (int i = right.pos[depth - 1]; i <= edge.pos[depth - 1]; ++i, right
						.forward()) {
					if (get(depth - 1, i).num < minsup)
						continue;
					fcnt--;
					for (int d = 0; d < depth; ++d)
						cc[d][right.pos[d]] += fcnt;
					candidateCount += fcnt;
					// System.out.println(i + ":" + fcnt + ":" +
					// candidateCount);
					if (candidateCount > candidateCountThreshold) {
						writeGrow(left, right, edge, minsup, treeWriter, cc);
						left = right.clone();
						left.forward();
						candidateCount = 0;
					}
				}
				if (candidateCount == 0) {
					left = right.clone();
				}
			}
			// System.out.println(p+":"+right.newSeg());
			if (item.num >= minsup) {
				fpWriter.write(join(edge.pos, " ") + ")\t");
				for (int i = 0; i < depth; ++i)
					fpWriter.write(get(i, edge.pos[i]).id + " ");
				fpWriter.write("\t" + get(depth - 1, p).num + "\n");
			}
		}
		if (candidateCount > 0)
			writeGrow(left, edge, edge, minsup, treeWriter, cc);
	}

	void expand(Reader reader) throws IOException {
		Scanner sc = new Scanner(reader);
		if (sc.nextInt() != tiers.size())
			throw new IOException("depth disagree");
		sc.next();
		int startIndex = 0;
		boolean separated = false;

		for (int d = 0; d < depth; ++d) {
			List<Item> ptier = d > 0 ? tiers.get(d - 1) : null;
			List<Item> tier = tiers.get(d);
			int parentIndex = startIndex;
			startIndex = tier.size();
			if (!separated && startIndex > 0) {
				// possible merge
				int id = sc.nextInt();
				if (id == tier.get(startIndex - 1).id) {
					startIndex -= 1;
				} else {
					separated = true;
					tier.add(new Item(id));
				}
			}
			while (sc.hasNext()) {
				String token = sc.next();
				if (token.equals(";")) {
					if (ptier != null) {
						ptier.get(parentIndex).num = tier.size();
					}
					break;
				} else if (token.equals(",")) {
					ptier.get(parentIndex).num = tier.size();
					parentIndex += 1;
				} else if (token.equals(":")) {
					if (ptier != null) {
						ptier.get(parentIndex).num = tier.size();
					}
					for (int i = startIndex; i < tier.size(); ++i) {
						tier.get(i).num = sc.nextInt();
					}
					break;
				} else {
					tier.add(new Item(Integer.parseInt(token)));
				}
			}
		}
	}

	void addPath(int[] items, int count) {
		for (int d = 0; d < items.length; ++d) {
			if (tiers.get(d).size() == 0) {
				tiers.get(d).add(new Item(items[d]));
			}
			Item it = get(d, tiers.get(d).size() - 1);
			if (it.id != items[d]) {
				tiers.get(d).add(new Item(items[d]));
				if (d < items.length - 1)
					it.num = tiers.get(d + 1).size();
				else
					it.num = count;
			}
		}
		for (int d = 0; d < depth - 1; ++d)
			get(d, tiers.get(d).size() - 1).num = tiers.get(d + 1).size();
	}

	void expandGrow(Reader reader) throws IOException {
		Scanner sc = new Scanner(reader);
		if (sc.nextInt() != tiers.size())
			throw new IOException("depth disagree");
		int currentDepth = -1;
		List<Item> tier = null;
		List<Item> ptier = null;
		int parentIndex = 0;
		int startIndex = 0;
		boolean separated = false;
		while (sc.hasNext()) {
			String token = sc.next();
			if (token.equals(";")) {
				if (ptier != null) {
					ptier.get(parentIndex).num = tier.size();
				}
				currentDepth++;
				ptier = tier;
				tier = tiers.get(currentDepth);
				parentIndex = startIndex;
				startIndex = tier.size();
				if (!separated && startIndex > 0) {
					// possible merge
					int id = sc.nextInt();
					if (id == tier.get(startIndex - 1).id) {
						startIndex -= 1;
					} else {
						separated = true;
						tier.add(new Item(id));
					}
				}
			} else if (token.equals(",")) {
				ptier.get(parentIndex).num = tier.size();
				parentIndex += 1;
			} else if (token.equals(":")) {
				if (ptier != null) {
					ptier.get(parentIndex).num = tier.size();
				}
				for (int i = startIndex; i < tier.size(); ++i) {
					tier.get(i).num = sc.nextInt();
				}
			} else {
				tier.add(new Item(Integer.parseInt(token)));
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
			ArrayTree t = new ArrayTree(1);
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
			t.checkAndGrow(3000, treeWriter, fpWriter);
			print(treeWriter.toString());
			//print(fpWriter.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test2() throws IOException {
		ArrayTree t = new ArrayTree(3);
		// t.print();
		t.expand(new StringReader("3 ; 0 ; 1 2 ; 2 3 5 7 , 3 4 : 3 4 5 6 7 3"));
		t.printTree();
		// println(t.hashCode());
		t.expand(new StringReader("3 ; 0 ; 2 4 ; 5 6 , 5 8 :  7 8 9 3"));
		t.printTree();
		// println(t.hashCode());
		t.scan(new int[] { 0, 1, 2 });
		t.scan(new int[] { 0, 2, 5 });
		t.printTree();
		Writer treeWriter = new StringWriter();
		Writer fpWriter = new StringWriter();
		t.checkAndGrow(5, treeWriter, fpWriter);
		// print("joke");
		print(treeWriter.toString());
		print(fpWriter.toString());
		// t.serialize(new OutputStreamWriter(System.out));
	}

	public static void main(String[] args) throws IOException {
		//		test2();
		test1();
	}

}
