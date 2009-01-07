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
	
	private int x = 0;
	private int y = 0;
	private int button = 0;
	
	private BufferedImage buffer;
	
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
		this(sen,500,500);
	}
	
	/**
	 * @param width
	 * @param height
	 */
	public ScribblePanel(ScribbleEventNotifier sen, int width, int height){
		buffer = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		bufferg2d = buffer.createGraphics();
		bufferg2d.setColor(Color.WHITE);
		bufferg2d.fillRect(0,0,width,height);
		bufferg2d.setColor(Color.BLACK);
		sen.setImage(buffer);

		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		setPreferredSize(new Dimension(width,height));
		
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
				moveTo(e.getX(),e.getY(),e.getButton());
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
				lineTo(e.getX(),e.getY());
			}
		});
		
	}
	
	protected void moveTo(int x, int y, int button) {
		bufferg2d.drawLine(x, y, x, y);
		this.x = x;
		this.y = y;
		this.button = button;
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
	public void setStrokeWidth(int strokeWidth) {
		if(bufferg2d != null){
			bufferg2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			bufferg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			bufferg2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
	}

	/**
	 * Sets the pen colour.
	 * @param penColour	the new pen colour
	 */
	public void setPenColour(Color penColour){
		if(bufferg2d != null){
			bufferg2d.setColor(penColour);
		}
	}
	
}
