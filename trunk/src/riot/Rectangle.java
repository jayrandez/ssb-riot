package riot;

public class Rectangle {
	public double x;
	public double y;
	public double width;
	public double height;
	
	public Rectangle(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public double minX() {
		return x;
	}
	
	public double minY() {
		return y;
	}
	
	public double maxX() {
		return x + width - 1;
	}
	
	public double maxY() {
		return y + height - 1;
	}
}
