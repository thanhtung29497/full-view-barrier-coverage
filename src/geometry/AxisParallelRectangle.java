package geometry;

import java.util.ArrayList;
import java.util.Arrays;

public class AxisParallelRectangle extends Polygon {
	
	private Double height;
	private Double width;
	
	public AxisParallelRectangle(Point upperleft, Double width, Double height) {
		super();
		Point upperright = new Point(upperleft.getX() + width, upperleft.getY());
		Point lowerleft = new Point(upperleft.getX(), upperleft.getY() - height);
		Point lowerright = new Point(upperright.getX(), upperright.getY() - height);
		this.vertices = new ArrayList<>(Arrays.asList(upperleft, upperright, lowerright, lowerleft));
		this.height = height;
		this.width = width;
	}
	
	public Double getHeight() {
		return this.height;
	}
	
	public Double getWidth() {
		return this.width;
	}
}
