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

	public Request(RequestCode code, String origin) {
		this.code = code;
		this.origin = origin;
		this.body = null;
	}

	public Request(RequestCode code, Object origin) {
		this.code = code;
		this.origin = origin.getClass().getSimpleName();
		this.body = null;
	}

	@Override
	public String toString() {
		return "Request " + code + " from " + origin;
	}
}
