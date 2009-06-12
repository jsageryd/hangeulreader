/**
 *
 */
package se.iroiro.md.graph.simple;

import java.text.DecimalFormat;
import java.util.Locale;

import se.iroiro.md.graph.Coordinate;

/**
 * @author j
 *
 */
public class SimpleCoordinate implements Coordinate, Cloneable {

	/**
	 * Cartesian X-coordinate
	 */
	private double x;

	/**
	 * Cartesian Y-coordinate
	 */
	private double y;

	/**
	 * Class constructor. <i>x</i> and <i>y</i> are Cartesian coordinates.
	 * @param x	x-coordinate
	 * @param y	y-coordinate
	 */
	public SimpleCoordinate(double x, double y){
		this.x = x;
		this.y = y;
	}

	/**
	 * Class constructor.
	 * The values of the <i>x</i>- and <i>y</i>-coordinate are copied from the specified <code>Coordinate</code>.
	 * @param c	the coordinate to read
	 */
	public SimpleCoordinate(Coordinate c) {
		this.x = c.getX();
		this.y = c.getY();
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Coordinate#getJavaX()
	 */
	public double getJavaX() {
		return getX();
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Coordinate#getJavaY()
	 */
	public double getJavaY() {
		return -getY();
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Coordinate#getX()
	 */
	public double getX() {
		return x;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Coordinate#getY()
	 */
	public double getY() {
		return y;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Coordinate#setJavaX(double)
	 */
	public void setJavaX(double x) {
		setX(x);
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Coordinate#setJavaY(double)
	 */
	public void setJavaY(double y) {
		setY(-y);
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Coordinate#setX(double)
	 */
	public void setX(double x) {
		this.x = x;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Coordinate#setY(double)
	 */
	public void setY(double y) {
		this.y = y;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Coordinate#isSamePositionAs(se.iroiro.md.graph.Coordinate)
	 */
	public boolean isSamePositionAs(Coordinate other) {
		if (Double.doubleToLongBits(getX()) != Double.doubleToLongBits(other.getX()))
			return false;
		if (Double.doubleToLongBits(getY()) != Double.doubleToLongBits(other.getY()))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Coordinate#getAngleTo(se.iroiro.md.graph.Coordinate)
	 */
	public double getAngleTo(Coordinate other) {
		if(isSamePositionAs(other)) return Double.NaN;
		double theta = Math.atan2(other.getY() - getY(), other.getX() - getX());
		while(theta < 0) theta += Math.PI * 2;
		if(theta == 0.0) theta = 0.0;	// prevent negative zero
		return theta;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Coordinate#translate(double, double)
	 */
	public Coordinate translate(double dx, double dy) {
		x += dx;
		y += dy;
		return this;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		DecimalFormat df = new DecimalFormat("0.00");
		return "["+df.format(getX())+","+df.format(getY())+"]";
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Coordinate#setXY(double, double)
	 */
	public void setXY(double x, double y) {
		setX(x);
		setY(y);
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Coordinate#rotate(double, se.iroiro.md.graph.Coordinate)
	 */
	public Coordinate rotate(double angle, Coordinate pivot) {
		double cos_a = Math.cos(angle);	// precalculate
		double sin_a = Math.sin(angle);	// "
		double norm_x = getX() - pivot.getX();	// move so that pivot is origo
		double norm_y = getY() - pivot.getY();	// "
		double new_x = norm_x * cos_a - norm_y * sin_a + pivot.getX();	// rotate around origo and move back to pivot offset
		double new_y = norm_y * cos_a + norm_x * sin_a + pivot.getY();	// "
		setXY(new_x, new_y);	// set new location
		return this;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Coordinate#distanceTo(se.iroiro.md.graph.Coordinate)
	 */
	public double distanceTo(Coordinate c) {
		double dx = c.getX() - getX();
		double dy = c.getY() - getY();
		return Math.sqrt(dx * dx + dy * dy);	// calculate distance using Pythagorean theorem
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Coordinate#scale(double, double)
	 */
	public Coordinate scale(double xm, double ym) {
		return new SimpleCoordinate(getX()*xm,getY()*ym);
	}

	public Coordinate scale(double m) {
		return scale(m,m);
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Coordinate#copy()
	 */
	public Coordinate copy() {
		return new SimpleCoordinate(this);
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Coordinate#getID()
	 */
	public String getID() {
		return String.format(Locale.US, "%Ax%A", x, y).replace('.', 'd').replace('-', 'n');
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
