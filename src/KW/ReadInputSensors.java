package KW;

import java.util.ArrayList;
import java.util.Scanner;


public class ReadInputSensors{
	public static ArrayList<Sensor> getSensors(){
		ArrayList<Sensor> setOfSensors = new ArrayList<Sensor>();
		try{
			Scanner sc = new Scanner(Config.FILE);
			for (int i = 0; i < Config.NUMBER; i++){
				double direction = sc.nextDouble();
				double x = sc.nextDouble();
				double y = sc.nextDouble();
				setOfSensors.add(new Sensor(direction, x, y));
				setOfSensors.get(i).setKey(i + 1);
			}
			sc.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return setOfSensors;
	}
}