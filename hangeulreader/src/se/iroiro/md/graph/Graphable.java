/**
 *
 */
package se.iroiro.md.graph;

/**
 * Represents an entity in a graph. Every entity has an associated ID and a boolean flag.
 * @author j
 * @param <T>	the type of the piggyback object
 */
public interface Graphable<T> extends Cloneable {

	/**
	 * Returns the piggyback object. If no piggyback object has been set, this method returns null.
	 * @return	the piggyback object
	 */
	public T getPiggybackObject();

	/**
	 * Sets the piggyback object.
	 * @param pbo	the object to piggyback
	 */
	public void setPiggybackObject(T pbo);

	/**
	 * Returns an identifier for the entity. If no ID has been set, -1 is returned.
	 * @return	an identifying integer
	 */
	public int getID();

	/**
	 * Sets an identifier for the entity.
	 * @param id	an identifying integer
	 */
	public void setID(int id);

	/**
	 * Determines if the entity has been flagged. This can be used for user-defined purposes
	 * and is never touched by the internal mechanisms of the graph structure.
	 * @return	<code>true</code> if flagged, otherwise <code>false</code>
	 */
	public boolean isFlagged();

	/**
	 * Sets the flag status for the entity.
	 * @param flagged	<code>true</code> if flagged, otherwise <code>false</code>
	 * @see Graphable#isFlagged()
	 */
	public void setFlagged(boolean flagged);

	/**
	 * Returns a string representation of the entity.
	 * @return	a string representation
	 */
	public String toString();

//	/**
//	 * Returns a deep-copy of this entity and all of its child entities. Piggyback object is not cloned.
//	 * @return	a deep-copy of this entity and all of its child entities.
//	 */
//	public Object clone();

}
