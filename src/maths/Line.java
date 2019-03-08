package maths;

public class Line {
	
	// normal: vector that is perpendicular to the line
	// direction: vector that is parallel to the line 
	
	private Vector normal;
	private Vector direction;
	private Point givenPoint;
	
	public Line(Point p1, Point p2) {
		this.givenPoint = p1;
		this.direction = new Vector(p1.getX() - p2.getX(), p1.getY() - p2.getY());
		this.normal = Intersections.getPerpendicularVector(this.direction);
	}
	
	public Vector getNormalVector() {
		return this.normal;
	}
	
	public Vector getDirectionVector() {
		return this.direction;
	}
	
	// c in general equation: ax + by = c
	public Double getConstantFactor() {
		return this.normal.getX() * this.givenPoint.getX() + this.normal.getY() * this.givenPoint.getY();
	}
	
	public Boolean contains(Point p) {
		Double x0 = this.givenPoint.getX();
		Double y0 = this.givenPoint.getY();
		Double a = this.normal.getX();
		Double b = this.normal.getY();
		Double x = p.getX();
		Double y = p.getY();
		return a * (x - x0) == b * (y - y0);
	}
}
