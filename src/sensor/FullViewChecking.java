package sensor;

import java.util.ArrayList;
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
		try {
		vectors.sort(Intersections.counterClockwiseOrder);
		} catch (Exception e) {
			System.out.println("");
		}
		List<Point> sortedPointSet = vectors.stream().map(vector -> vector.getDestination())
			.collect(Collectors.toList());
		
		return sortedPointSet;
	}
	
	private Boolean checkIfFullViewCovered(Polygon subregion, List<Point> circularList) {
		Boolean isFullViewCovered = true;
		
		for (int index = 0; index < circularList.size() && isFullViewCovered; ++index) {
			Point p1 = circularList.get(index);
			Point p2 = circularList.get((index + 1) % circularList.size());
			LineSegment segment = new LineSegment(p1, p2);
			List<Circle> subtendingCircles = Locus.findCircleBySubtendedAngle(this.fullViewAngle, segment);
			
			for (Circle circle: subtendingCircles) {
				for (LineSegment side: subregion.getSides()) {
					List<Point> intersections = Intersections.find(circle, side);
					if (!intersections.isEmpty()) {
						isFullViewCovered = false;
						break;
					}
				}
				
				for (Point vertex: subregion.getVertices()) {
					if (Utils.calEuclideanDistance(circle.getCentre(), vertex).compareTo(circle.getRadius()) <= 0) {
						isFullViewCovered = false;
						break;
					}
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
			if(circularList.size()<Math.PI/Config.THETA) return false;
			if (!this.checkIfFullViewCovered(subregion, circularList)) {
				return false;
			}
		}
		
		return true;
			
		
	}
	
}
