package apriori1;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ATree {
	public String item = null;
	public int count = 0;
	public int depth = -1;
	public ATree parent = null;
	Map<String, ATree> children = null;
	

	public ATree() {
	}

	private ATree(String s, ATree parent_node) {
		item = s;
		parent = parent_node;
		if (parent != null)
			depth = parent.depth + 1;
	}

	public void addPath(String[] items, int start, int end) {
		addChild(items[start]);
		if (start < end - 1)
			children.get(items[start]).addPath(items, start + 1, end);
	}

	public void addChild(String item) {
		if (children == null)
			children = new TreeMap<String, ATree>();

		if (!children.containsKey(item))
			children.put(item, new ATree(item, this));
	}

	public ATree getChild(String item) {
		assert hasChild(item);
		return children.get(item);
	}

	boolean hasChild(String item) {
		return children!=null && children.containsKey(item);
	}

	void incrPathCount(String[] items, int start, int end) {
		count++;
		if (leaf()) return;
		for (int i = start; i < end; ++i) {
			if (hasChild(items[i]))
				getChild(items[i]).incrPathCount(items, i + 1, end);
		}
	}

	void print(int indent) {
		System.out.println("(" + count + ")");
		for (String item : children.keySet()) {
			for (int i = 0; i < indent; ++i)
				System.out.print(" ");
			System.out.print(item);
			children.get(item).print(indent + 8);
		}
	}

	int getPathCount(String[] items, int start) {
		if (leaf())
			return count;
		else
			return children.get(items[start]).getPathCount(items, start + 1);
	}

	// 对于具有相同parent的叶节点两两连接生成新的候选项集，并测试新候选项集的子项集是否都是频繁项集
	int grow(int treeDepth) throws IOException {
		int cnt = 0;
		if (depth < treeDepth - 2) {
			Iterator<String> iter = children.keySet().iterator();
			while (iter.hasNext()) {
				String sitem = iter.next();
				int r = getChild(sitem).grow(treeDepth);
				if (r == 0)
					iter.remove();
				else
					cnt += r;
			}
		} else if (children.size() >= 2) {
			ArrayList<String> items = new ArrayList<String>();
			for (String item : children.keySet())
				items.add(item);

			String[] itemArray = new String[treeDepth + 1];
			if (depth >= 0)
				itemArray[depth] = item;

			List<String> toBeDeleted = new ArrayList<String>();

			for (int i = 0; i < items.size(); ++i) {
				itemArray[depth + 1] = items.get(i);
				int pcnt = 0;
				for (int j = i + 1; j < items.size(); ++j) {
					itemArray[depth + 2] = items.get(j);
					if (parent == null || parent.checkValid(itemArray)) {
						children.get(items.get(i)).addChild(items.get(j));
						pcnt++;
					}
				}
				if (pcnt == 0)
					toBeDeleted.add(items.get(i));
				cnt += pcnt;
			}

			for (String item : toBeDeleted) {
				children.remove(item);
			}
		}
		return cnt;
	}

	boolean checkValid(String[] itemArray) {
		if (true)
			return true;
		// 不含该节点则不检查
		if (!hasChild(itemArray[depth+2])) return true;
		// 第depth+1个必然是当前节点的子节点
		//TODO: 有错误
		if (!contain(itemArray, depth + 2))
			return false;
		// 已经是根节点，已检查所有情况
		if (depth < 0)
			return true;
		itemArray[depth] = item;
		return parent.checkValid(itemArray);
	}

	boolean leaf() {
		return children == null || children.size() == 0;
	}

	int getDepth() {
		if (leaf())
			return 1;
		else
			return children.entrySet().iterator().next().getValue().getDepth() + 1;
	}

	int checkFrequency(String[] pattern, int minsup, int treeDepth,
			Writer writer) throws IOException {
		if (depth >= 0)
			pattern[depth] = item;
		if (depth < treeDepth - 1) {
			int cnt = 0;
			Iterator<String> iter = children.keySet().iterator();
			while (iter.hasNext()) {
				String sitem = iter.next();
				int r = getChild(sitem).checkFrequency(pattern, minsup,
						treeDepth, writer);
				if (r == 0)
					iter.remove();
				else
					cnt += r;
			}
			return cnt;
		} else {
			if (count < minsup) {

				return 0;
			} else {
				// fk.util.out.printArray(pattern);
				if (writer != null) {
					for (int i = 0; i < pattern.length; ++i)
						writer.write(pattern[i] + " ");
					writer.write(count + "\n");
					writer.flush();
				}
				return 1;
			}
		}
	}

	boolean contain(String[] items, int start) {
		if (!children.containsKey(items[start]))
			return false;
		if (start == items.length - 1)
			return true;
		return children.get(items[start]).contain(items, start + 1);
	}

	void write(Writer writer) throws IOException {
		if (children == null || children.size() == 0)
			writer.write("leaf " + count + " ");
		else {
			for (String item : children.keySet()) {
				writer.write(item + " ");
				children.get(item).write(writer);
			}
			writer.write("up ");
		}
	}

	void mergeCount(ATree node) throws IOException {
		if (children.size() != node.children.size())
			throw new IOException(children.keySet() + " "
					+ node.children.keySet());
		if (children == null || children.isEmpty())
			count += node.count;
		else {
			for (String item : children.keySet())
				getChild(item).mergeCount(node.getChild(item));
		}
	}

	public static void main(String[] args) throws IOException {
		int minsup = 10;
		DataBase db = new DataBase("f:/data/aers.dat");
		ATree root = new ATree("root", null);

		Map<String, Integer> item_count = new HashMap<String, Integer>();
		for (ItemSet transaction : db.transactions) {
			if (transaction.items.length > 16)
				continue;
			for (String item : transaction.items) {
				int cnt = item_count.containsKey(item) ? item_count.get(item)
						: 0;
				item_count.put(item, cnt + 1);
			}
		}
		for (String item : item_count.keySet()) {
			if (item_count.get(item) >= minsup) {
				root.addChild(item);
				root.getChild(item).count = item_count.get(item);
			}
		}
		FileWriter fw = new FileWriter("patterns.txt");
		int depth = 1;
		int cnt;
		do {
			System.out.println("growing depth=" + depth);
			if ((cnt = root.grow(depth)) == 0)
				break;
			System.out.println(cnt);
			depth += 1;
			System.out.println("scaning depth=" + depth);
			for (ItemSet transaction : db.transactions) {
				root.incrPathCount(transaction.items, 0, transaction.items.length);
			}
			cnt = root.checkFrequency(new String[depth], depth, minsup, fw);
			System.out.println(cnt);
		} while (cnt > 1);
	}
}