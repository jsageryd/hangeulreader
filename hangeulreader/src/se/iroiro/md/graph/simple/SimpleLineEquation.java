/**
 *
 */
package se.iroiro.md.graph.simple;

import java.text.DecimalFormat;

import se.iroiro.md.graph.Coordinate;
import se.iroiro.md.graph.LineEquation;

/**
 * The <code>LineEquation</code> class describes an equation of a straight line.
 * @author j
 *
 */
public class SimpleLineEquation implements LineEquation {

	private double slope = Double.NaN;
	private double intercept = Double.NaN;
	private double horizontalIntercept = Double.NaN;

	/**
	 * Class constructor with (Cartesian) coordinate parameters.
	 * @param x1	the <i>x</i>-value of the first point
	 * @param y1	the <i>y</i>-value of the first point
	 * @param x2	the <i>x</i>-value of the second point
	 * @param y2	the <i>y</i>-value of the second point
	 */
	public SimpleLineEquation(double x1, double y1, double x2, double y2){
		if(x1 == y1 && y1 == x2 && x2 == y2){
			DecimalFormat df = new DecimalFormat("0.##");
			throw new IllegalArgumentException("Coordinates [" + df.format(x1) + ","
					+ df.format(y1) + "],[" + df.format(x2) + ","
					+ df.format(y2) + "] do not describe a line.");
		}else if(x1 == x2){
			setHorizontalIntercept(x1);
		}else{
			setSlope((y2 - y1) / (x2 - x1));
			setIntercept(y1 - (getSlope() * x1));
			setHorizontalIntercept(getX(0));	// added but hasn't been tested yet (2008-09-20).
		}
	}

	/**
	 * Class constructor with slope and intersect parameters.
	 * @param slope	the slope
	 * @param intercept	the intercept
	 */
	public SimpleLineEquation(double slope, double intercept){
		setSlope(slope);
		setIntercept(intercept);
	}

	/**
	 * Class constructor with horizontal intercept parameter.
	 * @param horizontalIntercept	the horizontal intercept
	 */
	public SimpleLineEquation(double horizontalIntercept){
		setHorizontalIntercept(horizontalIntercept);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String equation;
		DecimalFormat df = new DecimalFormat("0.##");
		if(!isVertical()){
			String slopeStr, interceptStr;
			if(getSlope() == 0.0){
				slopeStr = "";
			}else if(getSlope() == 1.0){
				slopeStr = "x";
			}else if(getSlope() == -1.0){
				slopeStr = "-x";
			}else{
				slopeStr = df.format(getSlope()) + "x";
			}
			if(getIntercept() == 0.0){
				interceptStr = "";
			}else{
				if("".equals(slopeStr)){
					interceptStr = (getIntercept() < 0.0 ? "-" : "");
				}else{
					interceptStr = (getIntercept() < 0.0 ? " - " : " + ");
				}
				interceptStr += df.format(Math.abs(getIntercept()));
			}
			equation = "y = " + slopeStr + interceptStr;
		}else{
			equation = "x = " + df.format(getHorizontalIntercept());
		}
		return equation;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.LineEquation#getSlope()
	 */
	public double getSlope() {
		return slope;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.LineEquation#getIntercept()
	 */
	public double getIntercept() {
		return intercept;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.LineEquation#isVertical()
	 */
	public boolean isVertical() {
		return Double.isNaN(getSlope()) || Double.isInfinite(getSlope());
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.LineEquation#getHorizontalIntercept()
	 */
	public double getHorizontalIntercept() {
		return horizontalIntercept;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.LineEquation#setSlope(double)
	 */
	public void setSlope(double slope) {
		if(Double.isNaN(slope) && Double.isNaN(getHorizontalIntercept())){
			throw new IllegalArgumentException("Set a valid horizontal intercept before setting an undefined slope.");
		}
		if(slope == 0.0) slope = 0.0;	// prevent negative zero
		this.slope = slope;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.LineEquation#setIntercept(double)
	 */
	public void setIntercept(double intercept) {
		if(Double.isNaN(intercept) || Double.isInfinite(intercept)){
			throw new IllegalArgumentException("Invalid value for intercept.");
		}
		if(intercept == 0.0) intercept = 0.0;	// prevent negative zero
		this.intercept = intercept;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.LineEquation#setHorizontalIntercept(double)
	 */
	public void setHorizontalIntercept(double horizontalIntercept) {
		this.horizontalIntercept = horizontalIntercept;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.LineEquation#getX(double)
	 */
	public double getX(double y){
		if(isVertical()) return getHorizontalIntercept();
		if(getSlope() == 0.0) return Double.NaN;
		double x = (y - getIntercept()) / getSlope();
		if(x == 0.0) x = 0.0;	// prevent negative zero
		return roundDouble(x);
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.LineEquation#getY(double)
	 */
	public double getY(double x){
		if(isVertical()) return Double.NaN;
		double y = (getSlope() * x) + getIntercept();
		if(y == 0.0) y = 0.0;	// prevent negative zero
		return roundDouble(y);
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.LineEquation#getIntersection(se.iroiro.md.graph.LineEquation)
	 */
	public Coordinate getIntersection(LineEquation eq){
		double x, y;
		if(Double.compare(getSlope(), eq.getSlope()) == 0){
			x = Double.NaN;
			y = Double.NaN;
		}else if(isVertical()){
			x = getHorizontalIntercept();
			y = eq.getY(x);
		}else if(eq.isVertical()){
			x = eq.getHorizontalIntercept();
			y = getY(x);
		}else{
			x = (eq.getIntercept() - getIntercept()) / (getSlope() - eq.getSlope());
			y = roundDouble(getY(x));
		}
		return new SimpleCoordinate(x,y);
	}

	/**
	 * Rounds the specified <code>double</code> as an attempt to prevent floating point errors.
	 * @param d	the <code>double</code> to round
	 * @return	the rounded value
	 */
	private double roundDouble(double d){
		double p = 1000000000;
		return Math.rint(d * p) / p;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.LineEquation#intersects(se.iroiro.md.graph.LineEquation)
	 */
	public boolean intersects(LineEquation eq){
		Coordinate is = getIntersection(eq);
		return !Double.isNaN(is.getX()) && !Double.isNaN(is.getY());
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.LineEquation#sameAs(se.iroiro.md.graph.LineEquation)
	 */
	public boolean sameAs(LineEquation other){
		return toString().equals(other.toString());
//		return getIntercept() == other.getIntercept() && getSlope() == other.getSlope() && getHorizontalIntercept() == other.getHorizontalIntercept();
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.LineEquation#touches(se.iroiro.md.graph.LineEquation)
	 */
	public boolean touches(LineEquation eq){
		return sameAs(eq) || intersects(eq);
	}

}
