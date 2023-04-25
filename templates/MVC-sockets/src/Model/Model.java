package Model;

import java.util.logging.Level;
import java.util.logging.Logger;

import Master.MVC;
import Services.Service;
import Services.Comunication.Request.Request;

public class Model implements Service {

	private MVC hub;

	public Model(MVC mvc) {
		this.hub = mvc;
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
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

	@Override
	public void sendRequest() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'sendRequest'");
	}

	@Override
	public void sendResponse() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'sendResponse'");
	}

}
