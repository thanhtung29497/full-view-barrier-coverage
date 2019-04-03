package sensor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import geometry.Point;
import geometry.Polygon;
import geometry.Vector;

public class KOmegaCoverage {
	
	private Integer k;
	private Double omega;
	private List<Point> coveredList;
	private List<Integer> openSet;
	private Point chosenPoint;
	private List<Point> circularList;
	private Polygon region;
	private Boolean isKOmegaCovered;
	
	public KOmegaCoverage(Integer k, Double omega, List<Point> coveredList) {
		this.k = k;
		this.omega = omega;
		this.coveredList = coveredList;
	}
	
	public boolean backtrackOnePoint(int openSetId, int closeSetId){
		
		for (int index = closeSetId + 1; index < this.circularList.size(); ++index) {
			
			Point currentPoint = this.circularList.get(index);
			Vector v1 = new Vector(this.chosenPoint, this.circularList.get(this.openSet.get(openSetId - 1)));
			Vector v2 = new Vector(this.chosenPoint, currentPoint);
			
			if (Vector.getAngleOfTwoVectors(v1, v2) - this.omega > -1e-10) {
				this.openSet.add(index);
				
				if (openSetId == this.k - 1) {
					return true;
				} else if (this.backtrackOnePoint(openSetId + 1, index)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void shiftOpensetOneIndex() {
		for (int index = 0; index < this.openSet.size(); ++ index) {
			Integer openSetId = this.openSet.get(index);
			
			this.openSet.set(index, (openSetId + 1) % this.circularList.size());
		}
	}
	
	public Boolean checkBasicGroup(Polygon region) {
        
		this.chosenPoint = region.getVertices().get(0);
		this.circularList = Utils.sortSensorsCounterClockwise(this.chosenPoint, this.coveredList);
		this.openSet = new ArrayList<>();
		this.openSet.add(0);
		
		this.backtrackOnePoint(1, 0);
		
		List<Point> basicGroup = this.openSet.stream().map(index -> this.circularList.get(index)).collect(Collectors.toList());

        if (basicGroup.size() < this.k) {
        	
            Boolean kOmegaCovered = true;
            do {
            	for (Point vertex: region.getVertices()) {
            		if (!this.checkPoint(vertex, basicGroup)) {
            			kOmegaCovered = false;
            			break;
            		}
            	}
                
            	if (kOmegaCovered) {
            		return true;
            	}
            	
            	if (this.openSet.get(this.openSet.size() - 1) != this.circularList.size() - 1) {
            		this.shiftOpensetOneIndex();
            	} else {
            		break;
            	}
                
            } while (true);
        }
        
        return false;
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
		
		if (coveredList.isEmpty()) {
        	return false;
        }
		
		this.chosenPoint = region.getVertices().get(0);
		this.circularList = Utils.sortSensorsCounterClockwise(this.chosenPoint, this.coveredList);
		this.region = region;
		this.isKOmegaCovered = false;
		
		for (int index = 0; index < this.circularList.size(); ++index) {
			this.openSet = new ArrayList<>();
			this.openSet.add(index);
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
			Vector v1 = new Vector(this.chosenPoint, this.circularList.get(this.openSet.get(openSetId - 1)));
			Vector v2 = new Vector(this.chosenPoint, currentPoint);
			Vector v0 = new Vector(this.chosenPoint, this.circularList.get(this.openSet.get(0)));
			
			if (Vector.getAngleOfTwoVectors(v1, v2) - this.omega > 0 &&
					(Math.PI * 2 - Vector.getAngleOfTwoVectors(v0, v2) / (this.k - openSetId) > this.omega)) {
				this.openSet.add(index);
				
				if (openSetId == this.k - 1) {
					
					Boolean found = true;
					List<Point> candidateGroup = this.openSet.stream()
							.map(id -> this.circularList.get(id))
							.collect(Collectors.toList());
					
					for (Point vertex: this.region.getVertices()) {
						if (!this.checkPoint(vertex, candidateGroup)) {
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
		for (Integer index: this.openSet) {
			System.out.println(this.circularList.get(index));
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
