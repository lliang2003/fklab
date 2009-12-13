package apriori;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

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

	ArrayTree(int depth) {
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
		int code = 0;
		for (int i = 0; i < tiers.size(); ++i) {
			for (int j = 0; j < tiers.get(i).size() && j < 2; ++j) {
				code = code * 2 + tiers.get(i).get(j).id;
			}
			if (tiers.get(i).size() > 1)
				return code;
		}
		return code;
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
		for (int i = start; i <= tr.length-tiers.size()+depth; ++i) {
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

	public void check(int minsup) {
		int[] indexes = new int[tiers.size()];
		int depth = tiers.size();
		for (indexes[depth-1] = 0; indexes[depth-1] < tiers.get(depth-1).size(); indexes[depth-1]++) {
			if (tiers.get(depth-1).get(indexes[depth-1]).num < minsup) continue;
			for (int i = depth-1; i > 0; -- i) {
				if (indexes[i] >= tiers.get(i-1).get(indexes[i-1]).num)
					indexes[i-1] ++;
				else break;
			}
			for (int i = 0; i < depth; ++i)
				System.out.print(tiers.get(i).get(indexes[i]).id+"["+indexes[i]+"] ");
			System.out.println();
		}
	}

	void expand(Reader reader) {
		Scanner sc = new Scanner(reader);
		Iterator<List<Item>> iter = tiers.iterator();
		List<Item> tier = null;
		List<Item> ptier = null;
		int parentIndex = 0;
		int startIndex = 0;
		boolean separated = false;
		while (sc.hasNext()) {
			String token = sc.next();
			// System.out.println(token);
			if (token.equals(";")) {
				if (ptier != null) {
					ptier.get(parentIndex).num = tier.size();
				}
				ptier = tier;
				tier = iter.next();
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

	void print() {
		for (List<Item> tier : tiers) {
			System.out.print("*) ");
			for (Item item : tier) {
				System.out.print(item.id + "[" + item.num + "] ");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		ArrayTree t = new ArrayTree(3);
		// t.print();
		t.expand(new StringReader("; 0 ; 1 2 ; 2 3 5 7 , 3 4 : 3 4 5 6 7 3"));
		t.print();
		p(t.hashCode());
		t.expand(new StringReader("; 0 ; 2 4 ; 5 6 , 5 8 :  7 8 9 3"));
		t.print();
		p(t.hashCode());
		t.scan(new int[]{0,1,2});
		
		t.print();
		t.check(5);
		// t.serialize(new OutputStreamWriter(System.out));
	}

}
