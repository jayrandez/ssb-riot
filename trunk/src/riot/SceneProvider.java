package riot;

public interface SceneProvider {
	public Scene nextScene();
	public SceneProvider nextProvider();
	public void receiveLocation(int x, int y);
	public void receivePress(int code, boolean pressed);
}
