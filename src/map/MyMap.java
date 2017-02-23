package map;

import engine.Main;
import graphicsObjects.PathingShape;

import java.awt.Point;
import java.awt.Rectangle;

//another possible map is a one byte map which stores an object id number
//pointing to which object is occupying the space, simplifying checking if
//an object still occupies that square.
public class MyMap {
	private int[] map;// store 32 boolean values in one int value
	private int size, totalMapW, totalMapH, numNodesWide, numNodesHigh,
			nodeWidth, nodeHeight;

	public MyMap(int totalMapW, int totalMapH, int nodeW, int nodeH) {
		this.totalMapW = totalMapW;
		this.totalMapH = totalMapH;
		this.nodeWidth = nodeW + nodeW % 2; // make it even so center will
											// always be int
		this.nodeHeight = nodeH + nodeH % 2;
		initMap();
	}

	void initMap() {
		// extend Width and height of totalMap to an integer multiple of
		// node
		totalMapW += nodeWidth - totalMapW % nodeWidth;
		totalMapH -= totalMapH % nodeHeight;
		int length = (int) Math.ceil((double) totalMapW / (double) nodeWidth
				* (double) totalMapH / (double) nodeHeight / (double) 32);
		map = new int[length];
		size = length;
		numNodesWide = totalMapW / nodeWidth;
		numNodesHigh = totalMapH / nodeHeight;
		System.out.println(numNodesWide + ", " + numNodesHigh + ", "
				+ totalMapW + ", " + totalMapH);
	}

	// the x,y values of a node, not of a pixel (ie y = 1 is nodeHeight in
	// pixels)
	public boolean get(int x, int y) {
		double mapPos = y * numNodesWide - 1 + x;
		if ((int)(mapPos/32) > size-1){
			return true;
		}
		double temp = (map[(int) (mapPos / 32)] & (1 << ((int) mapPos % 32)));
		if (temp == 0) {
			return false;
		} else {
			temp = Math.log10(temp) / Math.log10(2);
			return (Math.floor(temp) == temp);
		}
	}

	// gets the node at the absolute coordinates of point p.
	public boolean getAbs(Point p) {
		return get((int) p.getX() / nodeWidth, (int) p.getY() / nodeHeight);
	}

	// toggles the node at nodeSpace coordinates x,y
	public void toggle(int x, int y) {
		int mapPos = y * numNodesWide - 1 + x;
		if ((mapPos/32) > size-1){
			return;
		}
		map[mapPos / 32] = (map[mapPos / 32] ^ (1 << (mapPos % 32)));
	}

	// toggles the node at the absolute coordinates of point p.
	public void toggleAbs(Point p) {
		toggle((int) p.getX() / nodeWidth, (int) p.getY() / nodeHeight);
	}

	// nodespace coordinates x,y
	public void toggle(int x, int y, boolean state) {
		if (get(x, y) != state) {
			toggle(x, y);
		}
	}

	// gets the node at the absolute coordinates of point p.
	public void toggleAbs(Point p, boolean state) {
		toggle((int) p.getX() / nodeWidth, (int) p.getY() / nodeHeight, state);
	}

	public void toggleAbs(int x, int y, boolean state) {
		toggle(x / nodeWidth, y / nodeHeight, state);
	}

	/*
	 * as a shape moves, it calls
	 * 
	 * 
	 * void checkNodes(MyDirectionVector dir, PathingShape shape){ Point p1, p2;
	 * int[] xpoints = shape.getXPoints(), ypoints = shape.getYPoints(); int
	 * npoints = shape.npoints; for (int i = 0; i < npoints; i++){ p1 = new
	 * Point(xpoints[i], ypoints[i]); p2 = new Point(xpoints[(i+1)%npoints],
	 * ypoints[(i+1)%npoints]); } }
	 */

	public void toggleShape(PathingShape shape, boolean state) {
		int minX = shape.getMinX(), minY = shape.getMinY();
		// truncate absolute points to nearest node
		minX -= minX % nodeWidth;
		minY -= minY % nodeHeight;
		Rectangle nodeSpaceBounds = shape.getNodeSpaceBounds(this);
		//System.out.println();
		// shape bounds in node coordinate space,
		// loop through all nodes contained in the rectangle
		// that also contains shape
		int curX, curY;
		for (int i = 0; i < nodeSpaceBounds.getWidth()
				* nodeSpaceBounds.getHeight(); i++) {
			curX = (int) (minX + (i % nodeSpaceBounds.getWidth()) * nodeWidth + nodeWidth / 2);

			curY = (int) (minY + ((int) (i / nodeSpaceBounds.getWidth()))
					* nodeHeight + nodeHeight / 2);
			// if shape contains the center of this node, set it to state.
			if (shape.contains(curX, curY)) {
				toggleAbs(curX, curY, state);
			}
		}
	}

	public int getTotMapW() {
		return totalMapW;
	}

	public int getTotMapH() {
		return totalMapH;
	}

	public int getNumNodeH() {
		return numNodesHigh;
	}

	public int getNumNodeW() {
		return numNodesWide;
	}

	public int getNodeW() {
		return nodeWidth;
	}

	public int getNodeH() {
		return nodeHeight;
	}

	public int size() {
		return size;
	}

	public String toString() {
		String outString = "";
		for (int i = 0; i < numNodesWide * numNodesHigh; i++) {
			if (i % numNodesWide == 0 && i != 0) {
				outString += "\n";
			}
			outString += get(i % numNodesWide, i / numNodesWide) + ", ";
		}
		return outString;
	}
}
