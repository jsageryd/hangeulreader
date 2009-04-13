/**
 *
 */
package se.iroiro.md.hangeulreader;

/**
 * Starter class.
 * @author j
 */
public class Go {

	public static final String VERSION="a71bba6+";

	/**
	 * Main method.
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Version "+VERSION);
		new GUI2().show();
	}

}