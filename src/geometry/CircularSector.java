package geometry;

public class CircularSector implements IShape {
	private Point centre;
	private Double radius;
	private Double angle;
	private Double direction;
	
	public CircularSector (Point centre, Double radius, Double angle, Double direction) {
		this.centre = centre;
		this.radius = radius;
		this.angle = angle;
		this.direction = direction;
	}
	
	public Point getCentre() {
		return this.centre;
	}
	
	public Double getRadius() {
		return this.radius;
	}
	
	public Double getAngle() {
		return this.angle;
	}
	
	public Double getDirection() {
		return this.direction;
	}

	@Override
	public Boolean contain(Point p) {
		Double distanceFromCenter = Utils.calEuclideanDistance(p, this.centre);
		Vector pointToCenter = new Vector(this.centre, p);
		Double angleToCenter = pointToCenter.getAngleToXAxis();
		
		Double angleOfTwoVectors = Math.abs(this.direction - angleToCenter);
		if (angleOfTwoVectors.compareTo(Math.PI) > 0) {
			angleOfTwoVectors = Math.PI * 2 - angleOfTwoVectors;
		}
		
		return distanceFromCenter - this.radius <= 1e-10 
				&& angleOfTwoVectors - this.angle <= 1e-10;
		
	}
}
