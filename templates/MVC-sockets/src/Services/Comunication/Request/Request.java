package Services.Comunication.Request;

import java.io.Serializable;

public class Request implements Serializable{

	private static final long serialVersionUID = 123456789L;
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
