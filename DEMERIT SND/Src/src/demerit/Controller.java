package demerit;

import java.util.ArrayList;

/**
 * The superclass for all Controllers.
 * Controllers represent the aspect of MVC in which models are connected to views.
 * Controls the logic of the application.
 */
abstract public class Controller {
	
	protected Core core;
	
	public Controller(Core core) {
		this.core = core;
	}
	
	abstract public View init(ArrayList<Object> parameters);

}
