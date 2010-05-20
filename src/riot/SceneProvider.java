package riot;

import java.io.Serializable;

public interface SceneProvider {
	public Scene nextScene();
	public SceneProvider nextProvider();
	public void receivePress(int code, boolean pressed);
	public void debug(Serializable message);
}
