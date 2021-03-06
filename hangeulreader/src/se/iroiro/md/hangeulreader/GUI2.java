/**
 *
 */
package se.iroiro.md.hangeulreader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import se.iroiro.scribble.ScribbleEventNotifierAdapter;
import se.iroiro.scribble.ScribblePanel;

import se.iroiro.md.hangeul.CharacterMeasurement;
import se.iroiro.md.hangeul.CharacterRenderer;
import se.iroiro.md.hangeul.Hangeul;
import se.iroiro.md.hangeul.HangeulClassifier;
import se.iroiro.md.hangeul.Jamo;
import se.iroiro.md.hangeul.JamoReferenceDB;
import se.iroiro.md.hangeul.Line;
import se.iroiro.md.hangeul.LineGroup;
import se.iroiro.md.hangeulreader.ImageRenderer.overlayType;

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
	private JPanel resultpane;
	private JPanel inputpane;
	private JTextField tf;
	private JTextField tf2;
	private JTextField jamotf;
	private JMenuBar menubar;

	final JamoReferenceDB jamoRef;
	private final HangeulClassifier hc;

	private BufferedImage tmp = null;

	private int scribblewidth;
	private int scribbleheight;
	private final int gridsep = 5;

	private CharacterMeasurement cm = null;
	private TreeMap<overlayType,JCheckBoxMenuItem> settingsmi;

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
	public GUI2(int scribblewidth, int scribbleheight, JamoReferenceDB jrdb, BufferedImage backgroundImage){
		this.scribblewidth = scribblewidth;
		this.scribbleheight = scribbleheight;
		frame = new JFrame("Hangeul reader");
		content = new JPanel(new BorderLayout(gridsep,gridsep));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		if(jrdb != null){
			jamoRef = jrdb;
		}else{
			jamoRef = new JamoReferenceDB();
		}
		hc = new HangeulClassifier(jamoRef);
		sp = new ScribblePanel(new ScribbleEventNotifierAdapter(){
			public void mousePressed(MouseEvent e){
				if(tmp != null){
					getImage().setData(tmp.getRaster());
				}
			}

			public void mouseReleased(MouseEvent e) {
				classify();
			}
		},backgroundImage);

		tf = new JTextField();
		tf2 = new JTextField();
		jamotf = new JTextField();
		tf.setEditable(false);
		tf2.setEditable(false);
		jamotf.setEditable(false);
		jamotf.setFont(getSizedFont(25));
		tf2.setFont(getSizedFont(13));
		jamotf.setHorizontalAlignment(JTextField.CENTER);
		tf.setHorizontalAlignment(JTextField.CENTER);
		tf2.setHorizontalAlignment(JTextField.CENTER);

		jamotf.setBackground(Color.WHITE);
		tf.setBackground(Color.WHITE);
		tf2.setBackground(Color.WHITE);

		tf.addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent e) {
				Dimension d = tf.getSize();
				int w = d.width;
				int h = d.height;
				int side = w < h ? w : h;
				tf.setFont(getSizedFont((int) (side*0.6)));
			}
		});

		Insets is = new Insets(10,10,10,10);

		Border b = new EmptyBorder(is);
		jamotf.setBorder(b);
		tf.setBorder(b);
		tf2.setBorder(b);

		inputpane = new JPanel();
		inputpane.setLayout(new GridLayout(1,1,0,0));
		inputpane.add(sp);

		resultpane = new JPanel();
		resultpane.setLayout(new BorderLayout(gridsep,gridsep));
		resultpane.add(jamotf,BorderLayout.NORTH);
		resultpane.add(tf,BorderLayout.CENTER);
		resultpane.add(tf2,BorderLayout.SOUTH);
		resultpane.setPreferredSize(new Dimension(200,0));

		menubar = new JMenuBar();
		final JMenu filemenu = new JMenu("File");
			final JMenuItem newmenu = new JMenuItem("Create new window");
			final JMenuItem loadmenu = new JMenuItem("Load image...");
			final JMenuItem savemenu = new JMenuItem("Save image...");
			final JMenuItem rendercharmenu = new JMenuItem("Render image from character...");
			final JMenuItem dotexport = new JMenuItem("Export relations to Graphviz file...");
			final JMenuItem pgftikzexport = new JMenuItem("Export relations to PGF/TikZ file...");
			final JMenuItem pgftikzexportlines = new JMenuItem("Export lines to PGF/TikZ file...");
		final JMenu testingmenu = new JMenu("Testing");
			final JMenuItem batchtestmenu = new JMenuItem("Run batch test...");
		final JMenu settingsmenu = new JMenu("Overlay");
		final JMenu helpmenu = new JMenu("Help");
			final JMenuItem usagemenu = new JMenuItem("Usage");
			final JMenuItem infomenu = new JMenuItem("Info");
		filemenu.add(newmenu);
		filemenu.add(loadmenu);
		filemenu.add(savemenu);
		filemenu.add(rendercharmenu);
		filemenu.add(new JSeparator());
		filemenu.add(dotexport);
		filemenu.add(pgftikzexport);
		filemenu.add(pgftikzexportlines);
		testingmenu.add(batchtestmenu);
		settingsmi = new TreeMap<overlayType,JCheckBoxMenuItem>();
		for(overlayType ol : overlayType.values()){
			JCheckBoxMenuItem mi = new JCheckBoxMenuItem(ol.toString());
			settingsmenu.add(mi);
			settingsmi.put(ol,mi);
		}
		helpmenu.add(usagemenu);
		helpmenu.add(infomenu);
		newmenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				new GUI2(jamoRef).show();
			}
		});
		loadmenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				loadImageFile();
			}
		});
		savemenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				saveImageFile();
			}
		});
		rendercharmenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String genCharStr = ((String) JOptionPane.showInputDialog(frame, "Input the character for which to generate an image.", "Character", JOptionPane.PLAIN_MESSAGE, null, null, "\uAC00"));
				if(genCharStr != null && genCharStr.length() > 0){
					char genChar = genCharStr.charAt(0);
					Font genFont = (Font) JOptionPane.showInputDialog(frame, "Select the font to use for the image.", "Font selection", JOptionPane.PLAIN_MESSAGE, null, jamoRef.getFonts().toArray(), 0);
					if(genFont != null){
						BufferedImage genImg = CharacterRenderer.makeCharacterImage(genChar, HangeulReaderTest.CHARSIZE, HangeulReaderTest.CHARSIZE, genFont);
						new GUI2(genImg.getWidth(), genImg.getHeight(), jamoRef, genImg).show();
					}
				}
			}
		});
		dotexport.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				exportDotGraph();
			}
		});
		pgftikzexport.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				exportPGFTikZ();
			}
		});
		pgftikzexportlines.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				exportPGFTikZlines();
			}
		});
		batchtestmenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				batchtest(jamoRef);
			}
		});
		ActionListener settingsal = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				paintImage();
			}
		};
		for(JCheckBoxMenuItem mi : settingsmi.values()){
			mi.addActionListener(settingsal);
		}
		usagemenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "Draw a hangeul syllable in the left area.\nAn external image may be loaded through drag-and-drop or through the load dialogue box.\nNote that only black areas of the input will be analysed. Dark grey is not black.\nShift-clicking clears the drawing area.\nUse the right mouse button or hold down the alt-key for eraser.");
			}
		});
		infomenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "Master's project in Language Technology\nJohan Sageryd (j@1616.se)\n\nLund University 2009");
			}
		});
		menubar.add(filemenu);
		menubar.add(testingmenu);
		menubar.add(settingsmenu);
		menubar.add(helpmenu);
		frame.setJMenuBar(menubar);

		/* Drag-and-drop support */
		FileDropTargetListener dtl = new FileDropTargetListener(jamoRef);
		new DropTarget(frame,dtl);
		new DropTarget(content,dtl);
		new DropTarget(inputpane,dtl);
		new DropTarget(resultpane,dtl);
		new DropTarget(sp,dtl);
		new DropTarget(tf,dtl);
		new DropTarget(tf2,dtl);
		new DropTarget(jamotf,dtl);
		/* --------------------- */
	}

	/**
	 * Classifies the image.
	 * @param image
	 */
	protected void classify() {
		try{
			sp.clipCanvasToPanelSize();
			if(tmp == null || (tmp.getWidth() != sp.getImage().getWidth() || tmp.getHeight() != sp.getImage().getHeight())){
				tmp = new BufferedImage(sp.getImage().getWidth(),sp.getImage().getHeight(),BufferedImage.TYPE_INT_RGB);
			}
			tmp.setData(sp.getImage().getRaster());

			tf2.setText("Analysing...");
			frame.paintAll(frame.getGraphics());
			hc.newClassification(tmp);
			Hangeul h = hc.getHangeul();
			List<Jamo> j = hc.getJamo();
			StringBuilder jstr = new StringBuilder();
			if(j != null){
				for(Jamo jamo : j){
					jstr.append(jamo.getChar()+" ");
				}
			}
			jamotf.setText(jstr.toString().trim());
			jamotf.setFont(getBoxFitFont(jamotf.getText(), (int) (jamotf.getWidth()*0.85), (int) (jamotf.getHeight()*0.75)));
			if(h != null){
				tf.setText(h.toString());
				tf2.setText("Hangeul syllable "+h.getName());
			}else{
				if(j != null && j.size() > 0){
					Jamo jamo = j.get(0);
					tf.setText(jamo.toString());
					tf2.setText("Hangeul jamo "+jamo.getName());
				}else{
					tf.setText("");
					tf2.setText("Unknown character");
				}
			}

			frame.paintAll(frame.getGraphics());
			cm = null;
			paintImage();
		} catch (OutOfMemoryError e){
			tf2.setText("Input is too complex.");
		} catch (Exception e){
			tf2.setText("An error occured.");
		}
	}

	/**
	 * Paints the image back onto the canvas after it has been read by the classifier.
	 * Overlay is painted if chosen.
	 */
	private void paintImage(){
		if(tmp != null){
			ir = null;
			for(overlayType ol : settingsmi.keySet()){
				if(settingsmi.get(ol).isSelected()){
					if(cm == null){
						cm = new CharacterMeasurement(tmp);
					}
					if(ir == null){
						ir = new ImageRenderer(cm);
					}
					ir.overlay(ol);
				}
			}
			if(ir != null){
				sp.getImage().setData((ir.getImage().getRaster()));
			}else{
				sp.getImage().setData(tmp.getRaster());
			}
			sp.repaint();
		}
	}


	/**
	 * Drag-and-drop listener class
	 * @author j
	 *
	 */
	class FileDropTargetListener implements DropTargetListener{

		JamoReferenceDB jamoRef;

		public FileDropTargetListener(JamoReferenceDB jamoRef){
			this.jamoRef = jamoRef;
		}

		public void dragEnter(DropTargetDragEvent dtde) {
		}

		public void dragExit(DropTargetEvent dte) {
		}

		public void dragOver(DropTargetDragEvent dtde) {
		}

		@SuppressWarnings("unchecked")
		public void drop(DropTargetDropEvent dtde) {
			Transferable tr = dtde.getTransferable();
			try {
				if(dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
					dtde.acceptDrop(dtde.getDropAction());
					List transferData = (List) tr.getTransferData(DataFlavor.javaFileListFlavor);
					if(transferData != null){
						File file = (File) transferData.get(0);

						BufferedImage img = null;
						try {
							img = ImageIO.read(file);
						} catch (IOException error) {
							throw new IllegalArgumentException("Could not load file.");
						}
						if(img != null){
							new GUI2(img.getWidth(),img.getHeight(),jamoRef,img).show();
						}
					}
					dtde.dropComplete(true);
				}else{
					dtde.rejectDrop();
				}
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void dropActionChanged(DropTargetDragEvent dtde) {
		}
	}

	private Font getFont(){
		List<Font> fl = jamoRef.getFonts();
		Font f = null;
		if(fl.size() > 0) f = fl.get(0);
		if(f == null){
			f = new Font("default",0,1);
		}
		return f;
	}

	private Font getSizedFont(int pixelsize){
		Font f = getFont();
		float fs = CharacterRenderer.getFontSize((Graphics2D) new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB).getGraphics(),f,pixelsize);
		return f.deriveFont(fs);
	}

	private Font getBoxFitFont(String string, int width, int height){
		Font f = getFont();
		float fs = CharacterRenderer.getBoxFitFontSize(string, (Graphics2D) new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB).getGraphics(),f,width,height);
		return f.deriveFont(fs);
	}

	private boolean testStartConfirm(){
		int startTest = JOptionPane.showConfirmDialog(frame, "The test will start, please check the console for progress details.\n\nDuring the test the GUI will be frozen.\nA message box will be shown upon completion.", "Test start", JOptionPane.OK_CANCEL_OPTION);
		return startTest == JOptionPane.OK_OPTION;
	}

	private void batchtest(JamoReferenceDB jamoRef){
		if(jamoRef.getFonts().size() < 1) {
			JOptionPane.showMessageDialog(frame, "No fonts loaded, cannot generate test data.");
			return;
		}
		HangeulReaderTest ht = new HangeulReaderTest();
		String resultsFile = null;
		boolean aborted = false;
		JOptionPane.showMessageDialog(frame, "In the following series of dialogues,\nplease input font, file to save results to, and test data.");
		Font fontToUse = (Font) JOptionPane.showInputDialog(frame, "Select the font to use for the test images", "Font selection", JOptionPane.PLAIN_MESSAGE, null, jamoRef.getFonts().toArray(), 0);
		if(fontToUse != null){
			JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
			fc.setMultiSelectionEnabled(false);
			fc.setFileFilter(new FileFilter(){
				public boolean accept(File f) {
					if(f.getName().endsWith(".txt")){
						return true;
					}
					return false;
				}

				public String getDescription() {
					return "Text file file (.txt)";
				}
			});
			int saveDialogReturnVal = fc.showSaveDialog(frame);
			if(saveDialogReturnVal == JFileChooser.APPROVE_OPTION){
				File saveFile = fc.getSelectedFile();
				if(saveFile != null){
					if(!saveFile.getName().toLowerCase().endsWith(".txt")){
						saveFile = new File(saveFile.getAbsolutePath().concat(".txt"));
					}
					if(saveFile.exists()){
						int canReplace = JOptionPane.showConfirmDialog(frame, "The file exists. Continue anyway?", "File exists", JOptionPane.YES_NO_OPTION);
						if(canReplace != JOptionPane.YES_OPTION){
							aborted = true;
						}
					}
					if(!aborted){
						resultsFile = saveFile.getPath();
						int genAll = JOptionPane.showConfirmDialog(frame, "Generate and test all 11 172 characters (will take quite some time)?", "Test scope", JOptionPane.YES_NO_CANCEL_OPTION);
						if(genAll == JOptionPane.YES_OPTION){
							if(testStartConfirm()){
								tf2.setText("Testing in progress...");
								frame.paintAll(frame.getGraphics());
								Helper.dump(ht.testAll(fontToUse,jamoRef),resultsFile);
								tf2.setText("Test finished.");
								JOptionPane.showMessageDialog(frame, "Test finished. Results stored:\n"+resultsFile);
							}else{
								aborted = true;
							}
						}else if(genAll == JOptionPane.NO_OPTION){
							String[] scopeOptions = {"Range","String","Cancel"};
							int useCharRange = JOptionPane.showOptionDialog(frame, "Input character range or string of characters?", "Test scope", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, scopeOptions, scopeOptions[0]);
							if(useCharRange == JOptionPane.YES_OPTION){
								// use range
								String fromString = ((String) JOptionPane.showInputDialog(frame, "Input the hangeul character to start from.", "Range", JOptionPane.PLAIN_MESSAGE, null, null, "\uAC00"));
								if(fromString != null && fromString.length() > 0){
									String toString = ((String) JOptionPane.showInputDialog(frame, "Input the hangeul character to end at.", "Range", JOptionPane.PLAIN_MESSAGE, null, null, "\uD7A3"));
									if(toString != null && toString.length() > 0){
										char fromChar = fromString.charAt(0);
										char toChar = toString.charAt(0);
										if(testStartConfirm()){
											tf2.setText("Testing in progress...");
											frame.paintAll(frame.getGraphics());
											Helper.dump(ht.test(fromChar,toChar,fontToUse,jamoRef),resultsFile);
											tf2.setText("Test finished.");
											JOptionPane.showMessageDialog(frame, "Test finished. Results stored:\n"+resultsFile);
										}else{
											aborted = true;
										}
									}else{
										aborted = true;
									}
								}else{
									aborted = true;
								}
							}else if(useCharRange == JOptionPane.NO_OPTION){
								// use string
								String charString = (String) JOptionPane.showInputDialog(frame, "Input a string of hangeul characters to use for the testing.", "String", JOptionPane.PLAIN_MESSAGE, null, null, "\uAC00\uAC01\uAC02\uAC03\uAC04\uAC05");
								if(charString != null && charString.length() > 0){
									if(testStartConfirm()){
										tf2.setText("Testing in progress...");
										frame.paintAll(frame.getGraphics());
										Helper.dump(ht.test(charString,fontToUse,jamoRef),resultsFile);
										tf2.setText("Test finished.");
										JOptionPane.showMessageDialog(frame, "Test finished. Results stored:\n"+resultsFile);
									}else{
										aborted = true;
									}
								}else{
									aborted = true;
								}
							}else{
								aborted = true;
							}
						}else{
							aborted = true;
						}
					}
				}else{
					JOptionPane.showMessageDialog(frame, "The file could not be created.\n\nRead-only?\nStrange file name?\n\nAborting.");
				}
			}else{
				aborted = true;
			}
		}else{
			aborted = true;
		}

		if(aborted){
			JOptionPane.showMessageDialog(frame, "Batch test aborted.");
		}
	}

	/**
	 * Saves the current buffer to file. This clips the canvas.
	 */
	private void saveImageFile(){
		JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
		fc.setMultiSelectionEnabled(false);
		fc.setFileFilter(new FileFilter(){
			public boolean accept(File f) {
				if(f.getName().endsWith(".png")){
					return true;
				}
				return false;
			}

			public String getDescription() {
				return "PNG";
			}
		});
		int saveDialogReturnVal = fc.showSaveDialog(frame);
		if(saveDialogReturnVal == JFileChooser.APPROVE_OPTION){
			File saveFile = fc.getSelectedFile();
			if(saveFile != null){
				if(!saveFile.getName().toLowerCase().endsWith(".png")){
					saveFile = new File(saveFile.getAbsolutePath().concat(".png"));
				}
				if(saveFile.exists()){
					int canReplace = JOptionPane.showConfirmDialog(frame, "The file exists. Overwrite?", "File exists", JOptionPane.YES_NO_OPTION);
					if(canReplace != JOptionPane.YES_OPTION){
						return;
					}
				}
				sp.clipCanvasToPanelSize();
				writeImage(sp.getImage(), saveFile);
			}
		}
	}

	/**
	 * Exports the structures to a dot graph.
	 */
	private void exportDotGraph(){
		if(cm == null) cm = new CharacterMeasurement(tmp);
		if(cm != null && cm.getLineGroups() != null && cm.getLineGroups().size() > 0){
			JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
			fc.setMultiSelectionEnabled(false);
			fc.setFileFilter(new FileFilter(){
				public boolean accept(File f) {
					if(f.getName().endsWith(".dot")){
						return true;
					}
					return false;
				}

				public String getDescription() {
					return "Graphviz dot-file";
				}
			});
			int saveDialogReturnVal = fc.showSaveDialog(frame);
			if(saveDialogReturnVal == JFileChooser.APPROVE_OPTION){
				File saveFile = fc.getSelectedFile();
				if(saveFile != null){
					if(!saveFile.getName().toLowerCase().endsWith(".dot")){
						saveFile = new File(saveFile.getAbsolutePath().concat(".dot"));
					}
					if(saveFile.exists()){
						int canReplace = JOptionPane.showConfirmDialog(frame, "The file exists. Overwrite?", "File exists", JOptionPane.YES_NO_OPTION);
						if(canReplace != JOptionPane.YES_OPTION){
							return;
						}
					}
					StringBuffer sb = new StringBuffer();
					sb.append("digraph G {\n\n");
					int lastLGsize = 0;
					for(LineGroup lg : cm.getLineGroups()){
						sb.append(lg.getGraph().toDotSubgraph(lastLGsize, lastLGsize)+"\n");
						lastLGsize += lg.getMap().size();
					}
					sb.append("}");
					Helper.dump(sb.toString(), saveFile.getAbsolutePath());
				}
			}
		}else{
			JOptionPane.showMessageDialog(frame, "There are no structures to export.");
		}
	}

	/**
	 * Exports the structures to a PGF/TikZ file.
	 */
	private void exportPGFTikZ(){
		if(cm == null) cm = new CharacterMeasurement(tmp);
		if(cm != null && cm.getLineGroups() != null && cm.getLineGroups().size() > 0){
			JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
			fc.setMultiSelectionEnabled(false);
			fc.setFileFilter(new FileFilter(){
				public boolean accept(File f) {
					if(f.getName().endsWith(".tikz")){
						return true;
					}
					return false;
				}

				public String getDescription() {
					return "PGF/TikZ file";
				}
			});
			int saveDialogReturnVal = fc.showSaveDialog(frame);
			if(saveDialogReturnVal == JFileChooser.APPROVE_OPTION){
				File saveFile = fc.getSelectedFile();
				if(saveFile != null){
					if(!saveFile.getName().toLowerCase().endsWith(".tikz")){
						saveFile = new File(saveFile.getAbsolutePath().concat(".tikz"));
					}
					if(saveFile.exists()){
						int canReplace = JOptionPane.showConfirmDialog(frame, "The file exists. Overwrite?", "File exists", JOptionPane.YES_NO_OPTION);
						if(canReplace != JOptionPane.YES_OPTION){
							return;
						}
					}
					StringBuffer tikz = new StringBuffer();
					tikz.append("\\begin{tikzpicture}\n");
					tikz.append("[scale=0.03,\n");
					tikz.append("node/.style={thick,circle,draw=black,fill=white}");
					tikz.append(",\n");
					tikz.append("edge/.style={line width=1,draw=black}");
					tikz.append("]\n\n");
					tikz.append(String.format("\\node (topleft)	at	(%d,%d)	[transparent]	{};\n", 0, 0));
					tikz.append(String.format("\\node (bottomright)	at	(%d,%d)	[transparent]	{};\n", tmp.getWidth()-1, -(tmp.getHeight()-1)));
					for(LineGroup lg : cm.getLineGroups()){
						tikz.append("\n");
						tikz.append(lg.getGraph().toPGFTikZ(false, true, false, true) + "\n");
					}
					tikz.append("\\end{tikzpicture}");
					Helper.dump(tikz.toString(), saveFile.getAbsolutePath());
				}
			}
		}else{
			JOptionPane.showMessageDialog(frame, "There are no structures to export.");
		}
	}

	/**
	 * Exports the lines to a PGF/TikZ file.
	 */
	private void exportPGFTikZlines(){
		if(cm == null) cm = new CharacterMeasurement(tmp);
		if(cm != null && cm.getLineGroups() != null && cm.getLineGroups().size() > 0){
			JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
			fc.setMultiSelectionEnabled(false);
			fc.setFileFilter(new FileFilter(){
				public boolean accept(File f) {
					if(f.getName().endsWith(".tikz")){
						return true;
					}
					return false;
				}

				public String getDescription() {
					return "PGF/TikZ file";
				}
			});
			int saveDialogReturnVal = fc.showSaveDialog(frame);
			if(saveDialogReturnVal == JFileChooser.APPROVE_OPTION){
				File saveFile = fc.getSelectedFile();
				if(saveFile != null){
					if(!saveFile.getName().toLowerCase().endsWith(".tikz")){
						saveFile = new File(saveFile.getAbsolutePath().concat(".tikz"));
					}
					if(saveFile.exists()){
						int canReplace = JOptionPane.showConfirmDialog(frame, "The file exists. Overwrite?", "File exists", JOptionPane.YES_NO_OPTION);
						if(canReplace != JOptionPane.YES_OPTION){
							return;
						}
					}
					StringBuffer tikz = new StringBuffer();
					tikz.append("\\begin{tikzpicture}\n");
					tikz.append("[scale=0.03,\n");
					tikz.append("edge/.style={line width=1,draw=black}");
					tikz.append("]\n\n");
					tikz.append(String.format("\\node (topleft)	at	(%d,%d)	[transparent]	{};\n", 0, 0));
					tikz.append(String.format("\\node (bottomright)	at	(%d,%d)	[transparent]	{};\n", tmp.getWidth()-1, -(tmp.getHeight()-1)));
					for(LineGroup lg : cm.getLineGroups()){
						tikz.append("\n");
						for(Line l : lg.getMap().keySet()){
							tikz.append(l.getGraph().toPGFTikZ(false, false, true, true));
						}
					}
					tikz.append("\n\\end{tikzpicture}");
					Helper.dump(tikz.toString(), saveFile.getAbsolutePath());
				}
			}
		}else{
			JOptionPane.showMessageDialog(frame, "There are no lines to export.");
		}
	}

	/**
	 * Writes an image to file.
	 * @param img	image to write
	 * @param file	file to write to
	 */
	private void writeImage(BufferedImage img, File file){
		try{
			ImageIO.write(img, "png", file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads an image from file.
	 */
	private void loadImageFile(){
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

	/**
	 * Shows the window and calls {@link GUI2#refresh()} to paint the image.
	 */
	public void show(){
		content.add(inputpane,BorderLayout.CENTER);
		content.add(resultpane,BorderLayout.EAST);
		frame.setContentPane(content);
		frame.setVisible(true);
		refresh();
		frame.paintAll(frame.getGraphics());
		classify();
	}

	/**
	 * Resizes the window to the size of the image, and redraws the image.
	 */
	public void refresh(){
		int frameWidth = scribblewidth + gridsep + resultpane.getWidth();
		int frameHeight = scribbleheight + menubar.getHeight();
		Insets is = frame.getInsets();
		if(is != null){
			frameWidth = frameWidth + is.left + is.right;
			frameHeight = frameHeight + is.top + is.bottom;
		}
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