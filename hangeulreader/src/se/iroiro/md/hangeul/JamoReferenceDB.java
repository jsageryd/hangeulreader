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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import se.iroiro.md.hangeulreader.Helper;

/**
 * Creates and contains a jamo reference database used by a classifier.
 * @author j
 *
 */
public class JamoReferenceDB {

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
	 * Generates a jamo reference database.
	 */
	public JamoReferenceDB() {
		generateJamoDB();
	}

	/**
	 * Returns a list of the structure map keys in correct order.
	 * @return	a list of the structure map keys in order
	 */
	public List<List<LineGroup>> getStructureMapOrdering() {
		return structureMapOrdering;
	}
	
	/**
	 * Returns a sorted list of the line groups contained in the structure map.
	 * @return	a sorted list of the line groups found in the structure map
	 */
	public List<LineGroup> getSortedStructureLineGroups() {
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
	 * Returns the structure map.
	 * @return	the structure map
	 */
	public Map<List<LineGroup>,Jamo> getStructureMap() {
		return structureMap;
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
	public static List<Font> getFonts() {
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
	
}
