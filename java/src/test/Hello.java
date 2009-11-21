package test;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

public class Hello {
    static Logger logger = Logger.getLogger(Hello.class);

    public static void main(String[] args){
        //BasicConfigurator.configure();

        logger.info("Fuck the world.");
        World w = new World();
        w.fuck();
        logger.info("Done.");

        System.out.println(System.getProperty("fk"));
    }
}
