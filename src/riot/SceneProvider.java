package riot;

import java.io.Serializable;

public interface SceneProvider {
	public Scene nextScene();
	public void begin();
	public void receivePress(int code, boolean pressed);
}