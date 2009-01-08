/**
 * 
 */
package se.iroiro.md.hangeulreader;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import se.iroiro.scribble.ScribbleEventNotifierAdapter;
import se.iroiro.scribble.ScribblePanel;

import se.iroiro.md.hangeul.CharacterMeasurement;
import se.iroiro.md.hangeul.CharacterRenderer;
import se.iroiro.md.hangeul.Hangeul;
import se.iroiro.md.hangeul.HangeulClassifier;
import se.iroiro.md.hangeul.JamoReferenceDB;

/**
 * GUI to display scribble panel and recognition result
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
	private JMenuBar menubar;
	
	private final HangeulClassifier hc;

	int scribblewidth;
	int scribbleheight;
	
	/**
	 * Class constructor. Initialises a GUI of default size with the specified jamo reference DB.
	 * @param jrdb
	 */
	public GUI2(JamoReferenceDB jrdb){
		this(300,300,jrdb,null);
	}
	
	/**
	 * Class constructor. Initialises a GUI of default size.
	 * @see GUI2#GUI2(int, int)
	 */
	public GUI2(){
		this(300,300);
	}
	
	/**
	 * Class constructor. Initialises a GUI with the specified scribble size.
	 * @param scribblewidth	scribble area width
	 * @param scribbleheight	scribble area height
	 */
	public GUI2(int scribblewidth, int scribbleheight){
		this(scribblewidth,scribbleheight,null,null);
	}
	
	/**
	 * Class constructor. Initialises a GUI with the specified scribble size, and the specified jamo reference DB.
	 * If the reference DB is null, it is created.
	 * @see GUI2#show()
	 */
	@SuppressWarnings("serial")
	public GUI2(int scribblewidth, int scribbleheight, JamoReferenceDB jrdb, BufferedImage backgroundImage){
		this.scribblewidth = scribblewidth;
		this.scribbleheight = scribbleheight;
		final int swidth = scribblewidth;
		final int sheight = scribbleheight;
		frame = new JFrame("Draw a hangeul in the leftmost field. Shift-clicking clears the field. Hold down the alt-key for eraser.");
		content = new JPanel(new GridLayout(1,3,10,0));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		final JamoReferenceDB jamoRef;
		if(jrdb != null){
			jamoRef = jrdb;
		}else{
			jamoRef = new JamoReferenceDB();
		}
		hc = new HangeulClassifier(jamoRef);
		sp = new ScribblePanel(new ScribbleEventNotifierAdapter(){
			BufferedImage tmp = null;
			public void mousePressed(MouseEvent e){
				if(tmp != null){
					getImage().setData(tmp.getRaster());
				}else{
					tmp = new BufferedImage(swidth,sheight,BufferedImage.TYPE_INT_RGB);
				}
			}

			public void mouseReleased(MouseEvent e) {
				hc.newClassification(getImage());
				Hangeul h = hc.getHangeul();
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
		},scribblewidth,scribbleheight,backgroundImage);

		tf = new JTextField();
		tf2 = new JTextField();
		tf.setEditable(false);
		tf2.setEditable(false);
		tf.setBorder(null);
		tf2.setBorder(null);
		List<Font> fl = jamoRef.getFonts();
		Font f = null;
		if(fl.size() > 0) f = fl.get(0);
		if(f != null){
			float fs = CharacterRenderer.getFontSize((Graphics2D) new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB).getGraphics(),f,(int) (scribbleheight * 0.75));
			f = f.deriveFont(fs);
		}else{
			f = new Font("default",0,72);
		}
		tf.setFont(f);
		
		menubar = new JMenuBar();
		JMenu filemenu = new JMenu("File");
		JMenuItem newmenu = new JMenuItem("Create new window");
		JMenuItem loadmenu = new JMenuItem("Load image...");
		filemenu.add(newmenu);
		filemenu.add(loadmenu);
		newmenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				new GUI2(jamoRef).show();
			}
		});
		loadmenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
				fc.setMultiSelectionEnabled(false);
				fc.setFileFilter(new FileFilter(){
					public boolean accept(File f) {
						if(f.getName().endsWith(".png") || f.getName().endsWith(".jpg") || f.getName().endsWith(".gif")){
							return true;
						}
						return false;
					}

					public String getDescription() {
						return "Image files (.png, .jpg, .gif)";
					}
				});
				int returnVal = fc.showOpenDialog(frame);
				if(returnVal == JFileChooser.APPROVE_OPTION){
					File f = fc.getSelectedFile();
					BufferedImage img = null;
					try {
						img = ImageIO.read(f);
					} catch (IOException error) {
						throw new IllegalArgumentException("Could not load file.");
					}
					if(img != null){
						new GUI2(img.getWidth(),img.getHeight(),jamoRef,img).show();
					}
				}
			}
		});
		menubar.add(filemenu);
		frame.setJMenuBar(menubar);
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
		frameHeight = frameHeight + is.top + is.bottom + menubar.getHeight();
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
