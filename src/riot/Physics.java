package riot;

public abstract class Physics {
	int x;
	int y;
	int rotation;
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getRotation() {
		return rotation;
	}
	
	public abstract void step();
}
