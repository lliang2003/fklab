import java.util.*;

public class Main1171 {

	static public int calc(int[] v, int[] vb, String dict, String s) {
		int sum = 0;
		int[] vc = new int[26];
		for (int i = 0; i < s.length(); ++i) {
			if (++vc[s.charAt(i) - 'a'] > vb[s.charAt(i) - 'a']) return 0;
			sum += v[s.charAt(i) - 'a'];
		}

//		System.out.println(s);
//		System.out.println(sum);
		return sum;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] v = { 2, 5, 4, 4, 1, 6, 5, 5, 1, 7, 6, 3, 5, 2, 3, 5, 7, 2, 1, 2, 4, 6, 6, 7, 5, 7 };
		int[] vb = new int[26];
		Scanner sc = new Scanner(System.in);
		String dict = sc.next();
		for (int i = 0; i < dict.length(); ++i)
			vb[dict.charAt(i) - 'a']++;
		ArrayList<String> as3 = new ArrayList<String>();
		ArrayList<String> as4 = new ArrayList<String>();
		int max = 0;
		loop: while (true) {
			String s = sc.next();
			if (s.equals("."))
				break;
			for (int i = 0; i < s.length(); ++i)
				if (vb[s.charAt(i) - 'a'] == 0)
					continue loop;
			if (s.length() == 3) 
				as3.add(s);
			else if (s.length() == 4) 
				as4.add(s);
			int sum = calc(v, vb, dict, s);
			max = max > sum ? max : sum;
		}
		
		for (int i = 0; i < as3.size(); ++i) {
			for (int j = i; j < as3.size(); ++j) {
				int sum = calc(v, vb, dict, as3.get(i) + as3.get(j));
				max = max > sum ? max : sum;
			}
			for (int j = 0; j < as4.size(); ++j) {
				int sum = calc(v, vb, dict, as3.get(i) + as4.get(j));
				max = max > sum ? max : sum;
			}
		}
		System.out.println(max);
	}

}
