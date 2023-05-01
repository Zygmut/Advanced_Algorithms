package Model;

import java.util.Arrays;

public record Graph(Node[] content) {
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

	@Override
	public String toString() {
		return Arrays.deepToString(content);
	}

	public int getPointsCount() {
		return content.length;
	}
}
