package Model;

import java.awt.image.BufferedImage;

public record Map(BufferedImage img, Graph graph) {

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Map with IMG = ")
				.append(img)
				.append(" and GRAPH = ")
				.append(graph);

		return sb.toString();
	}

}
