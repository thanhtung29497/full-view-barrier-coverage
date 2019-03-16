package geometry;

public class Utils {
	public static Double calEuclideanDistance(Point p1, Point p2) {
		return Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) + (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
	}
}
