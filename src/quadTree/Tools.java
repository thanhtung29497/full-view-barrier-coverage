package quadTree;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Comparator;

public class Tools{
	public static Point lowerLeft;
	public static Point lowerRight;
	public static Point upperRight;
	public static boolean nodeIsCovered;
//	maybe we should use array instead of arraylist for oneCycle
	public static ArrayList<Sensor> oneCycle = new ArrayList<Sensor>();
	public static ArrayList<Sensor> coverGroup;
	public static ArrayList<ArrayList<Sensor>> setKGroup = new ArrayList<ArrayList<Sensor>>();
	public static void printSensorsVerFull(ArrayList<Sensor> sensors){
		for (int i = 0; i < sensors.size(); i++){
			System.out.printf("Direction = %f\t(%f, %f)%n", sensors.get(i).getDirection(), sensors.get(i).getCenter().getX(), sensors.get(i).getCenter().getY());
		}
	}
	public static void printSensors(ArrayList<Sensor> sensors){
		for (int i = 0; i < sensors.size(); i++){
			System.out.print(sensors.get(i).getKey() + "\t");
		}
		System.out.println();
	}
	public static double distance(Point p1, Point p2){
		return Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) + (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
	}
	public static boolean checkCover(Point p, ArrayList<Sensor> sensors) {
		for (int i = 0; i < sensors.size(); i++) {
			if (!p.checkCover(sensors.get(i)))
				return false;
		}
		return true;
	}
	public static boolean check(Point p, ArrayList<Sensor> sensors){
		ArrayList<Sensor> temp = new ArrayList<Sensor>();
		
		for (int i = 0; i < sensors.size(); i++){
			temp.add(sensors.get(i));
		}
		
		for (int i = 0; i < temp.size(); i++){
			temp.get(i).setDirectionWithPoint(p);
		}
		temp.sort(new Comparator<Sensor>(){
			public int compare(Sensor one, Sensor two){
				if (one.directionWithPoint > two.directionWithPoint){
					return 1;
				}
				else if (one.directionWithPoint < two.directionWithPoint){
					return -1;
				}
				else
					return 0;
			}
		});
		
		for (int i = 0; i < temp.size() - 1; i++){
			if (temp.get(i + 1).directionWithPoint - temp.get(i).directionWithPoint < Config.OMEGA){
				return false;
			}
		}
		if (360 + temp.get(0).directionWithPoint - temp.get(temp.size() - 1).directionWithPoint < Config.OMEGA){
			return false;
		}
		return true;
	}
	public static double calculateAngle(Point p1, Point p2){
		double hypotenuse = distance(p1, p2);
		Point p = new Point(p2.getX(), p1.getY());
		double adjacent = distance(p, p1);
		double angle = Math.acos(adjacent / hypotenuse) * 180 / Math.PI;
		double angle_2;
		if (p1.getX() < p2.getX())
			angle_2 = angle;
		else
			angle_2 = 180 - angle;
		if (p1.getY() < p2.getY()){
			return 360 - angle_2;
		}
		else
			return angle_2;
	}
	public static void printKeyLevel2(ArrayList<Sensor> sensors){
		for (int i = 0; i < sensors.size(); i++){
			System.out.print(sensors.get(i).keyLevel2 + "\t");
		}
		System.out.println();
	}
	public static void initCycle(){
		for (int i = 0; i < Config.K; i++){
			oneCycle.add(null);
		}
	}
	public static ArrayList<Sensor> findBasicKGroup(ArrayList<Sensor> sensors){
		coverGroup = sensors;
		oneCycle.set(0, coverGroup.get(0));
		oneCycle.get(0).keyLevel2 = 0;
		if (backtrack(1, 0)){
			ArrayList<Sensor> basicCycle = new ArrayList<Sensor>();
			for (int i = 0; i < oneCycle.size(); i++){
				basicCycle.add(oneCycle.get(i));
			}
			return basicCycle;
		}
		else{
			return null;
		}
	}
	public static boolean backtrack(int n, int j){
		for (int i = j + 1; i < coverGroup.size(); i++){
			double direction = coverGroup.get(i).directionWithPoint;
			double beforeDir = oneCycle.get(n - 1).directionWithPoint;
			if ((direction - beforeDir > Config.FIX_OMEGA) && ((360 - direction + oneCycle.get(0).directionWithPoint) / (Config.K - n) > Config.FIX_OMEGA))
			{
				oneCycle.set(n, coverGroup.get(i));
				oneCycle.get(n).keyLevel2 = i;
				if (n == Config.K - 1) {
					return true;
				}
				else if (backtrack(n + 1, i))
					return true;
			}
		}
		return false;
	}
	public static boolean findAndCheck(ArrayList<Sensor> sensors) {
		coverGroup = new ArrayList<Sensor>();
		for (int i = 0; i < sensors.size(); i++) {
			coverGroup.add(sensors.get(i));
		}
		nodeIsCovered = false;
		
		while((!nodeIsCovered) && (coverGroup.size() >= Config.K) && (coverGroup.get(0).directionWithPoint + 360 - coverGroup.get(coverGroup.size() - 1).directionWithPoint < 360 - (Config.K - 1) * Config.OMEGA)) {
			oneCycle.set(0, coverGroup.get(0));
			backtrackVer2(1, 0);
			coverGroup.remove(0);
		}
		return nodeIsCovered;
	}
	public static ArrayList<ArrayList<Sensor>> findKGroup(ArrayList<Sensor> sensors){
		initCycle();
		coverGroup = sensors;
		oneCycle.set(0, coverGroup.get(0));
		setKGroup.clear();
		backtrackVer2(1, 0);
		return setKGroup;
	}
	public static boolean backtrackVer2(int n, int j){
		for (int i = j + 1; i < coverGroup.size(); i++){
			double direction = coverGroup.get(i).directionWithPoint;
			double beforeDir = oneCycle.get(n - 1).directionWithPoint;
			if ((direction - beforeDir > Config.OMEGA) && ((360 - direction + oneCycle.get(0).directionWithPoint) / (Config.K - n) > Config.OMEGA))
			{
				oneCycle.set(n, coverGroup.get(i));
				if (n == Config.K - 1){
					
//					check function doesn't change information of oneCycle;
					boolean check_ll = check(lowerLeft, oneCycle);
					boolean check_lr = check(lowerRight, oneCycle);
					boolean check_ur = check(upperRight, oneCycle);
					boolean check = check_ll && check_lr && check_ur;
					if (check) {
						nodeIsCovered = true;
						return true;
					}
				}
				else if (backtrackVer2(n + 1, i)){
					return true;
				}
			}
		}
		return false;
	}
	public static void drawOutput(ArrayList<Sensor> groupSensors, Graphics2D g2) {
		for (int i = 0; i < groupSensors.size(); i++) {
			if (true) {
				double x = groupSensors.get(i).getCenter().getX();
				double y = groupSensors.get(i).getCenter().getY();
				double angle = groupSensors.get(i).getDirection();
				Shape l = new Arc2D.Double(x - Config.RADIUS, y - Config.RADIUS, Config.RADIUS * 2, Config.RADIUS * 2, angle - Config.ANGLE, Config.ANGLE * 2, Arc2D.OPEN);
				g2.draw(l);
				double x1 = Config.RADIUS * Math.cos((angle + Config.ANGLE) / 180 * Math.PI) + x;
				double y1 = Config.RADIUS * Math.sin((angle + Config.ANGLE) / 180 * Math.PI) * (-1) + y;
				double x2 = Config.RADIUS * Math.cos((angle - Config.ANGLE) / 180 * Math.PI) + x;
				double y2 = Config.RADIUS * Math.sin((angle - Config.ANGLE) / 180 * Math.PI) * (-1) + y;
				g2.draw(new Line2D.Double(x, y, x2, y2));
				g2.draw(new Line2D.Double(x, y, x1, y1));
			}	
		}
	}
}