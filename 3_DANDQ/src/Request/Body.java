package Request;

import java.util.HashMap;

public class Body<T> {

	private HashMap<BodyCode, T> body;
	private final RequestType type;

	public Body(RequestType type) {
		this.body = new HashMap<>();
		this.type = type;
	}

	public void add(BodyCode code, T value) {
		this.body.put(code, value);
	}

	public T get(BodyCode code) {
		return this.body.get(code);
	}

	public HashMap<BodyCode, T> getBody() {
		return this.body;
	}

	public void setBody(HashMap<BodyCode, T> body) {
		this.body = body;
	}

	public RequestType getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return "Body{" +
				"body=" + body +
				", type=" + type +
				'}';
	}
}
