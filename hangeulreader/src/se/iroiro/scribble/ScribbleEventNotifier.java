/**
 *
 */
package se.iroiro.scribble;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Send a reference to an object implementing this interface to be notified on mouse events.
 * @author j
 *
 */
public interface ScribbleEventNotifier {
	/**
	 * Invoked when a mouse button has been depressed.
	 * @param e	Event object containing additional data
	 */
	public void mousePressed(MouseEvent e);

	/**
	 * Invoked when a mouse button has been released.
	 * @param e	Event object containing additional data
	 */
	public void mouseReleased(MouseEvent e);

	/**
	 * Returns the image buffer of the scribble panel.
	 * @return	the image buffer
	 */
	public BufferedImage getImage();

	/**
	 * Sets the image buffer of the scribble panel.
	 * This method is used only by the scribble panel itself and it need not be called elsewhere.
	 * @param buffer	the image buffer
	 */
	public void setImage(BufferedImage buffer);

}
