package fk;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Formatter;

public class FormatterTest {
	
	static void log(String f, Object... args) {
		System.out.println(args.getClass().getName());
		System.out.printf(f, args);
	}
	
	void testPrintf() {
		System.out.println("Test Printf ...");
		String f = "format(%s %d %<#x %<#o %(5.2f %e ): %n %% %n";
		System.out.printf(f, f, 45, -4875.1234, 456.78);
		System.out.printf("%+d %+f %,d %#f %c %n", 4, 5.0, 666666, 8.0, 'c');
		System.out.printf("%2$d %1$d %n", 1, 2);
		System.out.printf("%tc | %1$tF | %1$tD | %1$tT | %1$tr %n", new Date());
		System.out.printf("%1$tY | %1$tB | %1$tA | %1$tH | %1$tM | %1$tS %n", new Date());
		System.out.printf("%1$ty | %1$tm | %1$td | %1$tZ | %1$ts  %n ", new Date());
		System.out.println("Test Printf over.");
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	// TODO Auto-generated method stub
		System.out.println("hello");
		log("hello%d%f\n", 4, 6.5);
		int []a = {1,2,3,4,5};
		Object []obj = new Object[3];
		obj[0] = new Integer(1);
		obj[1] = new Integer(1);
		obj[2] = new Integer(1);
		System.out.println(obj.getClass().getName());
		System.out.println(a.getClass().getName());
		System.out.printf("%d %d %d\n", obj);		
		//System.out.printf("%d %d %d\n", a);
		System.out.println(4);
		Formatter f = new Formatter();
		System.out.println(f.format("%d %<d", 1));
		System.out.println(NumberFormat.getCurrencyInstance().format(5.5));
		new FormatterTest().testPrintf();
		
	}
}
