package fk;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class Test {
	public static void main(String[] args) throws FileNotFoundException {
		Set<Integer> sa = new TreeSet<Integer>();
		sa.add(5);
		sa.add(9);
		sa.add(1);
		sa.add(5);
		System.out.println(sa);
		System.out.println(Arrays.toString(new int[]{1,2,3}));
		System.out.println("ok" == "ok");
		Scanner sc = new Scanner(new FileInputStream("f:/data/prr.dat"));
		System.out.println(sc.next());
		System.out.println(sc.next());
		System.out.println(sc.next());
		System.out.println(sc.next());
		System.out.println(sc.next());
		System.out.println(sc.next());
		System.out.println(sc.next());
		System.out.println(sc.nextLine());
		System.out.println(sc.nextLine());
		System.out.println(sc.nextLine());
		System.out.println(sc.nextLine());

	}

}
