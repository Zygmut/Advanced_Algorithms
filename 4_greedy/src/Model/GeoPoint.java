package Model;

import java.io.Serializable;
import java.util.Objects;

public record GeoPoint(double x, double y) implements Serializable {
	private static final long serialVersionUID = 6999L;

	private double euclideanDistanceTo(GeoPoint target) {
		return Math.sqrt(
			Math.pow(this.x - target.x, 2) + Math.pow(this.y - target.y, 2)
		);
	}

	private double manhattanDistanceTo(GeoPoint target) {
		return Math.abs(this.x - target.x) + Math.abs(this.y - target.y);
	}

	private double chebyshevDistanceTo(GeoPoint target) {
		return Math.max(Math.abs(this.x - target.x), Math.abs(this.y - target.y));
	}

	private double cosineDistanceTo(GeoPoint target) {
		return 1 - (
			(this.x * target.x + this.y * target.y) /
			(Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2)) *
			Math.sqrt(Math.pow(target.x, 2) + Math.pow(target.y, 2)))
		);
	}

	private double minkowskiDistanceTo(GeoPoint target, double p) {
		return Math.pow(
			Math.pow(Math.abs(this.x - target.x), p) +
			Math.pow(Math.abs(this.y - target.y), p),
			1 / p
		);
	}

	private double haversineDistanceTo(GeoPoint target) {
		final double R = 6371e3;
		final double phi1 = Math.toRadians(this.x);
		final double phi2 = Math.toRadians(target.x);
		final double deltaPhi = Math.toRadians(target.x - this.x);
		final double deltaLambda = Math.toRadians(target.y - this.y);

		final double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
			Math.cos(phi1) * Math.cos(phi2) *
			Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
		final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return R * c;
	}

	public double distanceTo(GeoPoint target, DistanceType distanceType) {
		if (Objects.isNull(distanceType)) distanceType = DistanceType.EUCLIDEAN;
		return switch (distanceType) {
			case EUCLIDEAN -> euclideanDistanceTo(target);
			case MANHATTAN -> manhattanDistanceTo(target);
			case CHEBYSHEV -> chebyshevDistanceTo(target);
			case COSINE -> cosineDistanceTo(target);
			case MINKOWSKI -> minkowskiDistanceTo(target, 2);
			case HAVERSINE -> haversineDistanceTo(target);
			default -> euclideanDistanceTo(target);
		};
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(").append(x).append(", ").append(y).append(")");
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (Objects.isNull(obj) || !(obj instanceof GeoPoint)) return false;
		GeoPoint target = (GeoPoint) obj;
		return this.x == target.x && this.y == target.y;
	}
}
