package fk;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;



public class LogTest {
	
	static class MyFormat extends Formatter {

		@Override
		public String format(LogRecord record) {
			// TODO Auto-generated method stub
			return record.getLoggerName() + " " + record.getLevel() + " " 
			+ record.getMessage() + "\n";
		}
		
	}
	
	static void log() {
		Logger logger = Logger.getLogger("testlog");
		logger.severe("test log");
		logger.warning("test log");
		logger.info("test log");
		logger.config("test log");
		logger.fine("test log");
		logger.finer("test log");
		logger.finest("test log");
	}
	
	static void testLog() throws SecurityException, IOException {
		System.out.println("Test Log ...");
		Logger logger = Logger.getLogger("testlog");
		
		logger.severe("Name = " + logger.getName() + " Level = " + logger.getLevel()
				+ " Parent = " + logger.getParent().getName());
		log();
		
		logger.setLevel(Level.FINE);
		logger.severe("Name = " + logger.getName() + " Level = " + logger.getLevel());
		Handler handler = new ConsoleHandler();
		handler.setLevel(Level.FINEST);
		logger.severe("ConsoleHandler level = " + handler.getLevel());
		logger.addHandler(handler);
		log();
		logger.removeHandler(handler);
		while(logger.getHandlers().length > 0)
			logger.removeHandler(logger.getHandlers()[0]);
		
		handler = new FileHandler("%h/fktest.log");
		Formatter f = new MyFormat();
		handler.setFormatter(f);
		logger.addHandler(handler);

		logger.setLevel(Level.OFF);
		logger.severe("Name = " + logger.getName() + " Level = " + logger.getLevel());
		logger.setLevel(Level.ALL);
		logger.severe("Name = " + logger.getName() + " Level = " + logger.getLevel());
		
		
		logger.entering("LogTest", "testLog2");
		logger.exiting("LogTest", "testLog2");
		
		try {
			RuntimeException ex = new RuntimeException("fuck");
			logger.throwing("fk.LogTest", "testLog", ex);
			throw ex;
		} catch (Exception ex) {
			logger.log(Level.WARNING, "catch exception", ex);
		}
		
		logger.log(Level.INFO, "hello", "world");
		logger.log(Level.INFO, "hello log", new Object[]{"1", "2", "joke", 6});
		logger.logp(Level.INFO, "MockLogTest", "MockTestLog", "hello logp");
		
		
		System.out.println("Test Log over.");
	}
	
	public static void main(String[] args) throws SecurityException, IOException {
		testLog();
	}


}
