/**
 * 
 */
package se.iroiro.md.hangeulreader;

import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;

import se.iroiro.scribble.ScribblePanel;

/**
 * GUI to display image and graph
 * @author j
 *
 */
public class GUI2 {

	private JFrame jf;
	private JPanel jp;
	private ScribblePanel sp;
	private ImageRenderer ir;
	
	/**
	 * Class constructor. Initialises the GUI, but does not show it.
	 * @see GUI2#show()
	 */
	@SuppressWarnings("serial")
	public GUI2(){
		jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sp = new ScribblePanel();
//		jp = new JPanel(){
//			Graphics2D g2d;
//			public void paintComponent(Graphics g){
//				g2d = (Graphics2D) g;
//				if(ir != null && ir.getImage() != null){
//					g2d.drawImage(ir.getImage(), 0, 0, null);
//				}
//			}
//		};
	}
	
	/**
	 * Shows the window and calls {@link GUI2#refresh()} to paint the image.
	 */
	public void show(){
		jf.getContentPane().add(sp);
//		jf.setContentPane(jp);
		jf.setVisible(true);
		sp.setStrokeWidth(10);
		refresh();
	}
	
	/**
	 * Resizes the window to the size of the image, and redraws the image.
	 */
	public void refresh(){
		int frameWidth = 600;
		int frameHeight = 500;
		Insets is = jf.getInsets();
		frameWidth = frameWidth + is.left + is.right;
		frameHeight = frameHeight + is.top + is.bottom;
		jf.setSize(frameWidth, frameHeight);
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
