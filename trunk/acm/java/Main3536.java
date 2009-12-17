import java.util.*;

public class Main3536 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	// TODO Auto-generated method stub
		
		Scanner sc = new Scanner(System.in);
		
		int n = sc.nextInt();
		
		ArrayList<Integer>	a = new ArrayList<Integer>();
		
		for (int i = 2; i <= n; ++i) {
			while (n % i == 0) {
				a.add(i);
				n /= i;
			}
		}
		
		for (int i = 0; i < a.size(); ++i)
			System.out.println(a.get(i));

	}

}
