/**
 *
 */
package se.iroiro.md.graph.simple;

import java.io.Serializable;

import se.iroiro.md.graph.Coordinate;
import se.iroiro.md.graph.XYPositioned;

/**
 * @author j
 *
 */
public abstract class AbstractXYPositioned implements XYPositioned, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -5953494097704963501L;

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
