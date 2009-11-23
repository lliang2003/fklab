package fk;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class CollectionTest {
	static class MC {
		HashSet<Integer> hs = new HashSet<Integer>();
	}
	
	static void testHashMap() {
		System.out.println("Test HashMap ...");
		HashMap<String, String> hmii = new HashMap<String, String>();
		String s = "a";
		hmii.put("1", s);
		hmii.put("2", "b");
		System.out.println(hmii.get("1"));
		String ss = hmii.get("1");
		ss = "aa";
		System.out.println(hmii.get("1") + " " + ss);
		//Iterator<String> iter = hmii.values().iterator();
		//hmii.put("3", "c");
		//while (iter.hasNext())	System.out.println(iter.next());
		
		HashMap<Integer, MC> hm = new HashMap<Integer, MC>();
		MC m = new MC();
		hm.put(1, m);
		MC a = hm.get(1);
		MC b = hm.get(1);
		System.out.println(a == b);
		System.out.println(a == m);
		m.hs.add(1);
		MC c = hm.get(1);
		System.out.println(c == m);
		System.out.println("Test HashMap over.");
	}
	
	static void testLinkedList() {
		System.out.println("\nTest LinkedList ...");
		LinkedList<Integer> list = new LinkedList<Integer>();
		list.add(1);
		list.add(3);
		list.add(0, 4);
		list.addLast(-1);
		ListIterator<Integer> iter = list.listIterator();
		iter.add(5);
		iter.add(2);
		while(iter.hasNext())
			System.out.print(iter.next() + " ");
		System.out.println();
		iter.add(8);
		while (iter.hasPrevious())
			System.out.print(iter.previousIndex() + "," + iter.previous() + "  ");
		System.out.println(iter.previousIndex());
		iter.remove();
		//iter.remove();
		iter.next();
		iter.set(99);
		while (iter.hasNext())
			System.out.print(iter.nextIndex() + "," + iter.next() + "  ");
		System.out.println();
		System.out.println(list);
		list.addAll(list);
		System.out.println(list);
		System.out.println("First = " + list.getFirst());
		System.out.println(list.contains(4));
		System.out.println(list.indexOf(4));
		System.out.println(list.lastIndexOf(4));
		System.out.println("Test LinkedList over.");
	}
	
	static void testArrayList() {
		Collection<MC> c = new ArrayList<MC>();
		c.add(new MC());
		c.add(new MC());
		MC[] ia = new MC[2];
		MC[] ib = new MC[2];
		System.out.println(c.toArray(ia).length);
		c.toArray(ib);
		System.out.println(ia.length);
		System.out.println(ia[0] == ia[1]);
		System.out.println(ib[0] == ia[0]);
		Collection<MC> cm= new ArrayList<MC>();
		MC m = new MC();
		m.hs.add(1);
		cm.add(m);
		Object[] ma = cm.toArray();
		System.out.println(ma.length);
		System.out.println(m == ma[0]);
		System.out.println(((MC)ma[0]).hs.contains(1));
		m.hs.remove(1);
		System.out.println(m.hs.contains(1));
		System.out.println(((MC)ma[0]).hs.contains(1));
		System.out.println(((MC)cm.toArray()[0]).hs.contains(1));

	}
	
	static void testQueue() {
		//Queue<Integer> q = new ArrayBlockingQueue<Integer>(3);
	}
	
	static void testPriorityQueue() {
		System.out.println("\nTest PriorityQueue ...");
		Queue<Integer> q = new PriorityQueue<Integer>();
		q.add(4);
		q.add(5);
		q.add(0);
		Iterator<Integer> iter = q.iterator();
		while (iter.hasNext())
			System.out.print(iter.next() + " ");
		System.out.println();
		System.out.println(q);
		while (!q.isEmpty())
			System.out.print(q.poll() + " ");
		System.out.println();
		System.out.println("Test PriorityQueue over.");
	}

	enum Size {SMALL, MEDIUM, LARGE};
	
	static void testEnumSet() {
		System.out.println("\nTest EnumSet ...");
		EnumSet<Size> all = EnumSet.allOf(Size.class);
		EnumSet<Size> some = EnumSet.of(Size.SMALL, Size.LARGE);
		System.out.println(all);
		System.out.println(some);
		System.out.println("Test EnumSet over.");
	}
	
	static void test() {
		System.out.println("\nTest  ...");
		Integer[] ai = {1,2,3};
		List<Integer> li = Arrays.asList(ai);
		List<Integer> li2 = Arrays.asList(1,2,3);
		List<Integer> li3 = Arrays.asList(new Integer[]{1,2,3});
		System.out.println(li);
		System.out.println(li2);
		System.out.println(li3);
		li.set(1, 5);
		System.out.println(li);
		Collections.sort(li);
		System.out.println(li);

		ArrayList<Integer> ali = new ArrayList<Integer>();
		ali.addAll(li);
		ali.addAll(ali);
		List<Integer> sli = ali.subList(0, 4);
		System.out.println(sli);
		sli.clear();
		System.out.println(sli);
		System.out.println(ali);
		List<Integer> uli = Collections.unmodifiableList(ali);
		System.out.println(uli);
		//uli.add(3);

		
		List<String> ls = Collections.nCopies(3, "joke");
		System.out.println(ls);
		//ls.set(1, "world");
		//System.out.println(ls);
		Set<Integer> lss = Collections.singleton(5);
		System.out.println(lss);

		System.out.println("\nTest over .");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	// TODO Auto-generated method stub
		testHashMap();
		testLinkedList();
		testPriorityQueue();
		testEnumSet();
		test();
		
	}

}
