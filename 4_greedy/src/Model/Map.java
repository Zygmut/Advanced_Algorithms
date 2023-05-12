package Model;

import java.io.Serializable;

public record Map(String img, Graph graph) implements Serializable{
	private static final long serialVersionUID = 7988697435L;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Map: img=")
				.append(img)
				.append(" graph=")
				.append(graph);

		return sb.toString();
	}

}
