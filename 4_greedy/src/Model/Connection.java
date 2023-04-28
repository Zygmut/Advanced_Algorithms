package Model;

public record Connection(String nodeId, double weight) {

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
