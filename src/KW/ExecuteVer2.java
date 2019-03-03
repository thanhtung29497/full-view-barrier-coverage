package KW;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import javax.imageio.ImageIO;

public class ExecuteVer2 {
    public static BufferedImage myImage = new BufferedImage(2000, 500, BufferedImage.TYPE_INT_ARGB);
    public static ArrayList<Sensor> setOfSensors;
    public static Graphics2D g2 = myImage.createGraphics();
    public static ArrayList<ArrayList<Node>> bunch;
    
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        
        // readInputSensors
        initialize();
        
        //set some features for g2 to draw
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.BLUE);
        
        // create elements for oneCycle
        Tools.initCycle();
        
        // all processing here
        browse();
        
        // drawToFile
        try {
            ImageIO.write(myImage, "png", Config.IMAGE_OUT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Runtime : " + (end - start));
    }

    public static void initialize() {
        setOfSensors = ReadInputSensors.getSensors();
        bunch = new ArrayList<ArrayList<Node>>();
        for (int i = 0; i < Config.AMOUNTX; i++) {
        	bunch.add(new ArrayList<Node>());
        }
        double unitX = Config.LENGTH / Config.AMOUNTX;
        double unitY = Config.WIDTH / Config.AMOUNTY;
        double xCoor, yCoor;
        for (int i = 0; i < Config.AMOUNTX; i++) {
        	for (int j = 0; j < Config.AMOUNTY; j++) {
        		xCoor = unitX * i + unitX / 2;
        		yCoor = unitY * j + unitY / 2;
        		bunch.get(i).add(new Node(xCoor, yCoor, unitX, unitY));
        	}
        }
    }

    public static void browse() {
    	double xCoor, yCoor, sizeX, sizeY;
    	for (int i = 0; i < Config.AMOUNTX; i++) {
    		for (int j = 0; j < Config.AMOUNTY; j++) {
    			
    			// retrieve information of the current node
    			xCoor = bunch.get(i).get(j).getX();
    			yCoor = bunch.get(i).get(j).getY();
    			sizeX = bunch.get(i).get(j).getSizeX();
    			sizeY = bunch.get(i).get(j).getSizeY();
    			
    			// four corners
    			Point upperLeft = new Point(xCoor - sizeX, yCoor - sizeY);
    			Point lowerLeft = new Point(xCoor - sizeX, yCoor + sizeY);
    			Point lowerRight = new Point(xCoor + sizeX, yCoor + sizeY);
    			Point upperRight = new Point(xCoor + sizeX, yCoor - sizeY);
    			
    			// find cover group for one cell
    			ArrayList<Sensor> coverGroup = new ArrayList<Sensor>();
    			Sensor s;
    			for (int counter = 0; counter < Config.NUMBER; counter++) {
    				s = setOfSensors.get(counter);
    				if (upperLeft.checkCover(s) && lowerLeft.checkCover(s) && lowerRight.checkCover(s) && upperRight.checkCover(s)) {
    					coverGroup.add(s);
    				}
    			}
    			
    			Tools.lowerLeft = lowerLeft;
            	Tools.lowerRight = lowerRight;
            	Tools.upperRight = upperRight;
            	
            	for (int counter = 0; counter < coverGroup.size(); counter++) {
            		coverGroup.get(counter).setDirectionWithPoint(upperLeft);
            	}
            	
    	    	coverGroup.sort(new Comparator<Sensor>() {
    	         	public int compare(Sensor one, Sensor two) {
    	         		if (one.directionWithPoint > two.directionWithPoint) {
    	         			return 1;
    	         		}
    	         		else if (one.directionWithPoint < two.directionWithPoint) {
    	         			return -1;
    	         		}
    	         		else 
    	         			return 0;
    	         	}
    	         });

    	    	if (Tools.findAndCheck(coverGroup))
            		bunch.get(i).get(j).isCovered = true;
    	    	
    	    	// fill or not
    	    	if (bunch.get(i).get(j).isCovered) {
    	    		g2.draw(new Rectangle2D.Double(xCoor - sizeX, yCoor - sizeY, sizeX * 2, sizeY * 2));
    	    	}
    		}
    	}
    }
}

