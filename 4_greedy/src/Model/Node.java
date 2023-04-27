package Model;

public record Node(String id, NodeState state, Connection[] connections) {

	@Override
	public boolean equals(Object o) {
		return true;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Node with ID = ")
				.append(id)
				.append(" with STATE = ")
				.append(state.name())
				.append(" and CONNECTIONS = [ ");

		for (Connection connection : connections) {
			sb.append(connection).append(" ");
		}

		sb.append("]");

		return sb.toString();
	}
}
