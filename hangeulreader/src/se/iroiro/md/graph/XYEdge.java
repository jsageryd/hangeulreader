/**
 * 
 */
package se.iroiro.md.graph;

/**
 * Represents an edge in the graph. An edge connects exactly 2 nodes.
 * @author j
 * @param <NP>	the type of the node piggyback object
 * @param <EP>	the type of the edge piggyback object
 */
public interface XYEdge<NP,EP> extends XYGraphable<EP> {
	/**
	 * Returns the the node this edge connects from.
	 * @return	the from-node
	 */
	public XYNode<NP,EP> getFrom();
	
	/**
	 * Sets the node this edge connects from.
	 * Removes this edge from the previous from-node and adds it to the new from-node's list of edges.
	 * @param from	the from-node
	 */
	public void setFrom(XYNode<NP,EP> from);

	/**
	 * Returns the node this edge connects to.
	 * @return	the to-node
	 */
	public XYNode<NP,EP> getTo();
	
	/**
	 * Sets the node this edge connects to.
	 * Removes this edge from the previous to-node and adds it to the new to-node's list of edges.
	 * @param to	the to-node
	 */
	public void setTo(XYNode<NP,EP> to);
	
	/**
	 * Reverses the direction of the edge. Swaps from-node and to-node.
	 */
	public void reverse();
	
	/**
	 * Returns the node opposite to the specified node. If from-node is specified, to-node is returned, and vice-versa.
	 * If the node specified is neither from- nor to-node, null is returned.
	 * @param opposite	The node opposite to the node to return.
	 * @return	the from-node if to-node is specified, the to-node if from-node is specified, otherwise null.
	 */
	public XYNode<NP,EP> getOpposite(XYNode<NP,EP> opposite);
	
	/**
	 * Returns the coordinate of the top left point of the rectangle bounding this edge.
	 * @return	the coordinate of the top left point of the bounding rectangle
	 */
	public Coordinate getTopLeft();
	
	/**
	 * Returns the coordinate of the bottom right point of the rectangle bounding this edge.
	 * @return	the coordinate of the bottom right point of the bounding rectangle
	 */
	public Coordinate getBottomRight();
	
	/**
	 * Returns <code>true</code> if the specified coordinate is within the rectangle bounding this edge.
	 * @param c	The coordinate to test
	 * @return	<code>true</code> if the specified coordinate is within the rectangle bounding this edge
	 */
	public boolean withinBounds(Coordinate c);
	
	/**
	 * Returns the equation for this edge.
	 * @return	the equation
	 */
	public LineEquation getEquation();
	
	/**
	 * Returns <code>true</code> if this edge touches the specified edge.
	 * If any point in this edge is the same as any point in the specified edge, this method will return <code>true</code>.
	 * @param edge	the edge to test
	 * @return	<code>true</code> if this edge touches the specified edge
	 */
	public boolean touches(XYEdge<NP,EP> edge);
	
	/**
	 * Returns the length of this edge.
	 * 
	 * @return	length of this edge
	 * @see	Coordinate#distanceTo(Coordinate)
	 */
	public double getLength();
	
	/**
	 * Unlinks the edge by setting from- and to-node to <code>null</code>.
	 * @see XYEdge#setFrom(XYNode)
	 * @see XYEdge#setTo(XYNode)
	 */
	public void unlink();
	
	/**
	 * Returns <code>true</code> if this edge links the nodes specified.
	 * If either argument is null, this method will return <code>false</code>.
	 * @param n1	node 1
	 * @param n2	node 2
	 * @return	<code>true</code> if this edge links the nodes specified
	 */
	public boolean links(XYNode<NP,EP> n1, XYNode<NP,EP> n2);
	
	/**
	 * Returns the port to which the head (to) of this edge connects.
	 * The port is an integer for flexibility, the number can for example represent
	 * eight compass directions, or even degrees.
	 * By default, this returns -1.
	 * @return	the head port
	 */
	public int getHeadPort();

	/**
	 * Sets the port to which the head (to) of this edge connects.
	 * @param hp	the port number to set
	 * @see XYEdge#getHeadPort()
	 */
	public void setHeadPort(int hp);
	
	/**
	 * Returns the port from which the tail (from) of this edge connects.
	 * The port is an integer for flexibility, the number can for example represent
	 * eight compass directions, or even degrees.
	 * By default, this returns -1.
	 * @return	the head port
	 */
	public int getTailPort();
	
	/**
	 * Sets the port from which the tail (from) of this edge connects.
	 * @param tp	the port number to set
	 * @see XYEdge#getTailPort()
	 */
	public void setTailPort(int tp);

	/**
	 * Resets cached fields.
	 * An edge may need to be reset by a node when the position has changed.
	 */
	public void reset();
	
}
