package map;

import java.awt.Graphics;
import java.awt.Point;
import engine.Main;

public class PathingNode implements Comparable<PathingNode> {
	private int prevX = -1, prevY = -1;
	private int x, y;
	private double gScore = 0, hScore = 0, fScore = 0;
	private boolean closed, open;

	public PathingNode(int x, int y, int w, int h) {
		// save memory by not storing constant values of w,h in multiple
		// places.. anything looking for w,h can check map

		// truncate point to top left corner of current grid position by diving
		// by w,h
		this.x = (int) (x / w);
		this.y = (int) (y / h);
	}

	public double getGScore() {
		return gScore;
	}

	public double getHScore() {
		return hScore;
	}

	public double getFScore() {
		return fScore;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isOpen() {
		return open;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setOpen(boolean o) {
		open = o;
	}

	public void setClosed(boolean c) {
		closed = c;
	}

	public void setGScore(double score) {
		this.gScore = score;
	}

	public void setHScore(double score) {
		this.hScore = score;
	}

	public void setFScore(double score) {
		this.fScore = score;
	}

	public int getPrevX() {
		return prevX;
	}

	public int getPrevY() {
		return prevY;
	}

	public void setCameFrom(PathingNode prev) {
		prevX = prev.getX();
		prevY = prev.getY();
	}

	public void setCameFrom(int x, int y) {
		prevX = x;
		prevY = y;
	}

	@Override
	public int compareTo(PathingNode arg0) {
		// TODO Auto-generated method stub
		return Double.compare(fScore, arg0.getFScore());
	}
}
