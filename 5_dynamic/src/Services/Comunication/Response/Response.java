package Services.Comunication.Response;

import java.io.Serializable;

import Services.Comunication.Content.Body;

/**
 * This class represents a response from a service to the {@code MVC (Hub)}. A
 * response contains a code, an origin, a body and a status. By default, the
 * body is null and the status is OK.
 *
 * This class is serializable so that it can be sent through sockets.
 *
 * @see Services.Comunication.Response.ResponseCode
 * @see Services.Comunication.Response.ResponseStatus
 * @see Services.Comunication.Content.Body
 * @see java.io.Serializable
 */
public class Response implements Serializable {

	private static final long serialVersionUID = 987654321L;
	public final ResponseCode code;
	public final String origin;
	public final Body body;
	public final ResponseStatus status;

	public Response(ResponseCode code, String origin) {
		this.code = code;
		this.origin = origin;
		this.body = null;
		this.status = ResponseStatus.OK;
	}

	public Response(ResponseCode code, Object origin) {
		this.code = code;
		this.origin = origin.getClass().getSimpleName();
		this.body = null;
		this.status = ResponseStatus.OK;
	}

	public Response(ResponseCode code, Object origin, ResponseStatus status) {
		this.code = code;
		this.origin = origin.getClass().getSimpleName();
		this.body = null;
		this.status = status;
	}

	public Response(ResponseCode code, Object origin, Body body) {
		this.code = code;
		this.origin = origin.getClass().getSimpleName();
		this.body = body;
		this.status = ResponseStatus.OK;
	}

	public Response(ResponseCode code, Object origin, Body body, ResponseStatus status) {
		this.code = code;
		this.origin = origin.getClass().getSimpleName();
		this.body = body;
		this.status = status;
	}

	@Override
	public String toString() {
		return "Response [code=" + code + ", from=" + origin + ", content=" + body + ", status=" + status + "]";
	}
}
