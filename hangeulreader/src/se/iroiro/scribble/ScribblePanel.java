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
	
	private int width = 0;
	private int height = 0;
	
	private int x = 0;
	private int y = 0;
	
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
		this(sen,300,300,null);
	}
	
	/**
	 * @param width
	 * @param height
	 */
	public ScribblePanel(ScribbleEventNotifier sen, int width, int height, BufferedImage backgroundImage){
		this.width = width;
		this.height = height;
		buffer = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		bufferg2d = buffer.createGraphics();
		clearBuffer();
		if(backgroundImage != null){
			bufferg2d.drawImage(backgroundImage,0,0,backgroundImage.getWidth(),backgroundImage.getHeight(),null);
		}
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
