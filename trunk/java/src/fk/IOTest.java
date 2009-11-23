package fk;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class IOTest {
	
	static void testStream() throws IOException {
		System.out.println("Test Stream ...");
		OutputStream os = new FileOutputStream("test.txt");
		os.write("fuck".getBytes());
		os.flush();
		os.write(" the world\n".getBytes());
		os.write("12345 this is a joke.\n".getBytes());
		
		DataOutputStream dos = new DataOutputStream(os);
		dos.writeInt(4);
		dos.writeDouble(5.5);
		dos.writeUTF("hello world");
		System.out.println("DataOutputStream size: " + dos.size());
		dos.close();
		
		InputStream is = new FileInputStream("test.txt");
		System.out.println("Avaiable: " + is.available());
		byte[] b = new byte[37];
		int r = is.read(b);
		System.out.println("Read: " + r);
		System.out.println(new String(b));

		//is = new FileInputStream("test.txt");
		DataInputStream dis = new DataInputStream(is);
		System.out.println("Avaiable: " + is.available());
		System.out.println(dis.readInt());
		System.out.println(dis.readDouble());
		System.out.println(dis.readUTF());
		dis.close();
		System.out.println("Test Stream over.");
	}
	
	static void testRandomAccessFile() throws IOException {
		System.out.println("\nTest testRandomAccessFile ....");
		RandomAccessFile ras = new RandomAccessFile("test.txt", "rw");
		System.out.println("Length: " + ras.length());
		System.out.println("Pos: " + ras.getFilePointer());
		ras.seek(37);
		System.out.println("Pos: " + ras.getFilePointer());
		System.out.println(ras.readInt());
		System.out.println(ras.readDouble());
		System.out.println(ras.readUTF());
		ras.writeUTF("joke");
		ras.seek(37);
		ras.writeInt(1234);
		ras.seek(37);
		System.out.println("Pos: " + ras.getFilePointer());
		System.out.println(ras.readInt());
		System.out.println(ras.readDouble());
		System.out.println(ras.readUTF());
		System.out.println("Length: " + ras.length());
		System.out.println("Test RandomAccessFile over.");
	}
	
	static void testReaderWriter() throws IOException {
		System.out.println("\nTest ReaderWriter ...");
		FileWriter fw = new FileWriter("test.txt");
		fw.write("hello world\n");
		PrintWriter pw = new PrintWriter(fw);
		pw.printf("fuck the %f %s\n", 9.9, "world");
		pw.close();
		FileReader fr = new FileReader("test.txt");
		char[] c = new char[100];
		fr.read(c);
		System.out.println(c);
		System.out.println("Test ReaderWriter over.");
	}
	
	static void testZip() throws IOException {
		System.out.println("\nTest ZipInputStream ...");
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream("IOTest.zip"));
		zos.putNextEntry(new ZipEntry("file1"));
		zos.write("hello\nworld".getBytes());
		zos.closeEntry();
		zos.putNextEntry(new ZipEntry("dir/file2"));
		zos.write("hello\nworld2".getBytes());
		zos.closeEntry();
		zos.close();
				
		ZipInputStream zis = new ZipInputStream(new FileInputStream("IOTest.zip"));
		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null) {
			System.out.println("Entry : " + entry.getName());
			BufferedReader br = new BufferedReader(new InputStreamReader(zis));
			String line;
			while ((line = br.readLine()) != null)
				System.out.println(line);
		}
		System.out.println("Test ZipInputStream over.");
	}
	
	static void testToken() throws IOException {
		System.out.println("\nTest Token ...");
		PrintWriter pw = new PrintWriter(new FileWriter("TokenTest.txt"));
		pw.println("kai fan|1985,8,17|male");
		pw.println("kk fan|1985,7,2|female");
		pw.close();
		
		BufferedReader br = new BufferedReader(new FileReader("TokenTest.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, "|, ");
			while (st.hasMoreTokens()) {
				System.out.println(st.nextToken());
			}
		}
		
		System.out.println("Test Token over.");
	}
	
	static class OST implements Serializable {
		OST(int i, double d, String s) {
			this.i = i;
			this.d = d;
			this.s = s;
		}
		int i;
		double d;
		String s;
	}
	
	static void testObjectStream() throws FileNotFoundException, IOException, ClassNotFoundException {
		System.out.println("\nTest ObjectStream ...");
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ObjectTest.txt"));
		oos.writeObject(new OST(3, 4.4, "obj"));
		oos.writeObject(new OST(6, 2.1, "obj2"));
		oos.close();
		
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("ObjectTest.txt"));
		OST obj1 = (OST)ois.readObject();
		OST obj2 = (OST)ois.readObject();
		System.out.println(obj1.i + " " + obj1.d + " " + obj1.s);
		System.out.println(obj2.i + " " + obj2.d + " " + obj2.s);
		System.out.println("Test ObjectStream over.");
	
	}
	
	static void testNIO() throws IOException {
		System.out.println("\nTest New IO ...");
		FileInputStream fis = new FileInputStream("TokenTest.txt");
		FileChannel channel = fis.getChannel();
		MappedByteBuffer mbb = channel.map(MapMode.READ_ONLY, 0, fis.available());
		System.out.println(mbb.capacity());
		byte[] b = new byte[mbb.capacity()];
		int i = 0;
		while(mbb.hasRemaining())
			b[i++] = mbb.get();
		//mbb.get(b);
		ByteArrayInputStream bis = new ByteArrayInputStream(b);
		Scanner sc = new Scanner(bis);
		while(sc.hasNextLine())
			System.out.println(sc.nextLine());
		
		System.out.println("Test New IO over.");
	}
	
	static void testFile() throws IOException {
		System.out.println("Test File ...");
		File f = new File("test2.txt");
		System.out.println(f.createNewFile());
		System.out.println(f.canExecute());
		System.out.println(f.canRead());
		System.out.println(f.canWrite());
		System.out.println(f.length());
		System.out.println(f.getAbsolutePath());
		System.out.println(new File("newdir").mkdir());
		System.out.println(f.renameTo(new File("newdir/test.txt.bak")));
		System.out.println(new File("newdir").list().length);
		System.out.println(new File("newdir").renameTo(new File("newdir2")));
		System.out.println(f.delete());
		
		for (File f1: new File("newdir2").listFiles()) {
			System.out.println("Remove " + f1.getAbsolutePath());
			f1.delete();
		}
		System.out.println(new File("newdir2").delete());
		
		System.out.println("Test File over.");
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		testStream();
		testRandomAccessFile();
		testReaderWriter();
		testZip();
		testToken();
		testObjectStream();
		testNIO();
		testFile();
	}

}
