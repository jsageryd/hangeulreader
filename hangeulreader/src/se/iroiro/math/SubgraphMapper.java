/**
 * 
 */
package se.iroiro.math;

import java.util.ArrayList;
import java.util.List;

/**
 * This class yields all possible mappings of a subgraph to a graph.
 * @author j
 *
 */
public class SubgraphMapper {

	/**
	 * Returns a list of possible node mappings A-B limited by M.
	 * The algorithm generates all possible matrices M' such that
	 * for any 1-value in M', the value of the corresponding cell in M is also 1.
	 * such that no row contains more than one 1 and no column contains more than one 1.
	 * Permutes rows and columns of each M' with adjacency matrix B
	 * and compares against adjacency matrix A.
	 * If all 1-values in A are also 1-values in B, M' is added to the return list.
	 * @param M0	mapping of nodes that are of equal type in subgraph-graph.
	 * @param A	adjacency matrix for the subgraph
	 * @param B adjacency matrix for the graph
	 * @return	a list of possible node mappings A-B limited by M.
	 */
	public static List<IntMatrix> getMapping(IntMatrix M0, IntMatrix A, IntMatrix B){
		List<IntMatrix> cand = findCandidates(M0, new IntMatrix(M0.rowCount(),M0.colCount()),0,A,B);
//		Iterator<IntMatrix> it = cand.iterator();
//		while(it.hasNext()){
//			IntMatrix m = it.next();
//			IntMatrix c = m.times((m.times(B)).transpose());
//			outer:
//			for(int i = 0; i < c.rowCount(); i++){
//				for(int j = 0; j < c.colCount(); j++){
//					if(A.get(i,j) == 1 && c.get(i,j) != 1) it.remove();
//					break outer;
//				}
//			}
//		}
		return cand;
	}
	
	private static List<IntMatrix> findCandidates(IntMatrix M0, IntMatrix Mprim, int depth, IntMatrix A, IntMatrix B){
		List<IntMatrix> cand = new ArrayList<IntMatrix>();
		outer:
		for(int j = 0; j < M0.colCount(); j++){	// iterate through all columns
			if(M0.get(depth,j) == 1){	// if M0 permits
				for(int i = 0; i < depth; i++){	// loop through cells above current, to see if we already used this column
					if(Mprim.get(i,j) == 1){
						continue outer;	// if it was used, continue with next column
					}
				}
				for(int x = 0; x < Mprim.colCount(); x++){	// else, if the column can be used, set this cell of Mprim to 1,
					Mprim.set(depth, x, x == j ? 1 : 0);	// and other values on the same row to 0
				}
				if(depth == M0.rowCount()-1){	// if we are at the last row (maximum depth), add the candidate to the list
//					cand.add((IntMatrix)Mprim.clone());	// (maybe replace this by isomorph-match?)
					IntMatrix c = Mprim.times((Mprim.times(B)).transpose());	// start isomorph-match
					boolean add = true;
					outer_:
						for(int y = 0; y < c.rowCount(); y++){
							for(int x = 0; x < c.colCount(); x++){		// check that permuted matrix c contains 1 wherever
								if(A.get(y,x) == 1 && c.get(y,x) != 1){	// adjacency matrix A contains 1. If not so, set add=false,
									add = false;						// and break.
									break outer_;
								}
							}
						}														// end isomorph match
					if(add){	// if add=true, then we had a match so add it to the list of possible mappings.
						cand.add((IntMatrix)Mprim.clone());	// TODO rename cand to mappings?
					}
				}else{
					cand.addAll(findCandidates(M0, Mprim, depth+1, A, B));	// if it is not the last row, recurse to next row
				}
			}
		}
		return cand;	// return all candidates found
	}
	
	
	// Below, an attempt to implement the algorithm as originally described by Ullmann.
	// Failed attempt and stroke of genius resulted in the recursive approach above.
	
//	/**
//	 * Returns a list of possible node mappings A-B limited by M.
//	 * The algorithm generates all possible matrices M' such that
//	 * for any 1-value in M', the value of the corresponding cell in M is also 1.
//	 * such that no row contains more than one 1 and no column contains more than one 1.
//	 * Permutes rows and columns of each M' with adjacency matrix B
//	 * and compares against adjacency matrix A.
//	 * If all 1-values in A are also 1-values in B, M' is added to the return list.
//	 * @param M0	mapping of nodes that are of equal type in subgraph-graph.
//	 * @param A	adjacency matrix for the subgraph
//	 * @param B adjacency matrix for the graph
//	 * @return	a list of possible node mappings A-B limited by M.
//	 */
//	public static List<IntMatrix> getMapping(IntMatrix M0, IntMatrix A, IntMatrix B){
//		// Generate all possible matrices M' such that
//		// for any 1-value in M', the value of the corresponding cell in M is also 1.
//		// For each matrix M', do the permutation/transpose thing and compare to A.
//		// If all 1-values in A are 1 in B, add M' to the list.
//		
//		// Pa is the node count for the subgraph
//		int Pa = A.rowCount();
//		
//		// Pb is the node count for the graph
//		int Pb = B.rowCount();
//		
//		if(M0.rowCount() != Pa || M0.colCount() != Pb){
//			throw new IllegalArgumentException("Dimensions of M0 must be A.side rows and B.side columns.");
//		}
//		
//		List<IntMatrix> result = new ArrayList<IntMatrix>();
//		
//		
//		// F records which columns have been used at an intermediate state of the computation,
//		// F[i] = 1 if the i:th column has been used. 
//		int[] F = new int[Pb];
//		
//		// H records which column has been selected at which depth.
//		// H[d] = k if the k:th column has been selected at depth d.
//		int[] H = new int[Pa];
//		
//		// We start at the first row (depth = 0)
//		int d = 0;
//		
//		// Init H[0] to -1
//		H[0] = -1;
//		
//		// Init M to M0
//		IntMatrix M = (IntMatrix) M0.clone();
//		
//		
//		// Step 2
//		for(int j = 0; j < Pb; j++){
//			if(M.get(d,j) == 1 && F[j] == 0){
//			}
//		}
//		
//		return result;
//	}
	
}
