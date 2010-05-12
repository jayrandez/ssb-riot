package riot;

import java.io.Serializable;
import java.util.ArrayList;

public class SheetDescriptor implements Serializable {
	String sheetName;
	String imageFile;
	ArrayList<AnimationDescriptor> animations;
	
	public String toString() {
		return " > " + animations.toString();
	}
}
