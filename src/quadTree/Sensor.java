package quadTree;

import java.util.Random;

public class Sensor{
	private Point center;
	private double direction;
	private int key;
	public double directionWithPoint;
	public int keyLevel2;
	Sensor(){
		Random rd = new Random();
		center = new Point();
		this.direction = rd.nextDouble() * 360;
		this.center.setX(rd.nextDouble() * Config.LENGTH);
		this.center.setY(rd.nextDouble() * Config.WIDTH); 
	}
	Sensor(double direction, double x, double y){
		center = new Point();
		this.direction = direction;
		this.center.setX(x);
		this.center.setY(y);
	}
	public Point getCenter(){
		return this.center;
	}
	public double getDirection(){
		return this.direction;
	}
	public void setKey(int key){
		this.key = key;
	}
	public int getKey(){
		return this.key;
	}
	public void setDirectionWithPoint(Point p){
		this.directionWithPoint = Tools.calculateAngle(p, this.center);
	}
}