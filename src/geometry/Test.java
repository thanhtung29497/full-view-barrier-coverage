package geometry;

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
		List<Point> intersections = Intersections.find(circle, segment);
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
		List<Point> intersections = Intersections.find(line, segment);
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
		List<Circle> circles = Locus.findCircleBySubtendedAngle(angle, new LineSegment(A, C));
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
		Point A = new Point(0.0, 0.0);
		Point Q = new Point(50.0, -100.0);
		Point R = new Point(200.0, -150.0);
		Point B = new Point(240.0, 0.0);
		Point P = new Point(220.0, 80.0);
		Point O = new Point(150.0, 100.0);
		Point S = new Point(50.0, 50.0);
		Point T = new Point(79.0, 8.0);
		Point U = new Point(94.0, -32.0);
		Point V = new Point(131.0, -26.0);
		Point W = new Point(148.0, -15.0);
		Point K = new Point(160.0, 0.0);
		Point L = new Point(160.0, 16.0);
		
		Double theta = Math.PI / 180 * 80;
		List<Point> pointSet = Arrays.asList(A, Q, R, B, P, O, S);
		Polygon polygon = new Polygon(Arrays.asList(T, U, V, W, K, L));
		List<Polygon> subPolygons = new ArrayList<>();
		subPolygons.add(polygon);
		
		for (int i = 0; i < pointSet.size(); ++i) {
			for (int j = i + 1; j < pointSet.size(); ++j) {
				Point p1 = pointSet.get(i);
				Point p2 = pointSet.get(j);
					
				LineSegment segment = new LineSegment(p1, p2);
				if (p1.getX().equals(50.0) && p1.getY().equals(-100.0) 
						&& p2.getX().equals(150.0) && p2.getY().equals(100.0)) {
					System.out.println();
				}
				if (Intersections.find(polygon, segment).isEmpty()) {
					
					List<Polygon> newSubPolygons = new ArrayList<>();
					Line line = new Line(p1, p2);
					
					for (Polygon subPolygon: subPolygons) {
						List<Polygon> polygons = Intersections.dividePolygonByLine(subPolygon, line);
						if (polygons.size() > 1) {
							System.out.println();
						}
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
				List<Circle> subtendingCircles = Locus.findCircleBySubtendedAngle(theta, segment);
				
				for (Circle circle: subtendingCircles) {
					for (LineSegment side: subPolygon.getSides()) {
						List<Point> intersections = Intersections.find(circle, side);
						if (!intersections.isEmpty()) {
							isFullViewCovered = false;
							break;
						}
					}
					
					for (Point vertex: subPolygon.getVertices()) {
						if (Utils.calEuclideanDistance(circle.getCentre(), vertex).compareTo(circle.getRadius()) <= 0) {
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
	
	public static void testCircularSectorAndPoint() {
		Point A = new Point(2.0, 4.0);
		Point B = new Point(0.0, 1.0);
		Point C = new Point(4.0, 1.0);
		Point D = new Point(0.0, 1.0);
		CircularSector sector = new CircularSector(A, new LineSegment(A, B).getLength(), 67.38/180 * Math.PI, Math.PI / 2 * 3);
		if (sector.contain(D)) {
			System.out.println("Contain");
		} else {
			System.out.println("Not Contain");
		}
	}
	
	public static void main(String argv[]) {
		Test.testCircularSectorAndPoint();
	}
	
}
