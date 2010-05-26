package riot;

import java.io.*;

public class Rectangle implements Cloneable {
	public double x;
	public double y;
	public double width;
	public double height;
	
	public Rectangle(Point origin, Size size) {
		this.x = origin.x;
		this.y = origin.y;
		this.width = size.width;
		this.height = size.height;
	}
	
	public Rectangle(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public Rectangle(DataInputStream stream) throws IOException {
		this.x = stream.readShort();
		this.y = stream.readShort();
		this.width = stream.readShort();
		this.height = stream.readShort();
	}
	
	public void writeTo(DataOutputStream stream) throws IOException {
		stream.writeShort((int)x);
		stream.writeShort((int)y);
		stream.writeShort((int)width);
		stream.writeShort((int)height);
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
	    if(this.minX() > other.maxX())
	    	return false;
	    return true;

	}
	
	public void forceRatio(double ratio) {
		double currentRatio = width / height;
		if(currentRatio > ratio)
			height = width / ratio;
		else
			width = height * ratio;
	}
	
	public void forceRatioKeepCentered(double ratio) {
		double centerX = x + width / 2.0;
		double centerY = y + height / 2.0;
		forceRatio(ratio);
		this.x = centerX - width / 2.0;
		this.y = centerY - height / 2.0;
	}
	
	public Size getSize() {
		return new Size(width, height);
	}
	
	public double area() {
		return width * height;
	}
	
	public Object clone() {
		return new Rectangle(x, y, width, height);
	}
}
