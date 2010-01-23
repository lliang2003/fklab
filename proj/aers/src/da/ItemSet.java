package da;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * This class represent an item set which can be serialized.
 * 
 * Items should be stored in numerical order.
 * When compared to another item set, the length is first
 * compared, then each numerical item is compared in order.
 * 
 * An item less than 1000000 represents a drug, in other case it represents a reaction.
 *  
 * @author fankai
 *
 */
public class ItemSet implements WritableComparable<Object> {
	public int[] _items = null;

	// This construction function is used by hadoop.
	public ItemSet() {
	} 

	// Use items directly.
	public ItemSet(int[] items) {
		_items = items;
	}

	public ItemSet(List<Integer> items) {
		int size = items.size();
		_items = new int[size];
		for (int i = 0; i < size; ++i)
			_items[i] = items.get(i);
	}

	public int drugHashCode() {
		int h = 0;
		for (int item : _items) {
			if (item < 1000000)
				h = h * 31 + item;
			else
				break;
		}
		return h;
	}

	public int reactionHashCode() {
		int h = 0;
		for (int item : _items) {
			if (item > 1000000)
				h = h * 31 + item;
		}
		return h;
	}

	@Override
	public int hashCode() {
		int h = 0;
		for (int item : _items)
			h = h * 31 + item;
		return h;
	}

	public boolean hasDrug() {
		return _items[0] < 1000000;
	}

	public boolean hasReaction() {
		return _items[_items.length - 1] > 1000000;
	}

	public ItemSet reactionSet() {
		int i = 0;
		while (i < _items.length && _items[i] < 1000000)
			++i;
		if (i == _items.length)
			return null;
		int[] reactions = new int[_items.length - i];
		System.arraycopy(_items, i, reactions, 0, reactions.length);
		return new ItemSet(reactions);
	}

	public ItemSet drugSet() {
		int i = 0;
		while (i < _items.length && _items[i] < 1000000)
			++i;
		int[] drugs = new int[i];
		System.arraycopy(_items, 0, drugs, 0, i);
		return new ItemSet(drugs);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		int size = in.readInt();
		_items = new int[size];
		for (int i = 0; i < size; ++i)
			_items[i] = in.readInt();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ItemSet)) {
			return false;
		}
		ItemSet other = (ItemSet) o;
		int size = _items.length;
		if (size != other._items.length)
			return false;
		for (int i = 0; i < size; ++i)
			if (_items[i] != other._items[i])
				return false;
		return true;
	}

	public void write(DataOutput out) throws IOException {
		out.writeInt(_items.length);
		for (int item : _items)
			out.writeInt(item);
	}

	@Override
	public String toString() {
		return Arrays.toString(_items);
	}

	public ItemSet(String str) {
		String[] tokens = str.substring(1, str.length() - 1).split(", ");
		_items = Util.intArray(tokens);
	}

	public int compareTo(Object o) {
		ItemSet other = (ItemSet) o;
		int size1 = _items.length;
		int size2 = other._items.length;
		if (size1 != size2)
			return size1 - size2;
		for (int i = 0; i < size1; ++i) {
			if (_items[i] > other._items[i])
				return 1;
			else if (_items[i] < other._items[i])
				return -1;
		}
		return 0;
	}

	public static class Comparator extends WritableComparator {
		public Comparator() {
			super(ItemSet.class);
		}

		public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
			int size1 = readInt(b1, s1);
			int size2 = readInt(b2, s2);
			if (size1 != size2)
				return size1 - size2;
			for (int i = 0; i < size1; ++i) {
				int n1 = readInt(b1, s1 + 4 + i * 4);
				int n2 = readInt(b2, s2 + 4 + i * 4);
				if (n1 > n2)
					return 1;
				else if (n1 < n2)
					return -1;
			}
			return 0;
		}
	}

	static { // register this comparator
		WritableComparator.define(ItemSet.class, new Comparator());
	}

	public static void main(String[] args) {
		ItemSet a, b, c;
		a = new ItemSet(new int[] { 1014070 });
		b = new ItemSet(new int[] { 6649, 1014399 });
		c = new ItemSet(new int[] { 1014146 });
		ItemSet aa = new ItemSet(new int[] { 6649 });

		System.out.println(a.compareTo(b));
		System.out.println(b.compareTo(b));
		System.out.println(b.compareTo(a));
		System.out.println(a.compareTo(c));
		System.out.println(c.compareTo(b));
		Object o = c;
		System.out.println(a.equals(o));
		System.out.println(b.drugSet());
		System.out.println(a.equals(b.drugSet()));

		HashMap<ItemSet, Integer> hm = new HashMap<ItemSet, Integer>();
		hm.put(a, 1);
		hm.put(b, 2);
		System.out.println(hm.get(aa));
		System.out.println(hm.get(b.drugSet()));
		System.out.println(hm.get(c));

		String s = "[1222, 2, 3]";
		System.out.println(Arrays.toString(s.split("[\\[\\], ]")));
		s = ",4,5,6,";
		System.out.println(Arrays.toString(s.split("[\\[\\],]")));

		System.out.println(-5 % 23);
	}

}
