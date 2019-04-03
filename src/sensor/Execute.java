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
import quadtree.Grid;
import quadtree.Quadtree;
import quadtree.TreeNode;

public class Execute {
	
	public static void draw(List<TreeNode> nodes, List<TreeNode> paths, List<Sensor> sensor, File imageFile) {
		BufferedImage myImage = new BufferedImage(Config.ROI_LENGTH.intValue()*2+120, Config.ROI_WIDTH.intValue()*2+120, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graph = myImage.createGraphics();
		graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// draw satisfied regions
        graph.setColor(Color.BLUE);
		graph.draw(new Rectangle2D.Double(60, 60, Config.ROI_LENGTH*2 - 0.1 , Config.ROI_WIDTH *2- 0.1 ));
		
        for(TreeNode node:nodes) {
        	graph.fill(new Rectangle2D.Double((node.getUpperleft().getX()+30)*2,
        			(node.getUpperleft().getY()-node.getSizeY()+30)*2,
        			node.getSizeX()*2,
        			node.getSizeY()*2));
        }
        
        // draw barrier (if any) 
        graph.setColor(Color.GREEN);
        if (paths != null) {
        	System.out.println("Exist barrier");
	        for(TreeNode node: paths) {
	        	graph.fill(new Rectangle2D.Double((node.getUpperleft().getX()+30)*2,
	        			(node.getUpperleft().getY()-node.getSizeY()+30)*2,
	        			node.getSizeX()*2,
	        			node.getSizeY()*2));
	        }
        }
        
        // draw sensors
        graph.setColor(Color.RED);
        for(Sensor sr:sensor) {
        	Point centre = ((CircularSector)sr.getShape()).getCentre();
        	graph.draw(new Rectangle2D.Double((centre.getX()+30)*2, (centre.getY()+30)*2, 1.0, 1.0));
        }
        try {
            ImageIO.write(myImage, "png", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public static void main(String argv[]) {
		
		File inputFile = Config.formatInputFile(Config.NUMBER_SENSOR, Config.ROI_LENGTH.intValue(), Config.ROI_WIDTH.intValue(), Config.UNIFORM);
		File outputFile = Config.formatOutputFile(Config.NUMBER_SENSOR, Config.ROI_LENGTH.intValue(), Config.ROI_WIDTH.intValue(), Config.UNIFORM, Config.FULL_VIEW);
		List<Sensor> setOfSensors = Sensor.readUniformSensorsFromFile(inputFile, Config.SENSOR_ANGLE, Config.SENSOR_RADIUS);
		
		Quadtree quadtree = new Quadtree(Config.ROI_LENGTH / 2.0, Config.ROI_WIDTH / 2, Config.ROI_LENGTH, Config.ROI_WIDTH);
//		quadtree.setNodeChecking(new FullViewChecking(setOfSensors, Config.THETA));
//		quadtree.setNodeChecking(new KOmegaChecking(setOfSensors, Config.K, Config.OMEGA));
		quadtree.setNodeChecking(new LevelChecking(2));
		
//		Grid grid = new Grid(Config.ROI_LENGTH / 2.0, Config.ROI_WIDTH / 2, Config.ROI_LENGTH, Config.ROI_WIDTH);
//		grid.setNodeChecking(new FullViewChecking(setOfSensors, Config.THETA));
		
		Date start = new Date();
		
		List<TreeNode> fullViewRegions = quadtree.run();
		List<TreeNode> fullViewPaths = quadtree.findShortestPath(fullViewRegions);
		
		Execute.draw(fullViewRegions, fullViewPaths, setOfSensors, outputFile);
		
		long runtime = new Date().getTime() - start.getTime();
		System.out.println("Number of regions: " + fullViewRegions.size());
		System.out.println("Runtime: " + runtime + "ms");
		
		
//		start = new Date();
//		
//		fullViewRegions = grid.runParallel();
//		Execute.draw(fullViewRegions,setOfSensors, outputFile);
//		
//		runtime = new Date().getTime() - start.getTime();
//		System.out.println("Number of regions: " + fullViewRegions.size());
//		System.out.println("Runtime: " + runtime + "ms");
//		
//		//start parallel code
//		System.out.println("Start Parallel");
//		start = new Date();
//		fullViewRegions = quadtree.runParallel();
//		
//		Execute.draw(fullViewRegions,setOfSensors, outputFile);
//		runtime = new Date().getTime() - start.getTime();
//		
//		System.out.println("Number of regions: " + fullViewRegions.size());
//		System.out.println("Runtime: " + runtime + "ms");
//		
//		//start parallel code2
//		System.out.println("Start Parallel 2");
//		start = new Date();
//		fullViewRegions = quadtree.runParallel2();
//
//		Execute.draw(fullViewRegions,setOfSensors, outputFile);
//		runtime = new Date().getTime() - start.getTime();
//				
//		System.out.println("Number of regions: " + fullViewRegions.size());
//		System.out.println("Runtime: " + runtime + "ms");
	}
}
