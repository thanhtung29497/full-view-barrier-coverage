package KW;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

public class GenerateRandomSensors{
	public static void main(String[] args){
		ArrayList<Sensor> setOfSensors = new ArrayList<Sensor>();
		for (int i = 0; i < Config.NUMBER; i++){
			setOfSensors.add(new Sensor());
			System.out.println(i + 1);
		}
		File file = new File("D:/HUST/SoICT/workspace/QuadTreeKW/data/input_data_500.txt");
		try{
			PrintStream pStr = new PrintStream(file);
			for (int i = 0; i < Config.NUMBER; i++){
				Sensor s = setOfSensors.get(i);
				pStr.printf("%f\t%f\t%f%n", s.getDirection(), s.getCenter().getX(), s.getCenter().getY());
			}
			pStr.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}