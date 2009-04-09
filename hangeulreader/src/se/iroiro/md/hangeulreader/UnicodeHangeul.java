/**
 *
 */
package se.iroiro.md.hangeulreader;

/**
 * @author j
 *
 */
public class UnicodeHangeul {

	static final int
	SBase = 0xAC00, LBase = 0x1100, VBase = 0x1161, TBase = 0x11A7,
	LCount = 19, VCount = 21, TCount = 28,
	NCount = VCount * TCount,   // 588
	SCount = LCount * NCount;   // 11172

	public static String decomposeHangul(char s) {
		int SIndex = s - SBase;
		if (SIndex < 0 || SIndex >= SCount) {
			return String.valueOf(s);
		}
		StringBuffer result = new StringBuffer();
		int L = LBase + SIndex / NCount;
		int V = VBase + (SIndex % NCount) / TCount;
		int T = TBase + SIndex % TCount;
		result.append((char)L);
		result.append((char)V);
		if (T != TBase) result.append((char)T);
		return result.toString();
	}

	public static String composeHangul(String source) {
		int len = source.length();
		if (len == 0) return "";
		StringBuffer result = new StringBuffer();
		char last = source.charAt(0);            // copy first char
		result.append(last);

		for (int i = 1; i < len; ++i) {
			char ch = source.charAt(i);

			// 1. check to see if two current characters are L and V

			int LIndex = last - LBase;
			if (0 <= LIndex && LIndex < LCount) {
				int VIndex = ch - VBase;
				if (0 <= VIndex && VIndex < VCount) {

					// make syllable of form LV

					last = (char)(SBase + (LIndex * VCount + VIndex) * TCount);

					result.setCharAt(result.length()-1, last); // reset last
					continue; // discard ch
				}
			}


			// 2. check to see if two current characters are LV and T

			int SIndex = last - SBase;
			if (0 <= SIndex && SIndex < SCount && (SIndex % TCount) == 0) {
				int TIndex = ch - TBase;
				if (0 < TIndex && TIndex < TCount) {

					// make syllable of form LVT

					last += TIndex;
					result.setCharAt(result.length()-1, last); // reset last
					continue; // discard ch
				}
			}
			// if neither case was true, just add the character
			last = ch;
			result.append(ch);
		}
		return result.toString();
	}

	public static String getHangulName(char s) {
		int SIndex = s - SBase;
		if (0 > SIndex || SIndex >= SCount) {
			throw new IllegalArgumentException("Not a Hangul Syllable: " + s);
		}
		int LIndex = SIndex / NCount;
		int VIndex = (SIndex % NCount) / TCount;
		int TIndex = SIndex % TCount;
		return JAMO_L_TABLE[LIndex]
		                                         + JAMO_V_TABLE[VIndex] + JAMO_T_TABLE[TIndex];
	}

	static private String[] JAMO_L_TABLE = {
		"G", "GG", "N", "D", "DD", "R", "M", "B", "BB",
		"S", "SS", "", "J", "JJ", "C", "K", "T", "P", "H"
	};

	static private String[] JAMO_V_TABLE = {
		"A", "AE", "YA", "YAE", "EO", "E", "YEO", "YE", "O",
		"WA", "WAE", "OE", "YO", "U", "WEO", "WE", "WI",
		"YU", "EU", "YI", "I"
	};

	static private String[] JAMO_T_TABLE = {
		"", "G", "GG", "GS", "N", "NJ", "NH", "D", "L", "LG", "LM",
		"LB", "LS", "LT", "LP", "LH", "M", "B", "BS",
		"S", "SS", "NG", "J", "C", "K", "T", "P", "H"
	};

}
