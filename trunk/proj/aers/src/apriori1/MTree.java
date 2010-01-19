package apriori1;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;

import apriori.DataBase;
import apriori.ItemSet;

public class MTree {
	public int leafCount = 0;
	public Node root = new Node("", null);
	public String uuid;

	public static class Node {
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
			assert hasChild(item);
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

		boolean hasChild(String item) {
			return children.containsKey(item);
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
		
		// grow 会改变原来的count tree
		int grow(List<MTree> newTrees, LinkedList<String> prefix,
				Writer treeWriter) throws IOException {
			int max = 10000;
			count = 0;
			List<Node> scanned = new ArrayList<Node>();
			List<Node> toDel = new ArrayList<Node>();
			if (isLeaf()) {
				for (String sib : parent.children.keySet()) {
					if (item.compareTo(sib) >= 0)
						continue;
					// addChild(sib);
					count += 1;
				}
			} else {
				Iterator<Node> iter = children.values().iterator();
				while (iter.hasNext()) {
					Node node = iter.next();
					prefix.add(node.item);
					int r = node.grow(newTrees, prefix, treeWriter);
					prefix.removeLast();
					if (r > 0) {
						if (count > max) { // split
							treeWriter.write(count+" ");
							for (String p : prefix) {
								treeWriter.write(p);
								treeWriter.write(" ");
							}
							for (Node n : scanned) {
								n.writeGrow(treeWriter);
							}
							for (String p : prefix) {
								treeWriter.write("^ ");
							}
							treeWriter.write("\n");
							count = 0;
							toDel.addAll(scanned);
							scanned.clear();
						}
						count += r;
						scanned.add(node);
					} else if (!node.isLeaf()) {
						//如果不是倒数第2层，ok
						iter.remove();
					}
				}
				for (Node node:toDel) {
					children.remove(node.item);
				}
			}
			return count;
		}

		int checkFrequency(String[] pattern, int minsup,
				TaskInputOutputContext<Object, Text, Text, IntWritable> context)
				throws IOException, InterruptedException {
			if (depth() > 0)
				pattern[depth() - 1] = item;
			if (isLeaf()) {
				if (count < minsup) {
					return 0;
				} else {
					// fk.util.out.printArray(pattern);
					if (context != null) {
						String pat = pattern[0];
						for (int i = 1; i < pattern.length; ++i)
							pat += " " + pattern[i];
						context.write(new Text(pat), new IntWritable(count));

					}
					return 1;
				}
			} else {
				int cnt = 0;
				Iterator<String> iter = children.keySet().iterator();
				while (iter.hasNext()) {
					String sitem = iter.next();
					int r = getChild(sitem).checkFrequency(pattern, minsup,
							context);
					if (r == 0)
						iter.remove();
					else
						cnt += r;
				}
				return cnt;
			}
		}

		void writeGrow(Writer writer) throws IOException {
			if (count == 0) return;
			writer.write(item);
			writer.write(" ");
			if (isLeaf()) {
				writer.write("[ ");
				try {
				for (Node sib : parent.children.values()) {
					if (item.compareTo(sib.item) >= 0)
						continue;
					writer.write(sib.item);
					writer.write(" ");
				}
				} catch (IOException e) {
					throw new IOException(e+ " "+ parent);
				}
				writer.write("] ");
			} else {
				for (Node node : children.values()) {
					if (node.count > 0)
						node.writeGrow(writer);
				}
				if (parent != null)
					writer.write("^ ");
			}
			
		}

		void mergeCount(Node node) throws IOException {
			if (children.size() != node.children.size())
				throw new IOException(children.keySet() + " "
						+ node.children.keySet());
			if (isLeaf())
				count += node.count;
			else {
				for (String item : children.keySet())
					getChild(item).mergeCount(node.getChild(item));
			}
		}

		void merge(Node node) throws IOException {
			if (node.isLeaf())
				return;
			if (isLeaf())
				children = new TreeMap<String, Node>();
			for (String item : node.children.keySet()) {
				if (children.containsKey(item))
					children.get(item).merge(node.children.get(item));
				else
					children.put(item, node.children.get(item));
			}
		}
	}

	public MTree() {
		uuid = java.util.UUID.randomUUID().toString();
	}

	public MTree(Reader reader) throws IOException {
		Scanner sc = new Scanner(reader);
		Node node = root;
		int currentDepth = -1;
		int cnt = 0;
		// uuid = sc.next();
		leafCount = sc.nextInt();
		String tmp = "";
		while (sc.hasNext()) {
			if (node == null)
				throw new IOException(tmp);
			String token = sc.next();
			tmp += " " + token;
			if (token.equals("[")) {
//				node.count = sc.nextInt();
				while (!(token = sc.next()).equals("]")) {
					node.addChild(token);
				}
				node = node.parent;
			} else if (token.equals("^")) {
				cnt += 1;
				node = node.parent;
				currentDepth -= 1;
			} else {
				node.addChild(token);
				node = node.getChild(token);
				currentDepth += 1;
			}
		}
		if (node != root)
			throw new IOException(tmp + " node != root,");
	}

	public void scan(String[] items) {
		root.incrCount(items, 0, items.length);
	}

	public void print() {
		root.print(4);
	}

	public void writeg(Writer writer) throws IOException {
		// writer.write(uuid + " ");
		writer.write(leafCount + " ");
		root.writeGrow(writer);
	}

	public int growWrite(List<MTree> trees, Writer treeWriter)
			throws IOException {
		leafCount = root.grow(trees, new LinkedList<String>(), treeWriter);
		if (trees == null)
			return -1;
		int cnt = leafCount;
		for (MTree t : trees) {
			cnt += t.leafCount;
		}
		return cnt;
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

	public void mergeCount(MTree t) throws IOException {
		root.mergeCount(t.root);
	}

	public void merge(MTree t) throws IOException {
		root.merge(t.root);
	}

	public void init(DataBase db, int minsup) {
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
				// System.out.print(item+":"+item_count.get(item)+ " ");
			}
		}
	}

	public void scan(DataBase db) {
		for (ItemSet tran : db.transactions)
			scan(tran.items);
	}


}
