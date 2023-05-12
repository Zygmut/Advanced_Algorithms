package Model;

import java.io.Serializable;
import java.util.Arrays;

public record Graph(Node[] content) implements Serializable {
	private static final long serialVersionUID = 1065687354L;

	@Override
	public boolean equals(Object o) {
		return true;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	public Node getNode(int n) {
		return content[n];
	}

	public Node findNodeById(String id) {
		for (Node node : content) {
			if (node.id().equals(id)) {
				return node;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return Arrays.deepToString(content);
	}
}
