package Model;

public record PairPoint(Point p1, Point p2) {
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(p1()).append(", ").append(p2()).append("]");
		return sb.toString();
	}
}
