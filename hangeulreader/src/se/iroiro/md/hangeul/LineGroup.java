/**
 *
 */
package se.iroiro.md.hangeul;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.iroiro.md.graph.Coordinate;
import se.iroiro.md.graph.Graph;
import se.iroiro.md.graph.XYEdge;
import se.iroiro.md.graph.XYNode;
import se.iroiro.md.graph.simple.AbstractXYPositioned;
import se.iroiro.md.graph.simple.SimpleCoordinate;
import se.iroiro.md.graph.simple.SimpleGraph;
import se.iroiro.md.graph.simple.SimpleXYEdge;
import se.iroiro.md.graph.simple.SimpleXYNode;

/**
 * This class represents a group of connected lines.
 * These are stored in a map, mapping each line to a node in a graph.
 * @author j
 *
 */
public class LineGroup extends AbstractXYPositioned {

	//TODO Change 'Object' to ConnectionType or similar.
	private Graph<Line,LineGroup> graph = null;
	private Map<Line,XYNode<Line,LineGroup>> map = null;

	private Coordinate position = null;
	private Coordinate topleft = null;
	private Coordinate bottomright = null;

	/**
	 * This method always returns Double.NaN, as the angle for a group is not defined.
	 * @return	always returns Double.NaN
	 */
	public double getAngle() {
		return Double.NaN;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYPositioned#getPosition()
	 */
	public Coordinate getPosition() {
		if(position == null) {
			Coordinate tl = getTopLeft();
			Coordinate br = getBottomRight();
			return new SimpleCoordinate((tl.getX() + br.getX())/2,	// Get centre point of bounding rectangle
										(br.getY() + tl.getY())/2);	// "
		}
		return position;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYPositioned#rotate(double, se.iroiro.md.graph.Coordinate)
	 */
	public void rotate(double angle, Coordinate pivot) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYPositioned#setPosition(double, double)
	 */
	public void setPosition(double x, double y) {	// TODO Maybe move the compensation section to another method, so that it can be re-used.
		double dx = x - getPosition().getX();	// calculate offsets
		double dy = y - getPosition().getY();
		Set<XYNode<Object,Line>> nodes = new HashSet<XYNode<Object,Line>>();	// to compensate for nodes in crossings, see below loop
		for(Line l : this.getMap().keySet()){	// move all lines
			Coordinate linePos = l.getPosition();
			linePos.translate(dx,dy);
			l.setPosition(linePos.getX(),linePos.getY());
			if(l.getGraph().getNodes().size() > 1){	// compensate for nodes in crossings that get moved more than once
				List<XYNode<Object,Line>> lineNodes = l.getGraph().getNodes();
				nodes.add(lineNodes.get(0));
				nodes.add(lineNodes.get(lineNodes.size()-1));
			}
		}
		for(XYNode<Object,Line> n : nodes){	// move the crossing-nodes back
			int d = n.getDegree()-1;
			n.getPosition().translate(-dx * d,-dy * d);
		}
		reset();
	}

	/**
	 * Returns the graph of the line group. This never returns null.
	 * @return	the graph of the line group
	 */
	public Graph<Line,LineGroup> getGraph() {
		if(graph == null) graph = new SimpleGraph<Line,LineGroup>();
		return graph;
	}

	/**
	 * Creates a node for the specified line and calls {@link LineGroup#addNode(XYNode)} to add it to the graph.
	 * The node is linked to all other nodes in the graph.
	 * @param l	the line for which to create a node to add
	 * @see LineGroup#addNode(XYNode)
	 */
	public boolean add(Line l){	//TODO Check result
		if(!contains(l)){
			XYNode<Line,LineGroup> n = new SimpleXYNode<Line,LineGroup>();
			n.setPosition(l.getPosition());
			getMap().put(l,n);
			n.setPiggybackObject(l);
			getGraph().addNode(n);
			reset();
			return true;
		}
		return false;
	}

	public boolean addNode(XYNode<Line,LineGroup> n){
		if(n != null && n.getPiggybackObject() != null &&
				!getGraph().getNodes().contains(n) && !contains(n.getPiggybackObject())){
			getMap().put(n.getPiggybackObject(),n);
			getGraph().addNode(n);
			reset();
			return true;
		}
		return false;
	}

	/**
	 * Connects the specified lines by creating an edge between them in the graph.
	 * Both lines have to exist in the group.
	 * If they do not, the method will return <code>false</code>.
	 * @param from	from-line
	 * @param to	to-line
	 * @return	<code>true</code> on success, otherwise <code>false</code>
	 */
	public boolean connect(Line from, Line to){
		if(contains(from) && contains(to)){
			XYEdge<Line,LineGroup> e = new SimpleXYEdge<Line,LineGroup>(getMap().get(from),getMap().get(to));
			getGraph().addEdge(e);
			reset();
			return true;
		}
		return false;
	}

	/**
	 * Returns the map. Never returns null.
	 * @return	the map
	 */
	public Map<Line,XYNode<Line, LineGroup>> getMap() {
		if(map == null) map = new HashMap<Line,XYNode<Line,LineGroup>>();
		return map;
	}

	/**
	 * Removes the specified line and its associated node along with all connected edges.
	 * @param l	the line to be removed
	 */
	public void remove(Line l){
		XYNode<Line,LineGroup> n = getMap().get(l);
		if(n != null){
			n.setPiggybackObject(null);
			for(XYEdge<Line,LineGroup> e : n.getEdges()){
				getGraph().removeEdge(e);
			}
			getGraph().removeNode(n);
			getMap().remove(l);
		}
		reset();
	}

	public boolean contains(Line l){
		return getMap().containsKey(l);
	}

	/**
	 * Returns <code>true</code> if the two specified lines are connected in the graph of this line group.
	 * @param l1	line 1
	 * @param l2	line 2
	 * @return	<code>true</code> if the two specified lines are connected in the graph of this line group
	 */
	public boolean isConnected(Line l1, Line l2) {
		if(contains(l1) && contains(l2)){
			return getMap().get(l1).isNeighbour(getMap().get(l2));
		}
		return false;
	}

	/**
	 * Returns the coordinate of the top left point of the rectangle bounding this line group.
	 * @return	the coordinate of the top left point of the bounding rectangle
	 */
	public Coordinate getTopLeft() {
		if(getMap().size() == 0) return null;
		if(topleft == null){
			topleft = new SimpleCoordinate(getMap().keySet().iterator().next().getTopLeft());
			for(Line l : getMap().keySet()){
				if(l.getTopLeft().getX() < topleft.getX()) topleft.setX(l.getTopLeft().getX());
				if(l.getTopLeft().getY() > topleft.getY()) topleft.setY(l.getTopLeft().getY());
			}
		}
		return topleft;
	}

	/**
	 * Returns the coordinate of the bottom right point of the rectangle bounding this line group.
	 * @return	the coordinate of the bottom right point of the bounding rectangle
	 */
	public Coordinate getBottomRight() {
		if(getMap().size() == 0) return null;
		if(bottomright == null){
			bottomright = new SimpleCoordinate(getMap().keySet().iterator().next().getBottomRight());
			for(Line l : getMap().keySet()){
				if(l.getBottomRight().getX() > bottomright.getX()) bottomright.setX(l.getBottomRight().getX());
				if(l.getBottomRight().getY() < bottomright.getY()) bottomright.setY(l.getBottomRight().getY());
			}
		}
		return bottomright;
	}

	/**
	 * Resets cached fields.
	 */
	private void reset() {
		position = null;
		topleft = null;
		bottomright = null;
	}

	/**
	 * Returns the string representations of all the lines contained in the line group,
	 * enclosed in square brackets.
	 */
	public String toString(){
		StringBuilder result = new StringBuilder("[");
		for(Line l : getMap().keySet()){
			result.append(l.getType());
		}
		return result + "]";
	}

	/**
	 * Returns the width of the line group.
	 * @return	the width of the line group
	 */
	public double getWidth() {
		return getBottomRight().getX() - getTopLeft().getX();
	}

	/**
	 * Returns the height of the line group.
	 * @return	the height of the line group
	 */
	public double getHeight() {
		return getTopLeft().getY() - getBottomRight().getY();
	}

}
