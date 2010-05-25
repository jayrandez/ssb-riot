package riot;

public class Vector {
	double value;
	double angle;
	
	public Vector(double value, double angle) {
		this.value = value;
		this.angle = angle;
	}
	
	public static Vector fromComponents(double xComponent, double yComponent) {
		double hypotenuse = Math.sqrt(Math.pow(xComponent, 2) + Math.pow(yComponent, 2));
		double theta = Math.atan(yComponent / xComponent);
		return new Vector(hypotenuse, theta);
	}
}
