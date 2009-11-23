package fk;
import java.io.*;
import java.net.*;
import java.util.*;

public class NetworkTest {
	
	static void testSocket() throws UnknownHostException, IOException {
		Socket s = new Socket("http://www.cppreference.com", 80);
		URL url = new URL("http://www.cppreference.com");
		InputStream is = s.getInputStream();
		//InputStream is = url.openStream();
		Scanner in = new Scanner(is);
		while(in.hasNextLine()) {
			System.out.println(in.nextLine());
		}
		System.out.println("over");
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
	// TODO Auto-generated method stub
		testSocket();
	}

}
