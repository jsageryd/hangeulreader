/**
 *
 */
package se.iroiro.md.graph;

import java.util.List;

/**
 * Represents a graph. Create an instance of this class and use it to instantiate edges and nodes.
 * This way, the need to explicitly specify class parameters for the edges/nodes is eliminated as it is inferred from the graph object.
 * @author j
 * @param <NP>	the type of the node piggyback object
 * @param <EP>	the type of the edge piggyback object
 */
public interface Graph<NP,EP> {

	/**
	 * Creates a new node object. This method does not add the node to the graph.
	 * @return	a new node object
	 */
	public XYNode<NP,EP> newNode();

	/**
	 * Creates a new edge object with node arguments. This method does not add the edge to the graph.
	 * @param from	from-node
	 * @param to	to-node
	 * @return	a new edge object
	 */
	public XYEdge<NP,EP> newEdge(XYNode<NP,EP> from, XYNode<NP,EP> to);

	/**
	 * Returns a list of all the nodes in this graph.
	 * @return	a list of all the nodes in this graph
	 */
	public List<XYNode<NP,EP>> getNodes();

	/**
	 * Returns a list of all the edges in this graph.
	 * @return	a list of all the edges in this graph
	 */
	public List<XYEdge<NP,EP>> getEdges();

	/**
	 * Adds the specified edge to the end of the graph's list of edges.
	 * This does not add the nodes of the edge.
	 * @param e	the edge to add
	 */
	public void addEdge(XYEdge<NP,EP> e);

	/**
	 * Adds the edges in the specified list to the end of the graph's list of edges.
	 * This does not add the nodes of the edges.
	 * @param e	the list of edges to add
	 */
	public void addEdges(List<XYEdge<NP,EP>> e);

	/**
	 * Adds the specified edge to the beginning of the graph's list of edges.
	 * This does not add the nodes of the edge.
	 * @param e	the edge to add
	 */
	public void addEdgeFirst(XYEdge<NP,EP> e);

	/**
	 * Adds the specified node to the end of the graph's list of nodes.
	 * @param n	the node to add
	 */
	public void addNode(XYNode<NP,EP> n);

	/**
	 * Adds the specified node to the beginning of the graph's list of nodes.
	 * @param n	the node to add
	 */
	public void addNodeFirst(XYNode<NP,EP> n);

	/**
	 * Removes the specified edge from the graph's list of edges.
	 * This does not remove the nodes associated with the edge.
	 * @param e	the edge to remove
	 */
	public void removeEdge(XYEdge<NP,EP> e);

	/**
	 * Removes the specified node from the graph's list of nodes.
	 * This does not remove the edges associated with the node.
	 * @param n	the node to remove
	 */
	public void removeNode(XYNode<NP,EP> n);

	/**
	 * Returns the coordinate of the top left point of the rectangle bounding this graph.
	 * If the graph contains no nodes, the method returns <code>null</code>.
	 * @return	the coordinate of the top left point of the rectangle bounding this graph, or <code>null</code>
	 */
	public Coordinate getTopLeft();

	/**
	 * Returns the coordinate of the bottom right point of the rectangle bounding this graph.
	 * If the graph contains no nodes, the method returns <code>null</code>.
	 * @return	the coordinate of the bottom right point of the rectangle bounding this graph, or <code>null</code>
	 */
	public Coordinate getBottomRight();

	/**
	 *  Returns the width of the rectangle bounding this graph.
	 * @return	the width of the rectangle bounding this graph
	 */
	public double getWidth();

	/**
	 * Returns the height of the rectangle bounding this graph.
	 * @return	the height of the rectangle bounding this graph
	 */
	public double getHeight();

	/**
	 * If the graph object has a cache, this method clears it.
	 */
	public void clearCache();

	/**
	 * Returns a string representation of the graph.
	 * @return	a string representation of the graph
	 */
	public String toString();

	/**
	 * Returns a duplicate of this graph, with deep-copied nodes and edges, but with piggyback references intact.
	 * @return	a duplicate of this graph
	 */
	public Object clone();

}
