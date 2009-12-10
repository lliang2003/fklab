package apriori;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DataBase {
	public List<ItemSet> transactions = new ArrayList<ItemSet>();

	public DataBase(String filename) throws FileNotFoundException{
		Scanner sc = new Scanner(new FileInputStream(filename));
		
		String line = null;
		while (true) {
			try {
				line = sc.nextLine();
			} catch (Exception  e) {
				break;
			}
			String[] tokens = line.split(" +");
			Arrays.sort(tokens);
			transactions.add(new ItemSet(tokens));
		}
	}

	public void scan(Tree tree) {
		for (ItemSet transaction : transactions) {
			tree.scan(transaction);
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		new DataBase("f:/data/aers.dat");

	}

}
