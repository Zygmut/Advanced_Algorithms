package Services.Comunication.Content;

import java.io.Serializable;

/**
 * This class represents the body of a {@code Request} or {@code Response}. A
 * body contains the content of the request or response. The content can be of
 * any type.
 *
 * This class is serializable so that it can be sent through sockets.
 *
 * @see java.io.Serializable
 * @see Services.Comunication.Request.Request
 * @see Services.Comunication.Response.Response
 */
public class Body implements Serializable {

	private static final long serialVersionUID = 135792468L;
	public final Object content;

	public Body(Object content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Body [content=" + content + "]";
	}
}
