package geometry;

import java.util.Arrays;
import java.util.List;

public class Locus {
	// find locus of vertex of an angle subtending a line segment (a circle) 
	public static List<Circle> findCircleBySubtendedAngle(Double angle, LineSegment segment) {
		Double length = segment.getLength();
		Double radius = length / (2 * Math.sin(angle));
		
		Point midPoint = segment.getMidPoint();
		Vector segmentVector = segment.getVectorForm();
		Vector perpendicularBisectorDirection = Vector.getPerpendicularVector(segmentVector);
		
		// parametric equation of perpendicular bisector of segment
		// x = at + xm
		// y = bt + ym
		Double a = perpendicularBisectorDirection.getX();
		Double b = perpendicularBisectorDirection.getY();
		Double xm = midPoint.getX();
		Double ym = midPoint.getY();
		
		if (angle.equals(Math.PI / 2)) {
			Point centre = new Point(xm, ym);
			return Arrays.asList(new Circle(centre, radius));
		}
		
		// distance between centre and midpoint
		// (x - x_m)^2 + (y - y_m)^2 = d^2
		
		Double distanceCentreAndMidPoint = length / (2 * Math.tan(angle));
		Double t = distanceCentreAndMidPoint / Math.sqrt(a * a + b * b);
		
		Point firstCentre = new Point(a * t + xm, b * t + ym);
		Point secondCentre = new Point(-a * t + xm, -b * t + ym);
		
		return Arrays.asList(
				new Circle(firstCentre, radius),
				new Circle(secondCentre, radius));
	}
}
