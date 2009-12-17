import java.util.*;

public class Main3278 {
	public static int calc(HashMap<Integer, Integer> hm, int start, int dest) {
		if (hm.containsKey(dest)) 
			return hm.get(dest);
		if (dest <= start) 
			return start - dest;
		int min = dest - start;
		if (dest % 2 == 0) {
			int t = calc(hm, start, dest / 2) + 1;
			if (min > t) min = t;
		} else if (dest > 1){
			int a = calc(hm, start, dest / 2) + 2;
			int b = calc(hm, start, (dest+1) / 2) + 2;
			if (min > a) min = a;
			if (min > b) min = b;
		}
		if (start * 2 > dest) {
			int t = start * 2 - dest + 1;
			if (min > t) min = t;
		}
		hm.put(dest, min);
		//System.out.printf("%d %d %d\n", start, dest, min);
		return min;
	}
	/**
	 * @param args 
	 */
	public static void main(String[] args) {
	// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		int a = sc.nextInt();
		int b = sc.nextInt();
		if (a >= b) {
			System.out.println(a - b);
			return;
		}
		 
		HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();

		System.out.println(calc(hm, a, b));

	}
}
