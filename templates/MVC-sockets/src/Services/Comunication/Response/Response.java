package Services.Comunication.Response;

import java.io.Serializable;

import Services.Comunication.Content.Body;

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
		return "Response [code=" + code + ", from=" + origin + ", content=" + body + "]";
	}
}
