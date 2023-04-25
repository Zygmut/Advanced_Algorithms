package Services;

import java.util.HashMap;
import java.util.Map;

import Services.Comunication.Request.Request;

public interface Server {
	/**
	 * Starts the server
	 */
	public void start();

	/**
	 * Stops the server
	 */
	public void stop();

	/**
	 * Handles all the requests that are sent to the server via sockets
	 *
	 */
	public void requestHandler();

	/**
	 * Validates a the request
	 *
	 * @param request
	 */
	public void requestValidator(Request request);

	/**
	 * Executes the request
	 *
	 * @param request
	 */
	public void requestExecutor(Request request);

	// public Response responseHandler();

	/**
	 * Loads the services that are available to the server where the key is the
	 * endpoint and the value is the service that is associated with the endpoint.
	 *
	 * @return Map<String, Service>
	 */
	public Map<String, Service> requestMapLoader();
}
