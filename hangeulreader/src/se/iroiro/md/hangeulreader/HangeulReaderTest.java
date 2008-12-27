/**
 * 
 */
package se.iroiro.md.hangeulreader;

import java.text.DecimalFormat;

import se.iroiro.md.hangeul.CharacterRenderer;
import se.iroiro.md.hangeul.Hangeul;
import se.iroiro.md.hangeul.HangeulClassifier;

/**
 * This class tests the classifier and displays results.
 * @author j
 *
 */
public class HangeulReaderTest {
	
	private static final int CHARSIZE = 200;
	
//	public void run(){
//		go(from,to);
//	}
	
	public HangeulReaderTest(char from, char to){
		go(makeString(from,to));
	}
	
	public HangeulReaderTest(String characters){
		go(characters);
	}
	
	private String makeString(char from, char to){
		StringBuilder s = new StringBuilder();
		for(char c = from; c <= to; c++){
			s.append(c);
		}
		return s.toString();
	}
	
	private void go(String characters){
		System.out.println("Preparing to scan " + (int) (characters.length()) + " characters.");
		HangeulClassifier hc = new HangeulClassifier();
		StringBuilder matches = new StringBuilder();
		StringBuilder misses = new StringBuilder();
		int count = 0;
		boolean isMatch;
		System.out.println("Reading images...");
		for(int nn = 0; nn < characters.length(); nn++){
//			CharacterRenderer.makeCharacterImage(c, CHARSIZE, CHARSIZE);
			char c = characters.charAt(nn);
			hc.newClassification(CharacterRenderer.makeCharacterImage(c, CHARSIZE, CHARSIZE));
			Hangeul h = hc.getHangeul();
			if(h != null && h.toString().charAt(0) == c){
				isMatch = true;
				matches.append(c);
			}else{
				isMatch = false;
//				if(h != null){
//					misses.append(String.valueOf(c)+"-"+h.toString()+" ");
//				}else{
//					misses.append(String.valueOf(c)+"-? ");
//				}
				misses.append(c);
			}
			if(count >= 100){
				count = 0;
				System.out.println();
			}
			count++;
			if(isMatch) System.out.print("o");
			if(!isMatch) System.out.print("-");
		}
		StringBuilder result = new StringBuilder();
		System.out.println();
		System.out.println();
		DecimalFormat df = new DecimalFormat("0.##");
		int matchesc = matches.length();
		result.append(matchesc + " correct out of " + characters.length() + " characters (" + df.format(((matchesc*100)/(characters.length())))+" %).\n\n");
		result.append("Correctly matched characters:\n");
		int counter = 0;
		for(int i = 0; i < matches.length(); i++){
			char c = matches.charAt(i);
			result.append(c);
//			Helper.p(c);
			if(++counter >= 80){
				counter = 0;
//				result.append("\n");
//				System.out.println();
			}
		}
		result.append("\n");
//		System.out.println();
		if(misses.length() > 0){
			result.append("\nMissed characters:\n");
//			System.out.println();
//			System.out.println("Missed characters:");
			counter = 0;
			for(int i = 0; i < misses.length(); i++){
				char c = misses.charAt(i);
//				Helper.p(c);
				result.append(c);
				if(++counter >= 80){
					counter = 0;
//					System.out.println();
//					result.append("\n");
				}
			}
//			System.out.println();
		}
		Helper.dump(result.toString(), "/Users/j/Desktop/result.txt");
		Helper.p(result);
		System.out.println();
		System.out.println();
	}
	
//	private void writeImage(BufferedImage img, String fileName){
//		try{
//			ImageIO.write(img, "png", new FileOutputStream(fileName));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
}
