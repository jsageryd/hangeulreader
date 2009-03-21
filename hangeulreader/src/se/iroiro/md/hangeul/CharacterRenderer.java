/**
 *
 */
package se.iroiro.md.hangeul;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * TODO this class needs tidying
 * Generates image files from character input.
 * @author j
 *
 */
public class CharacterRenderer {

	/**
	 * Returns an image of the specified character.
	 * @param c	the character to draw
	 * @param width	the width of the image
	 * @param height	the height of the image
	 * @return	an image of the specified character
	 */
	public static BufferedImage makeCharacterImage(char c, int width, int height){
		return makeCharacterImage(c, width, height, "Verdana");
	}

	/**
	 * TODO deprecate?
	 * Returns an image of the specified character in the specified font.
	 * @param c	the character to draw
	 * @param width	the width of the image
	 * @param height	the height of the image
	 * @param fontName	the name of the font to use
	 * @return	an image of the specified character
	 */
	public static BufferedImage makeCharacterImage(char c, int width, int height, String fontName){
		BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) img.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(new Color(255,255,255));
		g2d.fillRect(0, 0, width, height);
		drawCharacter(g2d, c, width / 2, height / 2, width < height ? width : height, fontName);
		return img;
	}

	/**
	 * TODO deprecate?
	 * Returns an image of the specified character in the specified font.
	 * @param c	the character to draw
	 * @param width	the width of the image
	 * @param height	the height of the image
	 * @param font	the font to use
	 * @return	an image of the specified character
	 */
	public static BufferedImage makeCharacterImage(char c, int width, int height, Font font){
		BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) img.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(new Color(255,255,255));
		g2d.fillRect(0, 0, width, height);
		drawCharacter(g2d, c, width / 2, height / 2, width < height ? width : height, font);
		return img;
	}

	/**
	 * TODO fix javadoc
	 * Draws the specified character.
	 * @param g2d	the <code>Graphics2D</code> on which to draw
	 * @param c	the character to draw
	 * @param x	the centre y-coordinate for the character
	 * @param y	the centre x-coordinate for the character
	 * @param maxSide	the maximum side of the character - the character will fit into a square with the side length of <code>maxSide</code>
	 * @param fontName	the name of the font to use
	 */
	private static void drawCharacter(Graphics2D g2d, char c, int x, int y, int maxSide, Font font){
		int fontSize = getFontSize(g2d, font, maxSide);
		Font f = font.deriveFont((float) fontSize);
		g2d.setColor(new Color(0,0,0));
		g2d.setFont(f);
		FontMetrics fm = g2d.getFontMetrics();
		int ox = (fm.charWidth(c) / 2);
		int oy = ((fm.getAscent()-fm.getDescent()) / 2);
		g2d.drawString(Character.toString(c), x-ox, y+oy);
	}

	/**
	 * TODO deprecate?
	 * Draws the specified character.
	 * @param g2d	the <code>Graphics2D</code> on which to draw
	 * @param c	the character to draw
	 * @param x	the centre y-coordinate for the character
	 * @param y	the centre x-coordinate for the character
	 * @param maxSide	the maximum side of the character - the character will fit into a square with the side length of <code>maxSide</code>
	 * @param fontName	the name of the font to use
	 */
	private static void drawCharacter(Graphics2D g2d, char c, int x, int y, int maxSide, String fontName){
		int fontsize = getFontSize(g2d, fontName, maxSide);
		g2d.setColor(new Color(0,0,0));
		g2d.setFont(new Font(fontName, Font.BOLD, fontsize));
		FontMetrics fm = g2d.getFontMetrics();
		int ox = (fm.charWidth(c) / 2);
		int oy = ((fm.getAscent()-fm.getDescent()) / 2);
		g2d.drawString(Character.toString(c), x-ox, y+oy);
	}

	/**
	 * TODO deprecate?
	 * Fits an 'M' in the specified font inside a square with side </code>maxSide</code>
	 * and returns the maximum font size that can be used.
	 * @param g2d	the <code>Graphics2D</code> to use for measurements (nothing will be drawn on it)
	 * @param fontName	the name of the font to use
	 * @param maxSide	the side of the bounding square
	 * @return	the maximum font size that can be used to fit an 'M' in the specified font into a square with side <code>maxSide</code>
	 */
	private static int getFontSize(Graphics2D g2d, String fontName, int maxSide){
		char c = 'M';
		int fontSize = 1;
		Font f;
		FontMetrics fm;
		do{
			f = new Font(fontName, Font.BOLD, fontSize++);
			fm = g2d.getFontMetrics(f);
		}while(fm.charWidth(c) < maxSide && fm.getHeight() < maxSide);
		return fontSize;
	}

	/**
	 * TODO fix javadoc
	 * Fits an 'M' in the specified font inside a square with side </code>maxSide</code>
	 * and returns the maximum font size that can be used.
	 * @param g2d	the <code>Graphics2D</code> to use for measurements (nothing will be drawn on it)
	 * @param fontName	the name of the font to use
	 * @param maxSide	the side of the bounding square
	 * @return	the maximum font size that can be used to fit an 'M' in the specified font into a square with side <code>maxSide</code>
	 */
	public static int getFontSize(Graphics2D g2d, Font font, int maxSide){
		char c = 'M';
		int fontSize = 1;
		Font f = font.deriveFont(1.0f);
		FontMetrics fm;
		do{
			f = f.deriveFont((float) (fontSize++));
			fm = g2d.getFontMetrics(f);
		}while(fm.charWidth(c) < maxSide && fm.getHeight() < maxSide);
		return fontSize;
	}

}
