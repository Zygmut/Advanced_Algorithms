package Model;

import java.io.Serializable;
import java.util.List;

public record Path(List<Node> path, double totalCost) implements Serializable{

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Path: path=[ ");
		for (Node node : path) {
			sb.append(node.id())
					.append(",");
		}
		sb.append("] totalCost=")
				.append(totalCost);

		return sb.toString();
	}
}
