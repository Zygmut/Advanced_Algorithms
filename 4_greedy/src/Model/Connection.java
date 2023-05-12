package Model;

import java.io.Serializable;

public record Connection(String nodeId, double weight) implements Serializable {
	private static final long serialVersionUID = 654821547L;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName())
				.append(": nodeId=")
				.append(nodeId)
				.append(" weight=")
				.append(weight);
		return sb.toString();
	}
}
