package Services;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import Services.Service;
import Services.Comunication.Request.Request;
import Services.Comunication.Response.Response;
import utils.Config;

/**
 * This interface represents a service in our application structure. A service
 * is an element of the MVC, those are the {@code Model}, {@code View} and
 * {@code Controller} of the application. A service is a thread that runs in the
 * background and communicates with the {@code MVC (Hub)} through the
 * {@code Request} and {@code Response} classes. A service can be started and
 * stopped, and it can send and receive {@code Request} and {@code Response}
 * objects.
 *
 * @see Services.Comunication.Request.Request
 * @see Services.Comunication.Response.Response
 */
public interface Service {
	/**
	 * Starts the service
	 */
	public void start();

	/**
	 * Stops the service
	 */
	public void stop();

	/**
	 * Allows comunication between events
	 *
	 * @param request
	 */
	public void notifyRequest(Request request);

	/**
	 * Sends a request to the server
	 *
	 * @param request the request to be sent
	 */
	default void sendRequest(Request request) {
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

	/**
	 * Sends a response to the client
	 *
	 * @param response the response to be sent
	 */
	default void sendResponse(Response response) {
		try (Socket socket = new Socket(Config.SERVER_HOST, Config.SERVER_PORT)) {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(response);
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "Error while sending response.", e);
		}
	}
}
