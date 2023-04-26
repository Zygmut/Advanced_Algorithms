package Controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import Services.Service;
import Services.Comunication.Request.Request;
import Services.Comunication.Response.Response;

public class Controller implements Service {

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

	@Override
	public void sendRequest(Request request) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'sendRequest'");
	}

	@Override
	public void sendResponse(Response response) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'sendResponse'");
	}

}
