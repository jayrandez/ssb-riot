package riot;

public class Label extends GameObject {
	int index;
	FontManager manager;
	String text;
	boolean centered;

	public Label(GameEngine engine, FontManager manager, Point location, String font) {
		super(engine, null, location, new Size(0,0));
		this.manager = manager;
		this.index = manager.getIndex(font);
		this.text = "";
		this.centered = Riot.LeftAlign;
	}
	
	public Label(GameEngine engine, FontManager manager, Point location, String font, String text) {
		super(engine, null, location, new Size(0,0));
		this.manager = manager;
		this.index = manager.getIndex(font);
		this.text = text;
		this.centered = Riot.LeftAlign;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Sprite getSprite() {
		return new TextSprite(manager, index, text, (int)getLocation().x, (int)getLocation().y);
	}
}
