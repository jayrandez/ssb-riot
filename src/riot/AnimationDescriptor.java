package riot;

import java.io.Serializable;

public class AnimationDescriptor implements Serializable {
	private static final long serialVersionUID = 7113223641101877085L;
	
	String animationName;
	int originX;
	int originY;
	int width;
	int height;
	int centerX;
	int centerY;
	int frames;
	int speed;
	boolean transparent;
	
	public String toString() {
		return animationName;
	}
}
