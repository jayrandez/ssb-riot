package riot;

public class AnimationDescriptor {
	private static final long serialVersionUID = 7113223641101877085L;
	
	public String animationName;
	public int originX;
	public int originY;
	public int width;
	public int height;
	public int centerX;
	public int centerY;
	public int frames;
	public int speed;
	public boolean transparent;
	
	public String toString() {
		return animationName;
	}
}
