package quadTree;

import java.util.*;

public class Test{
	public static void main(String[] args){
		Point p = new Point(500, 300);
		ArrayList<Sensor> setOfSensors = ReadInputSensors.getSensors();
		ArrayList<Sensor> coverGroup = new ArrayList<Sensor>();
		for (int i = 0; i < Config.NUMBER; i++){
			if (p.checkCover(setOfSensors.get(i))){
				coverGroup.add(setOfSensors.get(i));
			}
		}
		
		//sort coverGoup 
		for (int i = 0; i < coverGroup.size(); i++){
			coverGroup.get(i).setDirectionWithPoint(p);
		}
		coverGroup.sort(new Comparator<Sensor>(){
			public int compare(Sensor one, Sensor two){
				if (one.directionWithPoint > two.directionWithPoint)
					return 1;
				else if (one.directionWithPoint < two.directionWithPoint)
					return -1;
				else
					return 0;
			}
		});
	
		ArrayList<Sensor> basicCycle = Tools.findBasicKGroup(coverGroup);
		for (int i = 0; i < basicCycle.size(); i++){
			System.out.println(basicCycle.get(i).directionWithPoint);
		}
		
		System.out.println("------------------------------------------------------");
		for (int i = 0; i < basicCycle.size(); i++)
		{
			System.out.println(basicCycle.get(i).keyLevel2);
		}
		System.out.println("------------------------------------------------------");
		ArrayList<Sensor> kGroup = basicCycle;
		if (kGroup != null){
			Point lowerLeft = new Point(500, 302);
			Point lowerRight = new Point(502, 302);
			Point upperRight = new Point(502, 300);
			
			boolean check_one;
			boolean check_two;
			boolean check_three;
			boolean check;
			do{
				for (int i = 0; i < kGroup.size(); i++){
					System.out.println(kGroup.get(i).keyLevel2);
				}
				System.out.println();
				check_one = Tools.check(lowerLeft, kGroup);
				check_two = Tools.check(lowerRight, kGroup);
				check_three = Tools.check(upperRight, kGroup);
				check = check_one && check_two && check_three;
				System.out.println(check);
				System.out.println("--------------------------------------------");
				if (check == true){
				
				}	
				else{
					for (int ii = 0; ii < Config.K; ii++){
						int keyLevel2 = kGroup.get(ii).keyLevel2;
						kGroup.set(ii, coverGroup.get(keyLevel2 + 1));
						kGroup.get(ii).keyLevel2 = keyLevel2 + 1;
					}
				}
			}
			while (check == false && kGroup.get(kGroup.size() - 1).keyLevel2 != coverGroup.size() - 1);
		}
	}
}