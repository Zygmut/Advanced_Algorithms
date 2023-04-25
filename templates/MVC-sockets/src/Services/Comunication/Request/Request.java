package Services.Comunication.Request;

import java.io.Serializable;

import Services.Comunication.Content.Body;

public class Request implements Serializable {

	private static final long serialVersionUID = 123456789L;
	public final RequestCode code;
	public final String origin;
	public final Body body;

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

	public Request(RequestCode code, Object origin, Body body) {
		this.code = code;
		this.origin = origin.getClass().getSimpleName();
		this.body = body;
	}

	@Override
	public String toString() {
		return "Request [code=" + code + ", from=" + origin + ", content=" + body + "]";
	}
}
