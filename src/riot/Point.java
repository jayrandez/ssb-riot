package riot;

import java.io.*;

public class Point implements Cloneable {
	public double x;
	public double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(DataInputStream stream) throws IOException {
		this.x = stream.readShort();
		this.y = stream.readShort();
	}

	public void writeTo(DataOutputStream stream) throws IOException {
		stream.writeShort((int)x);
		stream.writeShort((int)y);
	}
	
	public Object clone() {
		return new Point(x, y);
	}
}
