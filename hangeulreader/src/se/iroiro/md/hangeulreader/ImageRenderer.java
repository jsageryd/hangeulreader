/**
 *
 */
package se.iroiro.md.hangeulreader;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import se.iroiro.md.graph.Coordinate;
import se.iroiro.md.graph.Graph;
import se.iroiro.md.graph.XYEdge;
import se.iroiro.md.graph.XYNode;
import se.iroiro.md.graph.simple.SimpleCoordinate;
import se.iroiro.md.hangeul.CharacterMeasurement;
import se.iroiro.md.hangeul.Line;
import se.iroiro.md.hangeul.LineGroup;
import se.iroiro.md.imagereader.GraphMatrix;

/**
 * @author j
 *
 */
public class ImageRenderer {

	private BufferedImage image;
	private CharacterMeasurement cm;
	private Graphics2D g2d;
	private Color edgeColour = DEFAULT_EDGE_COLOUR;
	private Color nodeColour = DEFAULT_NODE_COLOUR;
	private int currentColour = -1;
	private double scale = 1;
	private int strokewidth = DEFAULT_STROKE_WIDTH;

	private static final int DEFAULT_STROKE_WIDTH = 3;
	private static final Color DEFAULT_COLOUR = Color.BLACK;
	private static final Color DEFAULT_EDGE_COLOUR = Color.BLUE;
	private static final Color DEFAULT_NODE_COLOUR = Color.BLUE;
	private static final Color[] colour = {
		Color.BLUE,
		Color.RED,
		Color.GREEN,
		Color.CYAN,
		Color.MAGENTA,
		Color.ORANGE,
		Color.PINK,
		Color.YELLOW
	};

	public enum overlayType {
		LINES { public String toString(){ return "Show lines"; }},
		STRUCTURE { public String toString(){ return "Show relations"; }},
//		MATRIX { public String toString(){ return "Show graph matrix"; }},
		LINETYPES { public String toString(){ return "Show line types"; }}
	}

	/**
	 * Class constructor. Data will be read from the specified character measurement.
	 * @param cm	the character measurement
	 */
	public ImageRenderer(CharacterMeasurement cm){
		this.cm = cm;
		image = new BufferedImage(cm.getImage().getWidth(), cm.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);
		scale = cm.getImageReader().getScale();
		initg2d();
		drawImage();
	}

	/**
	 * Overlays the image with the specified data.
	 * @param ol	data type(s) to overlay
	 */
	public void overlay(overlayType... ol){
		for(overlayType type : ol){
			switch(type){
			case LINES: drawLines(); break;
			case STRUCTURE: drawStructures(); break;
//			case MATRIX: drawMatrix(); break;
			case LINETYPES: drawLineTypes(); break;
			}
		}
	}

	/**
	 * Creates the <code>Graphics2D</code> object and loads it with default parameters.
	 */
	private void initg2d(){
		g2d = (Graphics2D) getImage().createGraphics();
		g2d.setColor(DEFAULT_COLOUR);
		setStrokeWidth(DEFAULT_STROKE_WIDTH);
		if(DEFAULT_STROKE_WIDTH > 2) g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	/**
	 * Sets the stroke width of the <code>Graphics2D</code>.
	 * @param width	the stroke width
	 */
	private void setStrokeWidth(int width){
		strokewidth = width;
		g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	}

	/**
	 * Returns the stroke width.
	 * @return	the stroke width
	 */
	private int getStrokeWidth(){
		return strokewidth;
	}

	/**
	 * Returns the rendered image.
	 * @return	the rendered image
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Draws the input image on the canvas.
	 */
	private void drawImage(){
		if(cm == null) return;
		g2d.drawImage(cm.getImage(), 0, 0, null);
	}

	/**
	 * Draws the graphs of all line groups.
	 */
	private void drawStructures() {
		if(cm == null || cm.getLineGroups() == null) return;

		int sw = getStrokeWidth();
		int d = 2;

		setEdgeColour(Color.BLACK);
		setNodeColour(getEdgeColour());
		setStrokeWidth(sw+d);
		for(LineGroup lg : cm.getLineGroups()){
			drawGraph(lg.getGraph());
		}
		setEdgeColour(Color.WHITE);
		setNodeColour(getEdgeColour());
		setStrokeWidth(sw);
		for(LineGroup lg : cm.getLineGroups()){
			drawGraph(lg.getGraph());
		}
	}

	/**
	 * Draws the line types of all lines
	 */
	private void drawLineTypes(){
		if(cm == null || cm.getLineGroups() == null) return;
		for(LineGroup lg : cm.getLineGroups()){
			for(Line l : lg.getMap().keySet()){
				drawDot(l.getPosition().scale(scale), 12, Color.GRAY);
				drawDot(l.getPosition().scale(scale), 10, Color.BLACK);
				drawDot(l.getPosition().scale(scale), 8, Color.WHITE);
				drawLineType(l.getType(), l.getPosition().scale(scale));
//				drawText(l.getType().toString(), (int) l.getPosition().scale(scale).getJavaX(), (int) l.getPosition().scale(scale).getJavaY(), Color.WHITE, Color.BLACK);
			}
		}
	}

	/**
	 * Draws a glyph to represent the specified line type.
	 * @param lt	the line type
	 * @param location	the location of the centre of the glyph
	 */
	private void drawLineType(Line.LineType lt, Coordinate location){
		g2d.setColor(Color.BLACK);
		setStrokeWidth(2);
		int r = 4;
		int cx = (int) Math.round(location.getJavaX());
		int cy = (int) Math.round(location.getJavaY());
		switch(lt){
		case HORIZONTAL:
			g2d.drawLine(cx-r, cy, cx+r, cy);
			break;
		case VERTICAL:
			g2d.drawLine(cx, cy-r, cx, cy+r);
			break;
		case DIAGONAL_LEFT:
			drawLine(new SimpleCoordinate(cx-r,-cy).rotate(Math.PI/4, location), new SimpleCoordinate(cx+r,-cy).rotate(Math.PI/4, location));
			break;
		case DIAGONAL_RIGHT:
			drawLine(new SimpleCoordinate(cx-r,-cy).rotate(-Math.PI/4, location), new SimpleCoordinate(cx+r,-cy).rotate(-Math.PI/4, location));
			break;
		case CIRCLE:
			g2d.drawOval(cx-r, cy-r, r*2-1, r*2-1);
			break;
		case OTHER_CLOSED_POLYGON:
			g2d.drawRect(cx-r, cy-r, r*2-1, r*2-1);
			break;
		case LEFT_BOX:
			g2d.drawPolyline(new int[] {cx+r,cx-r,cx-r,cx+r}, new int[] {cy-r,cy-r,cy+r,cy+r}, 4);
			break;
		case RIGHT_BOX:
			g2d.drawPolyline(new int[] {cx-r,cx+r,cx+r,cx-r}, new int[] {cy-r,cy-r,cy+r,cy+r}, 4);
			break;
		case UPPER_BOX:
			g2d.drawPolyline(new int[] {cx-r,cx-r,cx+r,cx+r}, new int[] {cy+r,cy-r,cy-r,cy+r}, 4);
			break;
		case LOWER_BOX:
			g2d.drawPolyline(new int[] {cx-r,cx-r,cx+r,cx+r}, new int[] {cy-r,cy+r,cy+r,cy-r}, 4);
			break;
		case TOPLEFT_CORNER:
			g2d.drawPolyline(new int[] {cx+r,cx-r,cx-r}, new int[] {cy-r,cy-r,cy+r}, 3);
			break;
		case TOPRIGHT_CORNER:
			g2d.drawPolyline(new int[] {cx-r,cx+r,cx+r}, new int[] {cy-r,cy-r,cy+r}, 3);
			break;
		case BOTTOMLEFT_CORNER:
			g2d.drawPolyline(new int[] {cx-r,cx-r,cx+r}, new int[] {cy-r,cy+r,cy+r}, 3);
			break;
		case BOTTOMRIGHT_CORNER:
			g2d.drawPolyline(new int[] {cx+r,cx+r,cx-r}, new int[] {cy-r,cy+r,cy+r}, 3);
			break;
		case Z_SHAPE:
			g2d.drawPolyline(new int[] {cx-r,cx+r,cx-r,cx+r}, new int[] {cy-r,cy-r,cy+r,cy+r}, 4);
			break;
		case UNKNOWN:
		}
		setStrokeWidth(DEFAULT_STROKE_WIDTH);
	}

	/**
	 * Draws all the lines
	 */
	private void drawLines(){
		setEdgeColour(Color.WHITE);
		setNodeColour(getEdgeColour());
		if(cm == null || cm.getLineGroups() == null) return;
		for(LineGroup lg : cm.getLineGroups()){
			for(XYNode<Line,LineGroup> n : lg.getGraph().getNodes()){	// traversing graph instead of linegroup keyset to match graph toString colours
				Line l = n.getPiggybackObject();
				setEdgeColour(getNextColour());
				setNodeColour(getEdgeColour());
				drawGraph(l.getGraph());
			}
		}
	}

	/**
	 * Draws the underlying graph matrix
	 */
	@SuppressWarnings("unused")
	private void drawMatrix(){
		g2d.setColor(Color.WHITE);
		GraphMatrix gm = cm.getImageReader().getMatrix();
		for(int y = 0; y < gm.getY_size(); y++){
			for(int x = 0; x < gm.getX_size(); x++){
				Coordinate pos = new SimpleCoordinate(x,-y).scale(scale);
				for(int e = 0; e <= 7; e++){
					if(gm.getEdge(x,y,e)){
						drawLine(pos,new SimpleCoordinate(gm.getNeighbourX(x,y,e),-gm.getNeighbourY(x,y,e)).scale(scale));
					}
				}
				if(gm.getCell(x,y)){
					drawDot(pos,2,Color.WHITE);
				}
			}
		}
		for(int y = 0; y < gm.getY_size(); y++){
			for(int x = 0; x < gm.getX_size(); x++){
				Coordinate pos = new SimpleCoordinate(x,-y).scale(scale);
//				int cn = GraphThinner.getConnectivityNumber_binary(gm,x,y);
//				if(gm.getCell(x,y)){
//					drawText(String.valueOf(cn),(int) pos.getJavaX(),(int) pos.getJavaY(),Color.WHITE,Color.BLACK);
//				}
			}
		}
	}

	private void drawText(String text, int x, int y, Color c){
		String fontname = "Verdana";
		int fontsize = 10;
		g2d.setFont(new Font(fontname, Font.BOLD, fontsize));
		FontMetrics fm = g2d.getFontMetrics();
		int px = x - (fm.stringWidth(text) / 2);
		int py = y + ((fm.getAscent()-fm.getDescent()) / 2);
		g2d.setColor(c);
		g2d.drawString(text, px, py);
	}

	@SuppressWarnings("unused")
	private void drawText(String text, int x, int y, Color c, Color b){
		int offset = 1;
		drawText(text, x-offset, y, b);
		drawText(text, x+offset, y, b);
		drawText(text, x, y+offset, b);
		drawText(text, x, y-offset, b);
		drawText(text, x-offset, y-offset, b);
		drawText(text, x+offset, y+offset, b);
		drawText(text, x+offset, y-offset, b);
		drawText(text, x-offset, y+offset, b);
		drawText(text, x, y, c);
	}

	/**
	 * Returns a random colour.
	 * @return	a random colour
	 */
	private Color getNextColour() {
		currentColour = (currentColour+1) % colour.length;
		return colour[currentColour];
	}

	/**
	 * Draws the specified graph.
	 * @param g	the graph to draw
	 */
	public void drawGraph(Graph<?,?> g){
		for(XYEdge<?,?> e : g.getEdges()){
			drawEdge(e);
		}
		for(XYNode<?,?> n : g.getNodes()){
			drawNode(n);
		}
	}

	/**
	 * Sets the node colour.
	 * @param c	the colour to set
	 */
	public void setNodeColour(Color c) {
		nodeColour = c;
	}

	/**
	 * Sets the edge colour.
	 * @param c	the colour to set
	 */
	public void setEdgeColour(Color c) {
		edgeColour = c;
	}

	/**
	 * Returns the node colour to use.
	 * @return	the node colour
	 */
	Color getNodeColour() {
		return nodeColour;
	}

	/**
	 * Returns the edge colour to use.
	 * @return	the edge colour
	 */
	public Color getEdgeColour() {
		return edgeColour;
	}

	/**
	 * Draws the specified node.
	 * @param n	the node to draw
	 */
	private void drawNode(XYNode<?,?> n) {
		drawDot(n.getPosition(),0, getNodeColour());
	}

	/**
	 * Draws a dot at the specified coordinate
	 * @param c	the coordinate
	 * @param r	the radius of the dot to be drawn
	 * @param colour	the colour of the dot
	 */
	private void drawDot(Coordinate c, int r, Color colour){
		g2d.setColor(colour);
		RenderingHints rh = g2d.getRenderingHints();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.fillOval((int) Math.round(c.getJavaX())-r, (int) Math.round(c.getJavaY())-r, r*2, r*2);
		g2d.setRenderingHints(rh);
	}

	/**
	 * Draws the specified edge.
	 * @param e	the edge to draw
	 */
	private void drawEdge(XYEdge<?,?> e) {
		g2d.setColor(getEdgeColour());
		drawLine(e.getFrom().getPosition().scale(scale), e.getTo().getPosition().scale(scale));
	}

	/**
	 * Draws a line from <code>from</code> to <code>to</code>.
	 * @param from	from-coordinate
	 * @param to	to-coordinate
	 */
	private void drawLine(Coordinate from, Coordinate to){
		g2d.drawLine((int) Math.round(from.getJavaX()),
					 (int) Math.round(from.getJavaY()),
					 (int) Math.round(to.getJavaX()),
					 (int) Math.round(to.getJavaY()));
	}

}