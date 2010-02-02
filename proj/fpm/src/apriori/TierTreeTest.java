package apriori;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class TierTreeTest extends util.Util {

	public static void test1(String db, int minsup) {
		System.out.println("file="+db+"\tminsup="+minsup);
		long start = System.currentTimeMillis();
		int longest = 0;
		try {
			TierTree.scount = 0;
			TierTree.nfp = 0;
			Map<Integer, Integer> count = new TreeMap<Integer, Integer>();
			TierTree t = new TierTree(1);
			Scanner sc = new Scanner(new FileReader(db));
			while (sc.hasNext()) {
				String line = sc.nextLine();
				String[] parts = line.split(" +");
				for (int i = 0; i < parts.length; ++i) {
					int n = Integer.parseInt(parts[i]);
					int c = 0;
					if (count.containsKey(n))
						c = count.get(n);
					count.put(n, c + 1);
				}
			}
			int[] items = new int[1];
			for (int i : count.keySet()) {
				items[0] = i;
				t.addPath(items, count.get(i));
			}
			// t.printTree();
			Writer fpWriter = new FileWriter("tmpfp.txt");
			t.check(minsup, fpWriter);
			// t.printTree();
			FileWriter fw = new FileWriter("tmptree2.txt");
			t.grow(fw);
			fw.close();
			for (int d = 2; d < 999; ++d) {
	//			println("depth=" + d);
				FileReader fr = new FileReader("tmptree" + d + ".txt");
				fw = new FileWriter("tmptree" + (d + 1) + ".txt");
				Scanner treeScanner = new Scanner(fr);
				int ncandidate = 0, nfp = 0;
				int nparts = 0;
				while (treeScanner.hasNext()) {
					nparts++;
					t = new TierTree(d);
					int n = 0;
					while (treeScanner.hasNext()) {
						
						t.expand(new StringReader(treeScanner.nextLine()));
					}
					sc = new Scanner(new FileReader(db));
					while (sc.hasNext()) {
						String[] parts = sc.nextLine().split(" +");
						int[] tr = new int[parts.length];
						for (int i = 0; i < tr.length; ++i)
							tr[i] = Integer.parseInt(parts[i]);
						t.scan(tr);
					}
					ncandidate += t.tiers[d].size();
					//t.printTree();
					int nf = t.check(minsup, fpWriter);
					nfp += nf;
					if (nf > 0) {t.grow(fw);longest = d;}
				}
				//println(nparts + " " + ncandidate + " " + nfp);
				if (nfp == 0)
					break;
				fw.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		println("cost:" + (System.currentTimeMillis() - start));
		println("fp count:"+TierTree.nfp+"\tlongest:"+longest);
		println("op cost:"+TierTree.scount);
	}

	public static void test2() throws IOException {
		TierTree t = new TierTree(3);
		// t.print();
		t.expand(new StringReader("0 ; 1 2 ; 2 3 5 7 , 3 5 : 7 6 5 8 7 6"));
		t.printTree();
		// println(t.hashCode());
		t.expand(new StringReader(
				"1 ; 2 4 5 ; 3 5 , 5 8 , 7 8 9 : 5 6 9 3 6 5 9"));
		t.printTree();
		// println(t.hashCode());
		t.scan(new int[] { 0, 1, 2 });
		t.scan(new int[] { 0, 2, 5 });
		System.out.println("after scan:");
		t.printTree();

		Writer treeWriter = new StringWriter();
		Writer fpWriter = new StringWriter();
		t.check(5, fpWriter);
		// print(fpWriter.toString());
		System.out.println("after check:");
		t.printTree();
		t.grow(treeWriter);
		print(treeWriter.toString());
		// t.serialize(new OutputStreamWriter(System.out));
	}

	public static void main(String[] args) throws IOException {
		// test2();
//		test1("d:/desktop/mushroom.dat", 3000);
//		test1("d:/desktop/mushroom.dat", 2000);
//		test1("d:/desktop/mushroom.dat", 1000);
//		test1("d:/desktop/mushroom.dat", 800);
//		test1("d:/desktop/mushroom.dat", 500);
//		test1("d:/desktop/T10I4D100K.dat", 100);
//		test1("d:/desktop/T10I4D100K.dat", 80);
//		test1("d:/desktop/T10I4D100K.dat", 50);
//		test1("d:/desktop/T10I4D100K.dat",30);
		test1("d:/desktop/T10I4D100K.dat",20);
		test1("d:/desktop/T10I4D100K.dat",15);
//		test1("d:/desktop/T40I10D100K.dat", 2000);
//		test1("d:/desktop/T40I10D100K.dat", 1000);
//		test1("d:/desktop/T40I10D100K.dat", 800);
		TierTree.apruning = false;
		TierTree.bpruning = true;
//		test1("d:/desktop/mushroom.dat", 3000);
//		test1("d:/desktop/mushroom.dat", 2000);
//		test1("d:/desktop/mushroom.dat", 1000);
//		test1("d:/desktop/mushroom.dat", 800);
//		test1("d:/desktop/mushroom.dat", 500);
//		test1("d:/desktop/T10I4D100K.dat", 100);
//		test1("d:/desktop/T10I4D100K.dat", 80);
//		test1("d:/desktop/T10I4D100K.dat", 50);
//		test1("d:/desktop/T10I4D100K.dat",30);
		test1("d:/desktop/T10I4D100K.dat",20);
		test1("d:/desktop/T10I4D100K.dat",15);
//		test1("d:/desktop/T40I10D100K.dat", 2000);
//		test1("d:/desktop/T40I10D100K.dat", 1000);
//		test1("d:/desktop/T40I10D100K.dat", 800);
	}
}
