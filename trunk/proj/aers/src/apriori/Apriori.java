package apriori;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.FileWriter;

public class Apriori {

	public DataBase db;
	public List<ItemSet> fitemsets = new ArrayList<ItemSet>();
	public final static int minsup = 8;
	public Tree tree = new Tree();

	public Apriori(DataBase db) throws IOException {
		this.db = db;
		tree.init(db, minsup);
		//tree.toFile("tmp.txt");		tree = new Tree("tmp.txt");
	}

	public void run() throws IOException {
		FileWriter fw = new FileWriter("patterns.txt") ;
		while (tree.checkFrequent(minsup, fw) > 1)  {
			//tree.print();
			if (tree.grow() == 0) return;
			//tree.toFile("tmp.txt");			tree = new Tree("tmp.txt");
			db.scan(tree);
		} ;
		//fw.close();
	}

	public static void main(String[] args) {
		try {
			DataBase db = new DataBase("f:/data/aers.dat");
			Apriori apr = new Apriori(db);
			System.out.println(new Date());
			apr.run();
			System.out.println(new Date());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
