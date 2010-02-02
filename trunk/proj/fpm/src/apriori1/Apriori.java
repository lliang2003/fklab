package apriori1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.FileWriter;

import apriori.TierTree;

public class Apriori {

	public DataBase db;
	public List<ItemSet> fitemsets = new ArrayList<ItemSet>();
	public static int minsup;
	public Tree tree = new Tree();
	public static boolean flag = true;
	public static int scount = 0;
	public static int nfp = 0;
	public static int longest = 0;
	
	private Date last = new Date();

	public Apriori(DataBase db) throws IOException {
		this.db = db;
		tree.init(db, minsup);
		//tree.toFile("tmp.txt");		tree = new Tree("tmp.txt");
	}

	public void run() throws IOException {
		FileWriter fw = new FileWriter("patterns.txt") ;
		while (tree.checkFrequent(minsup, null) > 1)  {
			Date now = new Date();
			//System.out.print("\tcost:"+(now.getTime()-last.getTime())+"\t");
			last  = now;
			//tree.print();
			if (tree.grow() == 0) return;
//			System.out.println("cost "+Apriori.scount+" search ops");
			tree.toFile("tmp"+tree.treeDepth+".txt");//			tree = new Tree("tmp.txt");
			db.scan(tree);
		} ;
		//fw.close();
//		System.out.println();
		Date now = new Date();
//		System.out.println("\tcost:"+(now.getTime()-last.getTime())+"\t");
//		System.out.println("cost "+Apriori.scount+" search ops");

	}
	
	public static void testPruneOrNot(String path, int minsup) throws IOException {
		Apriori.minsup = minsup;
		DataBase db = new DataBase(path);
		Apriori.flag = true;
		System.out.println(path+" minsup="+minsup+" flag="+flag);
		new Apriori(db).run();
		Apriori.flag = false;
		System.out.println(path+" minsup="+minsup+" flag="+flag);
		new Apriori(db).run();
	}
	
	public static void testPruneEff(String path, int minsup) throws IOException {
		System.out.println("file="+path+"\tminsup="+minsup);
		Apriori.minsup = minsup;
		DataBase db = new DataBase(path);
		Apriori.flag = false;
		Apriori.longest = 0;
		Apriori.nfp = 0;
//		System.out.println(path+" minsup="+minsup+" flag="+flag);
		new Apriori(db).run();
		System.out.println("fp count:"+Apriori.nfp+"\tlongest:"+ Apriori.longest);
		System.out.println("cost "+Apriori.scount+" search ops");

	}

	public static void main(String[] args) {
		try {
//			test("f:/share/dataset/T10I4D100K(From IBM Generator).dat", 100);
//			test("f:/share/dataset/T10I4D100K(From IBM Generator).dat", 50);
//			test("f:/share/dataset/T10I4D100K(From IBM Generator).dat", 20);
//			test("f:/share/dataset/T40I10D100K(From IBM Generator).dat", 800);
//			test("f:/share/dataset/T40I10D100K(From IBM Generator).dat", 1000);
//			test("f:/share/dataset/T40I10D100K(From IBM Generator).dat", 1500);
//			test("f:/share/dataset/mushroom.dat", 2000);
//			test("f:/share/dataset/mushroom.dat", 1500);
//			testPruneEff("d:/desktop/mushroom.dat", 500);
			testPruneEff("d:/desktop/mushroom.dat", 2000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
