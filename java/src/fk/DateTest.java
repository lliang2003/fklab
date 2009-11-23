package fk;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateTest{
	void testDate() {
		Date d1 = new Date();
		System.out.printf("Current Date(%d) : %s\n", d1.getTime(), d1.toString());
		Date d2 = new Date();
		System.out.printf("Current Date(%d) : %s\n", d2.getTime(), d2.toString());
		System.out.printf("d1 before d2 ? %b\n", d1.before(d2));
		System.out.printf("d1 after d2 ? %b\n", d1.after(d2));
	}
	
	void testCalendar() {
		System.out.println(Calendar.APRIL);
		System.out.println(Calendar.AUGUST);
		Calendar cal = Calendar.getInstance();
		System.out.printf("TimeZone : %s\n", cal.getTimeZone().toString());
		System.out.printf("Calendar : %s\n", cal.getTime());
		cal.add(Calendar.DAY_OF_YEAR, 3);
		System.out.printf("Calendar : %s\n", cal.getTime());
		cal.set(Calendar.HOUR_OF_DAY, 4);
		System.out.printf("Calendar : %s\n", cal.getTime());

		Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
		System.out.printf("Calendar2 : %s\n", cal2.getTime());
	}
	
	void testGregorianCalendar() {
		GregorianCalendar gc = new GregorianCalendar(1985, 8, 17, 20, 15, 15);
		System.out.println(gc.getTime());
		System.out.printf("%d %d %d %n", gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DATE));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	// TODO Auto-generated method stub
		try {
			DateTest dt = new DateTest();
			dt.testDate();
			dt.testCalendar();
			dt.testGregorianCalendar();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
