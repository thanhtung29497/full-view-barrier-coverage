package sensor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import geometry.AxisParallelRectangle;
import geometry.Circle;
import geometry.CircularSector;
import geometry.Intersections;
import geometry.Line;
import geometry.LineSegment;
import geometry.Locus;
import geometry.Point;
import geometry.Polygon;
import geometry.Utils;
import geometry.Vector;
import quadtree.INodeChecking;
import quadtree.TreeNode;

public class FullViewChecking implements INodeChecking {
	
	private List<Sensor> setOfSensors;
	private Double fullViewAngle;
	
	public FullViewChecking(List<Sensor> setOfSensors, Double angle) {
		this.setOfSensors = setOfSensors;
		this.fullViewAngle = angle;
	}
	
	private List<Point> getCoveredList(Polygon region) {
		List<Point> coveredList = new ArrayList<>();
		for (Sensor sensor: this.setOfSensors) {
			Boolean isCovered = true;
			for (Point vertex: region.getVertices()) {
				if (!sensor.cover(vertex)) {
					isCovered = false;
				}
			}
			
			if (isCovered) {
				coveredList.add(((CircularSector)sensor.getShape()).getCentre());
			}
		}
		
		return coveredList;
	}
	
	private List<Polygon> divideRegionByCoveredList(Polygon region, List<Point> circularList) {
		List<Polygon> subregions = new ArrayList<>();
		subregions.add(region);
		
		for (int i = 0; i < circularList.size(); ++i) {
			for (int j = i + 1; j < circularList.size(); ++j) {
				Point p1 = circularList.get(i);
				Point p2 = circularList.get(j);
					
				LineSegment segment = new LineSegment(p1, p2);
			
				if (Intersections.find(region, segment).isEmpty()) {
					
					List<Polygon> newSubregions = new ArrayList<>();
					Line line = new Line(p1, p2);
					
					for (Polygon subregion: subregions) {
						newSubregions.addAll(Intersections.dividePolygonByLine(subregion, line));
					}
					
					subregions = newSubregions;
				}
			}
		}
		
		return subregions;
	}

	private List<Point> sortSensorsCounterClockwise(Point center, List<Point> setOfSensors) {
		List<Vector> vectors = new ArrayList<>();
		for (Point point: setOfSensors) {
			vectors.add(new Vector(center, point));
		}
		
		vectors.sort(Intersections.counterClockwiseOrder);

		List<Point> sortedPointSet = vectors.stream().map(vector -> vector.getDestination())
			.collect(Collectors.toList());
		
		return sortedPointSet;
	}
	
	private Boolean checkIfRegionInsideCircularList(Polygon subregion, List<Point> circularList) {
		Point centroid = subregion.getCentroid();
		
		for (int index = 0; index < circularList.size(); ++index) {
			Point p1 = circularList.get(index);
			Point p2 = circularList.get((index + 1) % circularList.size());
			Vector v1 = new Vector(centroid, p1);
			Vector v2 = new Vector(centroid, p2);
			Double angleOfTwoVectors = v2.getAngleToXAxis() - v1.getAngleToXAxis();
			if (angleOfTwoVectors < 1e-10) {
				angleOfTwoVectors += Math.PI * 2;
			}
			if (angleOfTwoVectors - Math.PI > 1e-10) {
				return false;
			}
		}
		
		return true;
	}
	
	private Boolean checkIfInSafeZone(Polygon subregion, List<Point> circularList) {
		Boolean isFullViewCovered = true;
		Point centroid = subregion.getCentroid();
		
		for (int index = 0; index < circularList.size() && isFullViewCovered; ++index) {
			Point p1 = circularList.get(index);
			Point p2 = circularList.get((index + 1) % circularList.size());
			LineSegment segment = new LineSegment(p1, p2);
			List<Circle> unsafeCircles = Locus.findCircleBySubtendedAngle(this.fullViewAngle, segment);
			
			for (Circle unsafeCircle: unsafeCircles) {
				for (LineSegment side: subregion.getSides()) {
					List<Point> intersections = Intersections.find(unsafeCircle, side);
					if (!intersections.isEmpty()) {
						isFullViewCovered = false;
						break;
					}
				}
				
				if (Utils.calEuclideanDistance(unsafeCircle.getCentre(), centroid).compareTo(unsafeCircle.getRadius()) <= 0) {
					isFullViewCovered = false;
					break;
				}
			}
		}
		
		return isFullViewCovered;
	}
	
	@Override
	public Boolean check(TreeNode node) {
		
		if (node.getSizeX() >= Config.SENSOR_RADIUS) {
			return false;
		}
		
		AxisParallelRectangle region = new AxisParallelRectangle(node.getUpperleft(), node.getSizeX(), node.getSizeY());
		List<Point> coveredList = this.getCoveredList(region);
		if (coveredList.isEmpty()) {
			return false;
		}
		
		List<Polygon> subregions = this.divideRegionByCoveredList(region, coveredList);
		
		for (Polygon subregion: subregions) {
			Point centroid = subregion.getCentroid();
			List<Point> circularList = this.sortSensorsCounterClockwise(centroid, coveredList);
			
			//if(circularList.size()<Math.PI/Config.THETA) return false;
			
			if (!this.checkIfRegionInsideCircularList(subregion, circularList)
					|| !this.checkIfInSafeZone(subregion, circularList)) {
				return false;
			}
		}
		
		return true;
			
		
	}
	
	public static void main(String argv[]) {
		Point A = new Point(4.0, 2.0);
		Point B = new Point(0.0, 0.0);
		Point C = new Point(4.0, 0.0);
		Point D = new Point(0.0, 2.0);
		Point E = new Point(2.0, 8.0);
		Point F = new Point(0.0, 6.0);
		Point G = new Point(4.0, 6.0);
		Point I = new Point(2.0, -2.0);
		Polygon region = new Polygon(Arrays.asList(A, D, B, C));
		TreeNode node = new TreeNode(region.getCentroid().getX(), region.getCentroid().getY(),
				Utils.calEuclideanDistance(A, D), Utils.calEuclideanDistance(A, C), 0);
		List<Sensor> sensors = Arrays.asList(E, F, G, I).stream().map(point -> {
			return new Sensor(new CircularSector(point, Config.SENSOR_RADIUS, Math.PI * 2, Math.PI * 3 / 2));
		}).collect(Collectors.toList());
		FullViewChecking checking = new FullViewChecking(sensors, Config.THETA);
		if (checking.check(node)) {
			System.out.println("Full view");
		} else {
			System.out.println("Not full view");
		}
		
	}
	
}
