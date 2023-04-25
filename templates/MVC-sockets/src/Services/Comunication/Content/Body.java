package Services.Comunication.Content;

import java.io.Serializable;

public class Body implements Serializable {

	private static final long serialVersionUID = 135792468L;
	// TODO: Change to a generic type
	public final Object content;

	public Body(Object content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Body [content=" + content + "]";
	}
}
