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
	
	public int minX() {
		return (int)x;
	}
	
	public int minY() {
		return (int)y;
	}
	
	public int maxX() {
		return (int)(x + width - 1);
	}
	
	public int maxY() {
		return (int)(y + height - 1);
	}
	
	public boolean overlaps(Rectangle other) {
		if(this.maxY() < other.minY())
			return false;
	    if(this.minY() > other.maxY())
	    	return false;
	    if(this.maxX() < other.minX())
	    	return false;
	    if(this.minX() > other.maxY())
	    	return false;
	    return true;

	}
}
