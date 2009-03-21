/**
 *
 */
package se.iroiro.md.hangeul;

import java.awt.image.BufferedImage;
import java.util.List;

import se.iroiro.md.graph.Graph;
import se.iroiro.md.imagereader.GraphMatrix;
import se.iroiro.md.imagereader.GraphThinner;
import se.iroiro.md.imagereader.ImageReader;

/**
 * Input image, output line groups.
 * @author j
 *
 */
public class CharacterMeasurement {

	private List<LineGroup> lineGroups = null;
	private ImageReader imageReader = null;

	/**
	 * Class constructor. Takes an image filename as argument.
	 * The image may be any raster format supported by the <code>BufferedImage</code> class.
	 * @param fileName	the image filename
	 */
	public CharacterMeasurement(String fileName) {
		imageReader = new ImageReader(fileName);
		go();
	}

	/**
	 * Class constructor. Takes an image as argument.
	 * @param image	the image
	 */
	public CharacterMeasurement(BufferedImage image) {
		imageReader = new ImageReader(image);
		go();
	}

	/**
	 * Runs all methods needed for the classification process.
	 * This method is called by the constructor after the image has been converted to a graph matrix by the image reader.
	 */
	private void go() {
		buildStructure(getImageReader().getMatrix());
	}

	/**
	 * Returns the image reader.
	 * @return	the image reader
	 */
	public ImageReader getImageReader() {
		return imageReader;
	}

	/**
	 * Creates a structure from the data in the graph matrix.
	 * This method creates a graph from the matrix, which is then searched for lines.
	 * The lines are then grouped into line groups.
	 * These groups are stored and can be retrieved through {@link CharacterMeasurement#getLineGroups()}.
	 * @param gm	the graph matrix to build from
	 */
	private void buildStructure(GraphMatrix gm){
		GraphThinner.thin(gm);	// thin graph matrix
		Graph<Object,Line> g = GraphTools.graphMatrixToGraph(gm);	// convert matrix to graph
		List<Line> lines = GraphTools.graphToLines(g);	// find the lines in the graph
//		GraphTools.proximityLink(lines, ((g.getWidth() + g.getHeight()) / 2) / 10);	// link nodes that are close to each other
		lineGroups = GraphTools.linesToLineGroups(lines);	// group connected lines
		GraphTools.removeShortLines(lineGroups);
	}

	/**
	 * Returns the line groups.
	 * @return	the line groups
	 */
	public List<LineGroup> getLineGroups() {
		return lineGroups;
	}

	/**
	 * Returns the input image.
	 * @return	the input image
	 */
	public BufferedImage getImage() {
		return getImageReader().getImage();
	}

}
