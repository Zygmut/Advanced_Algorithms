package utils;

import static java.lang.Thread.sleep;

public class Helpers {

	public static final SynchronizedCounter syncCount = new SynchronizedCounter(0);

	private Helpers() {
	}

	public static class SynchronizedCounter {
		private int count;

		public SynchronizedCounter(int n) {
			this.count = n;
		}

		public synchronized void inc() {
			this.count++;
		}

		public synchronized int get() {
			return this.count;
		}

		public synchronized void dec() {
			this.count--;
		}
	}

	public static void await() {
		try {
			sleep(100);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}

}
