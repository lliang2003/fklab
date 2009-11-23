package fk.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;


public class Prime {
	public static ArrayList<Integer> generate(int n) {
		ArrayList<Integer> primes = new ArrayList<Integer>();
		for (int i = 2; i < n; ++i) {
			boolean flag = true;
			long k = (Math.round(Math.sqrt(i))) + 1;
			for (int j = 2; j < k; ++j) {
				if (i % j == 0) {
					flag = false;
					break;
				}
			}
			if (flag) primes.add(i);
		}
		return primes;
	}

	public static ArrayList<Integer> generate2(int n) {
		ArrayList<Integer> primes = new ArrayList<Integer>();
		for (int i = 2; i < n; ++i) {
			boolean flag = true;
			for (int j = 0; j < primes.size(); ++j) {
				if (i % primes.get(j) == 0) {
					flag = false;
					break;
				}
			}
			if (flag) primes.add(i);
		}
		return primes;
	}

	public static ArrayList<Integer> generate21(int n) {
		ArrayList<Integer> primes = new ArrayList<Integer>();
		int[] p = new int[n];
		int np = 0;
		for (int i = 2; i < n; ++i) {
			boolean flag = true;
			for (int j = 0; j < np; ++j) {
				if (i % p[j] == 0) {
					flag = false;
					break;
				}
			}
			if (flag) {
				p[np++] = i;
				primes.add(i);
			}
		}
		return primes;
	}
	
	public static ArrayList<Integer> generate3(int n) {
		ArrayList<Integer> primes = new ArrayList<Integer>();
		boolean[] beprime = new boolean[n];
		Arrays.fill(beprime, true);
		for (int i = 2; i * i <= n; ++i) {
			if (!beprime[i]) continue;
			for (int j = i * 2; j < n; j += i)
				beprime[j] = false;
		}
		//for (int i = 2; i < n; ++i)
		//	if (beprime[i]) primes.add(i);
		return primes;
	}
	
	public static ArrayList<Integer> generate4(int n) {
		BitSet b = new BitSet(n);
		b.set(2, n + 1);
		for (int i = 2; i * i <= n; ++i) {
			if (!b.get(i)) continue;
			for (int j = i * 2; j < n; j += i)
				b.clear(j);
		}
		ArrayList<Integer> primes = new ArrayList<Integer>();
		for (int i = 2; i < n; ++i) 
			if (b.get(i)) primes.add(i);
		return primes;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final int num = 100000;

		Timer t = new Timer();
		ArrayList<Integer> r = generate(num);
		System.out.println("Testing generate() ...");
		System.out.printf("Cost %d miliseconds generating %d primes.\n", t.elapsed(), r.size());
		//System.out.println(r.toString());
	
		t.restart();
		System.out.println("Testing generate2() ...");
		r = generate2(num);
		System.out.printf("Cost %d miliseconds generating %d primes.\n", t.elapsed(), r.size());

		t.restart();
		System.out.println("Testing generate21() ...");
		r = generate21(num);
		System.out.printf("Cost %d miliseconds generating %d primes.\n", t.elapsed(), r.size());
		//System.out.println(r.toString());
		
		t.restart();
		System.out.println("Testing generate3() ...");
		r = generate3(num*100);
		System.out.printf("Cost %d miliseconds generating %d primes.\n", t.elapsed(), r.size());
		//System.out.println(r.toString());

		t.restart();
		System.out.println("Testing generate4() ...");
		r = generate4(num*10);
		System.out.printf("Cost %d miliseconds generating %d primes.\n", t.elapsed(), r.size());
	}

}
