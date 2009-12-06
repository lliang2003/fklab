package auto;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import apriori.DataBase;
import apriori.ItemSet;

public class NTree {
	public int candidates = 0;
	public Node root = new Node("", null);
	public int treeDepth = 1;
	public List<String> prefix = new ArrayList<String>();

	public static class Node {
		public String item;
		public int count = 0;
		public int depth = -1;
		public Node parent = null;
		Map<String, Node> children = new TreeMap<String, Node>();

		Node(String s, Node parent_node) {
			item = s;
			parent = parent_node;
			if (parent != null)
				depth = parent.depth + 1;
		}

		public void add(String[] items, int start, int end) throws IOException {
			if (items[start].length() == 0)
				throw new IOException();
			addChild(items[start]);
			if (start < end - 1)
				children.get(items[start]).add(items, start + 1, end);
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
			System.out.println(item + "(" + count + ")");
			for (String item : children.keySet()) {
				for (int i = 0; i < indent; ++i)
					System.out.print(" ");
				// System.out.print(item);
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
		int grow(List<NTree> newTrees, int treeDepth, List<String> prefix, Mapper<Object,Text,Text,Text>.Context context)
				throws IOException, InterruptedException {
			int cnt = 0;
			int max = 10;
			List<String> toBeDeleted = new ArrayList<String>();
			List<String> scanned = new ArrayList<String>();
			if (depth < treeDepth - 2) {
				Iterator<String> iter = children.keySet().iterator();
				while (iter.hasNext()) {
					String sitem = iter.next();
					prefix.add(sitem);
					int r = getChild(sitem).grow(newTrees, treeDepth, prefix, context);
					prefix.remove(prefix.size() - 1);
					if (r == 0)
						iter.remove();
					else {
						if (cnt > max) {
							NTree t = new NTree();
							t.treeDepth = treeDepth + 1;
							t.prefix.addAll(prefix);
							for (String item : scanned)
								t.root.children.put(item, children.get(item));
							t.candidates = cnt;
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

			} else if (children.size() >= 2) {
				assert depth == treeDepth - 2;
				ArrayList<String> items = new ArrayList<String>();
				for (String item : children.keySet())
					items.add(item);

				String[] itemArray = new String[treeDepth + 1];
				if (depth >= 0)
					itemArray[depth] = item;

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
					else {
						if (cnt > max) {
							NTree t = new NTree();
							t.treeDepth = treeDepth + 1;
							t.prefix.addAll(prefix);
							// if (true) throw new IOException(prefix.get(0));
							for (String item : scanned) {
								t.root.children.put(item, children.get(item));
							}
							t.candidates = cnt;
							cnt = 0;
							newTrees.add(t);
							toBeDeleted.addAll(scanned);
							scanned.clear();
							if (context != null) 
								context.write(new Text(t.toString()), new Text());
						}
						cnt += pcnt;
						scanned.add(items.get(i));
					}
				}
			}
			for (String item : toBeDeleted) {
				children.remove(item);
			}
			return cnt;
		}

		boolean checkValid(String[] itemArray) {
			if (true)
				return true;
			// 第depth+1个必然是当前节点的子节点
			if (!contain(itemArray, depth + 2))
				return false;
			// 已经是根节点，已检查所有情况
			if (depth <= 0)
				return true;
			itemArray[depth] = item;
			return parent.checkValid(itemArray);
		}

		int checkFrequency(String[] pattern, int treeDepth, int minsup,
				Writer writer) throws IOException {
			if (depth >= 0)
				pattern[depth] = item;
			if (depth < treeDepth - 1) {
				int cnt = 0;
				Iterator<String> iter = children.keySet().iterator();
				while (iter.hasNext()) {
					String sitem = iter.next();
					int r = getChild(sitem).checkFrequency(pattern, treeDepth,
							minsup, writer);
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
			writer.write(item + " ");
			if (children == null || children.size() == 0)
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
			if (children == null || children.isEmpty())
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

	public NTree() {
	}

	public NTree(String filename) throws IOException {
		this(new FileReader(filename));
	}

	public NTree(Reader reader) throws IOException {
		Scanner sc = new Scanner(reader);
		Node node = root;
		int currentDepth = -1;
		int cnt = 0;
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
				node.depth = currentDepth;
			}
		}
		// if (node != root) throw new IOException(tmp+" node != root,");
	}

	public void scan(String[] items) {
		root.incrCount(items, 0);
	}

	void print() {
		root.print(4);
	}

	void toFile(String filename) throws IOException {
		FileWriter fw = new FileWriter(filename);
		write(fw);
		fw.close();
	}

	public void write(Writer writer) throws IOException {
		writer.write(treeDepth + " ");
		for (String s : prefix)
			writer.write(s + " ");
		root.write(writer);
		for (int i = 0; i < prefix.size(); ++i)
			writer.write("up ");
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

	int getCount(String[] items) {
		return root.getCount(items, 0);
	}

	public List<NTree> grow(Mapper<Object,Text,Text,Text>.Context context) throws IOException, InterruptedException {
		List<NTree> trees = new ArrayList<NTree>();
		candidates = root.grow(trees, treeDepth, prefix, context);
		trees.add(this);
		treeDepth++;
		int cnt = 0;
		for (NTree t: trees) {
			cnt += t.candidates;
		}
		return trees;
	}

	public int checkFrequent(int minsup, Writer writer) throws IOException {
		System.out.print("depth=" + treeDepth + " checking ...");
		int cnt = root.checkFrequency(new String[treeDepth], treeDepth, minsup,
				writer);
		System.out.println("\tfrequent node=" + cnt);
		return cnt;
	}

	public void mergeCount(NTree t) throws IOException {
		root.mergeCount(t.root);
	}

	public void merge(NTree t) throws IOException {
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
		NTree tree = new NTree();
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
