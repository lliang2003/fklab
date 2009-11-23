package fk;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigNumberTest {

	public void testBigInteger() {
		BigInteger bi = BigInteger.valueOf(2);
		for (int i = 3; i < 50; ++i) {
			bi = bi.multiply(BigInteger.valueOf(i));
			System.out.println(bi);
		}
	}
	
	public void testBigDecimal() {
		BigDecimal bd = BigDecimal.valueOf(5.5555);
		for (int i = 3; i < 50; ++i) {
			bd = bd.multiply(BigDecimal.valueOf(0.33));
			System.out.println(bd);
		}
		for (int i = 3; i < 50; ++i) {
			bd = bd.divide(BigDecimal.valueOf(0.33));
			System.out.println(bd);
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	// TODO Auto-generated method stub
		new BigNumberTest().testBigInteger();
		new BigNumberTest().testBigDecimal();
	}

}
