package Services;

import Services.Comunication.Request.Request;
import Services.Comunication.Response.Response;

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
