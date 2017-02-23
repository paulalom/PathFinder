package graphicsObjects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import engine.Main;

import map.MyMap;
import map.PathingNode;
import myMath.MyMath;

public class PathingShape extends Polygon {
	private Color shapeColor = new Color(50, 50, 255), fullShapeColor;
	private boolean selected = false;
	private ArrayList<Point> path = new ArrayList<Point>();
	private Point center;
	private int minX = 99999999, maxX = 0, minY = 99999999, maxY = 0;
	private int[] dFromCenterX, dFromCenterY;

	public PathingShape(int[] xcoords, int[] ycoords, Color shapeColor) {
		super(xcoords, ycoords, xcoords.length);
		this.shapeColor = shapeColor;
		this.fullShapeColor = shapeColor;
		center = MyMath.getCenter(this);
		setBounds();
		setDFromCenter();
	}

	// needed to translate a decimal amount rather than integer only
	private void translate(double deltax, double deltay) {
		for (int i = 0; i < npoints; i++) {
			xpoints[i] += deltax;
			ypoints[i] += deltay;
		}
		center = MyMath.getCenter(this);
	}

	/*
	 * not sure about the efficiency of "getBounds()" (also the fact that it
	 * doesn't guarantee the smallest containing rectangle) so im writing
	 * this... time complexity isn't important since im only doing this once and
	 * npoints will always be small
	 */
	private void setBounds() {

		for (int i = 0; i < npoints; i++) {
			if (xpoints[i] > maxX) {
				maxX = xpoints[i];
			}
			if (xpoints[i] < minX) {
				minX = xpoints[i];
			}
			if (ypoints[i] > maxY) {
				maxY = ypoints[i];
			}
			if (ypoints[i] < minY) {
				minY = ypoints[i];
			}
		}
	}

	private void setDFromCenter() {
		dFromCenterX = new int[npoints];
		dFromCenterY = new int[npoints];

		for (int i = 0; i < npoints; i++) {
			dFromCenterX[i] = (int) (xpoints[i] - center.getX());
			dFromCenterY[i] = (int) (ypoints[i] - center.getY());
		}

	}

	public void draw(Graphics g) {
		g.setColor(shapeColor);
		g.fillPolygon(xpoints, ypoints, npoints);
	}

	public void drawShapeBoundary(Graphics g) {
		g.setColor(Color.red);
		MyMap map = Main.app.screenMap;
		Rectangle a = getNodeSpaceBounds(map);
		g.drawRect((int) a.getX() * map.getNodeW() + 2,
				(int) a.getY() * map.getNodeH() + 2,
				(int) a.getWidth() * map.getNodeW(),
				(int) a.getHeight() * map.getNodeH());
	}

	public void move() {
		if (path.isEmpty()) {
			return;
		}
		Point p = path.get(path.size() - 1);
		double moveX = p.getX(), moveY = p.getY(), moveHyp = Math.sqrt(Math
				.pow(moveX, 2) + Math.pow(moveY, 2));
		double moveSpeed = 5, msx = 0, msy = 0, theta;

		msx = moveSpeed * moveX / moveHyp;
		msy = moveSpeed * moveY / moveHyp;
		if (moveX < center.getX()) {
			msx *= -1;
			if (Math.abs(moveX - center.getX()) < moveSpeed) {
				msx = 0;
			}
		}
		if (moveY < center.getY()) {
			msy *= -1;
			if (Math.abs(moveY - center.getY()) < moveSpeed) {
				msy = 0;
			}
		}

		translate(msx, msy);
		if (centeredOn(path.get(path.size() - 1))) {
			path.remove(path.size() - 1);
			if (path.isEmpty()) {
				Main.app.clearMutex();// signal actions are available
			}
		}
	}

	private boolean centeredOn(Point p) {
		return (Math.abs(center.getX() - p.getX()) < 5 && Math.abs(center
				.getY() - p.getY()) < 5);
	}

	public void select() {
		if (selected) {
			shapeColor = fullShapeColor;// to prevent color drift on many clicks
		} else {
			shapeColor = shapeColor.darker();
			shapeColor = shapeColor.darker();
			shapeColor = shapeColor.darker();
		}
		selected = !selected;
	}

	public void select(boolean state) {
		selected = !state;
		select();
	}

	public void setColor(Color color) {
		fullShapeColor = color;
		shapeColor = color;
		if (selected) {
			shapeColor = shapeColor.darker();
			shapeColor = shapeColor.darker();
			shapeColor = shapeColor.darker();
		}
	}

	public double getArea() {
		double area = 0;
		for (int i = 0; i < npoints; i++) {
			area += (xpoints[i] + xpoints[(i + 1) % npoints])
					* (ypoints[i] - ypoints[(i + 1) % npoints]);
		}
		return Math.abs(area);
	}

	public double getSurfaceArea() {
		double SA = 0;
		for (int i = 0; i < npoints - 1; i++) {
			SA += MyMath.trueDistance(xpoints[i], ypoints[i], xpoints[(i + 1)
					% npoints], ypoints[(i + 1) % npoints]);
		}
		return SA;
	}

	public Rectangle getNodeSpaceBounds(MyMap map) {
		int tMinX = minX, tMaxX = maxX, tMinY = minY, tMaxY = maxY;
		// convert absolute points into node points
		tMinX -= tMinX % map.getNodeW();// truncate to the left edge
		tMinX /= map.getNodeW();// convert to nodeSpace
		tMaxX -= tMinX % map.getNodeW(); // rightmost
		tMaxX /= map.getNodeW();
		tMinY -= tMinY % map.getNodeH();// truncate to top
		tMinY /= map.getNodeH();
		tMaxY -= tMaxY % map.getNodeH();// bottom most
		tMaxY /= map.getNodeH();
		return new Rectangle(tMinX, tMinY, tMaxX - tMinX + 1, tMaxY - tMinY + 1);
	}

	public boolean getselected() {
		return selected;
	}

	public int getNPoints() {
		return npoints;
	}

	public int[] getXPoints() {
		return xpoints;
	}

	public int[] getYPoints() {
		return ypoints;
	}

	public void setPath(ArrayList<Point> temp) {
		this.path = temp;
	}

	public Point getCenter() {
		return center;
	}

	public Color getColor() {
		return fullShapeColor;
	}

	public int getMinX() {
		return minX;
	}

	public int getMinY() {
		return minY;
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMaxY() {
		return maxY;
	}

	public int[] getDFromCenterX() {
		return dFromCenterX;
	}

	public int[] getDFromCenterY() {
		return dFromCenterY;
	}
}
