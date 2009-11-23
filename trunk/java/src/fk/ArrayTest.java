package fk;

import java.util.ArrayList;
import java.util.Arrays;

public class ArrayTest {
	
	public void testArray() {
		System.out.println("Test Array ...");
		int[] a = new int[]{5,4,3,1,2};
		System.out.printf("Array: %s.\n", a.toString());
		System.out.println(a.toString());
		System.out.println("Test Array over.");
	}
	
	public void  testArrayList() {
		System.out.println("Test ArrayList ...");
		
		ArrayList<Integer> al = new ArrayList<Integer>();
		al.ensureCapacity(100);
		System.out.println(al.size());
		for (int i = 0; i < 10; ++i) al.add(i);
		System.out.println(al.size());
		al.trimToSize();
		fk.util.out.printArray(al);
		
		ArrayList<Integer>	ai = new ArrayList<Integer>();
		for (int i = 0; i < 10; ++i) ai.add(i * i);
		fk.util.out.printArray(ai);
		ai.addAll(al);
		fk.util.out.printArray(ai);
		
		al.addAll(ai);
		fk.util.out.printArray(al);
		
		Integer[] a = new Integer[10];
		a = ai.toArray(a);
		System.out.println(a.length);
		fk.util.out.printArray(a);
		Arrays.sort(a);
		fk.util.out.printArray(a);
		
		System.out.println("Test ArrayList over.");
	}

	@SuppressWarnings("unchecked")
	void testClone() {
		ArrayList<Integer> a = new ArrayList<Integer>();
		a.add(new Integer(4));
		ArrayList<Integer> b = (ArrayList<Integer>)a.clone();
		System.out.println(a.get(0) == b.get(0));
	}
	
	public static void main(String[] args) {
		ArrayTest at = new ArrayTest();
		at.testArray();
		at.testArrayList();
		at.testClone();
		
	}

}
