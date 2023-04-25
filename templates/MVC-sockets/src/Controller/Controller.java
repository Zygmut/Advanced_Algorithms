package Controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import Master.MVC;
import Request.Service;
import Request.Request;

public class Controller implements Service {

	private MVC hub;

	public Controller(MVC mvc) {
		this.hub = mvc;
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
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

}
