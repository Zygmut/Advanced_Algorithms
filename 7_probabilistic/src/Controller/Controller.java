package Controller;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
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
		final BigInteger coefficient1 = new BigInteger("-136037213");
		final BigInteger coefficient2 = new BigInteger("104139000000");
		final BigInteger coefficient3 = new BigInteger("529591153");
		final BigInteger coefficient4 = new BigInteger("4959000000");
		final BigInteger coefficient5 = new BigInteger("379750009");
		final BigInteger coefficient6 = new BigInteger("119016000");
		final BigInteger coefficient7 = new BigInteger("1762072807");
		final BigInteger coefficient8 = new BigInteger("39672000");
		final BigInteger coefficient9 = new BigInteger("3768076");
		final BigInteger coefficient10 = new BigInteger("12825");
		final BigInteger coefficient11 = new BigInteger("532529531");
		final BigInteger coefficient12 = new BigInteger("661200");
		final BigInteger coefficient13 = new BigInteger("25545169");
		final BigInteger coefficient14 = new BigInteger("46284");

		final BigInteger term1 = coefficient1.multiply(x.pow(6)).divide(coefficient2);
		final BigInteger term2 = coefficient3.multiply(x.pow(5)).divide(coefficient4);
		final BigInteger term3 = coefficient5.multiply(x.pow(4)).divide(coefficient6);
		final BigInteger term4 = coefficient7.multiply(x.pow(3)).divide(coefficient8);
		final BigInteger term5 = coefficient9.multiply(x.pow(2)).divide(coefficient10);
		final BigInteger term6 = coefficient11.multiply(x).divide(coefficient12);
		final BigInteger term7 = coefficient13.divide(coefficient14);

		return compactTimeFromMillis(term1.subtract(term2).add(term3).subtract(term4).add(term5).subtract(term6).add(term7));
	}

	private Map<BigInteger, BigInteger> getFactors(String number) {

		Map<BigInteger, BigInteger> primeFactors = new HashMap<>();
		BigInteger num = new BigInteger(number);
		BigInteger divisor = BigInteger.valueOf(2);

		while (num.compareTo(BigInteger.ONE) > 0) {
			if (num.isProbablePrime(100)) {
				primeFactors.put(num, BigInteger.ONE);
				break;
			}

			if (num.remainder(divisor).equals(BigInteger.ZERO)) {
				primeFactors.put(divisor, primeFactors.getOrDefault(divisor, BigInteger.ZERO).add(BigInteger.ONE));
				num = num.divide(divisor);
			} else {
				divisor = divisor.nextProbablePrime();
			}
		}

		return primeFactors;
	}

	// If someone has a better idea, please do. This shit is disgusting 🤮
	private Duration compactTimeFromMillis(BigInteger millis){
		BigInteger time = new BigInteger(millis.toString());
		TimeUnit unit = TimeUnit.MILLISECONDS;

		for (int idx = 0; idx < TimeUnit.values().length && time.toString().length() > 5; idx++) {
			time = unit.transform(time);
			unit = TimeUnit.values()[idx];
		}

		Duration res = unit.intoDuration(time);
		return res;

	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			case GET_FACTORS -> {
				final String number = (String) request.body.content;
				final Duration expectedTime= getEstimatedTime(number.length());
				if (expectedTime.compareTo(Duration.ofMinutes(1)) >= 0) {
					this.sendResponse(new Response(ResponseCode.GET_FACTORS, this,
							new Body(new Result(expectedTime, null))));
				}
				final Instant start = Instant.now();
				final Map<BigInteger, BigInteger> result = getFactors(number);
				final Instant end = Instant.now();

				this.sendResponse(new Response(ResponseCode.GET_FACTORS, this,
						new Body(new Result(Duration.between(start, end), result))));
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
