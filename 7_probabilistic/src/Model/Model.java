package Model;

import java.util.logging.Level;
import java.util.logging.Logger;

import Services.Service;
import Services.Comunication.Request.Request;

public class Model implements Service {

	private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	public Model() {
		// Initialize model things here
	}

	@Override
	public void start() {
		logger.log(Level.INFO, "Model started.");
	}

	@Override
	public void stop() {
		logger.log(Level.INFO, "Model stopped.");
	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			default -> {
				logger.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

}
