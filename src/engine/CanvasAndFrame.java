package engine;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JFrame;


class PathFrame extends JFrame // create frame for canvas
{
	public PathCanvas canvas;
	public PathFrame(PathingApplication parent) // constructor
	{
		super("Pathfinder");
		setBounds(0, 0, 1024, 768);// set frame
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container con = this.getContentPane(); // inherit main frame
		con.setBackground(Color.black); // paint background
		canvas = new PathCanvas(parent); // create drawing canvas
		con.add(canvas);
		setVisible(true);// add to frame and show
	}
	/*
	 * public static void main(String arg[]) { new GFrame(); }
	 */
}

class PathCanvas extends Canvas {
	private Image dbImage; // needed for double buffering
	private Graphics dbg;
	private PathingApplication parent;
	
	public PathCanvas(PathingApplication p){
		super();
		parent = p;
	}
	
	public void paint(Graphics g) {
		parent.drawPathingTextFields(g);
		parent.drawObjs(g);
		parent.drawGrid(g);
		parent.drawPathingButtons(g);
		parent.drawNewPoints(g);
		parent.drawFT(g);
	}

	public boolean mouseDown(Event e, int x, int y) {
		return parent.pathMouseDown(e, x, y);
	}
	
	public void update(Graphics g) // double buffering so images are clear.
	{
		if (dbImage == null) {
			dbImage = createImage(this.getSize().width, this.getSize().height);
			dbg = dbImage.getGraphics();
		}
		dbg.setColor(getBackground());
		dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);
		dbg.setColor(getForeground());
		paint(dbg);
		g.drawImage(dbImage, 0, 0, this);
	}	
}
