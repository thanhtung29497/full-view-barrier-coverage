package sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import geometry.CircularSector;
import geometry.Point;
import geometry.Polygon;
import geometry.Vector;

public class Utils {
	
	public static List<Point> sortSensorsCounterClockwise(Point center, List<Point> setOfSensors) {
		List<Vector> vectors = new ArrayList<>();
		for (Point point: setOfSensors) {
			vectors.add(new Vector(center, point));
		}
		
		vectors.sort(Vector.counterClockwiseOrder);

		List<Point> sortedPointSet = vectors.stream().map(vector -> vector.getDestination())
			.collect(Collectors.toList());
		
		return sortedPointSet;
	}
	
	public static List<Point> getCoveredList(Polygon region, List<Sensor> sensors) {
		List<Point> coveredList = new ArrayList<>();
		for (Sensor sensor: sensors) {
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
}
