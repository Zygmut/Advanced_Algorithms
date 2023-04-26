package Services;

import Services.Comunication.Request.Request;
import Services.Comunication.Response.Response;

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
	public void sendRequest(Request request);

	/**
	 * Sends a response to the client
	 *
	 * @param response the response to be sent
	 */
	public void sendResponse(Response response);
}
