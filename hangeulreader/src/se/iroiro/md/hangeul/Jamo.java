/**
 *
 */
package se.iroiro.md.hangeul;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.iroiro.md.graph.Coordinate;
import se.iroiro.md.graph.simple.SimpleCoordinate;

/**
 * Represents one hangeul jamo.
 * @author j
 *
 */
public class Jamo {

	private char jamo;
//	private List<LineGroup> lineGroups = null;
	private List<List<LineGroup>> structures = null;
	private Map<LineGroup,LineGroup> map = null;

	public enum jamoType {
		INITIAL,
		MEDIAL,
		FINAL,
		UNKNOWN
	}

	/**
	 * Creates a jamo representing the specified character.
	 * @param jamo	the character to represent
	 */
	public Jamo(char jamo){
		this.jamo = jamo;
	}

	/**
	 * Creates a jamo representing the specified character, with the specified list of possible structures.
	 * @param jamo	the character to represent
	 * @param structures	the structures representing this character
	 */
	public Jamo(char jamo, List<List<LineGroup>> structures){
		this(jamo);
		this.structures = structures;
	}

	/**
	 * Creates a jamo, copying the character, structure list and mapping from the specified jamo.
	 * Structure list and mapping are copied by reference only.
	 * @param jamo	jamo to copy from
	 */
	public Jamo(Jamo jamo) {
		this(jamo.getChar());
		this.structures = jamo.structures;
		this.map = jamo.map;
	}

	/**
	 * Returns the possible structures for this jamo.
	 * @return	the line groups
	 */
	public List<List<LineGroup>> getStructures(){
		if(structures == null) structures = new ArrayList<List<LineGroup>>();
		return structures;
	}

	/**
	 * Adds a possible structure to the list of structures.
	 * @param structure	the structure to add
	 */
	public void addStructure(List<LineGroup> structure){
		getStructures().add(structure);
	}

	/**
	 * Returns the jamo type of this jamo.
	 * @return	the jamo type
	 */
	public jamoType getType(){
		if(jamo >= '\u1100' && jamo <= '\u1112'){
			return jamoType.INITIAL;
		}else if(jamo >= '\u1161' && jamo <= '\u1175'){
			return jamoType.MEDIAL;
		}else if(jamo >= '\u11A8' && jamo <= '\u11C2'){
			return jamoType.FINAL;
		}
		return jamoType.UNKNOWN;
	}

	/**
	 * Returns the name of the jamo.
	 * @return the name of the character
	 */
	public String getName() {
		final int INITIAL_BASE = '\u1100';
		final String[] INITIAL = {
			"G", "GG", "N", "D", "DD", "R", "M", "B", "BB",
			"S", "SS", "EMPTY", "J", "JJ", "C", "K", "T", "P", "H"
		};

		final int MEDIAL_BASE = '\u1161';
		final String[] MEDIAL = {
			"A", "AE", "YA", "YAE", "EO", "E", "YEO", "YE", "O",
			"WA", "WAE", "OE", "YO", "U", "WEO", "WE", "WI",
			"YU", "EU", "YI", "I"
		};

		final int FINAL_BASE = '\u11A8';
		final String[] FINAL = {
			"G", "GG", "GS", "N", "NJ", "NH", "D", "L", "LG", "LM",
			"LB", "LS", "LT", "LP", "LH", "M", "B", "BS",
			"S", "SS", "NG", "J", "C", "K", "T", "P", "H"
		};

		switch(getType()) {
		case INITIAL:
			return INITIAL[jamo - INITIAL_BASE];
		case MEDIAL:
			return MEDIAL[jamo - MEDIAL_BASE];
		case FINAL:
			return FINAL[jamo - FINAL_BASE];
		default:
			throw new IllegalArgumentException("Not a Jamo: " + jamo);
		}
	}

	/**
	 * Returns the jamo character.
	 * @return	the character
	 */
	public char getChar(){
		return jamo;
	}

	/**
	 * Sets the jamo character.
	 */
	public void setChar(char jamo){
		this.jamo = jamo;
	}

	/**
	 * Returns the character as a string.
	 * @return	the character as a string
	 */
	public String toString(){
		return String.valueOf(jamo);
	}

	/**
	 * Sets a map object, mapping lines in the jamo structure to real lines.
	 * @param map	the map to set
	 */
	public void setMapping(Map<LineGroup, LineGroup> map) {
		this.map = map;
	}

	/**
	 * Returns the mapping from structures to real lines for this jamo.
	 * @return	the mapping from structures to real lines
	 */
	public Map<LineGroup,LineGroup> getMapping(){
		return map;
	}

	/**
	 * Returns the real position of this jamo.
	 * This is dependent on the mapping object, so this must be set.
	 * If there is no mapping, or mapping is empty, this method will return <code>null</code>.
	 * @return	the real position of this jamo
	 */
	public Coordinate getPosition(){
		if(map != null){
			LineGroup first = map.values().iterator().next();
			Coordinate tl = new SimpleCoordinate(first.getTopLeft());
			Coordinate br = new SimpleCoordinate(first.getBottomRight());
			Coordinate ctl, cbr;
			for(LineGroup lg : map.values()){
				ctl = lg.getTopLeft();
				cbr = lg.getBottomRight();
				if(ctl.getX() < tl.getX()) tl.setX(ctl.getX());
				if(ctl.getY() > tl.getY()) tl.setY(ctl.getY());
				if(cbr.getX() > br.getX()) br.setX(cbr.getX());
				if(cbr.getY() < br.getY()) br.setY(cbr.getY());
			}
			return new SimpleCoordinate((tl.getX()+br.getX())/2,(tl.getY()+br.getY())/2);
		}
		return null;
	}

}
