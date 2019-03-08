package maths;

import java.util.ArrayList;

public class LineSegment {
	private ArrayList<Point> endpoints;
	
	public LineSegment(Point p1, Point p2) {
		this.endpoints = new ArrayList<>();
		this.endpoints.add(p1);
		this.endpoints.add(p2);
	}
	
	public ArrayList<Point> getEndpoints() {
		return this.endpoints;
	}
	
	public Point getFirstEndpoint() {
		return this.endpoints.get(0);
	}
	
	public Point getSecondEndpoint() {
		return this.endpoints.get(1);
	}
	
	public Vector getVectorForm() {
		return new Vector(this.getFirstEndpoint(), this.getSecondEndpoint()); 
	}
	
	public Double getLength() {
		return Intersections.calEuclideanDistance(this.getFirstEndpoint(), this.getSecondEndpoint());
	}
	
	public Point getMidPoint() {
		Point first = this.getFirstEndpoint();
		Point second = this.getSecondEndpoint();
		return new Point((first.getX() + second.getX()) / 2.0, (first.getY() + second.getY()) / 2.0);
	}
	
	public Boolean contain(Point C) {
		Point A = this.getFirstEndpoint();
		Point B = this.getSecondEndpoint();
		// --> check if C is between A and B
		
		Vector AB = new Vector(A, B);
		Vector AC = new Vector(A, C);
		
		Double crossProduct = Intersections.cross(AB, AC);
		if (!crossProduct.equals(0.0)) {
			return false;
		}
		
		// --> A, B, C are aligned	
		
		Double ABdotAC = Intersections.dot(AB, AC);
		Double ABdotAB = Intersections.dot(AB, AB);
		return 0 <= ABdotAC && ABdotAC <= ABdotAB;

	}
}
