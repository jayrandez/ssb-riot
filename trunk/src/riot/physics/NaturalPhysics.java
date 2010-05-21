package riot.physics;

import java.util.ArrayList;

import riot.*;

public class NaturalPhysics extends AnimationPhysics {
	double width;
	double height;
	double gravity;

	public NaturalPhysics(SpriteManager manager, Location location, double width, double height, double gravity) {
		super(manager, location);
	}

	@Override
	public ArrayList<Rectangle> getBoundingBoxes() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
