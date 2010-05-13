package riot;

public interface SceneProvider {
	public abstract Scene nextScene();
	public abstract SceneProvider nextProvider();
}
