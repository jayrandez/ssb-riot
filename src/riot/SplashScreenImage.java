package riot;

public class SplashScreenImage extends GameObject {

	public SplashScreenImage(GameEngine engine, SpriteManager manager) {
		super(engine, manager, new Point(0,0), new Size(640,480));
		setAnimation("splashscreen", "splashscreen");
	}
}
