package sensor;

import java.io.File;

public class Config {
	public static final Double ROI_LENGTH = 400.0;
	public static final Double ROI_WIDTH = 400.0;
	public static final Double SENSOR_RADIUS = 100.0;
	public static final Integer NUMBER_SENSOR = 2000;
	public static final Double SENSOR_ANGLE = Math.PI / 4;
	public static final Double OMEGA = Math.PI / 3;
	public static final Double THETA = Math.PI / 3;
	public static final Double FIX_OMEGA = 65.0 / 180 * Math.PI ;
	public static final Integer K = 3;
	public static final String INPUT_DIRECTORY = new File(".").getAbsolutePath() + "/data/comparison/";
	public static final String OUTPUT_DIRECTORY = new File(".").getAbsolutePath() + "/output/";
	public static final String UNIFORM = "uniform";
	public static final String GAUSS = "normal"; 
	public static final String K_OMEGA = "komega";
	public static final String FULL_VIEW = "fullview";
	public static final Integer NUMBER_THREAD = 4;
	public static File formatInputFile(Integer number, Integer roiLength, Integer roiWidth, String distribution) {
		String fileName = distribution + roiWidth.intValue() + "_" + roiLength.intValue() + "_" + number + ".txt";
		return new File(Config.INPUT_DIRECTORY + fileName);
	}
	
	public static File formatOutputFile(Integer number, Integer roiLength, Integer roiWidth, String distribution, String coverStyle) {
		String fileName = coverStyle + "_" + distribution + roiWidth.intValue() + "_" + roiLength.intValue() + "_" + number + ".png";
		return new File(Config.OUTPUT_DIRECTORY + fileName);
	}
}
