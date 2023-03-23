package request;

public class Request {
    public final RequestCode code;
    public final String origin;

    public Request(RequestCode code, String origin) {
        this.code = code;
        this.origin = origin;
    }

    public Request(RequestCode code, Object origin) {
        this.code = code;
        this.origin = origin.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return "Request " + code + " from " + origin;
    }
}
