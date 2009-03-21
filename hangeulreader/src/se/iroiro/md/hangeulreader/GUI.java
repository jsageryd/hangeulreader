/**
 *
 */
package se.iroiro.md.hangeulreader;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * GUI to display image and graph
 * @author j
 *
 */
public class GUI {

	private JFrame jf;
	private JPanel jp;
	private ImageRenderer ir;

	/**
	 * Class constructor. Initialises the GUI, but does not show it.
	 * @see GUI#show()
	 */
	@SuppressWarnings("serial")
	public GUI(){
		jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jp = new JPanel(){
			Graphics2D g2d;
			public void paintComponent(Graphics g){
				g2d = (Graphics2D) g;
				if(ir != null && ir.getImage() != null){
					g2d.drawImage(ir.getImage(), 0, 0, null);
				}
			}
		};
	}

	/**
	 * Shows the window and calls {@link GUI#refresh()} to paint the image.
	 */
	public void show(){
		jf.setContentPane(jp);
		jf.setVisible(true);
		refresh();
	}

	/**
	 * Resizes the window to the size of the image, and redraws the image.
	 */
	public void refresh(){
		int frameWidth = 300;
		int frameHeight = 200;
		Insets is = jf.getInsets();
		if(ir != null && ir.getImage() != null && is != null){
			frameWidth = ir.getImage().getWidth() + is.left + is.right;
			frameHeight = ir.getImage().getHeight() + is.top + is.bottom;
		}
		jf.setSize(frameWidth, frameHeight);
		jp.repaint();
	}

	/**
	 * Returns the image renderer.
	 * @return	the image renderer
	 */
	public ImageRenderer getImageRenderer() {
		return ir;
	}

	/**
	 * Sets the image renderer.
	 * @param ir	the image renderer
	 */
	public void setImageRenderer(ImageRenderer ir) {
		this.ir = ir;
	}

}
