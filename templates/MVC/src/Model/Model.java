package Model;

import java.util.logging.Level;
import java.util.logging.Logger;

import Master.MVC;
import Request.Notify;
import Request.Request;

public class Model implements Notify {

	private MVC hub;

	public Model(MVC mvc) {
		this.hub = mvc;
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
