package graphicsObjects;
import java.awt.*;

public class PathingTextField extends Rectangle {
	protected Color shapeColor = new Color(50, 50, 255);
	protected Color textColor = new Color(255, 255, 50);
	protected boolean pressed;
	protected String text = "Hello.";
	protected Integer textWidth, textHeight;
	
	
	public PathingTextField(int x, int y, int l, int w, String text, Color shapeColor){
		super(x, y, l, w);
		this.text = text;
		this.shapeColor = shapeColor;
	}
	
	public void draw(Graphics g){
		g.setColor(shapeColor);
		g.fillRect(x, y, width, height);
	}
	
	public void drawText(Graphics g) {
		//width required is (total width space - text width space)/2
		if(textWidth == null){
			textWidth = (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
		}
		if(textHeight == null){
			textHeight = (int) g.getFontMetrics().getStringBounds(text, g).getHeight();
		}
		//System.out.println(sortX.get(0) + ", " + sortY.get(0) + ", " + shapeWidth + ", " + shapeHeight + ", " + textWidth + ", " + textHeight);// 17, 15
		g.setColor(textColor);//sortX and sortY is the sorted values of the x,y coords
		g.drawString(text, (x + (width - textWidth)/2), (y + (height - textHeight)/2 + textHeight));
		//Wtf, drawstring draws from bottom left of string
	}
	
	public void setText(String text){
		this.text = text;
	}
	public String getText(){
		return text;
	}
	public void setColor(Color color){
		shapeColor = color;
	}
	public Color getColor(){
		return shapeColor;
	}
}
