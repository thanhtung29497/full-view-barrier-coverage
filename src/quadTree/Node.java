package quadTree;

public class Node{
	private double xCoor;
	private double yCoor;
	private double sizeX;
	private double sizeY;
	private Node[] children;
	private int rank;
	public boolean isCovered = false;
	Node(){
	}
	Node(double xCoor, double yCoor, double sizeX, double sizeY){
		this.xCoor = xCoor;
		this.yCoor = yCoor;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	public void split(){
		children = new Node[4];
		children[0] = new Node(xCoor - sizeX / 2, yCoor - sizeY / 2, sizeX / 2, sizeY / 2);
		children[1] = new Node(xCoor - sizeX / 2, yCoor + sizeY / 2, sizeX / 2, sizeY / 2);
		children[2] = new Node(xCoor + sizeX / 2, yCoor + sizeY / 2, sizeX / 2, sizeY / 2);
		children[3] = new Node(xCoor + sizeX / 2, yCoor - sizeY / 2, sizeX / 2, sizeY / 2);
		for (int i = 0; i < 4; i++){
			children[i].setRank(this.rank + 1);
		}
	}
	public Node[] getChildren(){
		return this.children;
	}
	public void setRank(int rank){
		this.rank = rank;
	}
	public int getRank(){
		return this.rank;
	}
	public double getX(){
		return this.xCoor;
	}
	public double getY(){
		return this.yCoor;
	}
	public double getSizeX(){
		return this.sizeX;
	}
	public double getSizeY(){
		return this.sizeY;
	}
}