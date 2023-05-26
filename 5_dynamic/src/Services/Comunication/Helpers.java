package Services.Comunication;

import static java.lang.Thread.sleep;

import java.util.function.Predicate;

public class Helpers {

	private Helpers() {
		throw new IllegalStateException("Utility class");
	}

	public static void await() {
		try {
			sleep(100);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}

	public static <T> void await(Predicate<T> predicate, T value) {
		while (predicate.test(value)) {
			try {
				sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
	}

	public static <T> void await(Predicate<T> predicate, T value, int sleep) {
		while (predicate.test(value)) {
			try {
				sleep(sleep);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
	}
}
