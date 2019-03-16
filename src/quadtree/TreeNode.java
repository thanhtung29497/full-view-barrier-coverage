package quadtree;

import java.util.Arrays;
import java.util.List;

import geometry.Point;
import quadTree.Node;

public class TreeNode {
	
	private Double centerX;
	private Double centerY;
	private Double sizeX;
	private Double sizeY;
	private Integer level;
	
	public TreeNode(Double centerX, Double centerY, Double sizeX, Double sizeY, Integer level) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.level = level;
	}
	
	public List<TreeNode> divide() {
		Double newSizeX = sizeX / 2;
		Double newSizeY = sizeY / 2;
		TreeNode upperleftChild = new TreeNode(centerX - newSizeX/2, centerY + newSizeY/2, newSizeX, newSizeY, level + 1);
		TreeNode upperrightChild = new TreeNode(centerX + newSizeX/2, centerY + newSizeY/2, newSizeX, newSizeY, level + 1);
		TreeNode lowerleftChild = new TreeNode(centerX - newSizeX/2, centerY - newSizeY/2, newSizeX, newSizeY, level + 1);
		TreeNode lowerrightChild = new TreeNode(centerX + newSizeX/2, centerY - newSizeY/2, newSizeX, newSizeY, level + 1);
		return Arrays.asList(upperleftChild, upperrightChild, lowerleftChild, lowerrightChild);
	}
	
	public Point getCenter() {
		return new Point(centerX, centerY);
	}
	
	public Double getSizeY() {
		return this.sizeY;
	}
	
	public Double getSizeX() {
		return this.sizeX;
	}
	
	public Integer getLevel() {
		return this.level;
	}
	
	public Point getUpperleft() {
		return new Point(centerX - sizeX / 2, centerY + sizeY / 2);
	}
	
}
