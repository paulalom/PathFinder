package myMath;

import engine.Main;
import graphicsObjects.PathingShape;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.PriorityQueue;

import map.MyMap;
import map.PathingNode;

public class MyMath {
	public static double getSlope(Point p1, Point p2) {
		if (-.05 < p1.y - p2.y && p1.y - p2.y < .05) {
			return -42424242;// a number that will never naturally occur
		} else {
			return (double) (p1.x - p2.x) / (double) (p1.y - p2.y);
		}
	}

	public static double getStdDev(double[] vals) {
		double av = getMean(vals);
		double temp = 0;
		for (int i = 0; i < vals.length; i++) {
			temp += Math.pow((av - vals[i]), 2);
		}
		return Math.sqrt(temp / (double) vals.length);
	}

	public static double getMean(double[] vals) {
		return sum(vals) / (double) vals.length;
	}

	public static double sum(double[] nums) {
		double total = 0;
		for (int i = 0; i < nums.length; i++) {
			total += nums[i];
		}
		return total;
	}

	public static int sum(int[] nums) {
		int total = 0;
		for (int i = 0; i < nums.length; i++) {
			total += nums[i];
		}
		return total;
	}

	public static double trueDistance(Point p1, Point p2) {
		return trueDistance(p1.x, p1.y, p2.x, p2.y);
	}

	public static double trueDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
	}

	public static double distanceH(Point p1, Point p2) {
		return distanceH(p1.x, p1.y, p2.x, p2.y);
	}

	public static double distanceH(int x1, int y1, int x2, int y2) {
		return Math.abs((x1 - x2) + (y1 - y2));
	}

	public static Point getCenter(PathingShape shape) {
		return new Point(sum(shape.xpoints) / shape.npoints, sum(shape.ypoints)
				/ shape.npoints);
	}

	public static boolean checkShapeIntersection(Point p1, Point p2,
			ArrayList shapeList) {
		return checkShapeIntersection((int) p1.getX(), (int) p1.getY(),
				(int) p2.getX(), (int) p2.getY(), shapeList);
	}

	// line-shape intersection
	public static boolean checkShapeIntersection(int x1, int y1, int x2,
			int y2, ArrayList shapeList) {
		PathingShape temp;
		int[] xpoints, ypoints;
		int x3, y3, x4, y4, npoints;
		for (int i = 0; i < shapeList.size(); i++) {
			temp = ((PathingShape) shapeList.get(i));
			xpoints = temp.getXPoints();
			ypoints = temp.getYPoints();
			npoints = temp.getNPoints();

			for (int j = 0; j < npoints; j++) {
				x3 = xpoints[j];
				x4 = xpoints[(j + 1) % npoints];
				y3 = ypoints[j];
				y4 = ypoints[(j + 1) % npoints];
				if (Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
					return true;
				}
			}
		}
		return false;
	}

	// point-shape intersection
	public static boolean checkShapeIntersection(int x, int y,
			ArrayList shapeList) {
		PathingShape temp;
		for (int i = 0; i < shapeList.size(); i++) {
			temp = (PathingShape) shapeList.get(i);
			if (temp.contains(x, y)) {// the clicked spot is inside another
				// shape
				return true;
			}
		}
		return false;
	}

	public static Point[] getShapePoints(int[] xpoints, int[] ypoints,
			int npoints) {
		Point[] points = new Point[npoints];
		for (int i = 0; i < npoints; i++) {
			points[i] = new Point(xpoints[i], ypoints[i]);
		}
		return points;
	}

	public static ArrayList<Point> pathTo(PathingShape shape, int x, int y,
			MyMap map, ArrayList collidingShapeList) {
		if (shape == null) {
			System.out.println("No shape selected");
			return null;
		}
		double tempGScore;
		boolean tempScoreIsBetter;
		// source point is
		PathingNode curNode = new PathingNode(shape.getCenter().x,
				shape.getCenter().y, map.getNodeW(), map.getNodeH());
		// pick one of the points of the shape to use as a source
		PathingNode dest = new PathingNode(x, y, map.getNodeW(), map.getNodeH());
		PathingNode[] nodeMap = new PathingNode[map.getNumNodeW()
				* map.getNumNodeH() + 1];
		PriorityQueue<PathingNode> openSet = new PriorityQueue<PathingNode>();

		curNode.setGScore((double) 0);
		curNode.setHScore(trueDistance(curNode.getX(), curNode.getY(),
				dest.getX(), dest.getY()));
		curNode.setFScore(curNode.getGScore() + curNode.getHScore());
		curNode.setOpen(true);
		nodeMap[curNode.getX() + curNode.getY() * map.getNumNodeW()] = curNode;
		openSet.add(curNode);

		PathingNode neighbour = null;
		int count = 0;
		while (!openSet.isEmpty()) {

			curNode = openSet.poll();
			curNode.setOpen(false);
			curNode.setClosed(true);

			if (curNode.getX() == dest.getX() && curNode.getY() == dest.getY()) {
				System.out.println("cur = dest, iterations = " + count);
				System.out.println("curNode = " + curNode.getX() + ", "
						+ curNode.getY());
				return reconstructPath(curNode, map.getNodeW(), map.getNodeH(),
						nodeMap, map);
			}
			count++;

			// getNeighbour
			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++) {

					if (i == 0 && j == 0) {
						continue;// dont include self in neighbours
					}
					if (i == -1 && curNode.getX() == 0) {
						continue;// beyond left side of screen; dont check
					} else if (i == 1
							&& curNode.getX() == map.getNumNodeW() - 1) {
						continue;// right
					}
					if (j == -1 && curNode.getY() == 0) {
						continue;// top
					} else if (j == 1
							&& curNode.getY() == map.getNumNodeH() - 1) {
						continue;// bottom
					}

					neighbour = nodeMap[(curNode.getX() + i)
							+ (curNode.getY() + j) * map.getNumNodeW()];

					if (neighbour == null) {// create neighbour and add to map
						neighbour = new PathingNode((curNode.getX() + i)
								* map.getNodeH(), (curNode.getY() + j)
								* map.getNodeW(), map.getNodeW(),
								map.getNodeH());
						nodeMap[(curNode.getX() + i) + (curNode.getY() + j)
								* map.getNumNodeW()] = neighbour;
					}
					if (neighbour.isClosed()) {
						continue;
					}
					// gets the distance between the two points and checks for
					// any
					// obstructions
					tempGScore = getGScore(curNode, neighbour, shape, map);
					if (tempGScore == -1) {
						neighbour.setClosed(true);
						continue;
					} else if (!neighbour.isOpen()) {
						// neighbour is new, so set it.
						tempScoreIsBetter = true;
					} else if (tempGScore < neighbour.getGScore()) {
						tempScoreIsBetter = true;
					} else {
						tempScoreIsBetter = false;
					}

					if (tempScoreIsBetter) {
						neighbour.setCameFrom(curNode);
						neighbour.setGScore(tempGScore);
						neighbour.setHScore(trueDistance(neighbour.getX(),
								neighbour.getY(), dest.getX(), dest.getY()));
						neighbour.setFScore((neighbour.getGScore() + neighbour
								.getHScore()));
						if (!neighbour.isOpen()) {
							openSet.add(neighbour);
							neighbour.setOpen(true);
						} else {
							openSet.remove(neighbour);
							openSet.add(neighbour);
							// reinsert neighbour into priority queue
						}
					}
				}
			}
		}
		System.out.println("iterations = " + count);
		System.out.println("No path found");
		return null;
	}

	private static double getGScore(PathingNode curNode, PathingNode neighbour,
			PathingShape shape, MyMap map) {
		// possible optimization in checking only
		// the points on the side of the //
		// shape in the direction moving, but
		// thats for later
		// dirX motion: -1 is <-, 1 is ->, 0 is none
		// int dirX = Double.compare(neighbour.getX(), curNode.getX());
		// int dirY = Double.compare(neighbour.getY(), curNode.getY());

		// nodeSpaceBounds
		Rectangle nSB = shape.getNodeSpaceBounds(map);
		for (int i = 0; i < nSB.getWidth() * nSB.getHeight(); i++) {
			if(map.get((int) Math.min(
					Math.max((neighbour.getX() - nSB.getWidth() / 2), 0) + i
							% nSB.getWidth(), map.getNumNodeW() - 1),
					(int) Math.min(
							Math.max((neighbour.getY() - nSB.getHeight() / 2),
									0) + i / nSB.getWidth(),
							map.getNumNodeH() - 1))){
				return -1;
			}
		}
		
		return curNode.getGScore()
				+ trueDistance(curNode.getX(), curNode.getY(),
						neighbour.getX(), neighbour.getY());
	}

	/*
	 * private static ArrayList<PathingNode> getNeighbours(PathingNode curNode,
	 * int nodeW, int nodeH) { ArrayList<PathingNode> returnList = new
	 * ArrayList<PathingNode>(); int newX, newY; for (int i = -1; i <= +1; i++)
	 * { for (int j = -1; j <= +1; j++) { if (i == 0 && j == 0) { continue;//
	 * dont include self in neighbours } newX = curNode.getX() + nodeW * i; newY
	 * = curNode.getY() + nodeH * j; returnList.add(new PathingNode(new
	 * Point(newX, newY))); } } return returnList; }
	 */

	private static ArrayList<Point> reconstructPath(PathingNode curPathNode,
			int nodeW, int nodeH, PathingNode[] nodeMap, MyMap nodeMeta) {
		ArrayList<Point> path = new ArrayList<Point>();
		int tempX, tempY;
		while (true) {
			path.add(new Point(curPathNode.getX() * nodeW + nodeW / 2,
					curPathNode.getY() * nodeH + nodeH / 2));
			tempX = curPathNode.getPrevX();
			tempY = curPathNode.getPrevY();
			if (tempX == -1) {// if one is empty both will be
				break;
			}
			curPathNode = nodeMap[tempX + nodeMeta.getNumNodeW() * tempY];
		}
		return path;
	}

	/*
	 * for now I'm putting aside shape normalization... It accomplishes its goal
	 * of keeping the number of points used in a shape to a minimum while still
	 * allowing customization, however it still needs improvement in the metrics
	 * for number of points and shape size corners especially need some work
	 */
	public static void normalizeShape(ArrayList<Point> list) {
		double[] dxpoints = new double[list.size()];
		double[] dypoints = new double[list.size()];
		double stdDevDX, stdDevDY;
		Point p1, p2, p3;
		double dx1, dx2, dy1, dy2;

		// find stdDevDx, stdDevDy
		for (int i = 0; i < list.size(); i++) {
			p1 = list.get(i);
			p2 = list.get((i + 1) % list.size());
			dxpoints[i] = p1.x - p2.x;
			dypoints[i] = p1.y - p2.y;
		}
		stdDevDX = getStdDev(dxpoints);
		stdDevDY = getStdDev(dypoints);
		// System.out.println("stdDevDX = " + stdDevDX + ", stdDevDY = "
		// + stdDevDY);
		int n;
		for (int i = 0; i < list.size(); i++) {
			n = list.size();
			p1 = list.get(i);
			p2 = list.get((i + 1) % n);
			p3 = list.get((i + 2) % n);
			dx1 = p1.x - p2.x;
			dx2 = p2.x - p3.x;
			dy1 = p1.y - p2.y;
			dy2 = p2.y - p3.y;
			if (Math.abs(dx1 - dx2) < stdDevDX * Math.log(n / 1.618)
					&& Math.abs(dy1 - dy2) < stdDevDY * Math.log(n / 1.618)) {
				// m1 = m2 +/- minDev, 1.618; the golden ratio for no reason
				Main.app.floatingPrint(p2.x, p2.y, "Points too linear");
				list.remove(list.indexOf(p2));
				p2 = null;
				if (n > 2) {
					i--;
				} else {
					break;
				}
			}
		}
		System.out.println("New shape has " + list.size() + " points");
	}

	public static boolean checkShapeEncapsulation(ArrayList shapeList,
			Polygon shape) {
		Polygon existingShape;
		for (int i = 0; i < shapeList.size(); i++) {
			existingShape = (Polygon) shapeList.get(i);
			for (int j = 0; j < existingShape.npoints; j++) {
				if (shape.contains(existingShape.xpoints[j],
						existingShape.ypoints[j])) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * public static ArrayList<Point> pathTo(PathingShape selectedShape, int x,
	 * int y, MyMap map) { // TODO Auto-generated method stub return null; }
	 */
}
