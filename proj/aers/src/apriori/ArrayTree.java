package apriori;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class ArrayTree {
	public static class Item {
		public Item(int id) {
			this.id = id;
		}

		public int id;

		public int num;
	}

	List<List<Item>> items = new ArrayList<List<Item>>();

	ArrayTree(int depth) {
		for (int i = 0; i < depth; ++i) {
			items.add(new ArrayList<Item>());
		}
	}

	void serialize(Writer writer) throws IOException {
		for (int d = 0; d < items.size(); ++d) {
			for (int i = 0; i < items.get(d).size(); ++i) {
				writer.write(items.get(d).get(i).id + ":" + " ");
			}
		}
	}

	void startTier() {

	}

	void expand(Reader reader) {
		Scanner sc = new Scanner(reader);
		Iterator<List<Item>> iter = items.iterator();
		List<Item> tier = null;
		String token;
		List<Item> ptier = null;
		int parentIndex = 0;
		int startIndex = 0;
		boolean separated = false;
		while (sc.hasNext()) {
			token = sc.next();
			// System.out.println(token);
			if (token.equals(";")) {
				if (ptier != null) {
					ptier.get(parentIndex).num = tier.size();
				}
				ptier = tier;
				tier = iter.next();
				parentIndex = startIndex;
				startIndex = tier.size();
				System.out.println(startIndex);
				if (!separated && startIndex > 0) {
					token = sc.next();
					if (token.equals(tier.get(startIndex - 1))) {
						System.out.println("ok");
						startIndex -= 1;
					} else {
						separated = true;
						tier.add(new Item(Integer.parseInt(token)));
					}
				}

			} else if (token.equals(",")) {
				// System.out.println(parentIndex);
				ptier.get(parentIndex).num = tier.size();
				parentIndex += 1;
			} else if (token.equals(":")) {
				if (ptier != null) {
					ptier.get(parentIndex).num = tier.size();
				}
				for (int i = startIndex; i < tier.size(); ++i) {
					// System.out.println("."+item.id);
					tier.get(i).num = sc.nextInt();
				}
			} else {
				if (!separated && startIndex > 0 && tier.size() == startIndex) {
					if (token.equals(tier.get(startIndex - 1))) {
						startIndex -= 1;
					} else separated = true;
				} else tier.add(new Item(Integer.parseInt(token)));
			}
		}
	}

	void print() {
		for (List<Item> tier : items) {
			for (Item item : tier) {
				System.out.print(item.id + "[" + item.num + "] ");
			}
			System.out.println();
		}
	}
	public static void main(String[] args) {
			ArrayTree t = new ArrayTree(2);
			t.print();
			t.expand(new StringReader("; 1 2 ; 2 3 5 7 , 3 4 : 3 4 5 6 7 3"));
			t.print();
			t.expand(new StringReader("; 2 4 ; 5 6 , 5 8 :  7 8 9 3"));
			t.print();
			// t.serialize(new OutputStreamWriter(System.out));
	}

}
