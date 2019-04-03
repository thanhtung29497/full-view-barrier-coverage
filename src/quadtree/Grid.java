package quadtree;

import java.util.ArrayList;
import java.util.List;

import sensor.Config;
class ExecuteG implements Runnable{
	List<TreeNode> satisfiedNodes;
	int n,m;
	double mx,my;
	double l,t;
	public INodeChecking nodeChecking;
	@Override
	public void run() {
		satisfiedNodes  = new ArrayList<>();
		for(int i = 0;i< n;i++)
			for(int j = 0;j<m;j++){
				TreeNode node = new TreeNode(mx/2 + mx* i + l , my/2 + my*j + t, mx,my,0);
				//System.out.printf("%f %f %f %f \n", mx/2 + mx* i + l , my/2 + my*j + t, mx,my);
				if (this.nodeChecking.check(node)) {
					satisfiedNodes.add(node);
				}
			}
	}
	
}
public class Grid {
	
	private TreeNode root;
	private Integer threshold;
	private INodeChecking nodeChecking;
	public final static Integer LEVEL_THRESHOLD = 7;
	
	public Grid(Double centerX, Double centerY, Double width, Double height) {
		this.root = new TreeNode(centerX, centerY, width, height, 0);
		this.threshold = Quadtree.LEVEL_THRESHOLD;
	}
	
	public Grid(Double centerX, Double centerY, Double width, Double height, Integer threshold) {
		this.root = new TreeNode(centerX, centerY, width, height, 0);		
		this.threshold = threshold;
	}
	
	public void setNodeChecking(INodeChecking nodeChecking) {
		this.nodeChecking = nodeChecking;
	}
	
	public List<TreeNode> bfs(List<TreeNode> nodes) {
		Double source = this.root.getUpperleft().getX();
		Double destination = this.root.getUpperleft().getX() + this.root.getSizeX();
		return new ArrayList<>();
	}
	
	
	public List<TreeNode> run() {
		
		List<TreeNode> satisfiedNodes = new ArrayList<>();
		
		if (this.nodeChecking == null) {
			return satisfiedNodes;
		}
		int n= 1<<(this.threshold+1);
		System.out.println(""+n);
		double mx,my;
		mx = root.getSizeX()/n;
		my = root.getSizeY()/n;
		for(int i = 0;i< n;i++)
			for(int j = 0;j<n;j++){
				TreeNode node = new TreeNode(mx/2 + mx* i, my/2 + my*j, mx,my,0);
				if (this.nodeChecking.check(node)) {
					satisfiedNodes.add(node);
				}
			}
		
		return satisfiedNodes;
	}
	public List<TreeNode> runParallel() {
		List<TreeNode> satisfiedNodes = new ArrayList<>();
		Thread t[] = new Thread[Config.NUMBER_THREAD];
		ExecuteG e[] = new ExecuteG[Config.NUMBER_SENSOR];
		if (this.nodeChecking == null) {
			return satisfiedNodes;
		}
		
		int n= 1<<(this.threshold+1);
		double mx,my;
		mx = root.getSizeX()/n;
		my = root.getSizeY()/n;
		for(int i=0;i<Config.NUMBER_THREAD;i++) {
			e[i] = new ExecuteG();
			e[i].l=0;
			e[i].t=root.getSizeY()/Config.NUMBER_THREAD * i;
			e[i].nodeChecking = this.nodeChecking;
			e[i].m=n/Config.NUMBER_THREAD;
			e[i].n=n;
			e[i].mx = mx;
			e[i].my = my;
			t[i] = new Thread(e[i]);
			t[i].start();
		}
		for(int i = 0;i<Config.NUMBER_THREAD;i++) {
			try {
				t[i].join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			satisfiedNodes.addAll(e[i].satisfiedNodes);
			System.out.println(e[i].satisfiedNodes.size());
		}
		return satisfiedNodes;
	}
	
}

