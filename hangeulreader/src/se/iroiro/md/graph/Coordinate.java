/**
 *
 */
package se.iroiro.md.graph;

/**
 * Represents an XY-coordinate.
 * @author j
 *
 */
public interface Coordinate {

	/**
	 * Returns the <i>x</i>-coordinate in Cartesian format.
	 * @return	the <i>x</i>-coordinate
	 */
	public double getX();

	/**
	 * Sets the <i>x</i>-coordinate in Cartesian format.
	 * @param x	the <i>x</i>-coordinate
	 */
	public void setX(double x);

	/**
	 * Returns the <i>x</i>-coordinate in java format.
	 * Note that this method exists only for conformity and returns the same value as {@link Coordinate#getX()}.
	 * @return	the <i>x</i>-coordinate in java format
	 */
	public double getJavaX();

	/**
	 * Sets the <i>x</i>-coordinate in java format.
	 * Note that this method exists only for conformity and sets the same value as {@link Coordinate#setX(double)}.
	 * @param x	the <i>x</i>-coordinate in java format
	 */
	public void setJavaX(double x);

	/**
	 * Returns the <i>y</i>-coordinate in Cartesian format.
	 * @return	the <i>y</i>-coordinate
	 */
	public double getY();

	/**
	 * Sets the <i>y</i>-coordinate in Cartesian format.
	 * @param y	the <i>y</i>-coordinate
	 */
	public void setY(double y);

	/**
	 * Returns the <i>y</i>-coordinate in java format.
	 * This method returns <code>-</code>{@link Coordinate#getY()}.
	 * @return	the <i>y</i>-coordinate in java format
	 */
	public double getJavaY();

	/**
	 * Sets the <i>y</i>-coordinate in java format.
	 * This methods calls {@link Coordinate#setY(double)} with </code>-y</code> as argument.
	 * @param y	the <i>y</i>-coordinate in java format
	 */
	public void setJavaY(double y);

	/**
	 * Determines if this <code>Coordinate</code> has the same coordinate as the specified <code>Coordinate</code>
	 * @param other	other coordinate
	 * @return	<code>true</code> if this <code>Coordinate</code> has the same coordinate as the other <code>Coordinate</code>
	 */
	public boolean isSamePositionAs(Coordinate other);

	/**
	 * Returns the angle in radians to the specified coordinate. 0 = right, PI / 2 = up.
	 * If the angle cannot be determined (i.e. coordinates are the same), Double.NaN is returned.
	 * @param other	the coordinate to which the angle is to be determined
	 * @return	angle in radians
	 */
	public double getAngleTo(Coordinate other);

	/**
	 * Translates this point, at location [<i>x</i>,<i>y</i>],
	 * by <code>dx</code> along the <i>x</i>-axis and <code>dy</code>
	 * along the <i>y</i> axis so that it after translation represents
	 * the point [<i>x</i>+<code>dx,<i>y</i>+<code>dy].
	 * Returns a reference to itself for convenience. No copy is made.
	 * @param dx	the distance to move this coordinate along the <i>x</i>-axis
	 * @param dy	the distance to move this coordinate along the <i>y</i>-axis
	 * @return	a reference to this coordinate
	 */
	public Coordinate translate(double dx, double dy);

	/**
	 * Returns a string representation of the coordinate in the form <code>[x,y]</code> with two decimal places.
	 * @return	a string representation of the coordinate
	 */
	public String toString();

	/**
	 * Sets the <i>x</i>- and <i>y</i>-coordinates in Cartesian format.
	 * @param x	the <i>x</i>-coordinate
	 * @param y	the <i>y</i>-coordinate
	 */
	public void setXY(double x, double y);

	/**
	 * Rotates the coordinate <code>angle</code> radians around pivot point <code>pivot</code>.
	 * Returns a reference to itself for convenience. No copy is made.
	 * @param angle	angle in radians
	 * @param pivot	pivot point
	 * @return	a reference to this coordinate
	 */
	public Coordinate rotate(double angle, Coordinate pivot);

	/**
	 * Returns the Euclidian distance between this coordinate and the specified coordinate.
	 * @param c	the coordinate to measure the distance to
	 * @return	the Euclidian distance to the specified coordinate
	 */
	public double distanceTo(Coordinate c);

	/**
	 * Multiplies the <i>x</i>-coordinate with <code>xm</code>, and the <i>y</i>-coordinate with <code>ym</code>.
	 * Stores the result in a new coordinate object and returns it.
	 * @param xm	the <i>x</i>-multiplier
	 * @param ym	the <i>y</i>-multiplier
	 * @return	a new, scaled coordinate object
	 * @see	Coordinate#scale(double)
	 */
	public Coordinate scale(double xm, double ym);

	/**
	 * Multiplies the <i>x</i>- and <i>y</i>-coordinates with <code>m</code>.
	 * Stores the result in a new coordinate object and returns it.
	 * Calling this method is equal to calling {@link Coordinate#scale(double, double)}
	 * with <code>xm == m</code> and <code>ym == m</code>.
	 * @param m	the multiplier value
	 * @return	a new, scaled coordinate object
	 * @see	Coordinate#scale(double, double)
	 */
	public Coordinate scale(double m);

	/**
	 * Returns a copy of the current instance.
	 * This is the same as creating a new instance of the class specifying the current instance as argument.
	 * @return	a copy of the current instance
	 */
	public Coordinate copy();

	/**
	 * Returns an ID based on the x and y values.
	 * @return	an ID
	 */
	public String getID();

	/**
	 * Returns a copy of this coordinate.
	 * @return	a copy of this coordinate
	 */
	public Object clone();

}
