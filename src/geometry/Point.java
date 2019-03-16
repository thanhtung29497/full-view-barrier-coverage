package geometry;

public class Point {
	private Double x;
	private Double y;
	private Boolean isNull = false;
	
	public Point(Double x, Double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point() {
		this.isNull = true;
	}
	
	public Double getX() {
		return this.x;
	}
	
	public Double getY() {
		return this.y;
	}
	
	public Boolean isNull() {
		return this.isNull;
	}
	
	public Boolean equals(Point other) {
		return this.x.equals(other.getX()) && this.y.equals(other.getY());
	}
	
	@Override
	public String toString()
	{
		return "(" + this.x + ", " + this.y + ")";
	}
}
