package sweepline;

public class Rectangle2D {
	public Point2D topLeft;
	public Point2D bottomRight;
	public int id;
	public Rectangle2D(Point2D p1, Point2D p2) {
		if(p1.x > p2.x) {
			topLeft = p2;
			bottomRight = p1;
		}
		topLeft = p1;
		bottomRight = p2;
	}
	
	public Rectangle2D(Point2D p1, Point2D p2, int id) {
		if (p1.x > p2.x) {
			topLeft = p2;
			bottomRight = p1;
		}
		topLeft = p1;
		bottomRight = p2;
		this.id = id;
	}
	
	public Interval getInter() {
		return new Interval(bottomRight.y, topLeft.y, id);
	}
	
	public Double getWidth() {
		return bottomRight.x - topLeft.x;
	}
	
	public Double getHeight() {
		return bottomRight.y - topLeft.y;
	}
	
	public Double getArea() {
		return getWidth() * getHeight();
	}
	
	public boolean compares(Rectangle2D other) {
		return topLeft.comparesTo(other.topLeft);
	}
	
	public Double iou(Rectangle2D other) {
		Double x1 = Math.max(topLeft.x, other.topLeft.x);
		Double x2 = Math.min(bottomRight.x, other.bottomRight.x);
		Double y1 = Math.max(topLeft.y, other.topLeft.y);
		Double y2 = Math.min(bottomRight.y, other.bottomRight.y);
		
		return (x2 - x1) * (y2 - y1);
	}
}
