/**
 * 
 */
package se.iroiro.md.graph.simple;

import se.iroiro.md.graph.Coordinate;
import se.iroiro.md.graph.XYPositioned;

/**
 * @author j
 *
 */
public abstract class AbstractXYPositioned implements XYPositioned {

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYPositioned#getAngleTo(se.iroiro.md.graph.XYPositioned)
	 */
	public double getAngleTo(XYPositioned other) {
		return getPosition().getAngleTo(other.getPosition());
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYPositioned#rotate(double)
	 */
	public void rotate(double angle) {
		rotate(angle, getPosition());
	}
	
	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.XYPositioned#setPosition(se.iroiro.md.graph.Coordinate)
	 */
	public void setPosition(Coordinate c){
		throw new UnsupportedOperationException("Not supported by this class. Use setPosition(double x, double y) instead.");
	}

}
