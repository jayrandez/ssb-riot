package riot;

public abstract class GameObject {
	private Physics objectPhysics;
	private Sprite objectSprite;
	
	public GameObject() {
		this.objectPhysics = null;
		this.objectSprite = null;
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
	
	public void setSprite(String sheet, String animation, int index) {
		objectSprite = new Sprite(sheet, animation, index, objectPhysics.getX(), objectPhysics.getY(), objectPhysics.getRotation());
	}
	
	public Sprite getSprite() {
		return objectSprite;
	}
	
	public void step() {
		if(objectPhysics != null) {
			objectPhysics.step();
		}
	}
}
