package Services;

import Services.Comunication.Request.Request;
import Services.Comunication.Response.Response;

/**
 * This interface represents a server in our application structure. A server is
 * the {@code MVC (Hub)} of our application structure. A server is a thread, in
 * our case the main thread, that runs in the background and communicates with
 * the {@code Services} through the {@code Request} and {@code Response}
 * classes. A server can be started and stopped, and it can send and receive
 * {@code Request} and {@code Response} objects.
 *
 * Also, a server should be able to load the endpoints of the services from a
 * config file.
 *
 * It's important that the sever should have to maps from the endpoints to the
 * services, specificly one for the requests and one for the responses. In our
 * case, it should be a {@code Map<RequestCode, Service[]>} and a
 * {@code Map<ResponseCode,
 * Service[]>}.
 *
 * @see Services.Comunication.Request.Request
 * @see Services.Comunication.Response.Response
 * @see Services.Service
 */
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
	 */
	public void mapLoader();

	/**
	 * Handles all the responses that are sent to the server via sockets.
	 *
	 * @param response the response to be handled
	 */
	public void responseHandler(Response response);

	/**
	 * Executes the response
	 *
	 * @param response the response to be executed
	 */
	public void responseExecutor(Response response);
}
