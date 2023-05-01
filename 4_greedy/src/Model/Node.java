package Model;

import java.io.Serializable;
import java.util.Arrays;

public record Node(String id, NodeState state, GeoPoint geoPoint, Connection[] connections) implements Serializable{

	private static final long serialVersionUID = 7981683548L;

	public boolean canGoTo(Node target){
		for (Connection connection : connections) {
			if(connection.nodeId().equals(target.id())){
				return true;
			}
		}

		return false;
	}

	public Node connectedTo(Node target, double weight) {
		if (this.connections() == null){
			return new Node(this.id(), this.state(), this.geoPoint(), new Connection[]{new Connection(target.id(), weight)});
		}

		Connection[] connections = Arrays.copyOf(this.connections(), this.connections().length + 1);

		connections[connections.length - 1] = new Connection(target.id(), weight);
		return new Node(this.id(), this.state(),this.geoPoint(), connections);
	}

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
		sb.append(this.getClass().getSimpleName())
				.append(": id=")
				.append(id)
				.append(" state=")
				.append(state.name())
				.append(" geoPoint=")
				.append(geoPoint)
				.append(" connections=[");
		if (this.connections().length == 0) {
			sb.append("]");
			return sb.toString();
		}

		sb.append(" ");
		for (Connection connection : connections) {
			sb.append(connection).append(" ");
		}

		sb.append("]");

		return sb.toString();
	}
}
