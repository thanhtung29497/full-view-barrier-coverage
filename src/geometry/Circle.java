package geometry;

public class Circle {
	private Point centre;
	private Double radius;
	
	public Circle(Point centre, Double radius) {
		this.centre = centre;
		this.radius = radius;
	}
	
	public Point getCentre() {
		return this.centre;
	}
	
	public Double getRadius() {
		return this.radius;
	}
	
	
	
}
