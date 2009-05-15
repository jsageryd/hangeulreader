/**
 *
 */
package se.iroiro.scribble;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Simple scribblable panel
 * @author j
 *
 */
@SuppressWarnings("serial")
public class ScribblePanel extends JPanel {

	private Graphics2D bufferg2d;
	private ScribbleEventNotifier sen;

	private int width = 1;
	private int height = 1;

	private int x = 0;
	private int y = 0;

	private int strokeWidth = 10;
	private Color penColour = Color.BLACK;

	private BufferedImage buffer;

	/**
	 * Clips the canvas to the actual size of the panel.
	 * Anything outside the panel area is discarded.
	 */
	public void clipCanvasToPanelSize(){
		Dimension d = getSize();
		if(d.width != width || d.height != height){
			width=d.width;
			height=d.height;
			initBuffer(buffer);
		}
	}

	/**
	 * Grows the canvas to cover new panel area without clipping.
	 */
	private void letCanvasGrow(){
		Dimension d = getSize();
		boolean reInit = false;
		if(d.width > width){
			reInit = true;
			width = d.width;
		}
		if(d.height > height){
			reInit = true;
			height = d.height;
		}
		if(reInit) initBuffer(buffer);
	}

	public void paintComponent(Graphics g){
		super.paintComponents(g);
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.drawImage(buffer,0,0,null);
	}

	/**
	 *	Empty class constructor.
	 */
	public ScribblePanel() {
		this(null);
	}

	public ScribblePanel(ScribbleEventNotifier sen){
		this(sen,null);
	}

	/**
	 * Initialises the image buffer of the panel to the correct size.
	 * Paints the specified image.
	 * @param imageToPaint
	 */
	private void initBuffer(BufferedImage imageToPaint){
		buffer = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		bufferg2d = buffer.createGraphics();
		setStrokeWidth(strokeWidth);
		setPenColour(penColour);
		clearBuffer();
		if(imageToPaint != null){
			bufferg2d.drawImage(imageToPaint,0,0,imageToPaint.getWidth(),imageToPaint.getHeight(),null);
		}
		if(sen != null){
			sen.setImage(buffer);
		}
	}

	/**
	 * @param width
	 * @param height
	 */
	public ScribblePanel(ScribbleEventNotifier sen, BufferedImage backgroundImage){
		this.sen = sen;
		if(backgroundImage != null){
			width = backgroundImage.getWidth();
			height = backgroundImage.getHeight();
		}
		initBuffer(backgroundImage);

		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

		addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent e) {
				letCanvasGrow();
			}
		});

		class MA extends MouseAdapter{	// extended in inner class to use custom event notifier
			ScribbleEventNotifier sen;
			public MA(ScribbleEventNotifier sen){
				this.sen = sen;
			}

			public void mousePressed(MouseEvent e){
				if(sen != null){
					sen.mousePressed(e);
					bufferg2d.drawImage(sen.getImage(),0,0,sen.getImage().getWidth(),sen.getImage().getHeight(),null);
					update(getGraphics());
				}
				if(e.isShiftDown()){
					clearBuffer();
				}else{
					if(e.getButton() == MouseEvent.BUTTON3 || e.isAltDown()){
						setPenColour(Color.WHITE);
						setStrokeWidth(20);
					}else{
						setPenColour(Color.BLACK);
						setStrokeWidth(10);
					}
					moveTo(e.getX(),e.getY());
				}
			}
			public void mouseReleased(MouseEvent e){
				if(sen != null){
					sen.mouseReleased(e);
					bufferg2d.drawImage(sen.getImage(),0,0,sen.getImage().getWidth(),sen.getImage().getHeight(),null);
					update(getGraphics());
				}
			}
		}
		addMouseListener(new MA(sen));

		addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent e){
				if(!e.isShiftDown()){
					lineTo(e.getX(),e.getY());
				}
			}
		});

	}

	/**
	 * Clears the image buffer by filling it with white.
	 */
	private void clearBuffer() {
		bufferg2d.setColor(Color.WHITE);
		bufferg2d.fillRect(0,0,width,height);
		bufferg2d.setColor(Color.BLACK);
	}

	protected void moveTo(int x, int y) {
		bufferg2d.drawLine(x, y, x, y);
		this.x = x;
		this.y = y;
		update(getGraphics());

	}

	protected void lineTo(int x, int y) {
		bufferg2d.drawLine(this.x, this.y, x, y);
		this.x = x;
		this.y = y;
		update(getGraphics());
	}

	/**
	 * Sets the stroke width.
	 * @param strokeWidth the new stroke width
	 */
	private void setStrokeWidth(int strokeWidth) {
		if(bufferg2d != null){
			this.strokeWidth = strokeWidth;
			bufferg2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			bufferg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			bufferg2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
	}

	/**
	 * Sets the pen colour.
	 * @param penColour	the new pen colour
	 */
	private void setPenColour(Color penColour){
		if(bufferg2d != null){
			this.penColour = penColour;
			bufferg2d.setColor(penColour);
		}
	}

	/**
	 * Returns the image buffer.
	 * @return	the image buffer
	 */
	public BufferedImage getImage(){
		return buffer;
	}

}
