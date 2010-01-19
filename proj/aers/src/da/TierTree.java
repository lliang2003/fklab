package da;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;

// A special type of count tree storing each level in a array called tier. 
public class TierTree {
	
	// Each item represents a node of the count tree.
	public static class Item implements Comparable<Item> {
		public Item(int id, int num) {
			this.id = id;
			this.num = num;
		}
		public Item(int id) {
			this(id, 0);
		}
		public int compareTo(Item item) {
			return id - item.id;
		}
		int id;
		/* For leaves indicate the count of the pattern. 
		 * For internal nodes indicate the tail index of children.
		 */
		int num;
		public String toString() {
			return id+"["+num+"]";
		}
	}

	// A tier represents a level of the TierTree.
	public static class Tier extends ArrayList<Item> {
		private static final long serialVersionUID = 8683452581122892189L;
		public void trimToSize(int newsize) {
			this.removeRange(newsize, this.size());
		}
		public Item last() {
			return get(size() - 1);
		}
	}

	public class Path {
		public int[] pos = new int[treeDepth + 1];
		public Item item(int dp) {
			return get(dp, pos[dp]);
		}
		// Move the path forward on a certain depth. 
		public boolean forward(int dp) {
			// Should not move the path out of the right tree border.
			if (pos[dp] == tiers[dp].size() - 1)
				return false;
			pos[dp]++;
			for (int d = dp; d < treeDepth; ++dp) {
				pos[d + 1] = get(d, pos[d] - 1).num;
			}
			// Using >= instead of ==, because in check() function this segment may has
			// been shrunk and thus make > possible. 
			for (int d = dp; d > 0 && pos[d] >= item(d-1).num; --d) {
				pos[d - 1]++;
			}
			return true;
		}

		public boolean forward() {
			return forward(treeDepth);
		}

		public void clone(Path p) {
			System.arraycopy(p.pos, 0, pos, 0, pos.length);
		}

		public boolean endOfSegment(int dp) {
			return item(dp-1).num == pos[dp] + 1;
		}
	}

	public int treeDepth; 	// depth of the tree
	int[][] cpos; 		// the check list for candidate
	Tier[] tiers; 		// has depth+1 tiers, the top one is not used

	// The first tier and the first item of each tier is not used for items.
	TierTree(int depth) {
		this.treeDepth = depth;
		tiers = new Tier[depth + 1];
		for (int dp = 0; dp <= depth; ++dp) {
			tiers[dp] = new Tier();
			tiers[dp].add(new Item(-1, 1));
		}
		tiers[0].add(new Item(-1));
		
		cpos = new int[depth + 1][];
		for (int d = 0; d <= depth; ++d) {
			cpos[d] = new int[d + 1];
		}
	}

	boolean checkValid(int dp, int childId) {
//		if (dp >= 0)
//			return true;
		if (dp == 0)
			return true;
		// println("checkValid "+dp+" "+Arrays.toString(cpos[dp-1])+" "+id);
		for (int i = 0; i < dp; ++i) {
			if (cpos[dp - 1][i] == -1) {
				cpos[dp][i + 1] = -1;
				return true;
			}
			int index = findInChildren(dp - 1, cpos[dp - 1][i], childId);
			if (index > 0) {
				cpos[dp][i + 1] = index;
			} else if ((i == dp - 1 || cpos[dp - 1][i + 1] == -1)
					&& childId > get(dp, get(dp - 1, cpos[dp - 1][i]).num - 1).id) {
				cpos[dp][i + 1] = -1;
				return true;
			} else
				return false;
		}
		return true;
	}

	
	// unique for all partial trees, defined by the items of the left border
	public int hashCode() {
		StringBuilder sb = new StringBuilder();
		for (int d = 1; d <= treeDepth; ++d) {
			sb.append(get(d, 1).id);
			sb.append(",");
			if (tiers[d].size() > 1)
				break;
		}
		return sb.toString().hashCode();
	}

	// Search a sorted segment for an item, return its index or minus number if not found.
	int binarySearch(Tier tier, int fromIndex, int toIndex, int key) {
		// println("bs "+fromIndex+" "+toIndex+" "+key);
		int low = fromIndex;
		int high = toIndex - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			int midVal = tier.get(mid).id;
			if (midVal < key)
				low = mid + 1;
			else if (midVal > key)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1); // key not found.
	}

	int findInChildren(int tierIndex, int pos, int id) {
		int left = get(tierIndex, pos - 1).num;
		int right = get(tierIndex, pos).num;
		return binarySearch(tiers[tierIndex + 1], left, right, id);
	}

	// Scan a transaction in the database and add counts for the item sets.
	public void scan(int[] tr) {
		// println("scan: "+Arrays.toString(tr));
		scan(tr, 0, 1, 1, tiers[1].size());
	}
	public void scan(int[] tr, int start, int dp, int fromIndex, int toIndex) {
		// System.out.printf("scan %s start=%d depth=%d from:%d to:%d\n",
		// Arrays.toString(tr), start, d, fromIndex, toIndex);
		for (int i = start; i < tr.length - treeDepth + dp; ++i) {
			int index = binarySearch(tiers[dp], fromIndex, toIndex, tr[i]);
			if (index < 0) {
				// The item in fromIndex is not larger than next item. 
				fromIndex = -index-1;
				if (fromIndex > toIndex)
					break;
				else
					continue;
			}				
			Item item = get(dp, index);
			if (dp == treeDepth) {
				item.num += 1;
			} else {
				scan(tr, i + 1, dp + 1, get(dp, index - 1).num, item.num);
			}
		}
	}

	public Item get(int tierIndex, int pos) {
		// println(tierIndex+" "+pos+" "+tiers[tierIndex].size());
		return tiers[tierIndex].get(pos);
	}

	public void set(int tierIndex, int pos, Item item) {
		tiers[tierIndex].set(pos, item);
	}

	// Write the candidate tree which is not instantiated in memory.
	public void writeCandidateTree(Path left, Path right, PrintWriter treeWriter, List<Integer> leaves)
			throws IOException {
//		System.out.println("Free Memory:"+Runtime.getRuntime().freeMemory()/1024/1024+" MB");
//		System.out.println("writing "+leaves.size()+ " candidates ...");
		for (int dp = 1; dp <= treeDepth; ++dp) {
			boolean newSegment = false;
			for (int i = left.pos[dp], pi = left.pos[dp - 1]; i <= right.pos[dp]; ++i) {
				if (get(dp - 1, pi).num == i) {
					pi++;
					newSegment = true;
				}
				if (get(dp, i).id >= 0) {
					if (newSegment) {
						treeWriter.write(", ");
						newSegment = false;
					}
					treeWriter.print(get(dp,i).id);
					treeWriter.print(" ");
				}
			}
			treeWriter.print("; ");
		}
		for (int i = 0, pi = left.pos[treeDepth]; i < leaves.size(); ++i) {
			if (get(treeDepth, pi).num == i) {
				treeWriter.write(", ");
				pi++;
			}
			while (get(treeDepth, pi).id < 0)
				pi++;
			treeWriter.print(leaves.get(i));
			treeWriter.print(" ");
		}
		treeWriter.print(";\n");
	}

	// Grow new candidates, output multiple new trees.
	@SuppressWarnings("unchecked")
	public void grow(PrintWriter treeWriter, TaskInputOutputContext context) throws IOException {
		if (tiers[treeDepth].size() == 1) return;	// Null tree.
		System.out.println("growing ... depth = "+treeDepth);
		for (int i = 1; i <= treeDepth; ++i)
			System.out.println("depth="+i+" count="+tiers[i].size());
		// Child trees with more candidates is output.
		int childrenThreshold = DA.getTreeThreshold(context.getConfiguration());
		ArrayList<Integer> leaves = new ArrayList<Integer>(childrenThreshold * 3 / 2);
		Path left = new Path(), right = new Path();
		left.forward();
		right.forward();
		grow(0, left, right, treeWriter, leaves, childrenThreshold, context);
		if (!leaves.isEmpty())
			writeCandidateTree(left, right, treeWriter, leaves);
	}

	@SuppressWarnings("unchecked")
	public void grow(int dp, Path left, Path right, PrintWriter treeWriter, 
			List<Integer> leaves, int childrenThreshold, TaskInputOutputContext context)
			throws IOException {
		Item item = right.item(dp);
		// println("grow "+dp+" "+Arrays.toString(left.pos)+" "+Arrays.toString(
		// right.pos)+" "+ccnt+" "+item.id);
		if (dp < treeDepth) {
			cpos[dp][0] = right.pos[dp]; // current position
			int start = get(dp, right.pos[dp] - 1).num;
			int end = item.num;
			int cnt = 0;
			// Process each child, invalid items is marked with -2.
			for (int p = start; p < end; ++p) {
				Item child = get(dp + 1, p);
				if (item.id == -2 || !checkValid(dp, child.id))
					child.id = -2;
				grow(dp + 1, left, right, treeWriter, leaves, childrenThreshold, context);
				if (child.id >= 0)
					cnt++;
			}
			if (cnt == 0)
				item.id = -2;
		} else {
			// last tier
			boolean hasCandidate = false;
			int start = right.pos[dp] + 1;
			int end = right.item(dp - 1).num;
			for (int p = start; p < end; ++p) {
				Item sib = get(dp, p);
				if (item.id != -2  && checkValid(dp, sib.id)) {
					leaves.add(sib.id);
					hasCandidate = true;
				}
			}
			if (hasCandidate)
				item.num = leaves.size(); // use as candidate count
			else
				item.id = -2;
				
			if (leaves.size() > childrenThreshold) {
				writeCandidateTree(left, right, treeWriter, leaves);
				left.clone(right);
				leaves.clear();
				context.progress();
			}
			// Make sure the left path indicate a valid candidate. 
			if (leaves.isEmpty())
				left.forward();
			right.forward();
		}
	}
	
	public void output() throws IOException {
		Path p = new Path();
		while (p.forward()) {
			for (int i = 1; i <= treeDepth; ++i)
				System.out.print(p.item(i).id+", ");
			System.out.println(":"+p.item(treeDepth).num);
		}
	}

	// Eliminate infrequent item sets.
	@SuppressWarnings("unchecked")
	public int check(int minsup, TaskInputOutputContext context) throws IOException, InterruptedException {
		Path path = new Path();
		int cnt = 0;
		int[] lastValidPos = new int[treeDepth + 1];
		for (int pos = 1; pos < tiers[treeDepth].size(); ++pos) {
			path.forward();
			// println("pos=" + pos+" "+tiers[depth].size());
			Item item = get(treeDepth, pos);
			if (item.num >= minsup) {
				cnt++;
				// output frequent pattern
				int[] fitems = new int[treeDepth];
				for (int i = 1; i <= treeDepth; ++i)
					fitems[i-1] = get(i, path.pos[i]).id;
				context.write(new ItemSet(fitems), new IntWritable(get(treeDepth, pos).num));
				set(treeDepth, ++lastValidPos[treeDepth], item);
			}
			// set segments
			for (int d = treeDepth; d > 0 && path.endOfSegment(d); --d) {
				// println("end of seg " + d);
				path.item(d - 1).num = lastValidPos[d] + 1;
				// System.out.println(Arrays.toString(path.pos));
				if (get(d - 1, path.pos[d-1]).num > get(d - 1, lastValidPos[d - 1]).num)
					set(d - 1, ++lastValidPos[d - 1], path.item(d - 1));
			}
			// println(Arrays.toString(vpos));
			// printTree();
			context.progress();
		}
		// Trim each tier.
		for (int d = 1; d <= treeDepth; ++d)
			tiers[d].trimToSize(lastValidPos[d]+1);
		return cnt;
	}

	// Expand this tree from a reader, which guarantees the order of data.
	void expand(Reader reader) throws IOException {
		Scanner sc = new Scanner(reader);
		int startIndex = 1; // used for the first tier
		boolean separated = false;
		for (int d = 1; d <= treeDepth; ++d) {
			Tier ptier = tiers[d - 1];
			Tier tier = tiers[d];
			int parentIndex = startIndex;
			startIndex = tier.size();
			if (!separated && startIndex > 1) {
				// possible merge
				int id = sc.nextInt();
				if (id == tier.last().id) {
					startIndex -= 1;
				} else {
					separated = true;
					tier.add(new Item(id));
				}
			}
			String token;
			while (!(token = sc.next()).equals(";")) {
				if (token.equals(",")) {
					ptier.get(parentIndex).num = tier.size();
					parentIndex += 1;
				} else if (token.equals(":")) {
					for (int i = startIndex; i < tier.size(); ++i) {
						tier.get(i).num = sc.nextInt();
					}
					break;
				} else {
					tier.add(new Item(Integer.parseInt(token)));
				}
			}
			ptier.get(parentIndex).num = tier.size();
		}
	}

	// Add a single item set to this tree, the order should be guaranteed.
	void addPath(int[] items, int count) {
		boolean split = false;
		for (int d = 1; d <= treeDepth; ++d) {
			if (split || items[d - 1] != tiers[d].last().id) {
				split = true;
				tiers[d].add(new Item(items[d - 1], count));
				tiers[d - 1].last().num = tiers[d].size();
			}
		}
	}
	void addPath(ItemSet iset, int count) {
		addPath(iset._items, count);
	}
	
	void printTree() throws IOException {
		for (List<Item> tier : tiers) {
			System.out.print("*) ");
			for (Item item : tier) {
				System.out.print(item.id + "[" + item.num + "] ");
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) throws IOException {
		TierTree t = new TierTree(3);
		t.expand(new StringReader("6622 6624 ; 1014350 1014365 , 1013868 ; 1014365 1014396 , 1014396 , 1014146 1014220 1014262 1014070 1014074 1014099 1014386 1014387 ;"));
		t.scan(Util.intArray("6624 1012095 1013868 1014070 1014074 1014146 1014220 1014262".split(" +")));
		t.output();
	}
}
