package Services.Comunication.Request;

import java.io.Serializable;

import Services.Comunication.Content.Body;

/**
 * This class represents a request from the {@code MVC (Hub)} to a service. A
 * request contains a code, an origin and a body. By default, the body is null.
 *
 * This class is serializable so that it can be sent through sockets.
 *
 * @see Services.Comunication.Request.RequestCode
 * @see Services.Comunication.Content.Body
 * @see java.io.Serializable
 */
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

	public Request(RequestCode code, String origin, Body body) {
		this.code = code;
		this.origin = origin;
		this.body = body;
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
