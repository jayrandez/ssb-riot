package riot;

/**
 * The descriptor class for a sprite animation which describes the animations frames
 * or otherwise every facet of the animation
 * Note that even static sprites are treated as animations with a single frame.
 */
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
	public boolean repeat;
	
	public String toString() {
		return animationName;
	}
}
