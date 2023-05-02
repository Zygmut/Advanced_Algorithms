package Model;

import java.io.Serializable;

public record GeoPoint(double x, double y) implements Serializable {
	private static final long serialVersionUID = 6999L;

	public double euclideanDistanceTo(GeoPoint target) {
		return Math.sqrt(
			Math.pow(this.x - target.x, 2) + Math.pow(this.y - target.y, 2)
		);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(").append(x).append(", ").append(y).append(")");
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof GeoPoint)) return false;
		GeoPoint target = (GeoPoint) obj;
		return this.x == target.x && this.y == target.y;
	}
}
