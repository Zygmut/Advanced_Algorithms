package Controller;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PrimalityTest {
    private PrimalityTest() {
    }

    public static boolean trialDivision(BigInteger number) {
        final BigInteger zero = BigInteger.ZERO;
        final BigInteger two = BigInteger.TWO;

        if (number.compareTo(BigInteger.ONE) <= 0)
            return false;

        if (number.equals(two) || number.equals(BigInteger.valueOf(3)))
            return true;

        if (number.mod(two).equals(zero) || number.mod(BigInteger.valueOf(3)).equals(zero))
            return false;

        final BigInteger sqrt = number.sqrt();
        BigInteger i = BigInteger.valueOf(5);
        BigInteger iPlusTwo = i.add(two);

        while (i.compareTo(sqrt) <= 0) {
            if (number.mod(i).equals(zero) || number.mod(iPlusTwo).equals(zero))
                return false;
            i = i.add(BigInteger.valueOf(6));
            iPlusTwo = iPlusTwo.add(BigInteger.valueOf(6));
        }

        return true;
    }

    private static BigInteger getRandomBase(BigInteger number, Random random) {
        BigInteger base;
        do {
            base = new BigInteger(number.bitLength(), random);
        } while (base.compareTo(BigInteger.TWO) < 0 || base.compareTo(number) >= 0);

        return base;
    }

    /**
     * base ^ {n - 1} % n == 1
     *
     * @param base
     * @param number
     * @return
     */
    private static boolean isFermatWitness(BigInteger base, BigInteger number) {
        return base.modPow(number.subtract(BigInteger.ONE), number).equals(BigInteger.ONE);
    }

    public static boolean fermat(BigInteger number, int iterations, long seed) {
        if (number.equals(BigInteger.TWO) || number.equals(BigInteger.valueOf(3))) {
            return true;
        }

        if (number.compareTo(BigInteger.TWO) < 0 || number.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            return false;
        }

        Random random = new Random(seed);

        for (int i = 0; i < iterations; i++) {
            if (!isFermatWitness(getRandomBase(number, random), number)) {
                return false;
            }
        }

        return true;
    }

    public static boolean millerRabin(BigInteger number, int iterations, int seed) {
        if (number.equals(BigInteger.TWO) || number.equals(BigInteger.valueOf(3))) {
            return true;
        }

        if (number.compareTo(BigInteger.TWO) < 0 || number.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            return false;
        }

        int s = 0;
        BigInteger d = number.subtract(BigInteger.ONE);

        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            s++;
            d = d.divide(BigInteger.TWO);
        }

        Random random = new Random(seed);

        for (int i = 0; i < iterations; i++) {
            if (!isMillerRabinWitness(getRandomBase(number, random), number, d, s)) {
                return false;
            }
        }

        return true;
    }

    public static boolean millerRabinParallel(BigInteger number, int iterations, int seed) {
        final BigInteger TWO = BigInteger.valueOf(2);

		if (number.equals(TWO) || number.equals(BigInteger.valueOf(3))) {
			return true;
		}

		if (number.compareTo(TWO) < 0 || number.mod(TWO).equals(BigInteger.ZERO)) {
			return false;
		}

		int s = 0;
		BigInteger d = number.subtract(BigInteger.ONE);

		while (d.mod(TWO).equals(BigInteger.ZERO)) {
			s++;
			d = d.divide(TWO);
		}

		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		final BigInteger finalD = d;
		final int finalS = s;
		try {
			Random random = new Random(seed);
			for (int i = 0; i < iterations; i++) {
				Future<Boolean> future = executor
						.submit(() -> isMillerRabinWitness(getRandomBase(number, random), number, finalD, finalS));

				try {
					if (!future.get()) {
						return false; // Found a witness, number is composite
					}
				} catch (Exception e) {
					e.printStackTrace(); // Handle any exception that occurred during computation
				}
			}
		} finally {
			executor.shutdown(); // Shutdown the executor service
		}

		return true; // No witnesses found, number is probably prime
	}

	public static boolean trialDivisionParallel(BigInteger number) {
		final BigInteger zero = BigInteger.ZERO;
		final BigInteger two = BigInteger.TWO;

		if (number.compareTo(BigInteger.ONE) <= 0)
			return false;

		if (number.equals(two) || number.equals(BigInteger.valueOf(3)))
			return true;

		if (number.mod(two).equals(zero) || number.mod(BigInteger.valueOf(3)).equals(zero))
			return false;

		final BigInteger sqrt = number.sqrt();

		int numThreads = Runtime.getRuntime().availableProcessors();
		ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

		try {
			BigInteger i = BigInteger.valueOf(5);
			BigInteger iPlusTwo = i.add(two);

			while (i.compareTo(sqrt) <= 0) {
				Future<Boolean>[] futures = new Future[numThreads];

				for (int j = 0; j < numThreads; j++) {
					final BigInteger currentI = i;
					final BigInteger currentIPlusTwo = iPlusTwo;
					futures[j] = executorService.submit(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return number.mod(currentI).equals(zero) || number.mod(currentIPlusTwo).equals(zero);
						}
					});

					i = i.add(BigInteger.valueOf(6));
					iPlusTwo = iPlusTwo.add(BigInteger.valueOf(6));
				}

				for (Future<Boolean> future : futures) {
					if (future.get()) {
						return false;
					}
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} finally {
			executorService.shutdown();
		}

		return true;
	}

    private static boolean isMillerRabinWitness(BigInteger base, BigInteger number, BigInteger d, int s) {
        BigInteger x = base.modPow(d, number);

        if (x.equals(BigInteger.ONE) || x.equals(number.subtract(BigInteger.ONE))) {
            return true;
        }

        for (int r = 1; r < s; r++) {
            x = x.modPow(BigInteger.TWO, number);

            if (x.equals(BigInteger.ONE)) {
                return false;
            }

            if (x.equals(number.subtract(BigInteger.ONE))) {
                return true;
            }
        }

        return false;
    }

}
