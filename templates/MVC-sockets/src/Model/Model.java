package Model;

import java.util.logging.Level;
import java.util.logging.Logger;

import Services.Service;
import Services.Comunication.Request.Request;

public class Model implements Service {

	public Model() {
		// Initialize model things here
	}

	@Override
	public void start() {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Model started.");
	}

	@Override
	public void stop() {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Model stopped.");
	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			case HELLO_WORLD -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.INFO, "Hello World!");
			}
			case HELLO_WORLD_2 -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.INFO, "Hello World 2!");
			}
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

}
