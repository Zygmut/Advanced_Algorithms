package Controller;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import Model.Result;
import Services.Service;
import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Response.Response;
import Services.Comunication.Response.ResponseCode;

public class Controller implements Service {

	private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	private static final int BASE_ITERATIONS = 10;
	private static final int BASE_SEED = 27;

	public Controller() {
		// Initialize controller things here
	}

	@Override
	public void start() {
		logger.log(Level.INFO, "Controller started.");
	}

	@Override
	public void stop() {
		logger.log(Level.INFO, "Controller stopped.");
	}

	private Duration getEstimatedTime(int length) {
		final BigInteger x = BigInteger.valueOf(length);
		BigInteger numerator1 = new BigInteger("5939");
		BigInteger denominator1 = new BigInteger("907200");
		BigInteger term1 = numerator1.multiply(x.pow(6)).divide(denominator1);

		BigInteger numerator2 = new BigInteger("409711");
		BigInteger denominator2 = new BigInteger("181440");
		BigInteger term2 = numerator2.multiply(x.pow(5)).divide(denominator2);

		BigInteger numerator3 = new BigInteger("-17477987");
		BigInteger denominator3 = new BigInteger("181440");
		BigInteger term3 = numerator3.multiply(x.pow(4)).divide(denominator3);

		BigInteger numerator4 = new BigInteger("40090411");
		BigInteger denominator4 = new BigInteger("25920");
		BigInteger term4 = numerator4.multiply(x.pow(3)).divide(denominator4);

		BigInteger numerator5 = new BigInteger("-2739635491");
		BigInteger denominator5 = new BigInteger("226800");
		BigInteger term5 = numerator5.multiply(x.pow(2)).divide(denominator5);

		BigInteger numerator6 = new BigInteger("2092430983");
		BigInteger denominator6 = new BigInteger("45360");
		BigInteger term6 = numerator6.multiply(x).divide(denominator6);

		BigInteger numerator7 = new BigInteger("-3722729");
		BigInteger denominator7 = new BigInteger("54");
		BigInteger term7 = numerator7.divide(denominator7);

		BigInteger result = term1.add(term2).add(term3).add(term4)
				.add(term5).add(term6).add(term7);

		return Duration.ofMillis(result.longValue());

	}

	private Result getFactors(String number) {

		Map<BigInteger, BigInteger> primeFactors = new HashMap<>();
		BigInteger num = new BigInteger(number);
		BigInteger divisor = BigInteger.valueOf(2);
		Instant start = Instant.now();

		while (num.compareTo(BigInteger.ONE) > 0) {

			if (Duration.between(start, Instant.now()).toMinutes() > 1) {
				return new Result(getEstimatedTime(number.length()), Collections.emptyMap());
			}

			if (num.isProbablePrime(100)) {
				primeFactors.put(num, BigInteger.ONE);
				break;
			}

			if (!num.remainder(divisor).equals(BigInteger.ZERO)) {
				divisor = divisor.nextProbablePrime();
				continue;
			}

			primeFactors.put(divisor, primeFactors.getOrDefault(divisor, BigInteger.ZERO).add(BigInteger.ONE));
			num = num.divide(divisor);
		}
		return new Result(Duration.between(start, Instant.now()), primeFactors);

	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			case GET_FACTORS -> {
				final String number = (String) request.body.content;
				final Duration expectedTime = getEstimatedTime(number.length());
				if (expectedTime.compareTo(Duration.ofMinutes(1)) >= 0) {
					this.sendResponse(new Response(ResponseCode.GET_FACTORS, this,
							new Body(new Result(expectedTime, null))));
				}

				this.sendResponse(new Response(ResponseCode.GET_FACTORS, this, new Body(getFactors(number))));
			}

			case CHECK_PRIMALITY -> {
				final Object[] params = (Object[]) request.body.content;
				final PrimalityFunction function = (PrimalityFunction) params[0];
				final BigInteger number = new BigInteger((String) params[1]);

				Instant start = Instant.now();
				boolean isPrime = switch (function) {
					case TRIAL_DIVISION -> PrimalityTest.trialDivision(number);
					case FERMAT -> {
						int iterations = Controller.BASE_ITERATIONS;
						try {
							iterations = (Integer) params[2];
						} catch (Exception e) {
							logger.log(Level.INFO, "Set iteratios to fallback value of {0}",
									Controller.BASE_ITERATIONS);
						}

						int seed = Controller.BASE_SEED;
						try {
							seed = (Integer) params[3];
						} catch (Exception e) {
							logger.log(Level.INFO, "Set seed to fallback value of {0}",
									Controller.BASE_SEED);
						}
						yield PrimalityTest.fermat(number, iterations, seed);
					}
					case MILLER_RABIN -> {
						int iterations = Controller.BASE_ITERATIONS;
						try {
							iterations = (Integer) params[2];
						} catch (Exception e) {
							logger.log(Level.INFO, "Set iteratios to fallback value of {0}",
									Controller.BASE_ITERATIONS);
						}

						int seed = Controller.BASE_SEED;
						try {
							seed = (Integer) params[3];
						} catch (Exception e) {
							logger.log(Level.INFO, "Set seed to fallback value of {0}",
									Controller.BASE_SEED);
						}
						yield PrimalityTest.millerRabin(number, iterations, seed);
					}
				};

				Instant end = Instant.now();

				this.sendResponse(new Response(ResponseCode.CHECK_PRIMALITY, this,
						new Body(new Result(Duration.between(start, end), isPrime))));

			}
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

}
