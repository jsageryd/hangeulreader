/**
 * 
 */
package se.iroiro.md.imagereader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * The <code>ImageReader</code> class creates a graph of nodes based on image data.
 * @author j
 *
 */
public class ImageReader {

	private GraphMatrix matrix;
	private final BufferedImage image;
//	private int scale = 2;	// Explicitly set scale = 2 for faster processing.
	private int scale = 0;	// Explicitly set scale = 2 for faster processing.
	private static final int DEFAULT_RESOLUTION = 2000;
//	private static final int DEFAULT_RESOLUTION = 60;
	
	/**
	 * Class constructor. Loads the image file specified. Uses a default resolution of 150.
	 * @param fileName
	 * @see ImageReader#calcScale(int)
	 */
	public ImageReader(String fileName){
		this(fileName, DEFAULT_RESOLUTION);
	}
	
	/**
	 * Class constructor. Loads the specified image. Uses a default resolution of 150.
	 * @param image
	 * @see ImageReader#calcScale(int)
	 */
	public ImageReader(BufferedImage image){
		this(image, DEFAULT_RESOLUTION);
	}
	
	/**
	 * Class constructor. Loads the specified image. Calculates the scale based on the specified resolution.
	 * @param image	the image to load
	 * @param resolution	the resolution to use
	 * @see ImageReader#calcScale(int)
	 */
	public ImageReader(BufferedImage image, int resolution){
		this.image = image;
		if(scale == 0) scale = calcScale(resolution);
		matrix = new GraphMatrix(image.getWidth()/getScale()+1, image.getHeight()/getScale()+1);
		populateNodes();
	}

	/**
	 * Class constructor. Loads the specified image. Uses the specified fixed scale rather than calculating it.
	 * @param scale	the scale to use
	 * @param image	the image to load
	 */
	public ImageReader(int scale, BufferedImage image){
		this.image = image;
		this.scale = scale;
		matrix = new GraphMatrix(image.getWidth()/getScale()+1, image.getHeight()/getScale()+1);
		populateNodes();
	}
	
	/**
	 * Class constructor. Loads the image file specified. Calculates the scale based on the specified resolution.
	 * @param fileName	the image file name
	 * @param resolution	the resolution to use
	 * @throws IllegalArgumentException	if the image file could not be loaded
	 * @see ImageReader#calcScale(int)
	 */
	public ImageReader(String fileName, int resolution) throws IllegalArgumentException{
//		x_Stopwatch s = new x_Stopwatch();
//		s.start();
		try {
			image = ImageIO.read(new File(fileName));
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not load file.");
		}
//		s.stop();
//		System.out.println("Image file open time: "+s.totaltime_str());
		if(scale == 0) scale = calcScale(resolution);
		matrix = new GraphMatrix(image.getWidth()/getScale(), image.getHeight()/getScale());
		populateNodes();
	}
	
	/**
	 * Class constructor. Loads the image file specified. Uses the specified fixed scale rather than calculating it.
	 * @param scale	the scale to use
	 * @param fileName	the image filename
	 * @throws IllegalArgumentException	if the image file could not be loaded
	 */
	public ImageReader(int scale, String fileName) throws IllegalArgumentException{
		try {
			this.image = ImageIO.read(new File(fileName));
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not load file.");
		}
		this.scale = scale;
		matrix = new GraphMatrix(image.getWidth()/getScale(), image.getHeight()/getScale());
		populateNodes();
	}
	

	/**
	 * Returns the calculated scale. The scale is calculated based on the image dimensions and the specified resolution.
	 * @param resolution	the resolution to use
	 * @return	the calculated scale
	 * //TODO Write more about how the scale is calculated.
	 */
	public int calcScale(int resolution){
		int s = (int) Math.rint((image.getWidth() + image.getHeight()) / 2.0 / resolution);
		return s < 1 ? 1 : s;
	}
	
	/**
	 * Returns the scale used. Scale 1 means every pixel, scale 2 means every other pixel, and so on.
	 * The higher the scale value, the more pixels skipped.
	 * @return	the scale
	 */
	public int getScale(){
		return scale;
	}
	
	/**
	 * Returns the input image.
	 * @return	the input image
	 */
	public BufferedImage getImage(){
		return image;
	}
	
	/**
	 * Populates the graph matrix based on the image data.
	 */
	private void populateNodes(){
		int s = getScale();
		for(int y = 0; y < matrix.getY_size(); y++){
			for(int x = matrix.getX_size()-1; x >= 0; x--){
				if(isColoured(image,x*s,y*s)){
					matrix.setCell(x,y,true);
					matrix.setEdge(x,y,1,matrix.getCell(x+1,y));
					matrix.setEdge(x,y,2,matrix.getCell(x+1,y-1));
					matrix.setEdge(x,y,3,matrix.getCell(x,y-1));
					matrix.setEdge(x,y,4,matrix.getCell(x-1,y-1));
				}
			}
		}
	}
	
	/**
	 * Returns <code>true</code> if the specified coordinate is determined to be coloured (i.e. positive).
	 * @param img	the image to scan
	 * @param x	the <i>x</i>-coordinate
	 * @param y	the <i>y</i>-coordinate
	 * @return	<code>true</code> if the specified coordinate is coloured
	 */
	private boolean isColoured(BufferedImage img, int x, int y){
		if(x < 0 || y < 0 || x >= img.getWidth() || y >= img.getHeight()) return false;
		int c = 0xff000000 | img.getRGB(x, y);						// taken colour reading code from the Color class
		int red = (c >> 16) & 0xFF;									// for speed.
		int green = (c >> 8) & 0xFF;
		int blue = (c >> 0) & 0xFF;
//		return red < 220 || green < 220 || blue < 220;
//		return red < 120 && green < 120 && blue < 120;
//		return red < 80 || green < 80 || blue < 80;
		return (red+green+blue) <= 15;	// average colour value must be less than 5, = fairly black
//		return (red+green+blue) == 0;
	}

	/**
	 * Returns the generated graph matrix.
	 * @return	the generated graph matrix
	 */
	public GraphMatrix getMatrix() {
		return matrix;
	}
}