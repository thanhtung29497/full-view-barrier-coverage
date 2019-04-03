package sweepline;

public final class Point2D  {
	
	public static final Point2D ORIGIN = new Point2D(0.0, 0.0);
	public Double x, y;
	public int hash;	
	
	public Point2D() {
		this(0.0, 0.0);
	}
	
	public Point2D(int min, int max, int offset) {
		x = (Math.random() * (max - min) + max);
		y = (Math.random() * (max -min - offset) + offset + min);
	}
	
	public Point2D(Double x, Double y) {
		this.x = x;
		this.y = y;
//		hash = (x.hashCode() * 23) ^ (y.hashCode() * 17);
	}
	
	public Point2D(Point2D other) {
		x = other.x;
		y = other.y;
	}
	
	public Double distaince(Point2D other) {
		return Math.sqrt((x - other.x) * (x - other.x) + 
				(y - other.y) * (y - other.y));
	}
	
	public boolean equals(Point2D other) {
		return Math.abs(y - other.y) < 1.0e-9 && Math.abs(x - other.x) < 1.0e-9;
	}
	
	public boolean notEquals(Point2D other) {
		return Math.abs(y - other.y) > 1.0e-9 || Math.abs(x - other.x) > 1.0e-9;
	}
	// 0 --> this, q and r are colinear 
	// 1 --> Clockwise 
	// 2 --> Counterclockwise 
	public int orientation(Point2D q, Point2D r) {
		Double val = (q.y - y) * (r.x - x)  - (q.x - x) * (r.y - y);
		if(Math.abs(val) < 1.0e-9) return 0; // colinear
		return Math.abs(val) > 1.0e-9? 1: 2;
	}
	
	public boolean comparesTo(Point2D other) {
		if (x < other.x - 1.0e-9)
			return true;
		else if (x > other.x + 1.0e-9)
			return false;
		else if (y < other.y)
			return true;
		return false;	
	}
}
