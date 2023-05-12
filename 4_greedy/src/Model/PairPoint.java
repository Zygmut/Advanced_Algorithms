package Model;

public record PairPoint(GeoPoint p1, GeoPoint p2) {
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(p1()).append(", ").append(p2()).append("]");
		return sb.toString();
	}
}
