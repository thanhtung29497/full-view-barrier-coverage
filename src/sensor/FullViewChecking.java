package sensor;

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

	
	
	private Boolean checkIfRegionInsideCircularList(Polygon subregion, List<Point> circularList) {
		Point centroid = subregion.getCentroid();
		
		for (int index = 0; index < circularList.size(); ++index) {
			Point p1 = circularList.get(index);
			Point p2 = circularList.get((index + 1) % circularList.size());
			Vector v1 = new Vector(centroid, p1);
			Vector v2 = new Vector(centroid, p2);
			Double angleOfTwoVectors = Vector.getAngleOfTwoVectors(v1, v2);
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
				
				if (geometry.Utils.calEuclideanDistance(unsafeCircle.getCentre(), centroid).compareTo(unsafeCircle.getRadius()) <= 0) {
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
		List<Point> coveredList = Utils.getCoveredList(region, this.setOfSensors);
		if (coveredList.isEmpty()) {
			return false;
		}
		
		List<Polygon> subregions = this.divideRegionByCoveredList(region, coveredList);
		
		for (Polygon subregion: subregions) {
			Point centroid = subregion.getCentroid();
			List<Point> circularList = Utils.sortSensorsCounterClockwise(centroid, coveredList);
			
			if(circularList.size()<Math.PI/Config.THETA) return false;
			
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
				geometry.Utils.calEuclideanDistance(A, D), geometry.Utils.calEuclideanDistance(A, C), 0);
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
