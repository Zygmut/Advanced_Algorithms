package Model;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import Services.Service;
import Services.Comunication.Request.Request;
import Services.Comunication.Response.Response;
import utils.Config;

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

	@Override
	public void sendRequest(Request request) {
		try (Socket socket = new Socket(Config.SERVER_HOST, Config.SERVER_PORT)) {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(request);

			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Response response = (Response) in.readObject();
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.INFO, "Response: {0}", response);
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "Error while sending request.", e);
		}
	}

	@Override
	public void sendResponse(Response response) {
		try (Socket socket = new Socket(Config.SERVER_HOST, Config.SERVER_PORT)) {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(response);
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "Error while sending response.", e);
		}
	}

}
