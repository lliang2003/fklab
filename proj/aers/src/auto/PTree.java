package auto;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;

import auto.MTree.Node;

public class PTree {
	public Node root = new Node("", null);

	public void addPath(String line) {
		String[] parts = line.split(":");
		String[] prefix = parts[0].split(" +");
		String[] leaves = parts[1].split(" +");
		Node node = root;
		for (String item: prefix) {
			node.addChild(item);
			node = node.getChild(item);
		}
		for (String leaf: leaves) {
			String[] tokens = leaf.split(".");
			node.addChild(tokens[0]);
			node.getChild(tokens[0]).count = Integer.parseInt(tokens[1]);
		}
	}

	public static class Node {
		public static LinkedList<String> pattern = new LinkedList<String>();
		public String item;
		public int count = 0;
		public Node parent = null;
		Map<String, Node> children = null;

		Node(String s, Node parent_node) {
			item = s;
			parent = parent_node;
		}

		public void addPath(String[] items, int start, int end) {
			addChild(items[start]);
			if (start < end - 1)
				children.get(items[start]).addPath(items, start + 1, end);
		}

		public void addChild(String item) {
			if (children == null)
				children = new TreeMap<String, Node>();
			if (!children.containsKey(item))
				children.put(item, new Node(item, this));
		}

		public Node getChild(String item) {
			return children.get(item);
		}

		int depth() {
			if (parent == null)
				return 0;
			return parent.depth() + 1;
		}

		public boolean isLeaf() {
			return children == null;
		}

		void incrCount(String[] items, int start, int end) {
			if (isLeaf())
				count++;
			else {
				for (int i = start; i < end; ++i) {
					if (children.containsKey(items[i]))
						children.get(items[i]).incrCount(items, i + 1, end);
				}
			}
		}

		void print(int indent) {
			System.out.println(item + "(" + count + ")");
			if (children == null)
				return;
			for (String item : children.keySet()) {
				for (int i = 0; i < indent; ++i)
					System.out.print(" ");
				// System.out.print(item);
				children.get(item).print(indent + 8);
			}
		}

		// return frequent item count
		public int checkAndGrow(int minsup, Writer treeWriter,
				TaskInputOutputContext<Text, Text, Text, Text> context)
				throws IOException, InterruptedException {
			if (isLeaf()) {
				if (count < minsup) {
					return 0;
				} else {
					if (context != null)
						context.write(new Text(pattern.toString()), new Text(count + ""));
					List<String> leaves = new ArrayList<String>();
					for (Node sib: parent.children.values()) {
						if (item.compareTo(sib.item) >= 0) continue;
						if (sib.count < minsup) continue;
						leaves.add(sib.item);
					}
					if (leaves.size() > 0) {
						for (String item:pattern)
							treeWriter.write(item+" ");
						treeWriter.write(": ");
						for (String item:leaves)
							treeWriter.write(item+".0 ");
						treeWriter.write("\n");
					}
					return 1;
				}
			} else {
				Iterator<String> iter = children.keySet().iterator();
				int cnt = 0;
				while (iter.hasNext()) {
					String childItem = iter.next();
					pattern.add(childItem);
					int r = getChild(childItem).checkAndGrow(minsup, treeWriter,
							context);
					if (r == 0)
						iter.remove();
					else
						cnt += r;
					pattern.removeLast();
				}
				return cnt;
			}
		}
	}
	
	public int depth() {
		Node node = root;
		int depth = 0;
		while (!node.isLeaf()) {
			node = node.children.entrySet().iterator().next().getValue();
			depth += 1;
		}
		return depth;
	}
	public void scan(String[] items) {
		root.incrCount(items, 0, items.length);
	}


}
