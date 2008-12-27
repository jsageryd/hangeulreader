/**
 * 
 */
package se.iroiro.md.imagereader;

import java.util.ArrayList;



/**
 * The <code>GraphThinner</code> class takes a graph of nodes and thins it.
 * @author j
 */
public class GraphThinner {

	/**
	 * Thins the graph to lines based on the connectivity number for each <code>GraphNode</code>.
	 * Calls deleteExtraEdges().
	 * 
	 * @see GraphThinner#deleteExtraEdges(GraphMatrix)
	 */
	public static void thin(GraphMatrix matrix){
		int dir=0;
		boolean direction = false;
		int deleted = 1;
		int cycle = 4;
		int counter = 0;
		ArrayList<Integer> rem_x, rem_y;
		while((deleted > 0 || cycle > 0) && counter < 4){
//			counter++;
			cycle -= deleted == 0 ? 1 : 0;
			deleted = 0;
			dir = ++dir % 4;
			rem_x = new ArrayList<Integer>();
			rem_y = new ArrayList<Integer>();
//			ArrayList<Noode> border = new ArrayList<Noode>();
			for(int y = 0; y < matrix.getY_size(); y++){
				for(int x = 0; x < matrix.getX_size(); x++){
//					if(!exists(x,y)) continue;
					if(!matrix.getCell(x,y)) continue;
//					if(edgeCount(x, y) == 0) continue;
					direction = false;
					switch(dir){
					case 0: direction = !matrix.getEdge(x,y,3); break;	// above
					case 1: direction = !matrix.getEdge(x,y,5); break;	// left
					case 2: direction = !matrix.getEdge(x,y,7); break;	// below
					case 3: direction = !matrix.getEdge(x,y,1); break;	// right
					}
//					switch(dir){
//					case 0: direction = !exists(x,y-1); break;	// above
//					case 1: direction = !exists(x-1,y); break;	// left
//					case 2: direction = !exists(x,y+1); break;	// below
//					case 3: direction = !exists(x+1,y); break;	// right
//					}
					if(direction && edgeCount(matrix,x,y) != 1 && getConnectivityNumber(matrix,x,y) == 1){
//						border.add(n);
						rem_x.add(x);
						rem_y.add(y);
//						matrix[y][x] = false;
						deleted++;
					}
				}
			}
			for(int i = 0; i < rem_x.size(); i++){
				int y = rem_y.get(i);
				int x = rem_x.get(i);
				matrix.setCell(x,y,false);
				for(int n = 1; n < 9; n++) matrix.setEdge(x,y,n,false);
//				matrix.setBit(x-1,y,1,false);
//				matrix.setBit(x-1,y+1,2,false);
//				matrix.setBit(x,y+1,3,false);
//				matrix.setBit(x+1,y+1,4,false);
//				matrix[1][y][x-1] = false;
//				matrix[2][y+1][x-1] = false;
//				matrix[3][y+1][x] = false;
//				matrix[4][y+1][x+1] = false;
//				n.unlink();
//				getNodes().remove(n);
			}
		}
		deleteExtraEdges(matrix);
	}
	
//	private boolean edgeCountNotOne(int x, int y){
////		if(!exists(x,y)) return 0;
////		byte count = 0;
////		if(true) return false;
//		boolean got_one = false;
//		boolean got_two = false;
//		for(int ny = -1; ny <= 1; ny++){
//			for(int nx = -1; nx <=1; nx++){
//				if(matrix.getNode(x+nx,y+ny)){
//					if(got_two) return true;
//					if(got_one) got_two = true;
//					got_one = true;
//				}
//			}
//		}
//		return false;
//	}

	//TODO Why is this method duplicated in GraphMatrix? Fix and use only either.
	private static int edgeCount(GraphMatrix matrix, int x, int y){
		int c = 0;
		for(int n = 1; n < 9; n++){
			if(matrix.getEdge(x,y,n)) c++;
		}
		return c;
	}
//	}
	
	/**
	 * Deletes diagonal edges which connect two perpendicular edges.
	 * This is to prevent T-shaped crossings in the input image from becoming Y-shaped in the graph.
	 */
//	public void deleteExtraEdges(){
//		for(int y = 0; y < matrix.getY_size(); y++){
//			for(int x = 0; x < matrix.getX_size(); x++){
//			Noode[] p = getPerpendiculars(n);
//			if(p != null){
//				for(Noode one : p){
//					for(Noode two : p){
//						if(one != null) one.unlinkNode(two);
//					}
//				}
//			}
//		}
//	}
		
	public static void deleteExtraEdges(GraphMatrix matrix){
		int e1, e2, e3;
//		if(true) return;
		for(int y = 0; y < matrix.getY_size(); y++){
			for(int x = 0; x < matrix.getX_size(); x++){
				for(int n = 1; n <= 8; n++){
					e1 = n;
					e2 = (n+2) % 8;
					if(e2 == 0) e2 = 8;
					e3 = (n+4) % 8;					// check for double right triangles
					if(e3 == 0) e3 = 8;
					if(matrix.getEdge(x,y,e1) && matrix.getEdge(x,y,e2) && matrix.getEdge(x,y,e3) && (e1 % 2 != 0)){
						int e2x = matrix.getNeighbourX(x,y,e2);
						int e2y = matrix.getNeighbourY(x,y,e2);
							if(edgeCount(matrix,e2x,e2y) <= 4){
//								System.out.println("double: x,y: ["+x+","+y+"]\tnx,ny: ["+nx+","+ny+"]\te1: "+e1+"\te2: "+e2+"\tn: "+n+"\ttop edgecount: "+edgeCount(nx,ny));
								int nte1 = (e2-3) & 7; // & 7 is same as % 8, but works properly for negative numbers.
								int nte2 = (e2+3) % 8;
								if(nte1 == 0) nte1 = 8;
								if(nte2 == 0) nte2 = 8;
								matrix.setEdge(e2x,e2y,nte1,false);
								matrix.setEdge(e2x,e2y,nte2,false);
							}else{
								matrix.setCell(x,y,false);
								for(int i = 1; i <= 8; i++) matrix.setEdge(x,y,i,false);
							}
					}
				}
				for(int n = 1; n <= 8; n++){
					e1 = n;
					e2 = (n+2) % 8;
					if(e2 == 0) e2 = 8;				// check for mirrored right triangles, and single right triangles
					int em = (e1+1) % 8;
					if(em == 0) em = 8;
					if(matrix.getEdge(x,y,e1) && matrix.getEdge(x,y,e2) && (n % 2 != 0)){
						int e1x = matrix.getNeighbourX(x,y,e1);
						int e1y = matrix.getNeighbourY(x,y,e1);
						int hyp = (e1+3) % 8;
						if(hyp == 0) hyp = 8;
						if(matrix.getEdge(e1x,e1y,hyp)){
							if(matrix.getEdge(x,y,em)){
								matrix.setEdge(x,y,e1,false);
								matrix.setEdge(x,y,e2,false);
								matrix.setEdge(e1x,e1y,hyp,false);
							}else{
								matrix.setEdge(x,y,e1,false);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Returns the connectivity number for the node.
	 * This number is based on the locations of the neighbouring nodes.
	 * 
	 * @return	the connectivity number for a node
	 */
	public static int getConnectivityNumber(GraphMatrix matrix, int x, int y){
		boolean[] e = new boolean[9];
		int[] n = new int[9];	// numeric
		int result = 0;	// start with result of 0.
		for(int i = 0; i <= 7; i++){
			e[i] = matrix.getEdge(x,y,i+1);	// populate the array
		}
		e[8] = e[0];
		for(int i = 0; i < 9; i++) n[i] = e[i] ? 1 : 0;		// if edge N exists, set n[N] to one, otherwise set it to 0.	// numeric
		for(int k = 0; k <= 7; k++){	//	for each edge		// \sum_{k=0}^7 n_{k}(1-n_{k+1})
//			if(e[k] && !e[k+1]) result++;	// binary
			result += n[k]*(1-n[k+1]);	// numeric
		}
		int result2 = 0;	// numeric
		for(int k = 0; k <= 3; k++){	// \sum_{k=0}^3 n_{2k}n_{2k+2}(1-n_{2k+1})
			// if right edge and above edge,
			// remove 1 from result if there is no above-right edge
			if(e[2*k] && e[2*k+2] && !e[2*k+1]){
//				result--;	// binary
			}
			result2 += n[2*k] * n[2*k+2] * (1-n[2*k+1]);	// numeric
		}
		result = result - result2;	// numeric
		for(int k = 0; k <= 7; k++){
			if(e[k] && e[(k+2) % 8] && e[(k+4) % 8] && matrix.edgeCount(x, y) == 3) result = 0;	// prevent deletion of triangles, remove them later.
		}
		return result;
	}
	
//	/**
//	 * Returns the connectivity number for the node.
//	 * This number is based on the locations of the neighbouring nodes.
//	 * 
//	 * @return	the connectivity number for a node
//	 */
//	public static int getConnectivityNumber(GraphMatrix matrix, int x, int y){
//		boolean[] e = new boolean[9];
////		int[] n = new int[9];
//		int result = 0;	// start with result of 0.
//		for(int i = 0; i <= 7; i++){
//			e[i] = matrix.getEdge(x,y,i+1);	// populate the array
//		}
////		e[0]  = matrix.getCell(x-1,y);	// left
////		e[1]  = matrix.getCell(x-1,y+1);	// below left
////		e[2]  = matrix.getCell(x,y+1);	// below
////		e[3]  = matrix.getCell(x+1,y+1);	// below right
////		e[4]  = matrix.getCell(x+1,y);	// right
////		e[5]  = matrix.getCell(x+1,y-1);	// above right
////		e[6]  = matrix.getCell(x,y-1);	// above
////		e[7]  = matrix.getCell(x-1,y-1);	// above left
//		e[8]  = e[0];
////		e[9]  = e[8] && e[1];
////		e[10] = e[1] && e[2];
////		e[11] = e[2] && e[3];
////		e[12] = e[3] && e[4];
////		e[13] = e[4] && e[5];
////		e[14] = e[5] && e[6];
////		e[15] = e[6] && e[7];
////		e[16] = e[7] && e[8];
////		e[17] = e[0] && e[2];
////		e[18] = e[2] && e[4];
////		e[19] = e[4] && e[6];
////		e[20] = e[6] && e[8];
////		for(int i = 0; i < 9; i++) n[i] = e[i] ? 1 : 0;		// if edge N exists, set n[N] to one, otherwise set it to 0.
//		for(int k = 0; k <= 7; k++){	//	for each edge		// \sum_{k=0}^7 1-n_{k}n_{k+1}
////			result += n[k] * (1 - n[k+1] * n[k+9]);
////			result += n[k] * (1 - n[k+1]);	// if edge N exists and edge N+1 (modulo 8) does not exist, add 1 to the result.
//			if(e[k] && !e[k+1]) result++;
//		}
//		for(int k = 0; k <= 3; k++){
////			result += n[2*k] * n[2*k+2] * (n[2*k+1] * n[2*k+9] * n[2*k+10] - n[k+17]);
////			result += n[2*k] * n[2*k+2] * (n[2*k+1] - n[k+17]);
////			result += n[2*k] * n[2*k+2] * (n[2*k+1] - (n[2*k] * n[2*k+2]));
//			
//			// if right edge and above edge,
//			// remove 1 from result if there is no above-right edge
//			if(e[2*k] && e[2*k+2] && !e[2*k+1]){
//				result--;
//			}
////					  n[0] * n[2] * (n[1] - (n[0] * n[2]));
//		}
//		for(int k = 0; k < 8; k++){	//TODO temporarily remarked below line
//			if(e[k] && e[(k+2) % 8] && e[(k+4) % 8] && matrix.edgeCount(x, y) == 3) result = 0;	// prevent deletion of triangles, remove them later.
//		}
////		if(pd == 3) result = 0;	// reinsert when perpendicular checking works
////		int pd = 0;
////		for(int k = 0; k < 8; k++){
//		
////			if((e[k] && e[(k+2) % 8]) || (e[(k+2) % 8]) && e[(k+4) % 8]) pd++;
////		}
////		if(pd == 3) result = 0;	// reinsert when perpendicular checking works
////		if(getPerpendiculars(node).length==3){
////			return 0;
////		}else{
//		return result;
////		}
//	}
	
//	/**
//	 * Returns an array containing the nodes perpendicular to the node.
//	 * In other words, the neighbours of this node, which are located at a 90 degree angle to one another.
//	 * 
//	 * @return	the neighbouring <code>GraphNode</code>s perpendicular to this <code>GraphNode</code>
//	 */
//	private Node[] getPerpendiculars(Node node){
//		HashSet<Node> perpendiculars = new HashSet<Node>();
//		Node[] circ_neigh = new Node[10];		// [0][1][2]
//												// [7][ ][3]
//												// [6][5][4]
//		circ_neigh[0] = node.getNeighbourAt(Node.THETA_ABOVELEFT);				
//		circ_neigh[1] = node.getNeighbourAt(Node.THETA_ABOVE);					
//		circ_neigh[2] = node.getNeighbourAt(Node.THETA_ABOVERIGHT);
//		circ_neigh[3] = node.getNeighbourAt(Node.THETA_RIGHT);
//		circ_neigh[4] = node.getNeighbourAt(Node.THETA_BELOWRIGHT);
//		circ_neigh[5] = node.getNeighbourAt(Node.THETA_BELOW);
//		circ_neigh[6] = node.getNeighbourAt(Node.THETA_BELOWLEFT);
//		circ_neigh[7] = node.getNeighbourAt(Node.THETA_LEFT);
//		circ_neigh[8] = circ_neigh[0];
//		circ_neigh[9] = circ_neigh[1];
//		for(int i=0; i<8; i++){
//			if(circ_neigh[i] != null && circ_neigh[i+2] != null){
//				perpendiculars.add(circ_neigh[i]);
//				perpendiculars.add(circ_neigh[i+2]);
//			}
//		}
//		Node[] result = (Node[])perpendiculars.toArray(new Node[0]);
//		return result;
//	}
//
//	public GraphMatrix getMatrix() {
//		return matrix;
//	}
}