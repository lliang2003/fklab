package fk;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadTest {

	static class RC implements Runnable {
		public void run() {
			System.out.println("RC runs.");
		}
	}

	static class TC extends Thread {

		public void run() {
			System.out.println("TC runs");
			while (!Thread.currentThread().isInterrupted())
				System.out.println("Not Interrupted");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static class TC2 extends Thread {
		static class TC22 {
			public synchronized void func(int num) {
				while (count + num < 7) {
					try {
						System.out.println("Thread " + num + " wait.");
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				for (int i = 0; i < 3; ++i)
					System.out.println(num + "e");
				++count;
				System.out.println("Count = " + count);
				this.notifyAll();
			}

		}

		TC2(int i) {
			this.num = i;
		}

		private int num;

		public void run() {
			for (int i = 0; i < 10; ++i) {
				System.out.println(num + " a" + i);
				Thread.yield();
				System.out.println(num + " b" + i);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			mylock.lock();
			for (int i = 0; i < 10; ++i) {
				while (num > count) {
					System.out.println("Thread " + num + " wait.");
					try {
						mycond.await(100, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println(num + " c" + i);
				Thread.yield();
				System.out.println(num + " d" + i);
			}
			++count;
			mycond.signalAll();
			mylock.unlock();
			System.out.println("Count = " + count);

			t.func(num);
			// throw new RuntimeException();

		}

		static Lock mylock = new ReentrantLock();

		static Condition mycond = mylock.newCondition();

		static TC22 t = new TC22();

		static int count = 1;
	}

	static class TC3 extends Thread {
		TC3(int i) {
			num = i;
		}

		private int num;

		public void run() {
			synchronized (obj) {
				for (int i = 0; i < 10; ++i)
					System.out.println(num + "f");
			}
		}

		static Object obj = new Object();
	}

	static class TD extends Thread {
		public void run() {
			for (int i = 0; i < 10; ++i) {
				System.err.println("This is deamon, count " + i);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	static class TH implements Thread.UncaughtExceptionHandler {
		public void uncaughtException(Thread t, Throwable e) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("From thread " + ((TC2) t).num);
			e.printStackTrace();
		}
	}

	static class TQ extends Thread {
		public TQ(int i) {
			num = i;
		}

		public void run() {
			try {
				if (num % 2 == 1) {
					for (int i = 0; i < 10; ++i) {
						System.out.println(num + " put " + i);
						bq.put(i);
					}
				} else {
					for (int i = 0; i < 10; ++i) {
						System.out.println(num + " get " + i);
						bq.take();
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private int num;

		//static BlockingQueue<Integer> bq = new ArrayBlockingQueue<Integer>(10);
		//static BlockingQueue<Integer> bq = new LinkedBlockingDeque<Integer>();
		static BlockingQueue<Integer> bq = new PriorityBlockingQueue<Integer>();
	}
	
	static void testThread() throws InterruptedException {
		new Thread(new RC()).start();
		Thread t = new TC();
		t.start();
		Thread.sleep(10);
		t.interrupt();
		t = new TD();
		t.setDaemon(true);
		t.start();
		Thread.setDefaultUncaughtExceptionHandler(new TH());
		new TC2(1).start();
		new TC2(2).start();
		new TC2(3).start();
		new TC3(1).start();
		new TC3(2).start();
		new TC3(3).start();
	}
	
	static void testBlockedQueue() throws InterruptedException {
		new TQ(1).start();
		new TQ(3).start();
		Thread.sleep(1000);
		new TQ(2).start();
		new TQ(4).start();
	}
	
	static class CC implements Callable<Integer> {
		public CC(int num) {this.num = num;}
		private int num;

		public Integer call() throws Exception {
			// TODO Auto-generated method stub
			System.out.println(new Date() + " CC " + num + " runs ...");
			Thread.sleep(1000);
			System.out.println(new Date() +" CC " + num + " run over.");
			return new Random().nextInt(9);
		}
		
	}
	static void testExecutors() throws InterruptedException, ExecutionException {
		ExecutorService esc = Executors.newCachedThreadPool();
		ExecutorService esf = Executors.newFixedThreadPool(3);
		ExecutorService ess = Executors.newSingleThreadExecutor();
		Future<Integer> rr = esc.submit(new RC(), 5);
		esc.submit(new CC(0));
		esc.submit(new CC(1));
		esc.submit(new CC(2));
		esc.submit(new CC(3));
		esc.submit(new CC(4));
		System.out.println(rr.get());
		esc.shutdown();
		esc.awaitTermination(1, TimeUnit.MINUTES);
		Future<Integer> r = esf.submit(new CC(5));
		esf.submit(new CC(6));
		esf.submit(new CC(7));
		esf.submit(new CC(8));
		esf.submit(new CC(9));
		esf.submit(new CC(10));
		//r.cancel(true);
		//System.out.println(r.isCancelled());
		System.out.println(r.get());
		esf.shutdown();
		esf.awaitTermination(1, TimeUnit.MINUTES);
		ess.submit(new CC(11));
		ess.submit(new CC(12));
		ess.submit(new CC(13));
		ess.shutdown();
		ess.awaitTermination(1, TimeUnit.MINUTES);
		FutureTask<Integer> task = new FutureTask<Integer>(new CC(16));
		new Thread(task).start();
		System.out.println(task.get());
	}
	
	static class TB extends Thread {
		public void run() {
			System.out.println("TB runs");
			++count;
			try {
				barrier.await();
				System.out.println("TB count = " + count);
				cdl.await();
				System.out.println("Count down over");
				s.acquire();
				Thread.sleep(1000);
				System.out.println("Get Semaphore");
				s.release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		private static int count = 0;
		private static CyclicBarrier barrier = new CyclicBarrier(4);
		static CountDownLatch cdl = new CountDownLatch(1);
		static Semaphore s = new Semaphore(2);
	}
	
	static void testSyncronizer() throws InterruptedException, BrokenBarrierException {
		new TB().start();
		new TB().start();
		new TB().start();
		//new TB().start();
		TB.barrier.await();
		System.out.println("flag1");
		Thread.sleep(200);
		System.out.println("flag2");
		TB.cdl.countDown();
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws ExecutionException 
	 * @throws BrokenBarrierException 
	 */
	public static void main(String[] args) throws InterruptedException, ExecutionException, BrokenBarrierException {
		// TODO Auto-generated method stub
		//testThread();
		//testBlockedQueue();
		//testExecutors();
		testSyncronizer();
	}

}
