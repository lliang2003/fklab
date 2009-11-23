package apriori;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;


public class Tree {
	public class TreeNode {
		String item;
		int count = 0;
		int depth = -1;
		TreeNode parent;
		Map<String, TreeNode> children = new TreeMap<String, TreeNode>();

		TreeNode(String s, TreeNode parent_node) {
			item = s;
			parent = parent_node;
			if (parent != null) depth = parent.depth+1;
		}
		
		void addChild(String item) {
			if (!children.containsKey(item))
				children.put(item, new TreeNode(item, this));
		}

		TreeNode getChild(String item) {
			assert hasChild(item);
			return children.get(item);
		}

		boolean hasChild(String item) {
			return children.containsKey(item);
		}

		void incrCount(String[] items, int start) {
			if (children.isEmpty())
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

		//对于具有相同parent的叶节点两两连接生成新的候选项集，并测试新候选项集的子项集是否都是频繁项集
		int grow() {
			int cnt = 0;
			if (depth < treeDepth-2) {
				Iterator<String> iter = children.keySet().iterator();
				while (iter.hasNext()) {
					String sitem = iter.next();
					int r = getChild(sitem).grow();
					if (r==0) iter.remove();
					else cnt += r;
				}
				return cnt;
			} else {
				assert depth == treeDepth-2;
				if (children.size() < 2)
					return 0;
				ArrayList<String> items = new ArrayList<String>();
				for (String item : children.keySet())
					items.add(item);
				String[] itemArray = new String[treeDepth+1];
				if (depth >= 0)
					itemArray[depth] = item;
				for (int i = 0; i < items.size(); ++i) {
					itemArray[depth+1] = items.get(i);
					for (int j = i + 1; j < items.size(); ++j) {
						itemArray[depth+2] = items.get(j);
						if (parent == null || parent.checkContain(itemArray))
						{
							children.get(items.get(i)).addChild(items.get(j));
							cnt++;
						}
					}
				}
			}
			return cnt;
		}
		
		boolean checkContain(String[] itemArray) {
			// 第depth+1个必然是当前节点的子节点
			if (!contain(itemArray, depth+2)) return false;
			// 已经是根节点，已检查所有情况
			if (depth == -1) return true;
			itemArray[depth] = item;
			return parent.checkContain(itemArray);
		}



		int checkFrequent(String[] pattern, int minsup) {
			if (depth>=0) pattern[depth] = item;
			if (depth < treeDepth - 1) {
				int cnt = 0;
				Iterator<String> iter = children.keySet().iterator();
				while (iter.hasNext()) {
					String sitem = iter.next();
					int r = getChild(sitem).checkFrequent(pattern, minsup);
					if (r==0) iter.remove();
					else cnt += r;
				}
				return cnt;
			} else {
				if (count < minsup) {
					return 0;
				}
				else {
					//fk.util.out.printArray(pattern);
					return 1;
				}
			}
		}

		boolean contain(String[] items, int start) {
			if (!children.containsKey(items[start]))
				return false;
			if (start == items.length-1) return true;
			return children.get(items[start]).contain(items, start + 1);
		}
		
		void toFile(FileWriter fw) throws IOException {
			if (children.size() == 0)
				fw.write("leaf "+count+"\n");
			else {
				for (String item:children.keySet()) {
					fw.write(item+" ");
					children.get(item).toFile(fw);
				}
				fw.write("up\n");
			}
		}
		void mergeCount(TreeNode node) {
			if (children.isEmpty())
				count += node.count;
			for (String item:children.keySet())
				getChild(item).mergeCount(node.getChild(item));
		}
	}

	Tree() {
		
	}
	
	Tree(String filename) throws IOException {
		Scanner sc = new Scanner(new FileReader(filename));
		TreeNode node = root;
		int currentDepth = -1;
		treeDepth = Integer.parseInt(sc.next());
		while (sc.hasNext()) {
			String token = sc.next();
			if (token.equals("leaf")) {
				node.count = Integer.parseInt(sc.next());
				node = node.parent;
				currentDepth -= 1;
			} else if (token.equals("up")) {
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
	
	TreeNode root = new TreeNode("", null);
	int treeDepth = 1;

	void scan(ItemSet transaction) {
		root.incrCount(transaction.items, 0);
	}
	void scan(String[] items) {
		root.incrCount(items, 0);
	}

	void print() {
		root.print(4);
	}
	
	void toFile(String filename) throws IOException {
		FileWriter fw = new FileWriter(filename);
		fw.write(treeDepth+" ");
		root.toFile(fw);
		fw.close();
	}

	int getCount(String[] items) {
		return root.getCount(items, 0);
	}

	public int grow() {
		System.out.printf("depth=%d growing ... %n", treeDepth);
		int cnt = root.grow();
		treeDepth++;
		System.out.println("depth=" + treeDepth + "\tcandidate=" + cnt);
		return cnt;
	}

	public int checkFrequent(int minsup) {
		System.out.print("depth="+treeDepth+" checking ...");
		int cnt = root.checkFrequent(new String[treeDepth], minsup);
		System.out.println("\tfrequent node=" + cnt);
		return cnt;
	}

	
	public void mergeCount(Tree t) {
		root.mergeCount(t.root);
	}


	/**
	 * @param args
	 */

}
