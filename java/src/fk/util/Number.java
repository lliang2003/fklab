package fk.util;

public class Number {
	
	public static int maxInt(int... values) {
		int m = Integer.MIN_VALUE;
		for (int i: values)
			m = (m > i ? m : i);
		return m;
	}
	
	public static <T extends Comparable<T>> T max(T... values) {
		T m = values[0];
		for (T v: values) {
			if (v.compareTo(m) > 0)
				m = v;
		}
		return  m;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	// TODO Auto-generated method stub
		System.out.println(maxInt(1,2,5,4));
		System.out.println(maxInt(new int[]{5,3,8}));
		System.out.println(max(new Integer[]{5,3,8}));

	}	

}
