package quadTree;

import java.io.File;

public class Config{
	public static final double LENGTH = 2000;
	public static final double WIDTH = 500;
	public static final double RADIUS = 400;
	public static final int NUMBER = 1000;
	public static final double ANGLE = 45;
	public static final double OMEGA = 60;
	public static final double FIX_OMEGA = 65;
	public static final int K = 3;
	public static File FILE = new File(new File(".").getAbsolutePath() + "/data/old/input_data_1000.txt");
	public static File IMAGE_OUT = new File(new File(".").getAbsolutePath() + "/data/image_out_fill_1000.png");
}
