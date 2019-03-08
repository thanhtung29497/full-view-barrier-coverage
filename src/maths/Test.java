package maths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Test {

	static void testCircleAndSegment() {
		Point N = new Point(50.0, 40.0);
		Point O = new Point(50.0, 70.0);
		Point P = new Point(70.0, 60.0);
		Circle circle = new Circle (N, 20.0);
		LineSegment segment = new LineSegment(O, P);
		List<Point> intersections = Intersections.findIntersection(circle, segment);
		for (Point p: intersections) {
			System.out.println(p.getX() + "," + p.getY());
		}
	}
	
	static void testLineAndSegment() {
		Point A = new Point(5.0, 5.0);
		Point B = new Point(0.0, 0.0);
		Point C = new Point(10.0, 10.0);
		Point D = new Point(15.0, 5.0);
		Line line = new Line(A, B);
		LineSegment segment = new LineSegment(C, D);
		List<Point> intersections = Intersections.findIntersection(line, segment);
		if (intersections.isEmpty()) {
			System.out.println("Line and segment do not intersect");
		} else {
			Point intersection = intersections.get(0);
			System.out.println("Line and segment intersect at point (" + intersection.getX() + "," + intersection.getY() + ")");
		}
	}
	
	static void testCircleSubtendAngle() {
		Point A = new Point(0.0, 20.0);
		Point C = new Point(20.0, 10.0);
		Double angle = Math.PI / 3.0;
		List<Circle> circles = Intersections.findCircleBySubtendedAngle(angle, new LineSegment(A, C));
		for (Circle circle: circles) {
			System.out.println("(" + circle.getCentre().getX() + "," + circle.getCentre().getY() + ")");
			System.out.println(circle.getRadius());
		}
	}
	
	static void testPolygonAndLine() {
		Point A = new Point(5.0, 10.0);
		Point B = new Point(0.0, 0.0);
		Point C = new Point(15.0, 0.0);
		Point D = new Point(15.0, 5.0);
		Point E = new Point(0.0, 20.0);
		Point F = new Point(-20.0, 0.0);
		Polygon polygon = new Polygon(Arrays.asList(A, B, C, D));
		Line line = new Line(E, F);
		List<Polygon> subPolygons = Intersections.dividePolygonByLine(polygon, line);
		for (Polygon subPolygon: subPolygons) {
			subPolygon.print();
		}
	}
	
	static void testPolygonAndPointSet() {
		Point A = new Point(0.0, 20.0);
		Point B = new Point(0.0, 0.0);
		Point C = new Point(30.0, 0.0);
		Point D = new Point(30.0, 20.0);
		Point E = new Point(30.0, 50.0);
		Point F = new Point(20.0, 30.0);
		Point G = new Point(-10.0, 40.0);
		Point H = new Point(0.0, 30.0);
		Double theta = Math.PI / 3.0;
		List<Point> pointSet = Arrays.asList(E, F, G, H);
		Polygon polygon = new Polygon(Arrays.asList(A, B, C, D));
		List<Polygon> subPolygons = new ArrayList<>();
		subPolygons.add(polygon);
		
		for (int i = 0; i < pointSet.size(); ++i) {
			for (int j = i + 1; j < pointSet.size(); ++j) {
				Point p1 = pointSet.get(i);
				Point p2 = pointSet.get(j);
					
				LineSegment segment = new LineSegment(p1, p2);
				if (Intersections.findIntersection(polygon, segment).isEmpty()) {
					
					List<Polygon> newSubPolygons = new ArrayList<>();
					Line line = new Line(p1, p2);
					
					for (Polygon subPolygon: subPolygons) {
						List<Polygon> polygons = Intersections.dividePolygonByLine(subPolygon, line);
						newSubPolygons.addAll(polygons);
					}
					
					subPolygons = newSubPolygons;
				}
			}
		}
		
		for (Polygon subPolygon: subPolygons) {
			subPolygon.print();
			Point centroid = subPolygon.getCentroid();
			
			List<Vector> vectors = new ArrayList<>();
			for (Point point: pointSet) {
				vectors.add(new Vector(centroid, point));
			}
			vectors.sort(Intersections.counterClockwiseOrder);
			List<Point> sortedPointSet = vectors.stream().map(vector -> vector.getDestination())
				.collect(Collectors.toList());
			
			Boolean isFullViewCovered = true;
			for (int index = 0; index < sortedPointSet.size() && isFullViewCovered; ++index) {
				Point p1 = sortedPointSet.get(index);
				Point p2 = sortedPointSet.get((index + 1) % sortedPointSet.size());
				LineSegment segment = new LineSegment(p1, p2);
				List<Circle> subtendingCircles = Intersections.findCircleBySubtendedAngle(theta, segment);
				
				for (Circle circle: subtendingCircles) {
					for (LineSegment side: subPolygon.getSides()) {
						List<Point> intersections = Intersections.findIntersection(circle, side);
						if (!intersections.isEmpty()) {
							isFullViewCovered = false;
							break;
						}
					}
					
					for (Point vertex: subPolygon.getVertices()) {
						if (Intersections.calEuclideanDistance(circle.getCentre(), vertex).compareTo(circle.getRadius()) <= 0) {
							isFullViewCovered = false;
							break;
						}
					}
				}
			}
			
			if (isFullViewCovered) {
				System.out.println("This region is full-view covered");
			} else {
				System.out.println("This region is not full-view covered");
			}
		}
		
	}
	
	public static void main(String argv[]) {
		Test.testPolygonAndPointSet();
	}
	
}
