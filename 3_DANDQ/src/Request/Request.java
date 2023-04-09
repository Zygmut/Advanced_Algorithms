package Request;

public class Request<T> {
	public final RequestCode code;
	public final String origin;
	public final Body<T> body;

	public Request(RequestCode code, String origin, Body<T> body) {
		this.code = code;
		this.origin = origin;
		this.body = body;
	}

	public Request(RequestCode code, Object origin, Body<T> body) {
		this.code = code;
		this.origin = origin.getClass().getSimpleName();
		this.body = body;
	}

	@Override
	public String toString() {
		return "Request " + code + " from " + origin;
	}
}
