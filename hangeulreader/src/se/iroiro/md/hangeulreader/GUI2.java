/**
 * 
 */
package se.iroiro.md.hangeulreader;

import java.awt.Insets;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import se.iroiro.scribble.ScribbleEventNotifierAdapter;
import se.iroiro.scribble.ScribblePanel;

import se.iroiro.md.hangeul.Hangeul;
import se.iroiro.md.hangeul.HangeulClassifier;
import se.iroiro.md.hangeulreader.Helper;

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
		sp = new ScribblePanel(new ScribbleEventNotifierAdapter(){
			private HangeulClassifier hc = new HangeulClassifier();

			public void mouseReleased(MouseEvent e) {
				hc.newClassification(getImage());
				Hangeul h = hc.getHangeul();
				if(h != null){
					Helper.p("Looks like "+h+" ("+h.getName()+")\n");
				}
			}
		});
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
