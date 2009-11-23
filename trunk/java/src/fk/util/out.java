package fk.util;

import java.util.ArrayList;

public class out {

	public static void printArray(Object[] a) {
		for (Object o: a)
			System.out.print(o + " ");
		System.out.println();
	}
	
	public static void printArray(ArrayList a) {
		printArray(a.toArray());
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	// TODO Auto-generated method stub

	}

}
