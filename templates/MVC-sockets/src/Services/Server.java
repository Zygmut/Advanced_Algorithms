package Services;

import java.util.Map;

import Services.Comunication.Request.Request;
import Services.Comunication.Request.RequestCode;
import Services.Comunication.Response.Response;

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
	public void requestHandler(Request request);

	/**
	 * Validates a the request
	 *
	 * @param request the request to be validated
	 *
	 * @return boolean true if the request is valid, false otherwise
	 */
	public boolean requestValidator(Request request);

	/**
	 * Executes the request
	 *
	 * @param request the request to be executed
	 */
	public void requestExecutor(Request request);

	/**
	 * Loads the services that are available to the server where the key is the
	 * endpoint and the value is the service that is associated with the endpoint.
	 *
	 * @return Map<RequestCode, Service[]> the map from the endpoint to the service
	 */
	public Map<RequestCode, Service[]> requestMapLoader();

	/**
	 * Handles all the responses that are sent to the server via sockets.
	 *
	 * @param response the response to be handled
	 */
	public void responseHandler(Response response);
}
