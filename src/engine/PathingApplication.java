package engine;

import java.awt.*;
import java.util.*;
import graphicsObjects.*;
import map.MyMap;
import myMath.MyMath;

/*
 * Paul Lomenda's first attempt at a pathfinding algorithm, with much unnecessary stuff
 */

public class PathingApplication implements Runnable {

	private Thread th;

	private PathingButton pressedButton;
	private PathingShape selectedShape = null;
	private PathFrame frame = new PathFrame(this);
	private ArrayList<Point> newPoints = new ArrayList<Point>();
	private ArrayList<FloatingText> ftList = new ArrayList<FloatingText>();
	private ArrayList<PathingShape> pathShapeList = new ArrayList<PathingShape>();
	private ArrayList<PathingButton> pathButtonList = new ArrayList<PathingButton>();
	private ArrayList<PathingTextField> pathTextFieldList = new ArrayList<PathingTextField>();
	private Color shapeColor = Color.blue;
	private int freq = 40, ftLifeTime = 150; // the delay time between cycles in
												// ms
	private boolean drawGrid, drawShapeBoundary, pathingMutex = true;
	public MyMap screenMap;

	public void init() {
		pathButtonList.add(// 0
				new PathingButton(0, 10, 100, 30, "Create Shape", shapeColor));
		pathButtonList.add(// 1
				new PathingButton(0, 50, 100, 30, "Finish Shape", shapeColor));
		pathButtonList.add(// 2
				new PathingButton(0, 90, 100, 30, "Select Shape", shapeColor));
		pathButtonList.add(// 3
				new PathingButton(0, 130, 100, 30, "Path To", shapeColor));
		pathButtonList.add(// 4
				new PathingButton(0, 170, 100, 30, "Delete Shape", shapeColor));
		pathButtonList.add(// 5
				new PathingButton(0, 210, 100, 30, "Clear Points", shapeColor));
		pathButtonList.add(// 6 a temporary solution
				new PathingButton(0, 250, 100, 30, "Change Color", shapeColor));
		pathButtonList.add(// 7
				new PathingButton(0, 290, 100, 30, "Draw Grid", shapeColor));
		pathButtonList.add(// 8
				new PathingButton(0, 330, 150, 30, "Draw Shape Boundary",
						shapeColor));
		pathTextFieldList.add(// 1 An example color block
				new PathingTextField(120, 250, 30, 30, " ", shapeColor));

		screenMap = new MyMap(frame.getWidth(), frame.getHeight(), 30, 30);
		// map any smaller than 25x25 nodes is too inefficient, will work on
		// that... map nodes too large means small objects may not be counted in
		// the map at all if they do not contain a center of node
		start();
	}

	public boolean pathMouseDown(Event e, int x, int y) {
		if (pathingMutex) {
			if (tryButtons(x, y)) {
				return true;
			}
			if (pressedButton != null) {
				if (pressedButton.getText().equals("Create Shape")) {
					placePoints(x, y);
				} else if (pressedButton.getText().equals("Select Shape")) {
					selectShape(x, y);
				} else if (pressedButton.getText().equals("Path To")) {
					startPathTo(x, y, screenMap);
				}
			}
		}
		return false;
	}

	public void clearMutex() {
		pathingMutex = true;
		selectedShape.select(false);
		pathShapeList.add(new PathingShape(selectedShape.getXPoints().clone(),
				selectedShape.getYPoints().clone(), selectedShape.getColor()));
		selectedShape = pathShapeList.get(pathShapeList.size() - 1);
		selectedShape.select(true);
		screenMap.toggleShape(selectedShape, true);
		pressedButton.press(false);
		pressedButton = null;
	}

	private void startPathTo(int x, int y, MyMap map) {
		if (selectedShape == null) {
			floatingPrint(x, y, "Error: no shape selected.");
			return;
		}
		map.toggleShape(selectedShape, false);
		System.out.println(map.toString());
		pathShapeList.remove(selectedShape);
		ArrayList<Point> temp = MyMath.pathTo(selectedShape, x, y, map,
				getPathShapeList());
		if (temp == null) {
			floatingPrint(x, y, "Pathing Failed.");
			pathShapeList.add(selectedShape);
			map.toggleShape(selectedShape, true);
		} else {
			selectedShape.setPath(temp);
			pathingMutex = false;
			System.out.println("Path set: " + temp);
		}
	}

	private boolean tryButtons(int x, int y) {
		if (tryPathingButtons(x, y)) {// a button is pressed (possibly off)
			if (pressedButton != null) {
				if (!newPoints.isEmpty()
						&& pressedButton.getText().equals("Finish Shape")) {
					if (pathShapeList.size() == 256) {
						floatingPrint((int) newPoints.get(newPoints.size() - 1)
								.getX(),
								(int) newPoints.get(newPoints.size() - 1)
										.getY(),
								"Error: Max shapes allowed reached.");
						// this will never happen, but just incase..
						return true;
					}
					if (checkSize(createShape(newPoints), 25)) {
						// prevent invisible or nearly invisible shapes
						floatingPrint((int) newPoints.get(newPoints.size() - 1)
								.getX(),
								(int) newPoints.get(newPoints.size() - 1)
										.getY(), "Error: New shape too small!");
						newPoints.clear();
						return true;
					}
					MyMath.normalizeShape(newPoints);
					if (MyMath.checkShapeEncapsulation(
							getPathShapeList(),
							new Polygon(getAllValInt(newPoints, "x"),
									getAllValInt(newPoints, "y"), newPoints
											.size()))) {
						// make sure shape doesnt contain any old shapes on
						// creation
						floatingPrint((int) newPoints.get(newPoints.size() - 1)
								.getX(),
								(int) newPoints.get(newPoints.size() - 1)
										.getY(),
								"Error: New shape encapsulates old one!");
						newPoints.clear();
					} else {
						createShape();
					}
				} else if (selectedShape != null
						&& pressedButton.getText().equals("Delete Shape")) {
					getPathShapeList().remove(
							getPathShapeList().indexOf(selectedShape));
					deleteShape(selectedShape);
					selectedShape = null;
				} else if (pressedButton.getText().equals("Clear Points")) {
					newPoints.clear();
				}
			}
			return true;
		}
		return false;
	}

	private void deleteShape(PathingShape shape) {
		screenMap.toggleShape(shape, false);
		shape = null;
	}

	private boolean checkSize(PathingShape shape, int i) {
		// TODO Auto-generated method stub
		// temp size function, will be improved
		if (shape == null) {
			return true;
		}
		if (shape.getNPoints() < 3) {
			return true;
		}
		if (shape.getArea() / shape.getSurfaceArea() < 10) {
			return true;
		}

		return false;
	}

	private boolean tryPathingButtons(int x, int y) {
		PathingButton temp;
		for (int i = 0; i < pathButtonList.size(); i++) {
			temp = pathButtonList.get(i);
			if (temp.contains(x, y)) {
				if (temp.getText().equals("Change Color")) {
					cycleColor();
				} else if (temp.getText().equals("Draw Grid")) {
					drawGrid = !drawGrid;
				} else if (temp.getText().equals("Draw Shape Boundary")) {
					drawShapeBoundary = !drawShapeBoundary;
				} else {
					temp.press();
					switchButton(temp);
				}
				return true;
			}
		}
		return false;
	}

	private void switchButton(PathingButton temp) {
		if (pressedButton == null) {
			pressedButton = temp;
		} else {
			if (temp.getPressed()) {// pressing on now
				pressedButton.press();// turn off old button
				pressedButton = temp;// set new pressedButton
			} else {// pressing button off
				pressedButton = null;// clear pressedButton
			}
		}
	}

	private void selectShape(int x, int y) {
		PathingShape temp;
		for (int i = 0; i < getPathShapeList().size(); i++) {
			temp = (PathingShape) getPathShapeList().get(i);
			if (temp.contains(x, y)) {
				if (selectedShape != temp) {
					if (selectedShape != null) {
						selectedShape.select();
					}
					selectedShape = temp;
				} else {
					selectedShape = null;
				}
				temp.select();
				return;
			}
		}
	}

	private void placePoints(int x, int y) {
		if (!newPoints.isEmpty()
				&& MyMath.checkShapeIntersection(x, y,
						(int) newPoints.get(newPoints.size() - 1).getX(),
						(int) newPoints.get(newPoints.size() - 1).getY(),
						getPathShapeList())) {
			floatingPrint(x, y, "Error: Shape Intersection!");
		} else if (MyMath.checkShapeIntersection(x, y, getPathShapeList())) {
			// compare point with itself to see if it is inside of any shape
			floatingPrint(x, y, "Error: Shape Intersection!");
		} else {
			newPoints.add(new Point(x, y));
		}
	}

	private void createShape() {
		PathingShape temp = createShape(newPoints, shapeColor);
		if (temp != null) {
			getPathShapeList().add(temp);
			newPoints.clear();
		} else {
			System.out.println("error creating shape");
		}
	}

	private PathingShape createShape(ArrayList<Point> points) {
		return createShape(points, shapeColor);
	}

	private PathingShape createShape(ArrayList<Point> points, Color color) {
		if (newPoints.size() < 3) {
			System.out
					.println("Cannot create a shape with less than 3 points.");
			return null;
		}
		// int[] tempx = newPointsX.toArray(new int[0]);
		// doesnt work so I made my own
		int[] tempX = getAllValInt(points, "x");
		int[] tempY = getAllValInt(points, "y");
		PathingShape temp = new PathingShape(tempX, tempY, color);
		screenMap.toggleShape(temp, true);
		return temp;
	}

	private int[] getAllValInt(ArrayList<Point> list, String val) {
		int[] temp = new int[list.size()];
		int i = 0;
		for (Point p : list) {
			if (val.equals("x")) {
				temp[i] = (int) p.getX();
			} else if (val.equals("y")) {
				temp[i] = (int) p.getY();
			} else {
				System.out.println("Yarr, Errarrrrr");
			}
			i++;
		}
		return temp;
	}

	private double[] getAllValDouble(ArrayList<Point> list, String val) {
		double[] temp = new double[list.size()];
		int i = 0;
		for (Point p : list) {
			if (val.equals("x")) {
				temp[i] = (double) p.getX();
			} else if (val.equals("y")) {
				temp[i] = (double) p.getY();
			} else {
				System.out.println("Yarr, Errarrrrr");
			}
			i++;
		}
		return temp;
	}

	private void cycleColor() {
		PathingTextField temp = null;
		for (int i = 0; i < pathTextFieldList.size(); i++) {
			temp = pathTextFieldList.get(i);
			if (temp.getText().equals(" ")) {
				break;
			}
		}
		if (temp.getColor() == Color.blue) {
			shapeColor = Color.white;
		} else if (temp.getColor() == Color.white) {
			shapeColor = Color.red;
		} else if (temp.getColor() == Color.red) {
			shapeColor = Color.orange;
		} else if (temp.getColor() == Color.orange) {
			shapeColor = Color.green;
		} else if (temp.getColor() == Color.green) {
			shapeColor = Color.yellow;
		} else {
			shapeColor = Color.blue;
		}
		temp.setColor(shapeColor);
		if (selectedShape != null) {
			selectedShape.setColor(shapeColor);
		}
	}

	public void floatingPrint(int x, int y, String text, int lifeTime, int freq) {
		ftList.add(new FloatingText(x, y, text, lifeTime, freq));
	}

	public void floatingPrint(int x, int y, String text) {
		floatingPrint(x, y, text, ftLifeTime, freq);
	}

	public void drawNewPoints(Graphics g) {
		int[] tempX = getAllValInt(newPoints, "x");
		int[] tempY = getAllValInt(newPoints, "y");

		g.setColor(new Color(255, 255, 50));
		for (int i = 0; i < tempX.length; i++) {
			g.fillOval(tempX[i], tempY[i], 5, 5);
		}
	}

	public void drawPathingButtons(Graphics g) {
		for (int i = 0; i < pathButtonList.size(); i++) {
			(pathButtonList.get(i)).draw(g);
			(pathButtonList.get(i)).drawText(g);
		}
	}

	public void drawPathingTextFields(Graphics g) {
		for (int i = 0; i < pathTextFieldList.size(); i++) {
			(pathTextFieldList.get(i)).draw(g);
			(pathTextFieldList.get(i)).drawText(g);
		}
	}

	public void drawObjs(Graphics g) {
		for (int i = 0; i < getPathShapeList().size(); i++) {
			((PathingShape) getPathShapeList().get(i)).draw(g);
			if (drawShapeBoundary) {
				((PathingShape) getPathShapeList().get(i)).drawShapeBoundary(g);
			}
		}
		if (selectedShape != null) {
			selectedShape.draw(g);
		}
	}

	void drawFT(Graphics g) {
		if (!ftList.isEmpty()) {
			for (FloatingText ft : ftList) {
				ft.draw(g);
			}
		}
	}

	public void drawGrid(Graphics g) {
		if (drawGrid) {
			g.setColor(shapeColor);
			for (int i = 0; i < screenMap.getNumNodeW(); i++) {
				for (int j = 0; j < screenMap.getNumNodeH(); j++) {
					g.drawLine(i * screenMap.getNodeW(), 0,
							i * screenMap.getNodeW(), frame.getHeight());
					g.drawLine(0, j * screenMap.getNodeH(), frame.getWidth(), j
							* screenMap.getNodeH());
				}
			}
		}
	}

	void move() {
		moveFT();
		moveShapes();
	}

	void moveShapes() {
		/*
		 * for (int i = 0; i < getPathShapeList().size(); i++) {
		 * getPathShapeList().get(i).move(); }
		 */
		if (selectedShape != null) {
			selectedShape.move();
		}
	}

	void moveFT() {
		LinkedList<Integer> rmvIndex = new LinkedList<Integer>();
		for (int i = 0; i < ftList.size(); i++) {
			if (ftList.get(i).isAlive()) {
				ftList.get(i).move();
			} else {
				rmvIndex.add(i);
			}
		}
		for (Integer i : rmvIndex) {
			ftList.remove(i);
		}
		rmvIndex.clear();
	}

	public void start() {
		th = new Thread(this);
		th.start();
	}

	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		while (true) {
			move();
			frame.canvas.repaint();
			delay(freq);
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		}
	}

	public void delay(int ms) {
		// sleep for a little bit
		try {
			Thread.sleep(ms);
		}

		catch (InterruptedException ex) {
			// do nothing
		}
	}

	public ArrayList<PathingShape> getPathShapeList() {
		return pathShapeList;
	}
}
