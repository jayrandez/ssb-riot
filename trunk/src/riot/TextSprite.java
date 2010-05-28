package riot;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TextSprite extends Sprite {
	Font font;
	int index;
	String text;
	int x;
	int y;
	boolean centered;
	Color color;
	
	public TextSprite(FontManager manager, int index, String text, int x, int y) {
		this.index = index;
		this.text = text;
		this.font = manager.getFont(index);
		this.color = manager.getColor(index);
		this.centered = manager.getCentered(index);
		this.x = x;
		this.y = y;
	}
	
	public TextSprite(FontManager manager, DataInputStream stream) throws IOException {
		this.index = stream.readShort();
		this.text = stream.readUTF();
		this.x = stream.readShort();
		this.y = stream.readShort();
		this.font = manager.getFont(index);
		this.color = manager.getColor(index);
		this.centered = manager.getCentered(index);
	}
	
	public void writeTo(DataOutputStream stream) throws IOException {
		stream.writeByte(Riot.TextSprite);
		stream.writeShort(index);
		stream.writeUTF(text);
		stream.writeShort(x);
		stream.writeShort(y);
	}
	
	public void drawTo(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setFont(font);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Color lastColor = g2d.getColor();
		g2d.setColor(color);
		if(centered) {
			FontMetrics fontMetrics = g2d.getFontMetrics();
			Rectangle stringBounds = new Rectangle(fontMetrics.getStringBounds(text, g2d).getBounds());
			x -= stringBounds.width / 2;
		}
		g2d.drawString(text, x, y);
		g2d.setColor(lastColor);
	}
}
