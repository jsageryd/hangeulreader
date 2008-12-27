/**
 * 
 */
package se.iroiro.md.hangeul;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import se.iroiro.md.graph.Coordinate;
import se.iroiro.md.graph.XYNode;
import se.iroiro.md.hangeulreader.Helper;

/**
 * Reads an image and tells what character it represents.
 * @author j
 *
 */
public class HangeulClassifier {

	/**
	 * The size of the jamo images (side in pixels) used for constructing the jamo database.
	 */
//	private static final int JAMO_SIZE = 200;	// generate structures from different sizes
	
	private Hangeul hangeul = null;
	private CharacterMeasurement cm;
	private List<Jamo> jamoDB = null;
	
	/**
	 * A map for quick jamo lookup based on structure.
	 * A jamo object contains a list of structures,
	 * this structure map maps all structures of all jamo,
	 * to each jamo object.
	 */
	private Map<List<LineGroup>,Jamo> structureMap = null;
	
	/**
	 * A list to accompany the structureMap, to get correct order.
	 */
	private List<List<LineGroup>> structureMapOrdering = null;

	/**
	 * A list to accompany the structureMap, ordering all line groups by their line count.
	 */
	private List<LineGroup> structureLineGroups = null;

	/**
	 * Creates a new classification from the specified image file.
	 * @param fileName	the image file to read
	 */
	public HangeulClassifier(String fileName) {
		System.out.println("Analyzing input image...");
		cm = new CharacterMeasurement(fileName);
		go();
	}

	/**
	 * Creates a new classification from the specified image.
	 * @param image	the image to read
	 */
	public HangeulClassifier(BufferedImage image) {
//		System.out.println("Analyzing input image...");
		cm = new CharacterMeasurement(image);
		go();
	}
	
	public HangeulClassifier(){	// TODO empty constructor to be able to run below method without first loading one char
		generateJamoDB();
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
	 * Process start.
	 */
	private void go(){
		if(cm == null || cm.getLineGroups() == null || cm.getLineGroups().size() == 0) return;
		generateJamoDB();
		
		
//		splitInputGroups();
		matchHangeul();

	}
	
	/**
	 * Scans the input line groups.
	 * If there is a group that cannot be mapped to a structure, it is split into two.
	 * Process is repeated until all groups can be matched, or there can be no more splits.
	 */
	private void splitInputGroups(){
		if(cm.getLineGroups() == null || cm.getLineGroups().size() == 0) return;
		List<Map<XYNode<Line, LineGroup>, XYNode<Line, LineGroup>>> map;
		Stack<LineGroup> stack = new Stack<LineGroup>();
		stack.addAll(cm.getLineGroups());
		nextLineGroup:
			while(stack.size() > 0){	// while there are still line groups to check
				LineGroup inputLG = stack.pop();
				for(List<LineGroup> structure : structureMapOrdering){
					for(LineGroup structLG : structure){
						map = GraphTools.getNodeMappings(inputLG,structLG);
						if(map != null && map.size() > 0){
							continue nextLineGroup;
						}
					}
				}
				List<LineGroup> parts = splitGroup(inputLG,cm.getLineGroups());
				if(parts != null && parts.size() > 1){	// if split is not possible, give up and don't re-add the line group to stack
					stack.addAll(parts);
				}
			}
	}

	/**
	 * Returns a sorted list of the line groups contained in the structure map.
	 * @return	a sorted list of the line groups found in the structure map
	 */
	private List<LineGroup> getSortedStructureLineGroups() {
		if(structureLineGroups == null){
			structureLineGroups = new ArrayList<LineGroup>();
			for(List<LineGroup> structure : structureMapOrdering){
				for(LineGroup structLG : structure){
					structureLineGroups.add(structLG);
				}
			}
			Collections.sort(structureLineGroups, new ReverseLineGroupComparator());
		}
		return structureLineGroups;
	}
	
	/**
	 * Splits the specified line group in the specified line group list into two parts.
	 * The graph in the specified line group will be left intact,
	 * however the specified line group list will be updated to reflect the changes
	 * and so will not contain the specified line group upon successful split
	 * as it will be replaced by the two parts.
	 * Returns a list of two line groups if split is successful.
	 * If a split cannot be made, the original line group is returned as the only item in the list.
	 * @param sticky	the line group to split
	 * @param lineGroups	the list of line groups that the line belongs to
	 * @return	a list of two line groups if split is successful, otherwise specified line group as the only item in the list
	 */
	private List<LineGroup> splitGroup(LineGroup sticky,
			List<LineGroup> lineGroups) {
		
		List<LineGroup> parts;
		Map<XYNode<Line, LineGroup>, XYNode<Line, LineGroup>> map;
		
		lineGroups.remove(sticky);	// remove group to be split

		for(LineGroup structLG : getSortedStructureLineGroups()){	// break out the first possible structure
			map = GraphTools.getBestNodeMapping(structLG,sticky);
			if(map != null){

				// split, if can break out, add the two parts and return them.
				// if not can split, do nothing, let loop go on.
				parts = GraphTools.disconnect(structLG,map,sticky);
				if(parts != null){
					for(LineGroup part : parts){
						lineGroups.add(part);
					}
					return parts;
				}
			}
		}
		
		return null;
	}

	/**
	 * Tries to match all line groups in input to jamo.
	 */
	@SuppressWarnings("unchecked")
	private void matchHangeul(){
		List<LineGroup> inputGroups = cm.getLineGroups();	// get line groups
		Collections.sort(inputGroups, new ReverseLineGroupComparator());	// sort by line count descending
		
		Map<List<LineGroup>,Map<LineGroup,LineGroup>> structureMapCandidates = null;
		
		List<Jamo> jamos = new ArrayList<Jamo>();	// list to store matched jamo in
		
		Map<LineGroup, LineGroup> mapping;
		
		while(structureMapCandidates == null || structureMapCandidates.size() > 0){
			int lastStructureSize = -1;
			structureMapCandidates = new IdentityHashMap<List<LineGroup>,Map<LineGroup,LineGroup>>();
			for(List<LineGroup> structure : structureMapOrdering){
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
				Jamo j = new Jamo(structureMap.get(bestStructure));
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
		
		for(Jamo j : jamos){
//			Helper.p(j+""+j.getPosition()+" ");
		}
//		Helper.p("\n");
//		Helper.p(jamos+" \t");
		hangeul = makeHangeul(jamos);	// try to combine the jamo to a hangeul
		
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
	
	private Map<List<LineGroup>, Jamo> makeStructureMap(List<Jamo> jamoDB) {
		Map<List<LineGroup>, Jamo> map = new IdentityHashMap<List<LineGroup>, Jamo>();
		for(Jamo j : jamoDB){
			for(List<LineGroup> s : j.getStructures()){
				map.put(s,j);
			}
		}
		return map;
	}
	
	/**
	 * Comparator class for sorting a list of line structures by primarily the line group count,
	 * secondarily the total number of lines contained, in descending order.
	 * @author j
	 */
	private class StructureComparator implements Comparator<List<LineGroup>>{
		public int compare(List<LineGroup> one, List<LineGroup> two){
			if(one == null || two == null) return 0;
			int c1 = 0;
			int c2 = 0;
			for(LineGroup lg : one){
				c1 += lg.getMap().size();
			}
			for(LineGroup lg : two){
				c2 += lg.getMap().size();
			}
			int c = two.size() - one.size();	// primarily compare line group count
			if(c == 0){
				c = c2 - c1;	// secondarily compare total line count
			}
			return c;
		}
	}

	/**
	 * Initialises the jamo database with information scanned from images of jamo (generated on-the-fly.)
	 * @return	a list of all possible jamo
	 */
	private List<Jamo> scanAllJamo(){
		System.out.println("Building jamo database...");
		List<Jamo> jamoDB = new ArrayList<Jamo>();
		
		StringBuilder jamos = new StringBuilder();

		List<Font> fonts = getFonts();
		
		/* Initial jamo */
		for(char c = '\u1100'; c <= '\u1112'; c++){
			jamos.append(c);
		}
		/* Medial jamo */
		for(char c = '\u1161'; c <= '\u1175'; c++){
			jamos.append(c);
		}
		/* Final jamo */
		for(char c = '\u11A8'; c <= '\u11C2'; c++){
			jamos.append(c);
		}
		
		char c;
		Jamo j;
		for(int i = 0; i < jamos.length(); i++){
//			if(true) break;	// debug tmp.
			c = jamos.charAt(i);
			j = new Jamo(c);
			for(Font font : fonts){
				j.addStructure(getCharacterLineGroups(c,font,50));
				j.addStructure(getCharacterLineGroups(c,font,100));
				j.addStructure(getCharacterLineGroups(c,font,150));
				j.addStructure(getCharacterLineGroups(c,font,200));
//				j.addStructure(getCharacterLineGroups(c,font,300));
				
				/* Special cases, auto image generation */
//				if(c == 'ᄀ') j.addStructure(getCharacterLineGroups('コ',font,50));	// ᄀ sometimes looks similar to コ
				if(c == '\u1100') j.addStructure(getCharacterLineGroups('\u30B3',font,50));	// ᄀ sometimes looks similar to コ
				/* ************* */
			}

			/* Special cases, manually drawn image files */
			final String SPECIALS_DIRECTORY = System.getProperty("user.dir")+"/specials/";
			String imageFile;
			for(int n = 0; n < 10; n++){
				if(n == 0){
					imageFile = SPECIALS_DIRECTORY+j.getName().toLowerCase()+".png";
				}else{
					imageFile = SPECIALS_DIRECTORY+j.getName().toLowerCase()+"_"+n+".png";
				}
				if(new File(imageFile).exists()){
					j.addStructure(new CharacterMeasurement(imageFile).getLineGroups());
				}
			}
			/* ************* */
			if(j.getStructures().size() > 0) jamoDB.add(j);
		}

		return jamoDB;
	}
	
	
	/**
	 * Returns a list of all fonts that are available.
	 * @return	a list of all available fonts
	 */
	private List<Font> getFonts() {
		List<Font> result = new ArrayList<Font>();
		
		String fontDir = System.getProperty("user.dir")+"/data/fonts/";
		
		//TODO make this more independent
		try {
			result.add(Font.createFont(Font.TRUETYPE_FONT, new File(fontDir+"AppleGothic.ttf")));
//			result.add(Font.createFont(Font.TRUETYPE_FONT, new File(fontDir+"AppleMyungjo.ttf")));
//			result.add(Font.createFont(Font.TRUETYPE_FONT, new File(fontDir+"#Gungseouche.dfont")));	// Useless. Individual jamo not available.
//			result.add(Font.createFont(Font.TRUETYPE_FONT, new File(fontDir+"#HeadlineA.dfont")));		// "
//			result.add(Font.createFont(Font.TRUETYPE_FONT, new File(fontDir+"#PCmyoungjo.dfont")));		// "
//			result.add(Font.createFont(Font.TRUETYPE_FONT, new File(fontDir+"#Pilgiche.dfont")));		// "
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

//	/**
//	 * TODO deprecate?
//	 * Creates an image of the specified character, scans it and returns the list of line groups found.
//	 * @param c	the character to scan
//	 * @return	the list of line groups found in the specified character
//	 */
//	private List<LineGroup> getCharacterLineGroups(char c, String fontName, int size){
//		BufferedImage img = CharacterRenderer.makeCharacterImage(c, size, size, fontName);
//		CharacterMeasurement cm = new CharacterMeasurement(img);
//		return cm.getLineGroups();
//	}

	/**
	 * TODO fix javadoc
	 * Creates an image of the specified character, scans it and returns the list of line groups found.
	 * @param c	the character to scan
	 * @return	the list of line groups found in the specified character
	 */
	private List<LineGroup> getCharacterLineGroups(char c, Font font, int size){
		BufferedImage img = CharacterRenderer.makeCharacterImage(c, size, size, font);
		CharacterMeasurement cm = new CharacterMeasurement(img);
		return cm.getLineGroups();
	}
	
	/**
	 * Returns the character measurement object.
	 * @return	the character measurement object
	 */
	public CharacterMeasurement getCharacterMeasurement(){
		return cm;
	}

	/**
	 * Generates the jamo database if it is not already created.
	 */
	public void generateJamoDB() {
		if(jamoDB == null || structureMap == null){
			jamoDB = scanAllJamo();	// if there are no reference jamo, make them.
			structureMap = makeStructureMap(jamoDB);	// make structure map
			structureMapOrdering = new ArrayList<List<LineGroup>>();	// structureMap is just a map of all structures found in all jamo,
			structureMapOrdering.addAll(structureMap.keySet());			// with the jamo as value for each structure
			Collections.sort(structureMapOrdering, new StructureComparator());	// structureMapOrdering is a sorted list of the structures
//			for(List<LineGroup> s : structureMapOrdering){
//				Helper.p(structureMap.get(s)+"\t"+s+"\n");
//			}
			for(Jamo j : jamoDB){
				Helper.p(j.getChar()+"\t");
				Helper.p(j.getStructures()+"\n");
			}
		}
	}
	
}
