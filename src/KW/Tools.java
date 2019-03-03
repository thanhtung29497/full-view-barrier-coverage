package KW;

import java.util.ArrayList;
import java.util.Comparator;

public class Tools{
	public static Point lowerLeft;
	public static Point lowerRight;
	public static Point upperRight;
	public static boolean nodeIsCovered;
	public static ArrayList<Sensor> oneCycle = new ArrayList<Sensor>();
	public static ArrayList<Sensor> coverGroup;
	
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
	public static void initCycle(){
		for (int i = 0; i < Config.K; i++){
			oneCycle.add(null);
		}
	}
	public static boolean findAndCheck(ArrayList<Sensor> sensors) {
		coverGroup = new ArrayList<Sensor>();
		for (int i = 0; i < sensors.size(); i++) {
			coverGroup.add(sensors.get(i));
		}
		nodeIsCovered = false;
		
		while((!nodeIsCovered) && (coverGroup.size() >= Config.K) && (coverGroup.get(0).directionWithPoint + 360 - coverGroup.get(coverGroup.size() - 1).directionWithPoint < 360 - (Config.K - 1) * Config.OMEGA)) {
			oneCycle.set(0, coverGroup.get(0));
			backtrack(1, 0);
			coverGroup.remove(0);
		}
		return nodeIsCovered;
	}
	public static boolean backtrack(int n, int j){
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
				else if (backtrack(n + 1, i)){
					return true;
				}
			}
		}
		return false;
	}
}