package riot;

public abstract class GameObject {
	private Physics objectPhysics;
	
	public GameObject() {
		this.objectPhysics = null;
	}
	
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
		if(objectPhysics != null) {
			objectPhysics.step();
		}
	}
}
