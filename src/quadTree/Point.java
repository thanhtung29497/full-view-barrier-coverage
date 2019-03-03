package quadTree;

public class Point{
	private double x;
	private double y;
	Point(){
	}
	Point(double x, double y){
		this.x = x;
		this.y = y;
	}
	public double getX(){
		return this.x;
	}
	public void setX(double x){
		this.x = x;
	}
	public double getY(){
		return this.y;
	}
	public void setY(double y){
		this.y = y;
	}
	public boolean checkCover(Sensor p){
		double d = Tools.distance(this, p.getCenter());
		double angleBetween = Tools.calculateAngle(p.getCenter(), this);
		if (d > Config.RADIUS)
			return false;
		if (Math.abs(angleBetween - p.getDirection()) < Config.ANGLE || Math.abs(angleBetween - p.getDirection()) > 360 - Config.ANGLE){
			return true;
		}
		return false;
	}
	public static void main(String[] args){
		Sensor s = new Sensor(350, 50, 150);
		Point p = new Point(400, 140);
		System.out.println(p.checkCover(s));
	}
}