package util;

public class Util {

	public static void p(Object o) {
		System.out.println(o);
	}

	
	public static String join(Iterable<? extends Object> list, String delimeter) {
		StringBuilder sb = new StringBuilder();
		for (Object item : list)
			sb.append(item + delimeter);
		return sb.toString();
	}
}
