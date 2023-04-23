package Request;

import java.util.HashMap;
import java.util.Map;

public class Body<T> {

	private HashMap<BodyCode, T> content;
	private final RequestType type;

	public Body(RequestType type) {
		this.content = new HashMap<>();
		this.type = type;
	}

	public Body(RequestType type, BodyCode code, T value) {
		this.content = new HashMap<>();
		this.content.put(code, value);
		this.type = type;
	}

	public void add(BodyCode code, T value) {
		this.content.put(code, value);
	}

	public T get(BodyCode code) {
		return this.content.get(code);
	}

	public Map<BodyCode, T> getContent() {
		return this.content;
	}

	public void setContent(Map<BodyCode, T> body) {
		this.content = (HashMap<BodyCode, T>) body;
	}

	public RequestType getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return "Body{" +
				"body=" + content +
				", type=" + type +
				'}';
	}
}
