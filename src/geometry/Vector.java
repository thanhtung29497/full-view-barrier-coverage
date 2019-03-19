package geometry;

public class Vector {
	private Double x;
	private Double y;
	private Point origin;
	private Point destination;
	
	public Vector(Double x, Double y) {
		this.x = x;
		this.y = y;
		this.origin = new Point(0.0, 0.0);
		this.destination = new Point(x, y);
	}
	
	public Vector (Point origin, Point destination) {
		this.x = destination.getX() - origin.getX();
		this.y = destination.getY() - origin.getY();
		this.origin = origin;
		this.destination = destination;
	}
	
	public Double getX() {
		return this.x;
	}
	
	public Double getY() {
		return this.y;
	}
	
	public Point getOrigin() {
		return this.origin;
	}
	
	public Point getDestination() {
		return this.destination;
	}
	
	public Double getAngleToXAxis() {
		Double angle = Math.atan2(this.y, this.x);
		if (angle < 1e-10) {
			return angle + Math.PI * 2;
		}
		return angle;
	}
	
	public static Double getRadianAngleOfTwoVectors(Vector v1, Vector v2) {
		Double angle1 = v1.getAngleToXAxis();
		Double angle2 = v2.getAngleToXAxis();
		Double angle = Math.abs(angle1 - angle2);
		if (angle.compareTo(Math.PI) > 0) {
			return Math.PI * 2 - angle;
		}
		return angle;
	}
	
	public static Vector getPerpendicularVector(Vector v) {
		return new Vector(v.getY(), -v.getX());
	}
	
	public static Double dot(Vector v1, Vector v2) {
		return v1.getX() * v2.getX() + v1.getY() * v2.getY();
	}
	
	public static Double cross(Vector v1, Vector v2) {
		return v1.getX() * v2.getY() - v1.getY() * v2.getX();
	}
	
}
