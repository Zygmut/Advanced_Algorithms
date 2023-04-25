package Services;

import Services.Comunication.Request.Request;

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
	 */
	public void sendRequest();

	/**
	 * Sends a response to the client
	 */
	public void sendResponse();
}
