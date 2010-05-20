package riot;

public abstract class GameObject {
	private Physics objectPhysics;
	
	public GameObject(Physics objectPhysics) {
		this.objectPhysics = objectPhysics;
	}
	
	public void setPhysics(Physics objectPhysics) {
		this.objectPhysics = objectPhysics;
	}
	
	public Physics getPhysics() {
		return objectPhysics;
	}
	
	public void step() {
		objectPhysics.step();
	}
}
