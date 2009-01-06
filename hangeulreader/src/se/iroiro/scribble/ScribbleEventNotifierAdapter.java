/**
 * 
 */
package se.iroiro.scribble;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Abstract class implementing the ScribbleEventNotifier interface,
 * adding a reference to the image buffer.
 * @author j
 *
 */
public abstract class ScribbleEventNotifierAdapter implements ScribbleEventNotifier{
	private BufferedImage buffer = null;

	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	
	public BufferedImage getImage(){
		return buffer;
	}

	public void setImage(BufferedImage buffer){
		this.buffer = buffer;
	}

}