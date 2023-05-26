package Model;

import java.io.Serializable;
import java.util.Objects;

public record ExecResultData(String id, Connection[] connections) implements Serializable {

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ExecResultData))
			return false;
		ExecResultData other = (ExecResultData) obj;
		return Objects.equals(connections, other.connections()) && Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(connections, id);
	}

	@Override
	public String toString() {
		return "ExecResultData [connnections=" + connections + ", id=" + id + "]";
	}

	public record Connection(String id, double value) implements Serializable {
	}

	public record Edge(String src, String dst, double weight) implements Serializable, Comparable<Edge> {
		@Override
		public int compareTo(Edge o) {
			return Double.compare(weight, o.weight);
		}
	}
}
