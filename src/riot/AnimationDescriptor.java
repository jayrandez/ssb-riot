package riot;

import java.io.Serializable;

public class AnimationDescriptor implements Serializable {
	String animationName;
	int originX;
	int originY;
	int width;
	int height;
	int centerX;
	int centerY;
	int frames;
	int speed;
	
	public String toString() {
		return animationName;
	}
}
