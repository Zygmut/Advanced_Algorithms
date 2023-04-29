package Model;

public record GeoPoint(double x, double y) {

	public double euclideanDistanceTo(GeoPoint target) {
		return Math.sqrt(Math.pow(this.x - target.x, 2) + Math.pow(this.y - target.y, 2));
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(").append(x).append(", ").append(y).append(")");
		return sb.toString();
	}

}
