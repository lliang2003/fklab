

import java.util.*;

public class POJ3579 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		while (sc.hasNextInt()) {
			int n = sc.nextInt();
			int[] num = new int[n]; 
			int[] diffs = new int[n * (n-1) / 2];
			for (int i = 0; i < n; ++i)
				num[i] = sc.nextInt();
			
			int p = 0;
			for (int i = 0; i < n; ++i) {
				for (int j = i + 1; j < n; ++j) {
					diffs[p++] = Math.abs(num[i] - num[j]);
				}
			}
			
			Arrays.sort(diffs);
			
			System.out.println(diffs[(diffs.length-1) / 2]);
		}

	}

}
