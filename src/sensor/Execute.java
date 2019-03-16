package sensor;

import java.io.File;
import java.util.Date;
import java.util.List;

import quadtree.Quadtree;
import quadtree.TreeNode;

public class Execute {
	public static void main(String argv[]) {
		Date start = new Date();
		File inputFile = Config.formatInputFile(Config.NUMBER_SENSOR, Config.ROI_WIDTH.intValue(), Config.ROI_LENGTH.intValue(), Config.UNIFORM);
		List<Sensor> setOfSensors = Sensor.readUniformSensorsFromFile(inputFile, Config.SENSOR_ANGLE, Config.SENSOR_RADIUS);
		
		Quadtree quadtree = new Quadtree(Config.ROI_LENGTH / 2.0, Config.ROI_WIDTH / 2, Config.ROI_LENGTH, Config.ROI_WIDTH);
		quadtree.setNodeChecking(new FullViewChecking(setOfSensors, Config.THETA));
		
		List<TreeNode> fullViewRegions = quadtree.run();
		
		long runtime = new Date().getTime() - start.getTime();
		for (TreeNode region: fullViewRegions) {
			System.out.println(region.getCenter() + " " + region.getSizeX() + "x" + region.getSizeY());
		}
		System.out.println("Number of regions: " + fullViewRegions.size());
		System.out.println("Runtime: " + runtime + "ms");
	}
}
