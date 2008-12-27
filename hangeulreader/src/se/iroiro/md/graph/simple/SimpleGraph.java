/**
 * 
 */
package se.iroiro.md.graph.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.iroiro.md.graph.Coordinate;
import se.iroiro.md.graph.XYEdge;
import se.iroiro.md.graph.Graph;
import se.iroiro.md.graph.XYNode;

/**
 * @author j
 *
 * @param <NP>	the type of the node piggyback object
 * @param <EP>	the type of the edge piggyback object
 */
public class SimpleGraph<NP,EP> implements Graph<NP,EP>, Cloneable {

	private List<XYEdge<NP,EP>> edges;
	private List<XYNode<NP,EP>> nodes;
	
	// Caches
	private Coordinate topLeft = null;
	private Coordinate bottomRight = null;
	
	/**
	 * Class constructor
	 */
	public SimpleGraph() {}	

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Graph#getEdges()
	 */
	public List<XYEdge<NP,EP>> getEdges() {
		if(edges == null) edges = new ArrayList<XYEdge<NP,EP>>();
		return edges;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Graph#getNodes()
	 */
	public List<XYNode<NP,EP>> getNodes() {
		if(nodes == null) nodes = new ArrayList<XYNode<NP,EP>>();
		return nodes;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Graph#newEdge(se.iroiro.md.graph.Node, se.iroiro.md.graph.Node)
	 */
	public XYEdge<NP, EP> newEdge(XYNode<NP,EP> from, XYNode<NP, EP> to) {
		return new SimpleXYEdge<NP,EP>(from,to);
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Graph#newNode()
	 */
	public XYNode<NP,EP> newNode() {
		return new SimpleXYNode<NP,EP>();
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYGraph#addEdge(se.iroiro.md.graph.XYEdge)
	 */
	public void addEdge(XYEdge<NP, EP> e) {
		getEdges().add(e);
		clearCache();
	}
	
	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Graph#addEdges(java.util.List)
	 */
	public void addEdges(List<XYEdge<NP, EP>> e) {
		getEdges().addAll(e);
		clearCache();
	}
	
	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Graph#addEdgeFirst(se.iroiro.md.graph.XYEdge)
	 */
	public void addEdgeFirst(XYEdge<NP, EP> e) {
		getEdges().add(0,e);
		clearCache();
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYGraph#addNode(se.iroiro.md.graph.XYNode)
	 */
	public void addNode(XYNode<NP, EP> n) {
		getNodes().add(n);
		clearCache();
	}
	
	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Graph#addNodeFirst(se.iroiro.md.graph.XYNode)
	 */
	public void addNodeFirst(XYNode<NP, EP> n) {
		getNodes().add(0, n);
		clearCache();
	}

	public void removeEdge(XYEdge<NP, EP> e) {
		getEdges().remove(e);
		clearCache();
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYGraph#removeNode(se.iroiro.md.graph.XYNode)
	 */
	public void removeNode(XYNode<NP, EP> n) {
		getNodes().remove(n);
		clearCache();
	}
	
	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Graph#getTopLeft()
	 */
	public Coordinate getTopLeft() {
		if(getNodes().size() == 0) return null;
		if(topLeft == null){
			topLeft = new SimpleCoordinate(getNodes().get(0).getPosition());
			for(XYNode<NP,EP> n : getNodes()){
				if(n.getPosition().getX() < topLeft.getX()) topLeft.setX(n.getPosition().getX());
				if(n.getPosition().getY() > topLeft.getY()) topLeft.setY(n.getPosition().getY());
			}
		}
		return topLeft;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Graph#getBottomRight()
	 */
	public Coordinate getBottomRight() {
		if(getNodes().size() == 0) return null;
		if(bottomRight == null){
			bottomRight = new SimpleCoordinate(getNodes().get(0).getPosition());
			for(XYNode<NP,EP> n : getNodes()){
				if(n.getPosition().getX() > bottomRight.getX()) bottomRight.setX(n.getPosition().getX());
				if(n.getPosition().getY() < bottomRight.getY()) bottomRight.setY(n.getPosition().getY());
			}
		}
		return bottomRight;
	}
	
	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Graph#getWidth()
	 */
	public double getWidth() {
		if(getTopLeft() != null && getBottomRight() != null){
			return getBottomRight().getX() - getTopLeft().getX();
		}else{
			return 0;
		}
	}
	
	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Graph#getHeight()
	 */
	public double getHeight() {
		if(getTopLeft() != null && getBottomRight() != null){
			return getTopLeft().getY() - getBottomRight().getY();
		}else{
			return 0;
		}
	}
	
	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Graph#clearCache()
	 */
	public void clearCache(){
		topLeft = null;
		bottomRight = null;
	}

	/**
	 * Returns the graph in GraphViz dot-format.
	 * @return	the graph in dot-format
	 */
	public String toString(){
		final String[] colour = {		// colours for graph nodes
			"blue",
			"red",
			"green",
			"cyan",
			"magenta",
			"orange",
			"pink",
			"yellow"
		};
		int currentColour=0;
		
		int nid=0;
		
//		String dot = "// Dot-file generated by hangeulreader\n\n";
		String dot = "";
		String[] ports = {"c","e","ne","n","nw","w","sw","s","se"};
		
		dot += "digraph G {\n\n";
		
		dot += "\tnode [shape=circle]\n";
		dot += "\tedge [arrowhead=none arrowtail=none]\n";	// for unidirected graphs, remark line to show arrowheads
		
		dot += "\n\t// Nodes\n\n";
		String label = "";
		for(XYNode<NP,EP> n : getNodes()){
			if(n.getID() == -1) n.setID(nid++);
			dot += "\t\"" + n.getID() + "\"";
			if(n.getPiggybackObject() != null){
				label = n.getPiggybackObject().toString();
			}
			dot += "[color="+colour[(currentColour++) % colour.length];
			if(label != ""){
				dot += " label=\""+label+"\"";
			}
			dot += "]\n";
		}
		
		dot += "\n\t// Edges\n\n";
		for(XYEdge<NP,EP> e : getEdges()){
			String tp = e.getTailPort() > 0 ? ":"+ports[e.getTailPort()] : "";
			String hp = e.getHeadPort() > 0 ? ":"+ports[e.getHeadPort()] : "";
			dot += "\t\"" + e.getFrom().getID() + "\""+tp+"\t->\t\"" + e.getTo().getID() + "\""+hp+"\n";
		}
		
		dot += "\n}\n\n";
		
		return dot;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@SuppressWarnings("unchecked")
	public Object clone(){
		try {
			SimpleGraph<NP, EP> g = (SimpleGraph<NP,EP>) super.clone();	// clone super
			g.edges = null;
			g.nodes = null;
			Map<XYNode<NP,EP>,XYNode<NP,EP>> nodeMap = new HashMap<XYNode<NP,EP>,XYNode<NP,EP>>();	// map for mapping nodes to their clones
			for(XYNode<NP,EP> n : getNodes()){	// for each node
				XYNode<NP,EP> new_n = (XYNode<NP,EP>) n.clone();	// clone it
				g.addNode(new_n);	// add the clone to the new graph
				nodeMap.put(n, new_n);	// map the original node to the clone
				for(XYEdge<NP,EP> e : n.getEdges()){
					XYNode<NP,EP> neighbour = e.getOpposite(n);	// for each neighbouring node "neighbour"
					if(neighbour != null){
						XYNode<NP,EP> new_neighbour = nodeMap.get(neighbour);	// find it in the node map (if it has already been cloned it will be here)
						if(new_neighbour != null){	// if found, create an edge. If not found, the edge will be created when the other node is
							if(e.getFrom() == neighbour){	// found anyway so this can be safely ignored here.
								g.addEdge(new SimpleXYEdge<NP,EP>(new_neighbour,new_n));	// make new edge, and set the from- and to-nodes correctly
							}else if(e.getTo() == neighbour){
								g.addEdge(new SimpleXYEdge<NP,EP>(new_n,new_neighbour));
							}
						}
					}
				}
			}
			return g;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
