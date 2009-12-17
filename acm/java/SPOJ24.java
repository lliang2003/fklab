import java.util.*;
import java.math.*;
public class SPOJ24 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		
		for (int i = 0; i < n; ++i) {
			int j = sc.nextInt();
			BigInteger bi = new BigInteger("" + j);
			for (int k = 1; k < j; ++k)
				bi  = bi.multiply(new BigInteger("" + k));
			System.out.println(bi);
		}

	}

}
