package util;

public class Util {

	public static void println(Object o) {
		System.out.println(o);
	}

	public static void print(Object o) {
		System.out.print(o);
	}
	
	public static void println() {
		System.out.println();
	}
	
	public static String join(Iterable<? extends Object> list, String delimeter) {
		StringBuilder sb = new StringBuilder();
		for (Object item : list)
			sb.append(item + delimeter);
		return sb.toString();
	}
	
	public static String join(int[] list, String delimeter) {
		StringBuilder sb = new StringBuilder();
		for (Object item : list)
			sb.append(item + delimeter);
		return sb.toString();
	}
}
