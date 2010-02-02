package apriori1;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;


public class Tree {
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

		public void add(String[] items, int start, int end) throws IOException {
			if (items[start].length()==0) throw new IOException();
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

		int checkFrequency(String[] pattern, int minsup, Writer writer)
				throws IOException {
			if (depth >= 0)
				pattern[depth] = item;
			if (depth < treeDepth - 1) {
				int cnt = 0;
				Iterator<String> iter = children.keySet().iterator();
				while (iter.hasNext()) {
					String sitem = iter.next();
					int r = getChild(sitem).checkFrequency(pattern, minsup,
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
					 Apriori.nfp += 1;
					 Apriori.longest = pattern.length;
					return 1;
				}
			}
		}

		boolean checkValid(String[] itemArray) {
			if (Apriori.flag) return true;
			// ��depth+1����Ȼ�ǵ�ǰ�ڵ���ӽڵ�
			if (!contain(itemArray, depth + 2))
				return false;
			// �Ѿ��Ǹ�ڵ㣬�Ѽ���������
			if (depth <= 0)
				return true;
			itemArray[depth] = item;
			return parent.checkValid(itemArray);
		}

		boolean contain(String[] items, int start) {
			Apriori.scount += 1;
			if (!children.containsKey(items[start]))
				return false;
			if (start == items.length - 1)
				return true;
			return children.get(items[start]).contain(items, start + 1);
		}

		public TreeNode getChild(String item) {
			assert hasChild(item);
			return children.get(item);
		}

		int getCount(String[] items, int start) {
			if (start >= items.length)
				return count;
			else
				return children.get(items[start]).getCount(items, start + 1);
		}

		// ���ھ�����ͬparent��Ҷ�ڵ�������������µĺ�ѡ����������º�ѡ�������Ƿ���Ƶ���
		int grow() throws IOException {
			int cnt = 0;
			if (depth < treeDepth - 2) {
				Iterator<String> iter = children.keySet().iterator();
				while (iter.hasNext()) {
					String sitem = iter.next();
					int r = getChild(sitem).grow();
					if (r == 0)
						iter.remove();
					else
						cnt += r;
				}
			} else if (children.size() >= 2) {
				assert depth == treeDepth - 2;
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
			} else {
				Apriori.scount += (depth+3)*(depth+2)/2;
			}
			return cnt;
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

		void print(int indent) {
			System.out.println("(" + count + ")");
			for (String item : children.keySet()) {
				for (int i = 0; i < indent; ++i)
					System.out.print(" ");
				System.out.print(item);
				children.get(item).print(indent + 8);
			}
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
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
//		DataBase db = new DataBase("f:/data/aers.dat");
//		Tree tree = new Tree();
//		tree.init(db, 260);
//		tree.grow();
//		db.scan(tree);
//		tree.checkFrequent(260, null);
//		tree.toFile("tree.txt");
//		Tree t = new Tree("tree.txt");
//		tree.print();
//		tree.mergeCount(t);
//		tree.print();
		Tree tree = new Tree("test.txt");
		System.out.println(tree.activeNodeCount);
	}

	public TreeNode root = new TreeNode("", null);

	public int treeDepth = 1;

	public int activeNodeCount = 0;
	public Tree() {
	}
	public Tree(Reader reader) throws IOException {
		Scanner sc = new Scanner(reader);
		TreeNode node = root;
		int currentDepth = -1;
		int cnt = 0;
		activeNodeCount = sc.nextInt();
		treeDepth = sc.nextInt();
		String tmp = "";
		while (sc.hasNext()) {
			if (node == null) throw new IOException(tmp);
			String token = sc.next();
			tmp += " "+token;
			if (token.equals("leaf")) {
				node.count = sc.nextInt();
				node = node.parent;
				currentDepth -= 1;
				activeNodeCount += 1;
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
	}

	public Tree(String filename) throws IOException {
		this(new FileReader(filename));
	}

	public int checkFrequent(int minsup, Writer writer) throws IOException {
		//System.out.print("depth=" + treeDepth + " checking ...");
		int cnt = root.checkFrequency(new String[treeDepth], minsup, writer);
		//System.out.println("\tfrequent node=" + cnt);
//		System.out.println("depth="+treeDepth+"\tfreq count="+cnt);
		return cnt;
	}

	int getCount(String[] items) {
		return root.getCount(items, 0);
	}

	public int grow() throws IOException {
		//System.out.printf("depth=%d growing ... %n", treeDepth);
		activeNodeCount = root.grow();
		treeDepth++;
//		System.out.println("depth=" + treeDepth + "\tcandidate="+ activeNodeCount);
//		System.out.println("depth="+treeDepth+"\tcandidate count="+activeNodeCount);
		return activeNodeCount;
	}

	public void init(DataBase db, int minsup) {
		Map<String, Integer> item_count = new HashMap<String, Integer>();
		for (ItemSet transaction : db.transactions) {
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

	public void mergeCount(Tree t) throws IOException {
		root.mergeCount(t.root);
	}

	void print() {
		root.print(4);
	}

	public void scan(ItemSet transaction) {
		root.incrCount(transaction.items, 0);
	}

	public void scan(String[] items) {
		root.incrCount(items, 0);
	}

	void toFile(String filename) throws IOException {
		FileWriter fw = new FileWriter(filename);
		write(fw);
		fw.close();
	}

	public void write(Writer writer) throws IOException {
		writer.write(activeNodeCount + " ");
		writer.write(treeDepth + " ");
		root.write(writer);
	}

}
