

import java.util.*;

public class Main3425 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		Map<Integer, Integer> hm = new HashMap<Integer, Integer>();
		int sum = 0;
		for (int i = 0; i < n; ++i) {
			int id = sc.nextInt();
			int a = sc.nextInt();
			int x = sc.nextInt();
			if (a == 1) {
				if (hm.containsKey(id)) {
					sum += 10 * hm.get(id);
					hm.put(id, hm.get(id) + 1);
				} else {
					hm.put(id, 1);
				}
				if (x == 1)
					sum += 40;
				else 
					sum += 20;
			} else {
				sum += 10;
			}
		}
		System.out.println(sum);
	}

}
