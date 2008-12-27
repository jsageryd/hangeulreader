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
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import se.iroiro.md.hangeul.Hangeul;
import se.iroiro.md.hangeul.HangeulClassifier;
import se.iroiro.md.hangeulreader.Helper;

/**
 * Simple scribblable panel
 * @author j
 *
 */
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
	 * 
	 */
	public ScribblePanel() {
		this(600,500);
	}
	
	/**
	 * @param width
	 * @param height
	 */
	public ScribblePanel(int width, int height){
		buffer = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		bufferg2d = buffer.createGraphics();
		bufferg2d.setColor(Color.WHITE);
		bufferg2d.fillRect(0,0,width,height);
		bufferg2d.setColor(Color.BLACK);

		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		setPreferredSize(new Dimension(width,height));
		
		addMouseListener(new MouseAdapter(){
			private HangeulClassifier hc = new HangeulClassifier();
			public void mousePressed(MouseEvent e){
				moveTo(e.getX(),e.getY(),e.getButton());
			}
			public void mouseReleased(MouseEvent e){
				hc.newClassification(buffer);
				Hangeul h = hc.getHangeul();
				if(h != null){
					Helper.p("Looks like "+h+" ("+h.getName()+")\n");
				}
			}
		});

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
