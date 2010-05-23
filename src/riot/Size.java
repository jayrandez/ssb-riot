package riot;

import java.io.*;

public class Size {
	public double width;
	public double height;
	
	public Size(double width, double height) {
		this.width = width;
		this.height = height;
	}
	
	public Rectangle toRectangle() {
		return new Rectangle(0, 0, width, height);
	}
	
	public Size(DataInputStream stream) throws IOException {
		this.width = stream.readShort();
		this.height = stream.readShort();
	}

	public void writeTo(DataOutputStream stream) throws IOException {
		stream.writeShort((int)width);
		stream.writeShort((int)height);
	}
}
