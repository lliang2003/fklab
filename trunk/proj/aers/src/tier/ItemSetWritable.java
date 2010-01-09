package tier;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.io.Writable;

public class ItemSetWritable implements Writable {
	private int size = 0;
	private int[] itemset = null;
	
	public ItemSetWritable(int[] items) {
		size = items.length;
		itemset = new int[size];
		System.arraycopy(items, 0, itemset, 0, size);
	}
	
	public void readFields(DataInput in) throws IOException {
		size = in.readInt();
		itemset = new int[size];
		for (int i = 0; i < size; ++i)
			itemset[i] = in.readInt();
	}
	
	public void write(DataOutput out) throws IOException {
		out.writeInt(size);
		for (int i = 0; i < size; ++i)
			out.writeInt(itemset[i]);
	}
	
	public String toString() {
		return Arrays.toString(itemset);
	}
}
