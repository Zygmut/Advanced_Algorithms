package Model;

import java.util.List;

public record Path(List<Node> path, double totalCost) {

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PATH = [ ");
		for (Node node : path) {
			sb.append(node.id())
					.append(" ");
		}
		sb.append("] with TOTAL COST = ")
				.append(totalCost);

		return sb.toString();
	}
}
