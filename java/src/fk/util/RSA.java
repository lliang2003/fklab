package fk.util;
import java.util.*;

public class RSA
{
	public int gcd(int a, int b)
	{
		if (a <=1 || b <= 1)
			return 1;
		if (a % b == 0)
			return b;
		else if (b % a == 0)
			return a;
		if (a < b)
			return gcd(a, b % a);
		else
			return gcd(a % b, b);
	}

	public void calc()
	{
		System.out.println("Please Input tow primes:");
		Scanner sc = new Scanner(System.in);
		int p = sc.nextInt();
		int q = sc.nextInt();
		int n = p*q;
		int s = (p-1)*(q-1);
		int e, d;
		for (e = 2; e < s; ++e)
			if (gcd(e, s) == 1) break;
		
		e = 31;
		for (d = 2; ; ++d)
			if (d * e % s == 1) break;
		
		System.out.println("p\tq\tn\ts\te\td");
	
		System.out.printf("%d\t%d\t%d\t%d\t%d\t%d\n", p, q, n, s, e, d);
		
		System.out.println("Please Input M:");
		 
		int m = sc.nextInt();
		
		long c = Math.round(Math.pow(m, e)) % n;
		
		long mm = Math.round(Math.pow(c, d)) %n;
		
		System.out.printf("After encrypt: %d\n", c);
		
		System.out.printf("After decrypt: %d\n", mm);
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		System.out.println(new RSA().gcd(12, 84));
		new RSA().calc();
	}

}
