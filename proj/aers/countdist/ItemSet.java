package apriori;

import java.util.Arrays;
import java.util.List;

public class ItemSet {
	public String[] items;

	ItemSet(String s) {
		items = new String[] {s};
	}

	ItemSet(ItemSet a, ItemSet b) {
		int n = a.items.length;
		items = new String[a.items.length + 1];
		System.arraycopy(a.items, 0, items, 0, n);
		items[n] = b.items[n - 1];
		Arrays.sort(items);
	}

	ItemSet(List<String> list) {
		items = new String[list.size()];
		for (int i = 0; i < list.size(); ++i)
			items[i] = list.get(i);
		Arrays.sort(items);
	}	
	
	ItemSet(String[] list) {
		items = list;
		Arrays.sort(items);
	}
	
	public String toString() {
		return Arrays.toString(items);
	}

	public boolean similar(ItemSet s) {
		if (items.length != s.items.length)
			return false;
		for (int i = 0; i < items.length - 1; ++i)
			if (items[i] != s.items[i]) {
				return false;
			}
		return true;
	}

	public boolean in(int[] items) {
		for (int i = 0, j = 0; j < items.length; ++i) {
			if (i == items.length)
				return false;
			if (items[i] == items[j])
				++j;
			else if (items[i] > items[j])
				return false;
		}
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
