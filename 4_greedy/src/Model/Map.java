package Model;


public record Map(String img, Graph graph) {

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
