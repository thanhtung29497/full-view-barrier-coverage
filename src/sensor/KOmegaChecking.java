package sensor;

import java.util.List;

import geometry.AxisParallelRectangle;
import geometry.Point;
import quadtree.INodeChecking;
import quadtree.TreeNode;

public class KOmegaChecking implements INodeChecking {
	
	private List<Sensor> setOfSensors;
	private Integer k;
	private Double omega;
	
	public KOmegaChecking(List<Sensor> setOfSensors, Integer k, Double omega) {
		this.setOfSensors = setOfSensors;
		this.k = k;
		this.omega = omega;
	}

	@Override
	public Boolean check(TreeNode node) {
		
		AxisParallelRectangle region = new AxisParallelRectangle(node.getUpperleft(), node.getSizeX(), node.getSizeY());
		List<Point> coveredList = Utils.getCoveredList(region, this.setOfSensors);
		
		KOmegaCoverage coverage = new KOmegaCoverage(this.k, this.omega, coveredList);
		return coverage.checkRegion(region);
	}

}
