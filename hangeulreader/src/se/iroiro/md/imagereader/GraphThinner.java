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
		int counter = 0;
		ArrayList<Integer> rem_x, rem_y;
		while((deleted > 0) && counter < 4){
			deleted = 0;
			dir = ++dir % 4;
			rem_x = new ArrayList<Integer>();
			rem_y = new ArrayList<Integer>();
			for(int y = 0; y < matrix.getY_size(); y++){
				for(int x = 0; x < matrix.getX_size(); x++){
					if(!matrix.getCell(x,y)) continue;
					direction = false;
					switch(dir){
					case 0: direction = !matrix.getEdge(x,y,3); break;	// above
					case 1: direction = !matrix.getEdge(x,y,5); break;	// left
					case 2: direction = !matrix.getEdge(x,y,7); break;	// below
					case 3: direction = !matrix.getEdge(x,y,1); break;	// right
					}
					if(direction && edgeCount(matrix,x,y) != 1 && getConnectivityNumber(matrix,x,y) == 1){
						rem_x.add(x);
						rem_y.add(y);
						deleted++;
					}
				}
			}
			for(int i = 0; i < rem_x.size(); i++){
				int y = rem_y.get(i);
				int x = rem_x.get(i);
				matrix.setCell(x,y,false);
				for(int n = 1; n < 9; n++) matrix.setEdge(x,y,n,false);
			}
		}
		deleteExtraEdges(matrix);
	}
	
	//TODO Why is this method duplicated in GraphMatrix? Fix and use only either.
	private static int edgeCount(GraphMatrix matrix, int x, int y){
		int c = 0;
		for(int n = 1; n < 9; n++){
			if(matrix.getEdge(x,y,n)) c++;
		}
		return c;
	}
	
	/**
	 * Deletes diagonal edges which connect two perpendicular edges.
	 * This is to prevent T-shaped crossings in the input image from becoming Y-shaped in the graph.
	 */
	public static void deleteExtraEdges(GraphMatrix matrix){
		int e1, e2, e3;
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
	
}