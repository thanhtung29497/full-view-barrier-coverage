package quadTree;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import javax.imageio.ImageIO;

public class ExecuteVer2 {
    public static BufferedImage myImage = new BufferedImage(2000, 500, BufferedImage.TYPE_INT_ARGB);
    public static BufferedImage myImage1 = new BufferedImage(2000, 500, BufferedImage.TYPE_INT_ARGB);
    
    public static ArrayList<Sensor> setOfSensors;
    
    public static Graphics2D g2 = myImage.createGraphics();
    
    public static File file1 = new File("D:/DataStructure/javaFile/output/Noname.png");
    public static Graphics2D g3 = myImage1.createGraphics();
    
    public static int bottom = 0;
    public static int top = 0;
    public static int counter = 0;
    public static int n;
    
    public static void main(String[] args) {
//    	Scanner sc = new Scanner(System.in);
//    	System.out.print("Enter n : ");
//    	n = sc.nextInt();
//    	sc.close();
    	
        long start = System.currentTimeMillis();
        
        // readInputSensors
        initialize();
        
        //set some features for g2 to draw
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.BLUE);
        g2.draw(new Rectangle2D.Double(0, 0, Config.LENGTH - 0.1, Config.WIDTH - 0.1));
        
        // create elements for oneCycle
        Tools.initCycle();
        
        // all processing here
        browse();
        
//        Tools.printSensorsVerFull(setOfSensors);
        
        // drawToFile
        try {
            ImageIO.write(myImage, "png", Config.IMAGE_OUT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("top = " + top);
        System.out.println("bottom = " + bottom);
        System.out.println("counter = " + counter);
        long end = System.currentTimeMillis();
        System.out.println("Runtime : " + (end - start));
    }

    public static void initialize() {
        setOfSensors = ReadInputSensors.getSensors();
    }

    public static void browse() {
    	// information of center cell
        double x = Config.LENGTH / 2;
        double y = Config.WIDTH / 2;
        double sizeX = Config.LENGTH / 2;
        double sizeY = Config.WIDTH / 2;
        
        // breadth first search
        Node root = new Node(x, y, sizeX, sizeY);
        ArrayList<Node> queue = new ArrayList<Node>();
        queue.add(root);
        while (!queue.isEmpty()) {
        	counter++;
            // pop node
            Node node = queue.remove(0);

            // retrieve information of the node
            x = node.getX();
            y = node.getY();
            sizeX = node.getSizeX();
            sizeY = node.getSizeY();

            // four-corners
            Point upperLeft = new Point(x - sizeX, y - sizeY);
            Point lowerLeft = new Point(x - sizeX, y + sizeY);
            Point lowerRight = new Point(x + sizeX, y + sizeY);
            Point upperRight = new Point(x + sizeX, y - sizeY);

            // findCoverGroup of one cell
            ArrayList<Sensor> coverGroup = new ArrayList<Sensor>();
            for (int i = 0; i < setOfSensors.size(); i++) {
                Sensor s = setOfSensors.get(i);
                if (upperLeft.checkCover(s) && lowerLeft.checkCover(s) && lowerRight.checkCover(s) && upperRight.checkCover(s)) {
                	coverGroup.add(s);
                }
            }
            
            Tools.lowerLeft = lowerLeft;
        	Tools.lowerRight = lowerRight;
        	Tools.upperRight = upperRight;
        	
        	for (int i = 0; i < coverGroup.size(); i++) {
        		coverGroup.get(i).setDirectionWithPoint(upperLeft);
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
        		node.isCovered = true;

            // split or not
            if (node.isCovered) {
            	// drawOutput
            	g2.fill(new Rectangle2D.Double(x - sizeX, y - sizeY, sizeX * 2, sizeY * 2));
            } 
            else if (node.getRank() < 7) {
                node.split();
                for (int i = 0; i < 4; i++) {
                    queue.add(node.getChildren()[i]);
                }
            }
        }
    }

    public static void forOnePoint(Point p) {
        ArrayList<Sensor> coverGroup = new ArrayList<Sensor>();
        for (int i = 0; i < Config.NUMBER; i++) {
            if (p.checkCover(setOfSensors.get(i))) {
                coverGroup.add(setOfSensors.get(i));
            }
        }
        
        for (int i = 0; i < coverGroup.size(); i++) {
        	coverGroup.get(i).setDirectionWithPoint(p);
        }
        
        coverGroup.sort(new Comparator<Sensor>() {
        	public int compare(Sensor one, Sensor two) {
        		if (one.directionWithPoint > two.directionWithPoint)
        			return 1;
        		else if (one.directionWithPoint < two.directionWithPoint)
        			return -1;
        		else 
        			return 0;
        	}
        });
        
        double x = p.getX();
        double y = p.getY();
        double sizeX = 250;
        double sizeY = 62.5;
        
        Point lowerLeft = new Point(x, y + sizeY);
        Point lowerRight = new Point(x + sizeX, y + sizeY);
        Point upperRight = new Point(x + sizeX, y);
        
        Tools.lowerLeft = lowerLeft;
        Tools.lowerRight = lowerRight;
        Tools.upperRight = upperRight;
        
        boolean t = Tools.findAndCheck(coverGroup);
        System.out.println(t);
        ArrayList<Sensor> sensors = Tools.oneCycle;
        g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g3.setColor(Color.BLUE);
        
        g3.draw(new Rectangle2D.Double(x, y, sizeX, sizeY));
        Tools.drawOutput(sensors, g3);
        try {
            ImageIO.write(myImage1, "png", file1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Sensor> findBasic(ArrayList<Sensor> coverGroup, Point p) {
        //sort coverGoup
        for (int i = 0; i < coverGroup.size(); i++) {
            coverGroup.get(i).setDirectionWithPoint(p);
        }
        coverGroup.sort(new Comparator<Sensor>() {
            public int compare(Sensor one, Sensor two) {
                if (one.directionWithPoint > two.directionWithPoint)
                    return 1;
                else if (one.directionWithPoint < two.directionWithPoint)
                    return -1;
                else
                    return 0;
            }
        });
        return Tools.findBasicKGroup(coverGroup);
    }
}

