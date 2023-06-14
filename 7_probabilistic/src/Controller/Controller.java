package Controller;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import Model.Result;
import Services.Service;
import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Response.Response;
import Services.Comunication.Response.ResponseCode;

public class Controller implements Service {

	final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	static final int BASE_ITERATIONS = 10;
	static final int BASE_SEED = 27;

	public Controller() {
		// Initialize controller things here
	}

	@Override
	public void start() {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Controller started.");
	}

	@Override
	public void stop() {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Controller stopped.");
	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			case GREET -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.INFO, "Controller heard {0} say {1}",
								new Object[] { request.origin, request.body.content });
				this.sendResponse(new Response(ResponseCode.GREET_BACK, this, new Body("Controller's here!")));
			}
			case CHECK_PRIMALITY -> {
				final Object[] params = (Object[]) request.body.content;
				final PrimalityFunction function = (PrimalityFunction) params[0];
				final BigInteger number = (BigInteger) params[1];

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
