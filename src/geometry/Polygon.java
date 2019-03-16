package geometry;

import java.util.ArrayList;
import java.util.List;

public class Polygon {
	
	// set of vertices should be sorted in clockwise order 
	
	protected List<Point> vertices;
	
	protected Polygon() {
		this.vertices = new ArrayList<>();
	}
	
	public Polygon(List<Point> vertices) {
		this.vertices = vertices;
	}
	
	public List<Point> getVertices() {
		return new ArrayList<>(this.vertices);
	}
	
	public ArrayList<LineSegment> getSides() {
		ArrayList<LineSegment> sides = new ArrayList<>();
		
		for (int index = 0; index < this.vertices.size(); ++index) {
			Point origin = this.vertices.get(index);
			Point destination = this.vertices.get((index + 1) % this.vertices.size());
			sides.add(new LineSegment(origin, destination));
		}
		
		return sides;
	}
	
	public Point getCentroid() {
		Double centroidX = 0.0;
		Double centroidY = 0.0;
		
		for (Point vertex: this.vertices) {
			centroidX += vertex.getX();
			centroidY += vertex.getY();
		}
		
		centroidX /= this.vertices.size();
		centroidY /= this.vertices.size();
		
		return new Point(centroidX, centroidY);
	}
	
	void print() {
		System.out.println("Polygon has " + this.vertices.size() + " vertices");
		for (Point vertex: this.vertices) {
			System.out.println("(" + vertex.getX() + "," + vertex.getY() + ")");
		}
	}
}
