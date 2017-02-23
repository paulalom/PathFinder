package graphicsObjects;
import java.awt.Color;
import java.awt.Graphics;

public class FloatingText {
	private String text;
	private int lifeTime, lifeRemaining, red = 255, green = 255, blue = 0,
			freq;
	private boolean direction = false, alive = true;
	private double x, y, alpha = 255;

	public FloatingText(int x, int y, String text, int lifeTime, int freq) {
		this.text = text;
		this.lifeRemaining = lifeTime;
		this.lifeTime = lifeTime;
		this.freq = freq;
		this.x = x;
		this.y = y;
	}

	public void move() {
		if (direction) {
			x = x - 0.5;
			y = y - 0.5;
		} else {
			x = x + 0.5;
			y = y - 0.5;
		}
		if (lifeRemaining % (lifeTime / 4) == 0) {// just some arbitrary timing
			direction = !direction;
		}
		alpha = (int) Math.max((alpha - ((double) 255 / (double) lifeTime)), 0);
		// will be ~invisible by the end
		lifeRemaining--;
		if (lifeRemaining <= 0) {
			alive = false;
		}
	}

	public void draw(Graphics g) {
		g.setColor(new Color(red, green, blue, (int) alpha));
		g.drawString(text, (int) x, (int) y);
	}

	public boolean isAlive() {
		return alive;
	}
}
