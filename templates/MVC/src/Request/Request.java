package Request;

public class Request {
    public RequestCode code;
    public String origin;
    public String target;

    public Request(RequestCode code, String origin, String target) {
        this.code = code;
        this.origin = origin;
        this.target = target;
    }

    public Request(RequestCode code, Object origin, Object target) {
        this.code = code;
        this.origin = origin.getClass().getSimpleName();
        this.target = target.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return "Request " + code + ": From " + origin + " to " + target;
    }
}
