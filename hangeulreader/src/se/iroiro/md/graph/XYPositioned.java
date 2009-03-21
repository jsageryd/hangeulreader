/**
 *
 */
package se.iroiro.md.graph;

/**
 * @author j
 *
 */
public interface XYPositioned {

	/**
	 * Returns the position of this object. This is the centre point of the object.
	 * The position may be an actual field, but it may also be calculated on-the-fly.
	 * This means there is no guarantee that the position object returned is actually referenced to by this object.
	 * @return	the position of this object
	 */
	public Coordinate getPosition();

	/**
	 * Sets the position <emphasis>object</emphasis> of this object. This is the centre point of the object.
	 * This method is used to directly set the position object,
	 * a handy feature which enables more than one object to share the same <code>Coordinate</code>-object;
	 * thus if one of the objects are moved, all objects sharing its position are automatically moved as well.
	 * <strong>Objects that always calculate their positions based on other objects always throw
	 * an <code>UnsupportedOperationException</code> here.</strong>
	 * @param position	the position object for this object
	 * @see XYPositioned#setPosition(double, double)
	 */
	public void setPosition(Coordinate position);

	/**
	 * Sets the position of this object. This is the centre point of the object.
	 * This method always creates a <strong>new</strong> <code>SimpleCoordinate</code> from the values specified.
	 * If the position of the object is calculated from external objects
	 * (e.g. two nodes determining the position of an edge),
	 * the positions of these external objects will be updated so that the position of this
	 * object will be <code>position</code>.
	 * @param x	<i>x</i>-coordinate
	 * @param y	<i>y</i>-coordinate
	 */
	public void setPosition(double x, double y);

	/**
	 * Rotates the object <code>angle</code> radians around its own centre (retrieved by {@link XYPositioned#getPosition()}.)
	 * @param angle	the angle in radians
	 */
	public void rotate(double angle);

	/**
	 * Rotates the object <code>angle</code> radians around the specified pivot point.
	 * @param angle	the angle in radians
	 * @param pivot	the pivot point
	 */
	public void rotate(double angle, Coordinate pivot);

	/**
	 * Returns the angle in radians to the specified object. 0 = right, PI / 2 = up.
	 * If the angle cannot be determined, Double.NaN is returned.
	 * @param other	the object to which the angle is to be determined
	 * @return	the angle in radians
	 */
	public double getAngleTo(XYPositioned other);

	/**
	 * Returns the angle in radians of this object. If the object has no angle, Double.NaN is returned.
	 * @return	the angle in radians
	 */
	public double getAngle();

}
