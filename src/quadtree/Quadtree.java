package quadtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import sweepline.NewRectangleSweep;
import sweepline.Point2D;
import sweepline.Rectangle2D;

import sensor.Config;

class Execute implements Runnable {
	static List<TreeNode> satisfiedNodes;
	static List<TreeNode> pendingNodes;
	static int nThreadRun = 0;
	private INodeChecking nodeChecking;
	private Integer threshold;
	public boolean inProces;
	TreeNode currentNode;
	
	public Execute() {
		inProces = false;
	} 
	
	public static synchronized TreeNode getNode(Execute thread) {
		TreeNode currentNode= null;
		if(!pendingNodes.isEmpty()){
			currentNode = pendingNodes.remove(0);
			if(!thread.inProces) {
				thread.inProces = true;
				nThreadRun++;
			}
		}else {
			if(thread.inProces) {
				thread.inProces = false;
				nThreadRun --;
			}
		}
		return currentNode;
	}

	public void setThresdHold(int trh) {
		this.threshold = trh;
	}
	public void setNodeChecking(INodeChecking nodeChecking) {
		this.nodeChecking = nodeChecking;
	}
	
	public void setSatisfiedNodes(List<TreeNode> satisf) {
		this.satisfiedNodes = satisf;
	}
	public void setPendingNodes(List<TreeNode> list) {
		this.pendingNodes = list;
	}
	
	
	public void run() {
		
		if (this.nodeChecking == null) {
			return;
		}
		
		while ((this.currentNode=Execute.getNode(this))!=null || (nThreadRun!=0)) {
			if(!inProces) {
				try {
					Thread.currentThread().sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				if (!this.nodeChecking.check(this.currentNode)) {
					if (currentNode.getLevel() > this.threshold) {
						continue;
					}
					List<TreeNode> subnodes = currentNode.divide();
					Execute.addPending(subnodes);
				} else {
					Execute.addSatif(currentNode);
				}
			}
		}
		System.out.println("A Thread is stopped");
		System.out.println(""+nThreadRun);
		return;
	}

	private static synchronized void addPending(List<TreeNode> subnodes) {
		// TODO Auto-generated method stub
		Execute.pendingNodes.addAll(subnodes);
	}

	private static synchronized void addSatif(TreeNode currentNode) {
		// TODO Auto-generated method stub
		Execute.satisfiedNodes.add(currentNode);
	}
	
}

class Execute2 implements Runnable{
	List<TreeNode> satisfiedNodes;
	List<TreeNode> pendingNodes;
	public Execute2() {}
	private INodeChecking nodeChecking;
	private Integer threshold;

	public void setThresdHold(int trh) {
		this.threshold = trh;
	}
	public void setNodeChecking(INodeChecking nodeChecking) {
		this.nodeChecking = nodeChecking;
	}
	
	
	public void run() {
		
		if (this.nodeChecking == null) {
			return ;
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
		System.out.println("A Thread is stopped");
	
		return;
	}
	
}

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
	
	public List<TreeNode> runParallel() {
		List<TreeNode> satisfiedNodes = new ArrayList<>();
		List<TreeNode> pendingNodes = new ArrayList<>(Arrays.asList(root));
		
		Execute thread[] = new Execute[Config.NUMBER_THREAD];
		Thread[] thrObj = new Thread[Config.NUMBER_THREAD];
		
		Execute.satisfiedNodes = satisfiedNodes;
		Execute.pendingNodes = pendingNodes;
		
		for(int i = 0 ; i<Config.NUMBER_THREAD;i++) {
			System.out.println("Thread["+i+"] is Creating...");
			thread[i] = new Execute();
			thread[i].setNodeChecking(this.nodeChecking);
			thread[i].setThresdHold(threshold);
			System.out.println("Thread["+i+"] is Starting...");
			thrObj[i] = new Thread(thread[i]);
			thrObj[i].start();
		}
		for(int i=0;i<Config.NUMBER_THREAD;i++)
			try {
				thrObj[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return satisfiedNodes;
	}
	
	public List<TreeNode> runParallel2() {
		Execute2 thread[] = new Execute2[Config.NUMBER_THREAD];
		Thread[] thrObj = new Thread[Config.NUMBER_THREAD];
		
		List<TreeNode> l = root.divide();
		
		for(int i = 0 ; i<4;i++) {
			System.out.println("Thread["+i+"] is Creating...");
			thread[i] = new Execute2();
			thread[i].satisfiedNodes = new ArrayList<TreeNode>();
			thread[i].pendingNodes = new ArrayList<TreeNode>(Arrays.asList(l.get(i)));
			thread[i].setNodeChecking(this.nodeChecking);
			thread[i].setThresdHold(threshold);
			System.out.println("Thread["+i+"] is Starting...");
			thrObj[i] = new Thread(thread[i]);
			thrObj[i].start();
		}
		for(int i=0;i<Config.NUMBER_THREAD;i++)
			try {
				thrObj[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		List<TreeNode> satisfiedNodes = new ArrayList<TreeNode> ();
		for(int i =0;i<4;i++) {
			satisfiedNodes.addAll(thread[i].satisfiedNodes);
		}
		return satisfiedNodes;
	}
	
	public List<TreeNode> findShortestPath(List<TreeNode> nodes) {
		NewRectangleSweep sweep = new NewRectangleSweep();
		Integer index = 0;
		List<Rectangle2D> rectangles = new ArrayList<>();
		Rectangle2D leftBarrier = new Rectangle2D(
				new Point2D(root.getUpperleft().getX(), root.getUpperleft().getY()), 
				new Point2D(root.getLowerleft().getX(), root.getLowerleft().getY()), 0);
		Rectangle2D rightBarrier = new Rectangle2D(
				new Point2D(root.getUpperright().getX(), root.getUpperright().getY()), 
				new Point2D(root.getLowerright().getX(), root.getLowerright().getY()), nodes.size() + 1);
		
		rectangles.add(leftBarrier);
		for (TreeNode node: nodes) {
			Point2D upperleft = new Point2D(node.getUpperleft().getX(), node.getUpperleft().getY());
			Point2D lowerright = new Point2D(node.getLowerright().getX(), node.getLowerright().getY());
			rectangles.add(new Rectangle2D(upperleft, lowerright, ++index));
		}
		rectangles.add(rightBarrier);
		
		List<Integer> shortestPath = sweep.solve(rectangles);
		if (shortestPath != null) {
			shortestPath.remove(0);
			shortestPath.remove(shortestPath.size() - 1);
			return shortestPath.stream().map(nodeId -> nodes.get(nodeId - 1)).collect(Collectors.toList());
		}
		
		return null;
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
