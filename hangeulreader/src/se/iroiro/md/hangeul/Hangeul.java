/**
 *
 */
package se.iroiro.md.hangeul;

import java.io.Serializable;

import se.iroiro.md.hangeulreader.UnicodeHangeul;


/**
 * Represents one hangeul character.
 * @author j
 *
 */
public class Hangeul implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2555690499817279461L;

	/**
	 * Constant for Unicode lookup
	 */
	private static final int
	SBase = 0xAC00,
//	LBase = 0x1100, VBase = 0x1161, TBase = 0x11A7,
	LCount = 19, VCount = 21, TCount = 28,
	NCount = VCount * TCount,   // 588
	SCount = LCount * NCount;   // 11172

	private char hangeul;
//	private char initial_jamo;
//	private char medial_jamo;
//	private char final_jamo;

	/**
	 * Class constructor. Creates an instance representing the character specified.
	 * @param hangeul	the character to represent
	 */
	public Hangeul(char hangeul){
		this.hangeul = hangeul;
	}

	/**
	 * Class constructor. Creates an instance representing the character specified by the three jamo.
	 * @param initial_jamo	the initial jamo
	 * @param medial_jamo	the medial jamo
	 * @param final_jamo	the final jamo (this may be empty)
	 */
	public Hangeul(char initial_jamo, char medial_jamo, char final_jamo){
		if(final_jamo > 0){
			char[] cs = {initial_jamo,medial_jamo,final_jamo};
			hangeul = UnicodeHangeul.composeHangul(String.valueOf(cs)).charAt(0);
		}else{
			char[] cs = {initial_jamo,medial_jamo};
			hangeul = UnicodeHangeul.composeHangul(String.valueOf(cs)).charAt(0);
		}
	}

	/**
	 * Returns the hangeul character as a string.
	 * @return	the hangeul character as a string
	 */
	public String toString(){
		return String.valueOf(hangeul);
	}

	/**
	 * Returns the name of the character.
	 * This method is a slightly modified copy of the implementation found at:
	 * <a href="http://www.unicode.org/reports/tr15/tr15-29.html#Hangul">http://www.unicode.org/reports/tr15/tr15-29.html#Hangul</a>
	 * @return the name of the character
	 */
	public String getName() {
		String[] JAMO_L_TABLE = {
			"G", "GG", "N", "D", "DD", "R", "M", "B", "BB",
			"S", "SS", "", "J", "JJ", "C", "K", "T", "P", "H"
		};

		String[] JAMO_V_TABLE = {
			"A", "AE", "YA", "YAE", "EO", "E", "YEO", "YE", "O",
			"WA", "WAE", "OE", "YO", "U", "WEO", "WE", "WI",
			"YU", "EU", "YI", "I"
		};

		String[] JAMO_T_TABLE = {
			"", "G", "GG", "GS", "N", "NJ", "NH", "D", "L", "LG", "LM",
			"LB", "LS", "LT", "LP", "LH", "M", "B", "BS",
			"S", "SS", "NG", "J", "C", "K", "T", "P", "H"
		};

		int SIndex = hangeul - SBase;
		if (0 > SIndex || SIndex >= SCount) {
			throw new IllegalArgumentException("Not a Hangul Syllable: " + hangeul);
		}
		int LIndex = SIndex / NCount;
		int VIndex = (SIndex % NCount) / TCount;
		int TIndex = SIndex % TCount;
		return JAMO_L_TABLE[LIndex] + JAMO_V_TABLE[VIndex] + JAMO_T_TABLE[TIndex];
	}

}
