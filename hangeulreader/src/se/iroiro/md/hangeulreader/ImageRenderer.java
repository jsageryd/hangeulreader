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
import se.iroiro.md.imagereader.GraphThinner;

/**
 * @author j
 *
 */
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
	
	private static final int STROKE_WIDTH = 1;
	private static final Color DEFAULT_COLOUR = Color.BLACK;
	private static final Color DEFAULT_EDGE_COLOUR = Color.BLUE;
	private static final Color DEFAULT_NODE_COLOUR = Color.RED;
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
	
	/**
	 * Class constructor. Data will be read from the specified character measurement.
	 * @param cm	the character measurement
	 */
	public ImageRenderer(CharacterMeasurement cm){
		this.cm = cm;
		image = new BufferedImage(cm.getImage().getWidth(), cm.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);
		scale = cm.getImageReader().getScale();
		initg2d();
		draw();
	}
	
	/**
	 * Draws everything.
	 */
	private void draw(){
		drawImage();
		setNodeColour(Color.DARK_GRAY);
		setEdgeColour(getNodeColour());
//		drawLineGroups();
		drawLines();
//		drawMatrix();
	}
	
	/**
	 * Creates the <code>Graphics2D</code> object and loads it with default parameters.
	 */
	private void initg2d(){
		g2d = (Graphics2D) getImage().createGraphics();
		g2d.setColor(DEFAULT_COLOUR);
		setStroke(STROKE_WIDTH);
		if(STROKE_WIDTH > 2) g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}
	
	/**
	 * Sets the stroke width of the <code>Graphics2D</code>.
	 * @param width	the stroke width
	 */
	private void setStroke(int width){
		g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
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
	private void drawLineGroups() {
		if(cm == null || cm.getLineGroups() == null) return;
		for(LineGroup lg : cm.getLineGroups()){
//			drawGraph(lg.getGraph());
			g2d.setColor(Color.GRAY);
//			drawDot(lg.getPosition(), 2);
			g2d.drawRect((int) (lg.getTopLeft().scale(scale).getJavaX()-scale),
					(int) (lg.getTopLeft().scale(scale).getJavaY()-scale),
					(int) (lg.getBottomRight().scale(scale).getX()-lg.getTopLeft().scale(scale).getX()+2*scale),
					(int) (lg.getTopLeft().scale(scale).getY()-lg.getBottomRight().scale(scale).getY()+2*scale));
		}
	}
	
	/**
	 * Draws all the lines
	 */
	private void drawLines(){
		setEdgeColour(Color.WHITE);
		setNodeColour(getEdgeColour());
//		DecimalFormat df = new DecimalFormat("0");
		if(cm == null || cm.getLineGroups() == null) return;
		for(LineGroup lg : cm.getLineGroups()){
//			for(Line l : lg.getMap().keySet()){
//			currentColour = -1;	// enable this line to reset colour counter for each line group, to make it consistent with graph toString output
			for(XYNode<Line,LineGroup> n : lg.getGraph().getNodes()){	// traversing graph instead of linegroup keyset to match graph toString colours
				Line l = n.getPiggybackObject();
				setEdgeColour(getNextColour());
				setNodeColour(getEdgeColour());
				drawGraph(l.getGraph());
			}
//			for(Line l : lg.getMap().keySet()){
//				g2d.setColor(Color.YELLOW);
//				drawDot(l.getFrom().getPosition(),6);
//			}
//			for(Line l : lg.getMap().keySet()){
//				g2d.setColor(Color.BLUE);
//				drawDot(l.getTo().getPosition(),3);
//			}
			for(Line l : lg.getMap().keySet()){
				drawText(l.getType().toString(), (int) l.getPosition().scale(scale).getJavaX(), (int) l.getPosition().scale(scale).getJavaY(), Color.WHITE, Color.BLACK);
			}
//			for(XYEdge<Line,LineGroup> e : lg.getGraph().getEdges()){
//			if(e.getTailPort() > -1){
//					drawText(String.valueOf(e.getTailPort()), (int) e.getFrom().getPosition().getJavaX(), (int) e.getFrom().getPosition().getJavaY(), Color.WHITE);
//				}
//				if(e.getHeadPort() > -1){
//					drawText(String.valueOf(e.getHeadPort()), (int) e.getTo().getPosition().getJavaX(), (int) e.getTo().getPosition().getJavaY(), Color.WHITE);
//				}
//			}
		}
	}
	
	/**
	 * Draws the underlying graph matrix
	 */
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
					drawDot(pos,2);
				}
			}
		}
		for(int y = 0; y < gm.getY_size(); y++){
			for(int x = 0; x < gm.getX_size(); x++){
				Coordinate pos = new SimpleCoordinate(x,-y).scale(scale);
				int cn = GraphThinner.getConnectivityNumber(gm,x,y);
				if(gm.getCell(x,y)){
//					drawText(String.valueOf(cn),(int) pos.getJavaX(),(int) pos.getJavaY(),Color.WHITE,Color.BLACK);
				}
			}
		}
	}
	
//	private void drawCorner(Corner c){
//		g2d.setColor(Color.WHITE);
//		setStroke(2);
//		drawLine(c.getPosition().scale(scale),c.getLeft());
//		drawLine(c.getPosition().scale(scale),c.getRight());
//		setStroke(STROKE_WIDTH);
//	}
	
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
		g2d.setColor(getNodeColour());
		drawDot(n.getPosition(),0);
	}
	
	/**
	 * Draws a dot at the specified coordinate
	 * @param c	the coordinate
	 * @param r	the radius of the dot to be drawn
	 */
	private void drawDot(Coordinate c, int r){
		RenderingHints rh = g2d.getRenderingHints();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.fillOval((int) (c.getJavaX()-r), (int) (c.getJavaY()-r), r*2, r*2);
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
		g2d.drawLine((int) (from.getJavaX()),
					 (int) (from.getJavaY()),
					 (int) (to.getJavaX()),
					 (int) (to.getJavaY()));
	}
	
	
	/* Below are old things, saved for convenience. */
//	private void drawArrowhead(x_Edge x_Edge){
//		Color c = g2d.getColor();
//		g2d.setColor(new Color(130,130,130));
//		double x1 = x_Edge.getCenter().getJavaX();
//		double y1 = x_Edge.getCenter().getJavaY();
//
//		x_Node l = new x_Node(x_Edge.getFrom().getX(),x_Edge.getFrom().getY(),false);
//		x_Node r = new x_Node(x_Edge.getFrom().getX(),x_Edge.getFrom().getY(),false);
//		l.rotate(Math.toRadians(45), x_Edge.getCenter());
//		r.rotate(Math.toRadians(-45), x_Edge.getCenter());
//
//		g2d.drawLine(scale(x1), scale(y1), scale(l.getJavaX()), scale(l.getJavaY()));
//		g2d.drawLine(scale(x1), scale(y1), scale(r.getJavaX()), scale(r.getJavaY()));
//
//		g2d.setColor(c);
//	}
//
//	private void drawStar(int x, int y, int size){
//		g2d.drawLine(x-size+1, y-size+1, x+size-1, y+size-1);
//		g2d.drawLine(x-size+1, y+size-1, x+size-1, y-size+1);
//		g2d.drawLine(x-size, y, x+size, y);
//		g2d.drawLine(x, y-size, x, y+size);
//	}
	
}
