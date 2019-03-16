package quadtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Quadtree {
	
	private TreeNode root;
	private Integer threshold;
	private INodeChecking nodeChecking;
	public final static Integer LEVEL_THRESHOLD = 7;
	
	public Quadtree(Double centerX, Double centerY, Double width, Double height) {
		this.root = new TreeNode(centerX, centerY, width, height, 0);
		this.threshold = Quadtree.LEVEL_THRESHOLD;
	}
	
	public Quadtree(Double centerX, Double centerY, Double width, Double height, Integer threshold) {
		this.root = new TreeNode(centerX, centerY, width, height, 0);		
		this.threshold = threshold;
	}
	
	public void setNodeChecking(INodeChecking nodeChecking) {
		this.nodeChecking = nodeChecking;
	}
	
	public List<TreeNode> run() {
		
		List<TreeNode> satisfiedNodes = new ArrayList<>();
		List<TreeNode> pendingNodes = new ArrayList<>(Arrays.asList(root));
		
		if (this.nodeChecking == null) {
			return satisfiedNodes;
		}
		
		while (!pendingNodes.isEmpty()) {
			TreeNode currentNode = pendingNodes.remove(0);
			if (!this.nodeChecking.check(currentNode)) {
				if (currentNode.getLevel() > this.threshold) {
					continue;
				}
				List<TreeNode> subnodes = currentNode.divide();
				pendingNodes.addAll(subnodes);
			} else {
				satisfiedNodes.add(currentNode);
			}
		}
		
		return satisfiedNodes;
	}
}
