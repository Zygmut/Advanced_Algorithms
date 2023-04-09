package Request;

public class Request {
	public final RequestCode code;
	public final String origin;
	public final Body body;

	public Request(RequestCode code, String origin, Body body) {
		this.code = code;
		this.origin = origin;
		this.body = body;
	}

	public Request(RequestCode code, Object origin, Body body) {
		this.code = code;
		this.origin = origin.getClass().getSimpleName();
		this.body = body;
	}

	@Override
	public String toString() {
		return "Request " + code + " from " + origin;
	}
}
