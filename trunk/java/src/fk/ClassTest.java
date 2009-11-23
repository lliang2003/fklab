package fk;
import static java.lang.System.out;

class SuperClass {
	public SuperClass() { 
		System.out.println("Construct SuperClass");
	}
	
	public SuperClass(int i) { 
		System.out.println("Construct SuperClass with parameter");
	}
	
	public void func() {
		System.out.println("SuperClass func");
	}
	
	protected int num;
}

class SubClass extends SuperClass {
	SubClass() {
		super(5);
		System.out.println("Construct SubClass");
	}

	SubClass(int i) {
		//super(5);
		System.out.println("Construct SubClass with parameter");
	}
	
	public void func() {
		System.out.println("SubClass func");
		super.func();
		num ++;
	}
	
	public void func(SubClass sc) {
		sc.num ++;
	}
}

class Member {
	public Member(int i) {
		System.out.println("Constructor Member " + i );
		set(i);
	}
	public void set(int i){i_ = i;}
	public int get() {return i_;}
	private int i_;
}

public class  ClassTest{
	
	public ClassTest(int num) {
		this();
		this.num = num;
		System.out.println("In constructor of ClassTest.");
	}
	
	public ClassTest() {
		a = new Integer(0);
		fm1 = new Member(1);
		System.out.println("In default constructor of ClassTest.");
	}
	
	public void finalize() {
		System.out.println("Finalize ClassTest.");
	}
	
	public void testfinal() {
		System.out.println(fm1.get());
		fm1.set(8);
		System.out.println(fm1.get());
	}
	
	public void test() {
		System.out.printf("%d %d %d %d %d %d %d %d %n", fm1.get(), fm2.get(), 
				m3.get(), m4.get(), sm5.get(), sm6.get(), num, a);
	}
	
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null) return false;
		if (this.getClass() != other.getClass()) return false;
		return this.num == ((ClassTest)other).num;
	}
	
	public int hashCode() {
		return num;
	}
	
	public void testInheritence() {
		System.out.println("\nTest Inheritence ...");
		SuperClass[] sc = new SuperClass[3];
		sc[0] = new SuperClass();
		sc[1] = new SubClass();
		sc[2] = new SubClass(5);
		
		System.out.println(sc.getClass().getName());
		for (int i = 0; i < sc.length; ++i) {
			System.out.println(sc[i].getClass().getName());
			sc[i].func();
		}
		System.out.println("Test Inheritence over.");
	}

	{
		System.out.println("In Initialization block.");
		m4 = new Member(4);
	}
	
	static {
		System.out.println("In Static Initialization block.");
		sm6 = new Member(6);
	}
	
	private final Integer a;
	private final Member fm1;
	private final Member fm2 = new Member(2);
	private Member m3 = new Member(3);
	private Member m4;
	private static Member sm5 = new Member(5);
	private static Member sm6;
	private int num;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		out.println("System.out is static imported.");
		ClassTest t = new ClassTest(4);
		ClassTest tt = new ClassTest();
		ClassTest ttt = new ClassTest(4);
		tt.testfinal();
		t.test();
		
		System.out.println(tt.equals(t));
		System.out.println(tt.equals(0));
		System.out.println(t.equals(tt));
		System.out.println(t.equals(ttt));
		System.out.println(ttt.equals(t));
		System.out.println(t == ttt);
		System.out.println(t.toString());
		System.out.println(t.getClass().getName());
		
		t.testInheritence();
		
		
	}
}
