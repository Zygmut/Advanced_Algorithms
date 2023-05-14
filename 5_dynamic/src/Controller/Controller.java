package Controller;

import Services.Comunication.Request.Request;
import Services.Service;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller implements Service {

	public Controller() {
	}

	@Override
	public void start() {
		Logger
				.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Controller started.");
	}

	@Override
	public void stop() {
		Logger
				.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Controller stopped.");
	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			default -> {
				Logger
						.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

}
