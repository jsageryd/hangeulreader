/**
 *
 */
package se.iroiro.math;

/**
 * This class represents matrix of integer values and contains selection of methods to manipulate it.
 * Note that the first cell in the matrix is (0,0) and not (1,1).
 *
 * @author j
 *
 */
public class IntMatrix implements Cloneable {

	/**
	 * Internal storage
	 */
	private int[][] A;

	/**
	 * Row dimension
	 */
	private int rows;

	/**
	 * Column dimension
	 */
	private int cols;

	/**
	 * Creates an matrix of size <code>rows</code>*<code>columns</code>
	 * and initialises all its values to zero.
	 *
	 * @param rows	the row dimension
	 * @param cols	the column dimension
	 */
	public IntMatrix(int rows, int cols){
		this(rows,cols,0);
	}

	/**
	 * Creates an matrix of size <code>rows</code>*<code>columns</code>
	 * and initialises all its values to the specified value.
	 *
	 * @param rows	the row dimension
	 * @param cols	the column dimension
	 * @param value	the value to set all cells to
	 */
	public IntMatrix(int rows, int cols, int value){
		this.rows = rows;
		this.cols = cols;
		A = new int[rows][cols];
		setAll(value);
	}

	/**
	 * Sets all cells in this matrix to <code>value</code>.
	 * @param value	the value to set
	 */
	public void setAll(int value){
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				A[i][j] = value;
			}
		}
	}

	/**
	 * Returns a deep copy of the object.
	 */
	public Object clone() {
		IntMatrix X = new IntMatrix(rows,cols);
		int[][] C = X.getArray();
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				C[i][j] = A[i][j];
			}
		}
		return X;
	}

	/**
	 * Sets the value of the specified cell.
	 * @param row	the row of the cell
	 * @param column	the column of the cell
	 * @param value	the value to set
	 */
	public void set(int row, int column, int value){
		A[row][column] = value;
	}

	/**
	 * Returns the value of the specified cell.
	 * @param row	the row of the cell
	 * @param column	the column of the cell
	 * @return	the value of the specified cell
	 */
	public int get(int row, int column){
		return A[row][column];
	}

	/**
	 * Returns a new matrix which is the transpose of this.
	 * @return	a transposed matrix
	 */
	public IntMatrix transpose(){
		IntMatrix X = new IntMatrix(cols,rows);
		int[][] C = X.getArray();
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				C[j][i] = A[i][j];
			}
		}
		return X;
	}

	/**
	 * Adds this matrix to the specified one and returns a new matrix with the result.
	 * @param B	the matrix add
	 * @return	a new matrix
	 */
	public IntMatrix plus(IntMatrix B){
		verifyDimensions(B);
		IntMatrix X = new IntMatrix(rows,cols);
		int[][] C = X.getArray();
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				C[i][j] = A[i][j] + B.A[i][j];
			}
		}
		return X;
	}

	/**
	 * Subtracts the specified matrix from this one and returns a new matrix with the result.
	 * @param B	the matrix to subtract
	 * @return	a new matrix
	 */
	public IntMatrix minus(IntMatrix B){
		verifyDimensions(B);
		IntMatrix X = new IntMatrix(rows,cols);
		int[][] C = X.getArray();
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				C[j][i] = A[i][j] - B.A[i][j];
			}
		}
		return X;
	}

	/**
	 * Multiplies this matrix with the the specified scalar and returns a new matrix with the result.
	 * @param s	the scalar to multiply with
	 * @return	a new matrix
	 */
	public IntMatrix times(int s){
		IntMatrix X = new IntMatrix(rows,cols);
		int[][] C = X.getArray();
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				C[j][i] = A[i][j] * s;
			}
		}
		return X;
	}

	/**
	 * Multiplies this matrix with the the specified one and returns a new matrix with the result.
	 * @param B	the matrix to multiply with
	 * @return	a new matrix
	 */
	public IntMatrix times(IntMatrix B){
		if(B.rows != cols){
			throw new IllegalArgumentException("Matrix inner dimensions must agree.");
		}
		IntMatrix X = new IntMatrix(rows,B.cols);
		int[][] C = X.getArray();
		for(int i = 0; i < X.rows; i++){
			for(int j = 0; j < X.cols; j++){
				for(int n = 0; n < cols; n++){
					C[i][j] += A[i][n] * B.A[n][j];
				}
			}
		}
		return X;
	}

	/**
	 * Returns the underlying array.
	 * @return	the array
	 */
	public int[][] getArray(){
		return A;
	}

	/**
	 * Returns a string representation of this matrix.
	 */
	public String toString(){
		String s = "";
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				s += A[i][j];
				if(j < cols-1) s+= "\t";
			}
			s += "\n";
		}
		return s;
	}

	/**
	 * Throws exception if the dimensions of the specified matrix differ from this one's.
	 * @param B	the matrix to check
	 */
	private void verifyDimensions(IntMatrix B){
		if(B.rows != rows || B.cols != cols){
			throw new IllegalArgumentException("Matrix dimensions must agree.");
		}
	}

	/**
	 * Returns the row dimension.
	 * @return	the number of rows
	 */
	public int rowCount() {
		return rows;
	}

	/**
	 * Returns the column dimension.
	 * @return	the number of column
	 */
	public int colCount() {
		return cols;
	}

}
