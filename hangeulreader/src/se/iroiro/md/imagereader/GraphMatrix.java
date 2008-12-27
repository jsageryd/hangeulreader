/**
 * 
 */
package se.iroiro.md.imagereader;

/**
 * The <code>GraphMatrix</code> class describes a boolean 8-connected graph.
 * @author j
 *
 */
public class GraphMatrix {

	/**
	 * Matrix to store the data. The matrix has three dimensions.
	 * For each xy-cell, there is a level value used to store the edges between the cells.
	 */
	private boolean matrix[][][];
	
	/**
	 * The number of columns
	 */
	private final int x_size;
	
	/**
	 * The number of rows
	 */
	private final int y_size;

	/**
	 * Class constructor. Creates a GraphMatrix with <code>x_size</code> columns and <code>y_size</code> rows.
	 * @param x_size	the number of columns
	 * @param y_size	the number of rows
	 */
	public GraphMatrix(int x_size, int y_size){
		this.x_size = x_size;
		this.y_size = y_size;
		matrix = new boolean[5][y_size][x_size];
	}
	
	/**
	 * TODO Remove the y-argument here or make this method obsolete.
	 * Returns the <i>x</i>-coordinate of the edge:th neighbour to the specified cell.
	 * @param x	the <i>x</i>-coordinate
	 * @param y	the <i>y</i>-coordinate
	 * @param edge	the edge number (1-8, where 1 is right and 3 is up)
	 * @return	the <code>x</code>-coordinate of the neighbouring cell
	 */
	public int getNeighbourX(int x, int y, int edge){
		switch(edge){
		case 1: return x+1;
		case 2: return x+1;
		case 3: return x;
		case 4: return x-1;
		case 5: return x-1;
		case 6: return x-1;
		case 7: return x;
		case 8: return x+1;
		default: return x;
		}
	}

	/**
	 * TODO Remove the x-argument here or make this method obsolete.
	 * Returns the <i>y</i>-coordinate of the edge:th neighbour to the specified cell.
	 * @param x	the <i>x</i>-coordinate
	 * @param y	the <i>y</i>-coordinate
	 * @param edge	the edge number (1-8, where 1 is right and 3 is up)
	 * @return	the <i>y</i>-coordinate of the neighbouring cell
	 */
	public int getNeighbourY(int x, int y, int edge){
		switch(edge){
		case 1: return y;
		case 2: return y-1;
		case 3: return y-1;
		case 4: return y-1;
		case 5: return y;
		case 6: return y+1;
		case 7: return y+1;
		case 8: return y+1;
		default: return y;
		}
	}

	/**
	 * Returns the boolean value of column <code>x</code>, row </code>y</code>, at level <code>level</code>.
	 * This method looks up the value in the underlying matrix, and also includes error-checking.
	 * If the specified coordinate is invalid (i.e. outside the matrix), this method will return <code>false</code>.
	 * @param x	the column
	 * @param y	the row
	 * @param level	the level (0-4)
	 * @return	the boolean value of column <code>x</code>, row </code>y</code>, at level <code>level</code>
	 */
	private boolean getBit(int x, int y, int level){
		try{
			return matrix[level][y][x];
		}catch(ArrayIndexOutOfBoundsException e){
			return false;
		}
	}

	/**
	 * Sets the boolean value of column <code>x</code>, row </code>y</code>, at level <code>level</code>.
	 * This method sets the value in the underlying matrix, and also includes error-checking.
	 * If the specified coordinate is invalid (i.e. outside the matrix),
	 * this method will silently return without throwing an exception.
	 * @param x	the column
	 * @param y	the row
	 * @param level	the level (0-4)
	 * @param value	the boolean value for column <code>x</code>, row </code>y</code>, at level <code>level</code>
	 */
	private void setBit(int x, int y, int level, boolean value){
		try{
			matrix[level][y][x] = value;
		}catch(ArrayIndexOutOfBoundsException e){}
	}
	
	/**
	 * Returns the boolean value of the cell at column <code>x</code>, row <code>y</code>.
	 * @param x	the column
	 * @param y	the row
	 * @return	the boolean value of the cell at column <code>x</code>, row <code>y</code>
	 */
	public boolean getCell(int x, int y){
		return getBit(x,y,0);
	}
	
	/**
	 * Sets the boolean value of the cell at column <code>x</code>, row <code>y</code>.
	 * Regardless of <code>value</code>, this method always removes all edges to the cell.
	 * @param x	the column
	 * @param y	the row
	 * @param value	the boolean value for the cell at column <code>x</code>, row <code>y</code>
	 */
	public void setCell(int x, int y, boolean value){
		setBit(x,y,0,value);
		for(int e = 1; e <= 8; e++) setEdge(x,y,e,false);
	}

	/**
	 * Sets the edge value for the specified cell. <code>true</code> means there is an edge, <code>false</code> means there is no edge.
	 * A cell has 8 edges numbered counter-clockwise 1 through 8, where 1 is the edge that connects to its right neighbour, and 3 connects to its above neighbour.
	 * @param x	the column
	 * @param y	the row
	 * @param edge	the edge number (1-8)
	 * @param value	the edge value
	 */
	public void setEdge(int x, int y, int edge, boolean value){
		switch(edge){
		case 1:
		case 2:
		case 3:
		case 4: setBit(x,y,edge,value); break;
		case 5: setBit(x-1,y,1,value); break;
		case 6: setBit(x-1,y+1,2,value); break;
		case 7: setBit(x,y+1,3,value); break;
		case 8: setBit(x+1,y+1,4,value); break;
		}
	}

	/**
	 * Returns <code>true</code> if there the specified edge exists.
	 * A cell has 8 edges numbered counter-clockwise 1 through 8, where 1 is the edge that connects to its right neighbour, and 3 connects to its above neighbour.
	 * @param x	the column
	 * @param y	the row
	 * @param edge	the edge number (1-8)
	 * @return	<code>true</code> if there the specified edge exists
	 */
	public boolean getEdge(int x, int y, int edge){
		if(!getBit(x,y,0)) return false;
		switch(edge){
		case 1:
		case 2:
		case 3:
		case 4: return getBit(x,y,edge);
		case 5: return getBit(x-1,y,1);
		case 6: return getBit(x-1,y+1,2);
		case 7: return getBit(x,y+1,3);
		case 8: return getBit(x+1,y+1,4);
		}
		return false;
	}
	
	/**
	 * Returns the number of edges connecting with the specified cell.
	 * @param x	the column
	 * @param y	the row
	 * @return	the number of edges connecting with the specified cell
	 */
	public int edgeCount(int x, int y){
		int count = 0;
		for(int n = 1; n <=8; n++){
			if(getEdge(x,y,n)) count++;
		}
		return count;
	}

	/**
	 * Returns the underlying 3-dimensional boolean matrix.
	 * @return	the underlying matrix
	 */
	public boolean[][][] getMatrix() {
		return matrix;
	}

	/**
	 * Returns the number of columns in this matrix.
	 * @return	the number of columns in this matrix
	 */
	public int getX_size() {
		return x_size;
	}

	/**
	 * Returns the number of rows in this matrix.
	 * @return	the number of rows in this matrix
	 */
	public int getY_size() {
		return y_size;
	}
	
	/**
	 * Returns a deep copy of the underlying 3-dimensional boolean matrix
	 * @return	a deep copy of the underlying matrix
	 */
	public boolean[][][] deepCopyMatrix(){
		boolean[][][] dup = new boolean[5][getY_size()][getX_size()];
		for(int y = 0; y < getY_size(); y++){
			for(int x = 0; x < getX_size(); x++){
				for(int n = 0; n < 5; n++){
					dup[n][y][x] = matrix[n][y][x];
				}
			}
		}
		return dup;
	}

	/**
	 * Returns a detailed string representation of all the cells and their connections.
	 * @return	a detailed string representation of all the cells and their connections
	 */
	public String toString(){
		String result = "";
		String r1 = "";
		String r2 = "";
		for(int y = 0; y < getY_size(); y++){
			for(int x = 0; x < getX_size(); x++){
				r2 += getCell(x,y) ? "o" : " ";
				r2 += getEdge(x,y,1) ? "---" : "   ";
				if(getEdge(x,y,3))
					r1 += "|";
				else
					r1 += " ";
				if(getEdge(x,y,2) && getEdge(x+1,y,4)){
						r1 += " X ";
				}else if(getEdge(x,y,2)){
					r1 += " / ";
				}else if(getEdge(x+1,y,4)){
					r1 += " \\ ";
				}else{
					r1 += "   ";
				}
			}
			if(r1 != "") result += r1;
			if(r2 != "") result += "\n" + r2;
			result += "\n";
			r1 = "";
			r2 = "";
		}
		return result;
	}
}