/**
 *
 */
package se.iroiro.md.hangeul;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import se.iroiro.md.graph.Coordinate;

/**
 * Reads an image and tells what character it represents.
 * @author j
 *
 */
public class HangeulClassifier {

	/**
	 * The size of the jamo images (side in pixels) used for constructing the jamo database.
	 */

	private Hangeul hangeul = null;
	private List<Jamo> jamos = null;
	private CharacterMeasurement cm;
	private JamoReferenceDB jamoRefDB;

	public HangeulClassifier(JamoReferenceDB jamoRefDB){	// TODO empty constructor to be able to run below method without first loading one char
		if(jamoRefDB == null) jamoRefDB = new JamoReferenceDB();
		this.jamoRefDB = jamoRefDB;
	}

	public HangeulClassifier(){
		this(null);
	}

	public void newClassification(String imageFile){
		cm = new CharacterMeasurement(imageFile);
		go();
	}

	public void newClassification(BufferedImage image){
		cm = new CharacterMeasurement(image);
		go();
	}

	/**
	 * Returns the hangeul found by the classification process.
	 * @return	the character found by the classification process
	 */
	public Hangeul getHangeul(){
		return hangeul;
	}

	/**
	 * Returns the jamo found by the classification process.
	 * @return	the jamo found by the classification process
	 */
	public List<Jamo> getJamo(){
		return jamos;
	}

	/**
	 * Process start.
	 */
	private void go(){
		jamos = null;
		hangeul = null;
		if(cm == null || cm.getLineGroups() == null || cm.getLineGroups().size() == 0) return;

		matchHangeul();
	}


	/**
	 * Tries to match all line groups in input to jamo.
	 */
	private void matchHangeul(){
		List<LineGroup> inputGroups = cm.getLineGroups();	// get line groups
		Collections.sort(inputGroups, new ReverseLineGroupComparator());	// sort by line count descending

		Map<List<LineGroup>,Map<LineGroup,LineGroup>> structureMapCandidates = null;

		List<Jamo> jamos = new ArrayList<Jamo>();	// list to store matched jamo in

		Map<LineGroup, LineGroup> mapping;

		while(structureMapCandidates == null || structureMapCandidates.size() > 0){
			int lastStructureSize = -1;
			structureMapCandidates = new IdentityHashMap<List<LineGroup>,Map<LineGroup,LineGroup>>();
			for(List<LineGroup> structure : jamoRefDB.getStructureMapOrdering()){
				mapping = GraphTools.getBestStructureMapping(structure,inputGroups);
				if(mapping != null && ((lastStructureSize == -1) || (mapping.size() >= lastStructureSize))){
					structureMapCandidates.put(structure,mapping);
					lastStructureSize = mapping.size();
				}
			}

			double bestSimSum = Double.NaN;
			double sim;
			List<LineGroup> bestStructure = null;
			for(List<LineGroup> structure : structureMapCandidates.keySet()){
				sim = GraphTools.similarityIndexSum(structureMapCandidates.get(structure));
				if((sim != -1) && (Double.isNaN(bestSimSum) || bestSimSum > sim)){
					bestSimSum = sim;
					bestStructure = structure;
				}
			}

			if(bestStructure != null){
				Map<LineGroup,LineGroup> map = structureMapCandidates.get(bestStructure);
				Jamo j = new Jamo(jamoRefDB.getStructureMap().get(bestStructure));
				j.setMapping(map);
				jamos.add(j);
				for(LineGroup mapped : map.values()){
					inputGroups.remove(mapped);
				}
			}
			if(inputGroups.size() == 0) break;
		}

//		for(LineGroup lg : inputGroups){
//			for(XYEdge<Line,LineGroup> e : lg.getGraph().getEdges()){
//				Helper.p("[");
//				Helper.p(e.getFrom().getPiggybackObject()+""+e.getTailPort()+" -> "+e.getTo().getPiggybackObject()+""+e.getHeadPort());
//				Helper.p("]\n");
//			}
//		}
//		Helper.p("\n");

//		for(Jamo j : jamos){
//			Helper.p(j+""+j.getPosition()+" ");
//		}
//		Helper.p("\n");
//		Helper.p(jamos+" \t");
		hangeul = makeHangeul(jamos);	// try to combine the jamo to a hangeul
		this.jamos = jamos;
	}

	/**
	 * Combines the list of jamo to a hangeul.
	 * If there is more than one vowel jamo, it is replaced by a jamo consisting of both.
	 * If the jamo cannot be combined to a hangeul, <code>null</code> is returned.
	 * @param jamos	the jamos to combine
	 * @return	a hangeul object
	 */
	private Hangeul makeHangeul(List<Jamo> jamos) {
		if(jamos == null || jamos.size() < 2) return null;

//		Helper.p(jamos+"\n");
		Collections.sort(jamos, new JamoVerticalPositionComparator());
//		Helper.p(jamos+"\n");

		if(jamos.get(0).getType() == Jamo.jamoType.FINAL){		// fix this, do vowel check.
			jamos.set(0,swapPosition(jamos.get(0)));
		}
		if(jamos.get(1).getType() == Jamo.jamoType.FINAL){		// fix this, do vowel check.
			jamos.set(1,swapPosition(jamos.get(1)));
		}
		if(jamos.size() > 2 && jamos.get(2).getType() == Jamo.jamoType.INITIAL){
			jamos.set(2,swapPosition(jamos.get(2)));
		}

		char initial_jamo = 0;
		char medial_jamo = 0;
		char final_jamo = 0;

		for(Jamo j : jamos){
			switch(j.getType()){
			case INITIAL: if(initial_jamo == 0) initial_jamo = j.getChar(); break;
			case MEDIAL: if(medial_jamo == 0) medial_jamo = j.getChar(); break;
			case FINAL: if(final_jamo == 0) final_jamo = j.getChar(); break;
			}
		}

		if(initial_jamo > 0 && medial_jamo > 0){
			return new Hangeul(initial_jamo,medial_jamo,final_jamo);
		}else{
			return null;
		}
	}

	/**
	 * Comparator class for sorting a list of jamo by the vertical position of its identified structure.
	 * Dependent on current classification results.
	 * @author j
	 */
	private class JamoVerticalPositionComparator implements Comparator<Jamo>{
		public int compare(Jamo one, Jamo two){
			if(one != null && two != null){
				Coordinate oneP = one.getPosition();
				Coordinate twoP = two.getPosition();
				if(oneP != null && twoP != null){
					double vOne = one.getPosition().getY();
					double vTwo = two.getPosition().getY();
					if(vOne < vTwo) return 1;
					if(vOne > vTwo) return -1;
				}
			}
			return 0;
		}
	}

	/**
	 * Takes a final or initial jamo, and makes it the other type.
	 * @param j	the jamo to convert
	 * @return	a converted jamo
	 */
	private Jamo swapPosition(Jamo j){
		Map<Character,Character> map = new HashMap<Character,Character>();
		Map<Character,Character> map2 = new HashMap<Character,Character>();

		map.put('\u1100','\u11A8');	// ᄀ
		map.put('\u1101','\u11A9');	// ᄁ
		map.put('\u1102','\u11AB');	// ᄂ
		map.put('\u1103','\u11AE');	// ᄃ
		map.put('\u1105','\u11AF');	// ᄅ
		map.put('\u1106','\u11B7');	// ᄆ
		map.put('\u1107','\u11B8');	// ᄇ
		map.put('\u1109','\u11BA');	// ᄉ
		map.put('\u110A','\u11BB');	// ᄊ
		map.put('\u110B','\u11BC');	// ᄋ
		map.put('\u110C','\u11BD');	// ᄌ
		map.put('\u110E','\u11BE');	// ᄎ
		map.put('\u110F','\u11BF');	// ᄏ
		map.put('\u1110','\u11C0');	// ᄐ
		map.put('\u1111','\u11C1');	// ᄑ
		map.put('\u1112','\u11C2');	// ᄒ

		for(char c : map.keySet()){
			map2.put(map.get(c), c);	// reversed
		}
		map.putAll(map2);

		Character jamo = map.get(j.getChar());
		if(jamo != null){
			return new Jamo(jamo);
		}else{
			return j;
		}
	}

	/**
	 * Returns the character measurement object.
	 * @return	the character measurement object
	 */
	public CharacterMeasurement getCharacterMeasurement(){
		return cm;
	}

	/**
	 * Comparator class for sorting a list of line groups by number of lines contained,
	 * in descending order.
	 * @author j
	 */
	private class ReverseLineGroupComparator implements Comparator<LineGroup>{
		public int compare(LineGroup one, LineGroup two){
			if(one != null && two != null){
				if(one.getMap().size() < two.getMap().size()) return 1;
				if(one.getMap().size() > two.getMap().size()) return -1;
			}
			return 0;
		}
	}

	/**
	 * Returns the jamo reference database object used by this classifier.
	 * @return the jamo reference database object
	 */
	public JamoReferenceDB getJamoRefDB() {
		return jamoRefDB;
	}

}
