package Request;

public interface Notify {
    /**
     * Allows comunication between events
     *
     * @param request
     */
    public void notifyRequest(Request<?> request);
}
