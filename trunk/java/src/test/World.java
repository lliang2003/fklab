package test;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
public class World {
    static Logger logger = Logger.getLogger(World.class);
    public void fuck() {
        //System.out.println("Welcome to world.");
        logger.info("Welcome to world ...");
    }
    public static void main(String[] args){
        BasicConfigurator.configure();
        new World().fuck();
    }
}
