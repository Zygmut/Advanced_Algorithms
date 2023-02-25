package Request;

public class Request {
    public RequestCode code;
    public String origin;

    public Request(RequestCode code, String origin) {
        this.code = code;
        this.origin = origin;
    }

    public Request(RequestCode code, Object origin) {
        this.code = code;
        this.origin = origin.getClass().getSimpleName();
    }

    public void setCode(RequestCode code){
        this.code = code;
    }

    @Override
    public String toString() {
        return "Request " + code + " from " + origin;
    }
}
