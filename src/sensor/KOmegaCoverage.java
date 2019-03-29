package sensor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import geometry.Point;
import geometry.Polygon;
import geometry.Vector;

public class KOmegaCoverage {
	
	private Integer k;
	private Double omega;
	private List<Point> coveredList;
	private List<Point> openSet;
	private Point chosenPoint;
	private List<Point> circularList;
	private Polygon region;
	private Boolean isKOmegaCovered;
	
	public KOmegaCoverage(Integer k, Double omega, List<Point> coveredList) {
		this.k = k;
		this.omega = omega;
		this.coveredList = coveredList;
	}
	
	public Boolean checkPoint(Point givenPoint, List<Point> kPoints) {
		List<Point> sortedPoint = Utils.sortSensorsCounterClockwise(givenPoint, kPoints);
		
		for (int index = 0; index < sortedPoint.size(); ++index) {
			Point p1 = kPoints.get(index);
			Point p2 = kPoints.get((index + 1) % kPoints.size());
			Vector v1 = new Vector(givenPoint, p1);
			Vector v2 = new Vector(givenPoint, p2);
			Double angle = Vector.getAngleOfTwoVectors(v1, v2); 
			if (angle - this.omega < 1e-10 || angle - Math.PI > -1e-10) {
				return false;
			}
		}
		
		return true;
	}
	
	public Boolean checkRegion(Polygon region) {
		this.chosenPoint = region.getCentroid();
		this.region = region;
		this.circularList = Utils.sortSensorsCounterClockwise(this.chosenPoint, this.coveredList);
		
		this.isKOmegaCovered = false;
		
		for (int index = 0; index < this.circularList.size(); ++index) {
			this.openSet = new ArrayList<>();
			this.openSet.add(this.circularList.get(index));
			this.backtrack(1, index);
			if (this.isKOmegaCovered) {
				return true;
			}
		}
		
		return false;
	}
	
	private void backtrack(int openSetId, int closeSetId) {
		if (this.isKOmegaCovered) {
			return;
		}
		
		for (int index = closeSetId + 1; index < this.circularList.size() && !this.isKOmegaCovered; ++index) {
			
			Point currentPoint = this.circularList.get(index);
			Vector v1 = new Vector(this.chosenPoint, this.openSet.get(openSetId - 1));
			Vector v2 = new Vector(this.chosenPoint, currentPoint);
			
			if (Vector.getAngleOfTwoVectors(v1, v2) - this.omega > -1e-10) {
				this.openSet.add(currentPoint);
				
				if (openSetId == this.k - 1) {
					
					Boolean found = true;
					for (Point vertex: this.region.getVertices()) {
						if (!this.checkPoint(vertex, openSet)) {
							found = false;
							break;
						}
					}
					
					if (found) {
						this.isKOmegaCovered = true;
						return;
					}
				} else {
					this.backtrack(openSetId + 1, index);
				}
				
				if (!this.isKOmegaCovered) {
					this.openSet.remove(this.openSet.size() - 1);
				}
			}
		}
	}
	
	public void printOpenSet() {
		for (Point point: this.openSet) {
			System.out.println(point);
		}
	}
	
	public static void main(String argv[]) {
		Point A = new Point(0.0, 2.0);
		Point B = new Point(0.0, 0.0);
		Point C = new Point(2.0, 0.0);
		Point D = new Point(2.0, 2.0);
		Point E = new Point(0.0, 4.0);
		Point F = new Point(3.0, 3.0);
		Point G = new Point(-2.0, -2.0);
		Point H = new Point(3.0, -2.0);
		Point K = new Point(-1.0, 4.0);
		Point I = new Point(1.0, -2.0);
		List<Point> sensors = Arrays.asList(E, K, G, H, F, I);
		Polygon region = new Polygon(Arrays.asList(A, B, C, D));
		KOmegaCoverage cover = new KOmegaCoverage(4, Math.PI / 6, sensors);
		if (cover.checkRegion(region)) {
			cover.printOpenSet();
		} else {
			System.out.println("Not K-Omega covered");
		}
		
		
	}
}
