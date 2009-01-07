/**
 * 
 */
package se.iroiro.md.hangeulreader;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import se.iroiro.scribble.ScribbleEventNotifierAdapter;
import se.iroiro.scribble.ScribblePanel;

import se.iroiro.md.hangeul.CharacterMeasurement;
import se.iroiro.md.hangeul.CharacterRenderer;
import se.iroiro.md.hangeul.Hangeul;
import se.iroiro.md.hangeul.HangeulClassifier;
import se.iroiro.md.hangeul.JamoReferenceDB;

/**
 * GUI to display image and graph
 * @author j
 *
 */
public class GUI2 {

	private JFrame frame;
	private JPanel content;
	private ScribblePanel sp;
	private ImageRenderer ir;
	private JTextField tf;
	private JTextField tf2;

	final int scribblewidth = 300;
	final int scribbleheight = 300;
	
	/**
	 * Class constructor. Initialises the GUI, but does not show it.
	 * @see GUI2#show()
	 */
	@SuppressWarnings("serial")
	public GUI2(){
		frame = new JFrame("Draw a hangeul in the leftmost field. Shift-clicking clears the field.");
		content = new JPanel(new GridLayout(1,3,10,0));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JamoReferenceDB jrdb = new JamoReferenceDB();
		sp = new ScribblePanel(new ScribbleEventNotifierAdapter(){
			private HangeulClassifier hc = new HangeulClassifier(jrdb);
			BufferedImage tmp = null;
			public void mousePressed(MouseEvent e){
				if(tmp != null){
					getImage().setData(tmp.getRaster());
				}else{
					tmp = new BufferedImage(scribblewidth,scribbleheight,BufferedImage.TYPE_INT_RGB);
				}
			}
			
			public void mouseReleased(MouseEvent e) {
				hc.newClassification(getImage());
				Hangeul h = hc.getHangeul();
				displayCharacter(h);
				if(h != null){
					tf.setText(h.toString());
					tf2.setText("HANGEUL CHARACTER "+h.getName());
				}else{
					tf.setText("?");
					tf2.setText("Unknown");
				}
				tmp.setData(getImage().getRaster());
				getImage().setData((new ImageRenderer(new CharacterMeasurement(getImage())).getImage().getRaster()));
			}
		},scribblewidth,scribbleheight);

		tf = new JTextField();
		tf2 = new JTextField();
		tf.setEditable(false);
		tf2.setEditable(false);
		tf.setBorder(null);
		tf2.setBorder(null);
		List<Font> fl = jrdb.getFonts();
		Font f = null;
		if(fl.size() > 0) f = fl.get(0);
		if(f != null){
			float fs = CharacterRenderer.getFontSize((Graphics2D) new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB).getGraphics(),f,(int) (scribbleheight * 0.75));
			f = f.deriveFont(fs);
		}else{
			f = new Font("default",0,72);
		}
		tf.setFont(f);
	}

	protected void displayCharacter(Hangeul h) {
		
	}

	/**
	 * Shows the window and calls {@link GUI2#refresh()} to paint the image.
	 */
	public void show(){
		content.add(sp);
		content.add(tf);
		content.add(tf2);
		frame.setContentPane(content);
		frame.setVisible(true);
		sp.setStrokeWidth(10);
		refresh();
	}
	
	/**
	 * Resizes the window to the size of the image, and redraws the image.
	 */
	public void refresh(){
		int frameWidth = scribblewidth*3;
		int frameHeight = scribbleheight;
		Insets is = frame.getInsets();
		frameWidth = frameWidth + is.left + is.right;
		frameHeight = frameHeight + is.top + is.bottom;
		frame.setSize(frameWidth, frameHeight);
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
