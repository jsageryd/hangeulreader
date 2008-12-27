/**
 * 
 */
package se.iroiro.md.hangeul;

import se.iroiro.md.graph.Coordinate;
import se.iroiro.md.graph.XYPositioned;
import se.iroiro.md.graph.simple.AbstractXYPositioned;

/**
 * Represents a corner of a line.
 * @author j
 *
 */
@Deprecated
public class Corner extends AbstractXYPositioned implements XYPositioned {

	private Coordinate left;
	private Coordinate centre;
	private Coordinate right;

	/**
	 * Class constructor. Initiates a corner with left, centre and right coordinate.
	 * @param left	the outer point of the left edge
	 * @param centre	the point where the edges meet
	 * @param right	the outer point of the right edge
	 */
	public Corner(Coordinate left, Coordinate centre, Coordinate right) {
		this.left = left;
		this.centre = centre;
		this.right = right;
	}

	/**
	 * Returns the angle of the corner, regardless of its rotation.
	 * @return	the angle of the corner
	 */
	public double getAngle() {
		double one = centre.getAngleTo(left);
		double two = centre.getAngleTo(right);
		while(two < one) two += Math.PI * 2;
		return two - one;
	}
	
	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYPositioned#getPosition()
	 */
	public Coordinate getPosition() {
		return centre;
	}
	
	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYPositioned#rotate(double, se.iroiro.md.graph.Coordinate)
	 */
	public void rotate(double angle, Coordinate pivot) {
		left.rotate(angle, pivot);
		centre.rotate(angle, pivot);
		right.rotate(angle, pivot);
	}
	
	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYPositioned#setPosition(double, double)
	 */
	public void setPosition(double x, double y) {
		centre.setXY(x,y);
		left.translate(x-left.getX(), y-left.getY());
		right.translate(x-right.getX(), y-right.getY());
	}
	
	/**
	 * Flips the corner so that inside becomes outside and outside becomes inside.
	 * A 90-degree corner becomes a 270-degree corner.
	 */
	public void flip(){
		Coordinate tmp = left;
		left = right;
		right = tmp;
	}

	/**
	 * Returns the outer point of the left edge.
	 * @return	the left coordinate
	 */
	public Coordinate getLeft(){
		return left;
	}

	/**
	 * Returns the outer point of the right edge.
	 * @return	the right coordinate
	 */
	public Coordinate getRight(){
		return right;
	}
	
}