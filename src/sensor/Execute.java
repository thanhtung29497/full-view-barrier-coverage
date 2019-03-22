package sensor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import geometry.CircularSector;
import geometry.Point;
import quadtree.Quadtree;
import quadtree.TreeNode;

public class Execute {
	public static void draw(List<TreeNode> tree, List<Sensor> sensor,String fileName) {
		BufferedImage myImage = new BufferedImage(Config.ROI_LENGTH.intValue()*2+120, Config.ROI_WIDTH.intValue()*2+120, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graph = myImage.createGraphics();
		graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graph.setColor(Color.BLUE);
		graph.draw(new Rectangle2D.Double(60, 60, Config.ROI_LENGTH*2 - 0.1 , Config.ROI_WIDTH *2- 0.1 ));
		
        for(TreeNode node:tree) {
        	graph.fill(new Rectangle2D.Double((node.getUpperleft().getX()+30)*2,
        			(node.getUpperleft().getY()-node.getSizeY()+30)*2,
        			node.getSizeX()*2,
        			node.getSizeY()*2));
        }
        graph.setColor(Color.RED);
        for(Sensor sr:sensor) {
        	Point centre = ((CircularSector)sr.getShape()).getCentre();
        	graph.draw(new Rectangle2D.Double((centre.getX()+30)*2, (centre.getY()+30)*2, 1.0, 1.0));
        }
        try {
            ImageIO.write(myImage, "png", new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	public static void main(String argv[]) {
		
		File inputFile = Config.formatInputFile(Config.NUMBER_SENSOR, Config.ROI_WIDTH.intValue(), Config.ROI_LENGTH.intValue(), Config.UNIFORM);
		//inputFile = new File(Config.INPUT_DIRECTORY + "test.txt");
		List<Sensor> setOfSensors = Sensor.readUniformSensorsFromFile(inputFile, Config.SENSOR_ANGLE, Config.SENSOR_RADIUS);
		
		Quadtree quadtree = new Quadtree(Config.ROI_LENGTH / 2.0, Config.ROI_WIDTH / 2, Config.ROI_LENGTH, Config.ROI_WIDTH);
		quadtree.setNodeChecking(new FullViewChecking(setOfSensors, Config.THETA));
		
		Date start = new Date();
		//List<TreeNode> fullViewRegions = new ArrayList<TreeNode>();
		List<TreeNode> fullViewRegions = quadtree.run();
		List<TreeNode> fullViewPaths = quadtree.bfs(fullViewRegions);
		
		Execute.draw(fullViewRegions,setOfSensors,"notParallel.png");
		
		long runtime = new Date().getTime() - start.getTime();
		for (TreeNode region: fullViewRegions) {
			System.out.println(region.getCenter() + " " + region.getSizeX() + "x" + region.getSizeY());
		}
		System.out.println("Number of regions: " + fullViewRegions.size());
		System.out.println("Runtime: " + runtime + "ms");
		
		//start paralell
		System.out.println("Start Paralell");
		start = new Date();
		fullViewRegions = quadtree.runParallel();
		quadtree.bfs(fullViewRegions);
		Execute.draw(fullViewRegions,setOfSensors,"parallel.png");
		runtime = new Date().getTime() - start.getTime();
		for (TreeNode region: fullViewRegions) {
			System.out.println(region.getCenter() + " " + region.getSizeX() + "x" + region.getSizeY());
		}
		System.out.println("Number of regions: " + fullViewRegions.size());
		System.out.println("Runtime: " + runtime + "ms");
	}
}
