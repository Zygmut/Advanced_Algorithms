package Request;

public interface Service {
    /**
     * Allows comunication between events
     *
     * @param request
     */
    public void notifyRequest(Request request);

    /**
     * Starts the service
     */
    public void start();

	/**
	 * Stops the service
	 */
	public void stop();
}
