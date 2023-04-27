package Model;

public record Connection(Node node, double weight) {

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ID = ")
				.append(node.id())
				.append(" and WEIGHT = ")
				.append(weight);
		return sb.toString();
	}
}
