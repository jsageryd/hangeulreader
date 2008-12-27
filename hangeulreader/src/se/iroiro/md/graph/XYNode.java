/**
 * 
 */
package se.iroiro.md.graph;

import java.util.Set;

/**
 * Represents a node in a graph. A node can have 0 or more connecting edges.
 * @author j
 * @param <NP>	the type of the node piggyback object
 * @param <EP>	the type of the edge piggyback object
 */
public interface XYNode<NP,EP> extends XYGraphable<NP> {
	
	/**
	 * Returns a list of all edges connecting to or from this node.
	 * @return	a list of edges
	 */
	public Set<XYEdge<NP,EP>> getEdges();
	
	/**
	 * Returns the number of edges connecting to or from this node.
	 * @return	the number of connecting edges
	 */
	public int getDegree();
	
	/**
	 * Adds an edge to the list of edges.
	 * Adding an edge to the list of edges does <strong>not</strong> imply that the node is added to the edge.
	 * @param e	an edge to add
	 */
	public void addEdge(XYEdge<NP,EP> e);
	
	/**
	 * Removes an edge from the list of edges.
	 * Removing an edge from the list of edges does <strong>not</strong> imply that the node is removed from the edge.
	 * @param e	the edge to remove
	 */
	public void removeEdge(XYEdge<NP,EP> e);
	
	/**
	 * Returns <code>true</code> if the specified node is connected to this node by at least one edge.
	 * @param n	the node to test
	 * @return	<code>true</code> if the specified node is connected to this node by at least one edge
	 */
	public boolean isNeighbour(XYNode<NP,EP> n);
	
	/**
	 * Returns a deep clone of this node, with piggyback reference intact.
	 * This is equal to creating a new node with the same coordinates,
	 * and assigning the piggyback object of this node to it.
	 * The clone always has an empty set of edges - references to previous edges are not wanted,
	 * and the new edges are unknown to this class.
	 * @return	a copy of this node
	 */
	public Object clone();
	
}
