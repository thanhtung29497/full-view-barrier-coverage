package maths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Intersections {
	
	public static Comparator<Vector> counterClockwiseOrder = new Comparator<>() {
		@Override
		public int compare(Vector v1, Vector v2) {
			return - Intersections.cross(v1, v2).intValue(); 
		}
	};
	
	public static Double calEuclideanDistance(Point p1, Point p2) {
		return Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) + (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
	}
	
	public static Vector getPerpendicularVector(Vector v) {
		return new Vector(v.getY(), -v.getX());
	}
	
	public static Double dot(Vector v1, Vector v2) {
		return v1.getX() * v2.getX() + v1.getY() * v2.getY();
	}
	
	public static Double cross(Vector v1, Vector v2) {
		return v1.getX() * v2.getY() - v1.getY() * v2.getX();
	}
	
	public static List<Point> findIntersection(Line line, LineSegment segment) {
		ArrayList<Point> intersections = new ArrayList<>();
		
		// general equation of line: ax + by = c
		Vector lineNormal = line.getNormalVector();
		Double a = lineNormal.getX();
		Double b = lineNormal.getY();
		Double c = line.getConstantFactor();
		
		// parametric equation of line segment: 
		// x = x0 + a0 * t
		// y = y0 + b0 * t with t in [0, 1]
		Vector segmentVector = segment.getVectorForm();
		Double x0 = segment.getFirstEndpoint().getX();
		Double y0 = segment.getFirstEndpoint().getY();
		Double a0 = segmentVector.getX();
		Double b0 = segmentVector.getY();
		
		// substitute x and y of segment's equation to line's equation
		// (a * a0 + b * b0) * t = c - a * x0 - b * y0
		
		Double A = a * a0 + b * b0;
		Double B = c - a * x0 - b * y0;
		
		// line is parallel to segment
		if (A == 0) {
			return intersections;
		}
		
		Double t = B/A;
		// intersection is not on the segment
		if (t > 1 || t < 0) {
			return intersections;
		}
		
		intersections.add(new Point(x0 + a0 * t, y0 + b0 * t));
		return intersections;
	}
	
	public static List<Point> findIntersection(Circle circle, LineSegment segment) {
		List<Point> intersections = new ArrayList<>();
		
		// circle's equation: (x - x_c)^2 + (y - y_c)^2 = r^2
		Double r = circle.getRadius();
		Double xc = circle.getCentre().getX();
		Double yc = circle.getCentre().getY();
		
		// parametric equation of segment: 
		// x = x0 + at
		// y = y0 + bt with t in [0,1]
		
		Double x0 = segment.getFirstEndpoint().getX();
		Double y0 = segment.getFirstEndpoint().getY();
		Double a = segment.getVectorForm().getX();
		Double b = segment.getVectorForm().getY();
		
		// substitute x and y in segment's equation to circle's equation
		// (a^2 + b^2) t^2 + 2(a.x0 - a.xc + b.y0 - b.yc) t + (x0 - xc)^2 + (y0 - yc)^2 - r^2 = 0 
		Double A = a * a + b * b;
		Double B = a * x0 - a * xc + b * y0 - b * yc;
		Double C = (x0 - xc) * (x0 - xc) + (y0 - yc) * (y0 - yc) - r * r;
		Double delta = B * B - A * C;
		
		if (delta < 0) {
			return intersections;
		} 
		if (delta == 0) {
			Double t = -B / A;
			if (0 <= t && t <= 1) {
				intersections.add(new Point(x0 + a * t, y0 + b * t));
			}	
			return intersections;
		}
		
		Double t1 = (-B + Math.sqrt(delta)) / A;
		Double t2 = (-B - Math.sqrt(delta)) / A;
		if (0 <= t1 && t1 <= 1) {
			intersections.add(new Point(x0 + a * t1, y0 + b * t1));
		}
		if (0 <= t2 && t2 <= 1) {
			intersections.add(new Point(x0 + a * t2, y0 + b * t2));
		}
		return intersections;
	}
	
	public static List<Polygon> dividePolygonByLine(Polygon polygon, Line line) {
		ArrayList<Point> setOfPoints = new ArrayList<>();
		ArrayList<Integer> intersectionIndices = new ArrayList<>();
		ArrayList<Polygon> subPolygons = new ArrayList<>();
		
		for (LineSegment side: polygon.getSides()) {
			List<Point> intersections = Intersections.findIntersection(line, side);
			
			Point firstEndpoint = side.getFirstEndpoint();
			Point secondEndpoint = side.getSecondEndpoint();
			
			setOfPoints.add(firstEndpoint);
			if (!intersections.isEmpty()) {
				Point intersection = intersections.get(0);
				if (!intersection.equals(secondEndpoint)) {
					setOfPoints.add(intersection);
					intersectionIndices.add(setOfPoints.size() - 1);
				}
			}
		}
		
		if (intersectionIndices.size() < 2) {
			subPolygons.add(polygon);
		} else {
			Integer firstIntersection = intersectionIndices.get(0);
			Integer secondIntersection = intersectionIndices.get(1);
			
			List<Point> firstSetPoint = new ArrayList<>(setOfPoints.subList(firstIntersection, secondIntersection));
			if (!setOfPoints.get(secondIntersection).equals(setOfPoints.get(secondIntersection - 1))) {
				firstSetPoint.add(setOfPoints.get(secondIntersection));
			}
			subPolygons.add(new Polygon(firstSetPoint));
			
			List<Point> secondSetPoint = new ArrayList<>(setOfPoints.subList(0, firstIntersection));
			if (!setOfPoints.get(firstIntersection).equals(setOfPoints.get(firstIntersection - 1))) {
				secondSetPoint.add(setOfPoints.get(firstIntersection));
			}
			secondSetPoint.addAll(new ArrayList<>(setOfPoints.subList(secondIntersection, setOfPoints.size())));
			subPolygons.add(new Polygon(secondSetPoint));
		}
		return subPolygons;
	}
	
	
	public static List<Point> findIntersection(Polygon polygon, LineSegment segment) {
		List<Point> intersections = new ArrayList<>();
		Line line = new Line(segment.getFirstEndpoint(), segment.getSecondEndpoint());
		
		for (LineSegment side: polygon.getSides()) {
			List<Point> intersectionLine = Intersections.findIntersection(line, side);
			if (!intersectionLine.isEmpty() && segment.contain(intersectionLine.get(0))) {
				intersections.add(intersectionLine.get(0));
			}
		}
		
		return intersections;
	}
	
	// find locus of vertex of an angle subtending a line segment (a circle) 
	public static List<Circle> findCircleBySubtendedAngle(Double angle, LineSegment segment) {
		Double length = segment.getLength();
		Double radius = length / (2 * Math.sin(angle));
		
		Point midPoint = segment.getMidPoint();
		Vector segmentVector = segment.getVectorForm();
		Vector perpendicularBisectorDirection = Intersections.getPerpendicularVector(segmentVector);
		
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
