package sensor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import geometry.CircularSector;
import geometry.IShape;
import geometry.Point;

public class Sensor {
	
	private IShape shape;
	
	public static List<Sensor> readUniformSensorsFromFile(File file, Double angle, Double radius) {
		List<Sensor> setOfSensors = new ArrayList<Sensor>();
		try{
			Scanner sc = new Scanner(file);
			while (sc.hasNextDouble()) {
				Double direction = sc.nextDouble() / 180 * Math.PI;
				Double x = sc.nextDouble();
				Double y = sc.nextDouble();
				CircularSector shape = new CircularSector(new Point(x, y), radius, angle, direction);
				setOfSensors.add(new Sensor(shape));
			}
			sc.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return setOfSensors;
	}
	
	public Sensor(IShape shape) {
		this.shape = shape;
	}
	
	public IShape getShape() {
		return this.shape;
	}
	
	public Boolean cover(Point p) {
		return this.shape.contain(p);
	}
}
