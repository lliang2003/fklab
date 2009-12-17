import java.util.*;
public class Main3579 {
	
	static int countle(int[] num, int index, int pivot) {
		int begin = index + 1;
		int end = num.length;
		while (begin < end) {
			int mid = (begin + end) / 2;
			if (num[mid] - num[index] <= pivot) {
				begin = mid + 1;				
			} else {
				end = mid;
			}
		}
		System.out.printf("countle: %d %d %d\n", index, pivot, begin - index - 1);
		return begin - index - 1;
	}

	static int count(int[] num, int pivot) {
		int cnt = 0;
		for (int i = 0; i < num.length - 1; ++i)
			cnt += countle(num, i, pivot);
		return cnt;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		while(sc.hasNext()) {
			int n = sc.nextInt();
			int[] num = new int[n];
			for (int i = 0; i < n; ++i)
				num[i] = sc.nextInt();
			Arrays.sort(num);
			int maxdiff = num[n-1] - num[0];
			
			int begin = 0; 
			int end = maxdiff + 1;
			while (begin < end) {
				int mid = (begin + end) /2;
				System.out.printf("%d %d %d\n", begin, end, count(num, mid));
				if (count(num, mid) < (n * (n-1) / 2 + 1) / 2)
					begin = mid + 1;
				else
					end = mid;
			}
			System.out.println(begin);
		}
		
	}

}
