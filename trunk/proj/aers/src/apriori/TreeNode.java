package apriori;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TreeNode {
	public String item;
	public int count = 0;
	public int depth = -1;
	public TreeNode parent = null;
	Map<String, TreeNode> children = new TreeMap<String, TreeNode>();
	TreeNode(String s, TreeNode parent_node) {
		item = s;
		parent = parent_node;
		if (parent != null)
			depth = parent.depth + 1;
	}

	public void add(String[] items, int start, int end) {
		addChild(items[start]);
		if (start < end-1)
			children.get(items[start]).add(items, start+1, end);
	}
	
	public void addChild(String item) {
		if (children == null)
			children = new TreeMap<String, TreeNode>();

		if (!children.containsKey(item))
			children.put(item, new TreeNode(item, this));
	}

	public TreeNode getChild(String item) {
		assert hasChild(item);
		return children.get(item);
	}

	boolean hasChild(String item) {
		return children.containsKey(item);
	}

	void incrCount(String[] items, int start) {
		if (children == null || children.isEmpty())
			count++;
		else {
			for (int i = start; i < items.length; ++i) {
				if (children.containsKey(items[i]))
					children.get(items[i]).incrCount(items, i + 1);
			}
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

	int getCount(String[] items, int start) {
		if (start >= items.length)
			return count;
		else
			return children.get(items[start]).getCount(items, start + 1);
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
		if (true) return true;
		// 第depth+1个必然是当前节点的子节点
		if (!contain(itemArray, depth + 2))
			return false;
		// 已经是根节点，已检查所有情况
		if (depth <= 0)
			return true;
		itemArray[depth] = item;
		return parent.checkValid(itemArray);
	}
	
	boolean isleaf() {
		return children == null || children.size() == 0;
	}
	
	int getDepth() {
		if (isleaf())
			return 1;
		else 
			return children.entrySet().iterator().next().getValue().getDepth()+1;
	}


	int checkFrequency(String[] pattern, int minsup, int treeDepth, Writer writer)
			throws IOException {
		if (depth >= 0)
			pattern[depth] = item;
		if (depth < treeDepth - 1) {
			int cnt = 0;
			Iterator<String> iter = children.keySet().iterator();
			while (iter.hasNext()) {
				String sitem = iter.next();
				int r = getChild(sitem).checkFrequency(pattern, minsup, treeDepth,
						writer);
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

	void mergeCount(TreeNode node) throws IOException {
		if (children.size() != node.children.size())
			throw new IOException(children.keySet()+" "+node.children.keySet());
		if (children==null || children.isEmpty())
			count += node.count;
		else {
			for (String item : children.keySet())
			getChild(item).mergeCount(node.getChild(item));
		}
	}
}