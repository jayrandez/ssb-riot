package riot;

public interface SceneProvider {
	public abstract void nextScene();
	public abstract SceneProvider nextProvider();
}
