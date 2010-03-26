/**
 *
 */
package se.iroiro.md.graph.simple;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import se.iroiro.md.graph.Coordinate;
import se.iroiro.md.graph.XYEdge;
import se.iroiro.md.graph.XYNode;
import se.iroiro.md.graph.simple.SimpleCoordinate;

/**
 * @author j
 *
 * @param <NP>	the type of the node piggyback object
 * @param <EP>	the type of the edge piggyback object
 */
public class SimpleXYNode<NP,EP> extends AbstractXYGraphable<NP> implements XYNode<NP,EP>, Cloneable, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -834670904965492957L;

	/**
	 * List of connecting edges
	 */
	private Set<XYEdge<NP,EP>> edges;

	/**
	 * Node position
	 */
	private Coordinate position = new SimpleCoordinate(0,0);

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYPositioned#getAngle()
	 */
	public double getAngle() {
		return Double.NaN;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYPositioned#getPosition()
	 */
	public Coordinate getPosition() {
		return position;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYPositioned#setPosition(se.iroiro.md.graph.Coordinate)
	 */
	public void setPosition(Coordinate position) {
		this.position = position;
		reset();
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYPositioned#setPosition(double, double)
	 */
	public void setPosition(double x, double y){
		if(position == null){
			position = new SimpleCoordinate(x,y);
		}else{
			position.setX(x);
			position.setY(y);
		}
		reset();
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYNode#addEdge(se.iroiro.md.graph.XYEdge)
	 */
	public void addEdge(XYEdge<NP, EP> e) {
		getEdges().add(e);
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYNode#getDegree()
	 */
	public int getDegree() {
		return getEdges().size();
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYNode#getEdges()
	 */
	public Set<XYEdge<NP, EP>> getEdges() {
		if(edges == null) edges = new HashSet<XYEdge<NP,EP>>();
		return edges;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYNode#removeEdge(se.iroiro.md.graph.XYEdge)
	 */
	public void removeEdge(XYEdge<NP, EP> e) {
		getEdges().remove(e);
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYPositioned#rotate(double, se.iroiro.md.graph.Coordinate)
	 */
	public void rotate(double angle, Coordinate pivot) {
		getPosition().rotate(angle, pivot);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return getPosition().toString();
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYNode#isNeighbour(se.iroiro.md.graph.XYNode)
	 */
	public boolean isNeighbour(XYNode<NP,EP> n) {
		for(XYEdge<NP,EP> e : getEdges()){
			if(e.links(this, n)) return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYNode#duplicate()
	 */
	public XYNode<NP,EP> duplicate() {
		XYNode<NP,EP> n = new SimpleXYNode<NP,EP>();
		n.setPosition(new SimpleCoordinate(getPosition()));
		n.setPiggybackObject(getPiggybackObject());
		return n;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@SuppressWarnings("unchecked")
	public Object clone(){
		try {
			SimpleXYNode<NP,EP> n = (SimpleXYNode<NP,EP>) super.clone();
			n.edges = null;
			n.setPosition((Coordinate) getPosition().clone());
			return n;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Resets cached fields. Loops through all connecting edges and calls reset() for each.
	 */
	private void reset() {
		for(XYEdge<NP,EP> e : getEdges()){
			e.reset();
		}
	}

}
