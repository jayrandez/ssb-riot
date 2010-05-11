package riot;

import java.io.Serializable;
import java.util.ArrayList;

public class SheetDescriptor implements Serializable {
	String sheetName;
	String imageFile;
	ArrayList<AnimationDescriptor> sprites;
	
	public String toString() {
		return " > " + sprites.toString();
	}
}
