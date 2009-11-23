package fk;

import java.util.Arrays;
import java.util.HashSet;

public class BasicTest {
	void testDataType() {
		System.out.printf("Integer : %d, %d, %d.\n", Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.SIZE);
		System.out.printf("Short : %d, %d, %d.\n", Short.MIN_VALUE, Short.MAX_VALUE, Short.SIZE);
		System.out.printf("Long : %d, %d, %d.\n", Long.MIN_VALUE, Long.MAX_VALUE, Long.SIZE);
		System.out.printf("Byte : %d, %d, %d.\n", Byte.MIN_VALUE, Byte.MAX_VALUE, Byte.SIZE);
		System.out.printf("Float : %f, %f, %d.\n", Float.MIN_VALUE, Float.MAX_VALUE, Float.SIZE);
		System.out.printf("Double : %f, %f, %d.\n", Double.MIN_VALUE, Double.MAX_VALUE, Double.SIZE);

		final int fi = 45;
		System.out.println(fi);
	}
	
	void testOperator() {
		System.out.println(1>2 || 2<3 && 1<3);
		System.out.println(1<2 || 2>3 && 1>3);
		System.out.println(1<2 && 2<3 || 1>3);
		System.out.println(1<<3);
		System.out.println(1&3);
	}

	public void testChar() {
		String greeting = "轩辕皇帝hello";
		int nn = greeting.length();
		int cpCount = greeting.codePointCount(0, greeting.length());
		System.out.println(cpCount);
		System.out.println(nn);
		System.out.println(greeting);

		String s = "is the set of integer";
		char ch = s.charAt(1);
		System.out.println(ch);
		
		String sub = s.substring(2, 9);
		System.out.println(sub);
		System.out.println(sub + s.substring(9));
		System.out.println("A".equals("a"));
		System.out.println("A".equalsIgnoreCase("a"));
		System.out.println("AA".equals("A" + "A"));//...
		System.out.println("AA" == "A" + "A");//...
	}
	
	public void testArray() {
		System.out.println("Test Array ...");
		int[] a = new int[10];
		for (int i = 0; i < 10; ++i) a[i] = i;
		for (int i : a) System.out.printf("%d ", i);
		System.out.println();
		int[] b = {3, 4, 5, 6, 7};
		for (int i : b) System.out.printf("%d ", i);
		System.out.println();
		System.arraycopy(a, 0, b, 2, 3);
		for (int i : b) System.out.printf("%d ", i);
		System.out.println();
		Arrays.sort(b);
		for (int i : b) System.out.printf("%d ", i);
		System.out.println();
		int[] c = new int[0];
		System.out.printf("%d %d %d %n", a.length, b.length, c.length);
		System.out.println(Arrays.binarySearch(a, 5));
		System.out.println(Arrays.binarySearch(a, 12));
		System.out.println(Arrays.binarySearch(a, -1));		
		Arrays.fill(a, 4);
		for (int i : a) System.out.printf("%d ", i);
		System.out.println();
		
		int[][] aa = new int[][]{{1, 2, 3}, {4, 5, 6}};
		System.out.println(aa.length);
		System.out.println(aa[0].length);
		int[][] bb = new int[3][];
		for (int i = 0; i < 3; ++i)
			bb[i] = new int[i+1];
		for (int[] ia: bb)
			System.out.println(ia.length);
		System.out.println();
		System.out.println("Test Array over. ");
	}

	enum Size {
		SMALL("S"), MEDIUM("M"), LARGE("L");
		
		private Size(String abbr) {
			this.abbr = abbr;
		}
		
		public String abbr() {
			return abbr;
		}
		
		private String abbr;
	}
	
	
	void testEnum() {
		System.out.println("\nTest Enum ...");
		
		Size l = Size.LARGE;
		Size s = Enum.valueOf(Size.class, "SMALL");
		Size m = Enum.valueOf(Size.class, "MEDIUM");
		System.out.println(l.toString() + " " + s.toString() + " " + m.toString());
		Size[] ss = Size.values();
		for (Size o: ss) {
			System.out.println(o.abbr() + " " + o);
		}
		
		System.out.println("Test Enum over.");
	}
	
	void testException() {
		System.out.println("\nTest Exception ...");
	
		try {
			System.out.println("In try block");
			throw new Exception();
		} catch (Exception e) {
			System.out.println("In catch block");
			//throw new RuntimeException("CATCH");
		} finally {
			System.out.println("In finally block");
			//throw new RuntimeException("FINALLY");
		}
		System.out.println("Test Exception over.");
	}
	
	static class Pair<T, U> {
		public Pair() { first = null; second = null; }
		public Pair(T first, U second) {
			this.first = first;
			this.second = second;
		}
		public static<V> V third(V a){
			return a;
		}
		public T first() { return first; }
		public U second() { return second; }
		public void setFirst(T f) { first = f; }
		public void setSecond(U s) { second = s; }
		private T first;
		private U second;
	}
	
	void testPair() {
		Pair p = new Pair();
		Pair pp = new Pair(2, "ok");
		System.out.println(pp.first() + " " + pp.second());
		System.out.println(pp.third(4));
		Pair[] ps = new Pair[10]	;
		ps[0] = new Pair(2, "ok");
		Pair<? extends Comparable, Double> p1 = new Pair(2, "ok");
		Pair<? super Comparable, Double> p2 = new Pair(2, "ok");
		p2.setFirst(5);
		System.out.println(p2.first());
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	// TODO Auto-generated method stub
		
		assert 1 < 2;
		//assert 2 > 3: "fuck";
		
		BasicTest bt = new BasicTest();
		bt.testChar();
		bt.testDataType();
		bt.testOperator();
		bt.testArray();
		bt.testEnum();
		bt.testException();
		bt.testPair();
		
		HashSet<String> hs = new HashSet<String>();
		String ss = "hello";
		hs.add(ss);
		hs.add("joke");
		hs.add("world");
		System.out.println(hs);
		String s = "hello";
		System.out.println(hs.contains(s));
	}

}
