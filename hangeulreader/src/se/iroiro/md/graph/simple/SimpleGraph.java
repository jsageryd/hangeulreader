/**
 *
 */
package se.iroiro.md.graph.simple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
public class SimpleGraph<NP,EP> implements Graph<NP,EP>, Cloneable, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2958266566727677188L;

	private List<XYEdge<NP,EP>> edges;
	private List<XYNode<NP,EP>> nodes;

	// Caches
	private transient Coordinate topLeft = null;
	private transient Coordinate bottomRight = null;

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
	 * Returns the graph in complete Graphviz dot-format.
	 * @return	the graph in dot-format
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("digraph G {\n\n");
		sb.append(toDotSubgraph(0,0));
		sb.append("\n}");
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Graph#toDotSubgraph(int, int)
	 */
	public String toDotSubgraph(int startingNodeID, int startingColourID){
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
			int currentColour=startingColourID;

			int nid=startingNodeID;

			StringBuilder dot = new StringBuilder();
			String[] ports = {"c","e","ne","n","nw","w","sw","s","se"};

			dot.append("subgraph {\n\n");

			dot.append("\tnode [shape=circle]\n");
			dot.append("\tedge [arrowhead=none arrowtail=none]\n");	// for unidirected graphs, remark line to show arrowheads

			if(getNodes().size() > 0){
				dot.append("\n\t// Nodes\n\n");
				String label = "";
				for(XYNode<NP,EP> n : getNodes()){
					if(n.getID() == -1) n.setID(nid++);
					dot.append("\t\"" + n.getID() + "\"");
					if(n.getPiggybackObject() != null){
						label = n.getPiggybackObject().toString();
					}
					dot.append("[color="+colour[(currentColour++) % colour.length]);
					if(label != ""){
						dot.append(" label=\""+label+"\"");
					}
					dot.append("]\n");
				}
			}

			if(getEdges().size() > 0){
				dot.append("\n\t// Edges\n\n");
				for(XYEdge<NP,EP> e : getEdges()){
					String tp = e.getTailPort() > 0 ? ":"+ports[e.getTailPort()] : "";
					String hp = e.getHeadPort() > 0 ? ":"+ports[e.getHeadPort()] : "";
					dot.append("\t\"" + e.getFrom().getID() + "\""+tp+"\t->\t\"" + e.getTo().getID() + "\""+hp+"\n");
				}
			}

			dot.append("\n}\n");

			return dot.toString();
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Graph#toPGFTikZ()
	 */
	public String toPGFTikZ(boolean createEnvironment, boolean createNodes, boolean continuous, boolean integercoordinates) {
		StringBuilder tikz = new StringBuilder();
		if(createEnvironment){
			tikz.append("\\begin{tikzpicture}\n");
			tikz.append("[scale=0.05,\n");
			if(createNodes && getNodes().size() > 0){
				tikz.append("node/.style={thick,circle,draw=black,fill=white}");
				if(getEdges().size() > 0){
					tikz.append(",\n");
				}
			}
			if(getEdges().size() > 0){
				tikz.append("edge/.style={line width=1,draw=black}");
			}
			tikz.append("]\n\n");
		}
		if(createNodes && getNodes().size() > 0){
			for(XYNode<NP,EP> n : getNodes()){
				Coordinate c = n.getPosition();
				if(integercoordinates){
					tikz.append(String.format(Locale.US, "\\node (%s)	at	(%d,%d)	[node]	{};\n", c.getID(), (int) Math.round(c.getX()), (int) Math.round(c.getY())));
				}else{
					tikz.append(String.format(Locale.US, "\\node (%s)	at	(%.6f,%.6f)	[node]	{};\n", c.getID(), c.getX(), c.getY()));
				}
			}
		}
		if(getEdges().size() > 0){
			if(createNodes) tikz.append("\n");
			Coordinate from;
			if(continuous){
				from = getEdges().get(0).getFrom().getPosition();
				if(createNodes){
					tikz.append(String.format(Locale.US, "\\draw [edge] (%s)", from.getID()));
				}else{
					if(integercoordinates){
						tikz.append(String.format(Locale.US, "\\draw [edge] (%d,%d)", (int) Math.round(from.getX()), (int) Math.round(from.getY())));
					}else{
						tikz.append(String.format(Locale.US, "\\draw [edge] (%.6f,%.6f)", from.getX(), from.getY()));
					}
				}
			}
			for(XYEdge<NP,EP> e : getEdges()){
				Coordinate to = e.getTo().getPosition();
				if(continuous){
					if(createNodes){
						tikz.append(String.format(Locale.US, "--(%s)", to.getID()));
					}else{
						if(integercoordinates){
							tikz.append(String.format(Locale.US, "--(%d,%d)", (int) Math.round(to.getX()), (int) Math.round(to.getY())));
						}else{
							tikz.append(String.format(Locale.US, "--(%.6f,%.6f)", to.getX(), to.getY()));
						}
					}
				}else{
					from = e.getFrom().getPosition();
					if(createNodes){
						tikz.append(String.format(Locale.US, "\\draw [edge] (%s)	--	(%s);\n", from.getID(), to.getID()));
					}else{
						if(integercoordinates){
							tikz.append(String.format(Locale.US, "\\draw [edge] (%d,%d)	--	(%d,%d);\n", (int) Math.round(from.getX()), (int) Math.round(from.getY()), (int) Math.round(to.getX()), (int) Math.round(to.getY())));
						}else{
							tikz.append(String.format(Locale.US, "\\draw [edge] (%.6f,%.6f)	--	(%.6f,%.6f);\n", from.getX(), from.getY(), to.getX(), to.getY()));
						}
					}
				}
			}
			if(continuous){
				if(getNodes().get(0).isNeighbour(getNodes().get(getNodes().size()-1))){
					tikz.append("--cycle");
				}
				tikz.append(";\n");
			}
		}
		if(createEnvironment){
			if(continuous) tikz.append("\n");
			tikz.append("\n\\end{tikzpicture}");
		}
		return tikz.toString();
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
