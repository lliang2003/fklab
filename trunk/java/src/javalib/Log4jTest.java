package fk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.TTCCLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.xml.XMLLayout;

public class Log4jTest {
	
	static void test() {
		Logger logger = Logger.getLogger("fk");
		Layout sl = new SimpleLayout();
		Appender ca = new ConsoleAppender(sl);
		logger.addAppender(ca);
		logger.setLevel(Level.ALL);
		logger.fatal("fatal message");
		logger.error("error message");
		logger.warn("warn message");
		logger.info("info message");
		logger.debug("debug message");
		logger.trace("trace message");
		try {
			Exception ex = new RuntimeException("mock exception");
			logger.warn("Throw Exception:", ex);
			throw ex;
		} catch (Exception ex) {
			logger.info("Catch Exception:", ex);
		}
		
	}
	
	static void testLayout() {
		System.out.println("\nTest Layout ...");
		Logger logger = Logger.getLogger("fk");
		logger.removeAllAppenders();
		Layout lt = new TTCCLayout();
		Appender ca = new ConsoleAppender(lt);
		logger.addAppender(ca);
		logger.info("Using TTCCLayout");
		
		Layout lh = new HTMLLayout();
		ca.setLayout(lh);
		logger.info("Using HTMLLayout");
		
		Layout ls = new SimpleLayout();
		ca.setLayout(ls);
		logger.info("Using SimpleLayout");
		
		Layout lx = new XMLLayout();
		ca.setLayout(lx);
		logger.info("Using XMLLayout");
		
		Layout lp = new PatternLayout();
		ca.setLayout(lp);
		logger.info("Using PatternLayout, default pattern");
		String p1 = "%c %C %d %F %l %L %M %p %r %x - %m\n";
		Layout lp1 = new PatternLayout(p1);
		ca.setLayout(lp1);
		logger.info("Using PatternLayout, with pattern : " + p1);
		String p2 = "%d %l %p - %m\n";
		Layout lp2 = new PatternLayout(p2);
		ca.setLayout(lp2);
		logger.info("Using PatternLayout, with pattern : " + p2);
		
		System.out.println("Test Layout over.");
	}
	
	static void testAppender() throws IOException {
		System.out.println("\nTest Appender ...");
		Logger logger = Logger.getLogger("fk");
		logger.removeAllAppenders();
		
		Appender ac = new ConsoleAppender(new SimpleLayout());
		ac.setName("console appender");
		logger.addAppender(ac);
		
		Appender af = new FileAppender(new SimpleLayout(), "f.log");
		af.setName("simple file appender");
		logger.addAppender(af);
		
		RollingFileAppender ar = new RollingFileAppender(new SimpleLayout(), "fr.log", true);
		ar.setName("rolling file appender");
		ar.setMaximumFileSize(100);
		ar.setAppend(false);
		ar.setMaxBackupIndex(3);
		logger.addAppender(ar);
		
		ByteArrayOutputStream ba = new ByteArrayOutputStream();
		Appender aw = new WriterAppender(new SimpleLayout(), ba);
		aw.setName("writer appender");
		logger.addAppender(aw);
		
		Appender ad = new DailyRollingFileAppender(new SimpleLayout(), "fd.log", ".yyyy-MM-dd-HH-mm");
		ad.setName("daily rolling file appender");
		logger.addAppender(ad);
		
		System.out.println("Appenders :");
		Enumeration<?> as = logger.getAllAppenders();
		while (as.hasMoreElements())
			System.out.println(((Appender)as.nextElement()).getName());
		
		
		for (int i = 0; i < 10; ++i)
			logger.info("test appender " + i);
		
		System.out.println("Content of bytearray:");
		System.out.println(ba);
		
		System.out.println("Test Appender over.");
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
	// TODO Auto-generated method stub
		test();
		testLayout();
		testAppender();
	}

}
