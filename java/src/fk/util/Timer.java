package fk.util;
import java.util.Date;

public class Timer {
	
	private long tstart;
	
	public Timer() {
		restart();
	}
	
	public void restart() {
		tstart = new Date().getTime();
	}
	
	public long elapsed() {
		return new Date().getTime() - tstart;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	// TODO Auto-generated method stub
		try {
			Timer t = new Timer();
			System.out.println(t.elapsed());
			Thread.sleep(100);
			System.out.println(t.elapsed());
			t.restart();
			System.out.println(t.elapsed());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
