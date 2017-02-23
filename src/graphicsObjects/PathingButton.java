package graphicsObjects;
import java.awt.*;

public class PathingButton extends PathingTextField {
	
	private Color fullShapeColor = new Color(50, 50, 255);
	private boolean pressed;

	public PathingButton(int x, int y, int l, int w, String text, Color shapeColor){
		super(x, y, l, w, text, shapeColor);
		fullShapeColor = shapeColor;
	}

	public void press() {
		if (pressed) {
			shapeColor = fullShapeColor;// to prevent color drift on many clicks
		} else {
			shapeColor = shapeColor.darker();
			shapeColor = shapeColor.darker();
			shapeColor = shapeColor.darker();
		}
		pressed = !pressed;
	}

	public void press(boolean state) {
		pressed = !state;
		press();
	}

	public boolean getPressed() {
		return pressed;
	}
}
