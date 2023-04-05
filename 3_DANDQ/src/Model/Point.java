package Model;

public record Point(double x, double y) {

	public double euclideanDistanceTo(Point target) {
		return Math.sqrt(Math.pow(this.x - target.x, 2) + Math.pow(this.y - target.y, 2));
	}

}
