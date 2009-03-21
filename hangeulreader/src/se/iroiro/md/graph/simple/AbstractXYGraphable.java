/**
 *
 */
package se.iroiro.md.graph.simple;

import se.iroiro.md.graph.XYGraphable;

/**
 * @author j
 *
 * @param <T>	the type of the piggyback object
 */
public abstract class AbstractXYGraphable<T> extends AbstractXYPositioned implements XYGraphable<T> {

	/**
	 * Identifying ID
	 */
	private int id = -1;

	/**
	 * Flagged-property
	 */
	private boolean flagged = false;

	/**
	 * Piggyback object
	 */
	private T pbo = null;

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Graphable#getID()
	 */
	public int getID() {
		return id;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Graphable#isFlagged()
	 */
	public boolean isFlagged() {
		return flagged;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Graphable#setFlagged(boolean)
	 */
	public void setFlagged(boolean flagged) {
		this.flagged = flagged;
	}

	/* (non-Javadoc)
	 * @see se.iroiro.md.graph.Graphable#setID(int)
	 */
	public void setID(int id) {
		this.id = id;
	}

	public T getPiggybackObject() {
		return pbo;
	}

	public void setPiggybackObject(T pbo) {
		this.pbo = pbo;
	}

}
