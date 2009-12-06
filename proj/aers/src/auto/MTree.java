package auto;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import apriori.DataBase;
import apriori.ItemSet;

public class MTree {
	public int leafCount = 0;
	public Node root = new Node("", null);
	public int treeDepth = 0;
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
			if (parent == null) return 0;
			return parent.depth()+1;
		}
		
		boolean isLeaf() {
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
			for (String item : children.keySet()) {
				for (int i = 0; i < indent; ++i)
					System.out.print(" ");
				// System.out.print(item);
				children.get(item).print(indent + 8);
			}
		}
		
		// 对于具有相同parent的叶节点两两连接生成新的候选项集，并测试新候选项集的子项集是否都是频繁项集
		int grow(List<MTree> newTrees, LinkedList<String> prefix, Mapper<Object,Text,Text,Text>.Context context)
				throws IOException, InterruptedException {
			int cnt = 0;
			int max = 10;
			List<String> toBeDeleted = new ArrayList<String>();
			List<String> scanned = new ArrayList<String>();
			if (isLeaf()) {
				children = new TreeMap<String, Node>();
				for (String sib: parent.children.keySet()) {
					if (item.compareTo(sib) >= 0) continue;
					addChild(sib);
					cnt += 1;
				}
			} else {
				Iterator<String> iter = children.keySet().iterator();
				while (iter.hasNext()) {
					String sitem = iter.next();
					prefix.add(sitem);
					int r = getChild(sitem).grow(newTrees, prefix, context);
					prefix.removeLast();
					if (r == 0)
						iter.remove();
					else {
						if (cnt > max) { //split
							MTree t = new MTree();
							Node node = t.root;
							for (String item: prefix) {
								node.addChild(item);
								node = node.getChild(item);
							}
							for (String item : scanned)
								node.children.put(item, children.get(item));
							t.leafCount = cnt;
							cnt = 0;
							toBeDeleted.addAll(scanned);
							scanned.clear();
							newTrees.add(t);
							if (context != null) 
								context.write(new Text(t.toString()), new Text());
						}
						cnt += r;
						scanned.add(sitem);
					}
				}
			}
			return cnt;
		}

		int checkFrequency(String[] pattern, int minsup,
				Writer writer) throws IOException {
			if (depth() > 0) pattern[depth()-1] = item;
			if (isLeaf()) {
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
			} else {
				int cnt = 0;
				Iterator<String> iter = children.keySet().iterator();
				while (iter.hasNext()) {
					String sitem = iter.next();
					int r = getChild(sitem).checkFrequency(pattern, 
							minsup, writer);
					if (r == 0)
						iter.remove();
					else
						cnt += r;
				}
				return cnt;
			} 
		}

		void write(Writer writer) throws IOException {
			writer.write(item + " ");
			if (isLeaf())
				writer.write("leaf " + count + " ");
			else {
				for (String item : children.keySet()) {
					children.get(item).write(writer);
				}
				if (parent != null)
					writer.write("up ");
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
		uuid = sc.next();
		treeDepth = sc.nextInt();
		String tmp = "";
		while (sc.hasNext()) {
			if (node == null)
				throw new IOException(tmp);
			String token = sc.next();
			tmp += " " + token;
			if (token.equals("leaf")) {
				node.count = sc.nextInt();
				node = node.parent;
				currentDepth -= 1;
			} else if (token.equals("up")) {
				cnt += 1;
				node = node.parent;
				currentDepth -= 1;
			} else {
				node.addChild(token);
				node = node.getChild(token);
				currentDepth += 1;
			}
		}
		if (node != root) throw new IOException(tmp+" node != root,");
	}

	public void scan(String[] items) {
		root.incrCount(items, 0, items.length);
	}

	void print() {
		root.print(4);
	}

	public void write(Writer writer) throws IOException {
		writer.write(uuid+" ");
		writer.write(treeDepth + " ");
		root.write(writer);
	}

	public String toString() {
		StringWriter sw = new StringWriter();
		try {
			write(sw);
		} catch (IOException e) {
			return null;
		}
		return sw.toString();
	}

	public List<MTree> grow(Mapper<Object,Text,Text,Text>.Context context) throws IOException, InterruptedException {
		List<MTree> trees = new ArrayList<MTree>();
		leafCount = root.grow(trees, new LinkedList<String>(), context);
		trees.add(this);
		treeDepth++;
		int cnt = 0;
		for (MTree t: trees) {
			cnt += t.leafCount;
		}
		return trees;
	}

	public int checkFrequent(int minsup, Writer writer) throws IOException {
		System.out.print("depth=" + treeDepth + " checking ...");
		int cnt = root.checkFrequency(new String[treeDepth], minsup,
				writer);
		System.out.println("\tfrequent node=" + cnt);
		return cnt;
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

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		DataBase db = new DataBase("f:/data/aers.dat");
		MTree tree = new MTree();
		int minsup = 100;
		tree.init(db, minsup);

//		tree.grow();
//		tree.scan(db);
//		tree.checkFrequent(minsup, null);
//
//		tree.grow();
//		System.out.println(tree);
//		tree.scan(db);
//		tree.checkFrequent(minsup, new OutputStreamWriter(System.out));
	}

}
