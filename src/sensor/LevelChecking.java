package sensor;

import quadtree.INodeChecking;
import quadtree.TreeNode;

public class LevelChecking implements INodeChecking {

	private Integer level;
	
	public LevelChecking(Integer level) {
		this.level = level;
	}
	
	@Override
	public Boolean check(TreeNode node) {
		if (node.getLevel() == this.level) {
			return true;
		}
		return false;
	}

}
