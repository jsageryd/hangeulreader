/**
 * 
 */
package se.iroiro.md.graph;

/**
 * <code>LineEquation</code> describes an equation of a straight line.
 * @author j
 *
 */
public interface LineEquation {

		/**
		 * Returns a string representation of the equation.
		 * @return	a string representation of the equation
		 */
		public String toString();

		/**
		 * Returns the slope value of the equation.
		 * If the slope is undefined, Double.NaN will be returned.
		 * @return	the slope
		 */
		public double getSlope();
		
		/**
		 * Returns the intercept value of the equation.
		 * @return	the intercept
		 */
		public double getIntercept();
		
		/**
		 * Returns <code>true</code> if this equation describes a perfectly vertical line.
		 * @return	<code>true</code> if this equation describes a perfectly vertical line
		 */
		public boolean isVertical();
		
		/**
		 * Returns the horizontal intercept value of the equation.
		 * @return	the horizontal intercept value of the equation
		 */
		public double getHorizontalIntercept();

		/**
		 * Sets the slope of the equation.
		 * @param slope	the slope
		 */
		public void setSlope(double slope);

		/**
		 * Sets the intercept of the equation.
		 * @param intercept	the intercept
		 */
		public void setIntercept(double intercept);

		/**
		 * Sets the horizontal intercept of the equation.
		 * @param horizontalIntercept	the horizontal intercept
		 */
		public void setHorizontalIntercept(double horizontalIntercept);
		
		/**
		 * Returns the <i>x</i>-value for the specified <i>y</i>-value.
		 * @param y	the <i>y</i>-value for which to find <i>x</i>
		 * @return	the <i>x</i>-value for the specified <i>y</i>-value
		 */
		public double getX(double y);
		
		/**
		 * Returns the <i>y</i>-value for the specified <i>x</i>-value.
		 * @param x	the <i>x</i>-value for which to find <i>y</i>
		 * @return	the <i>y</i>-value for the specified <i>x</i>-value
		 */
		public double getY(double x);
		
		/**
		 * Returns the point where the line described in this equation intersects the line described by the specified equation.
		 * @param eq	the equation describing the line to find the point of intersection for
		 * @return	the point where the line described in this equation intersects the line described by the specified equation
		 */
		public Coordinate getIntersection(LineEquation eq);
		
		/**
		 * Returns <code>true</code> if the line described by this equation ever intersects the line described by the specified equation.
		 * @param eq	the equation describing the line for which to check for intersection
		 * @return	<code>true</code> if the line described by this equation ever intersects the line described by the specified equation
		 */
		public boolean intersects(LineEquation eq);

		/**
		 * Returns <code>true</code> if this equation describes the same line as the specified equation.
		 * @param other	the equation to test
		 * @return	<code>true</code> if this equation describes the same line as the specified equation
		 */
		public boolean sameAs(LineEquation other);
		
		/**
		 * Returns </code>true</code> if this equation describes the same line as the specified equation, or if these lines intersect.
		 * Calling this method is the same as doing <code>sameAs(other) || intersects(other)</code>.
		 * @param other	the equation to test
		 * @return	</code>true</code> if this equation describes the same line as the specified equation, or if these lines intersect
		 */
		public boolean touches(LineEquation other);

}