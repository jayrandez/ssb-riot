package demerit;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * An extension of a Class Loader which will be used to dynamically
 * extend the classpath during run time (for modularity & reflection)
 */
public class ExtendedClassLoader extends URLClassLoader {
	
	public HashMap<String, String> availableModules;
	public ArrayList<String> enabledModules;
	
    public ExtendedClassLoader() {
    	super(new URL[0]);
    	availableModules = new HashMap<String, String>();
    	enabledModules = new ArrayList<String>();
    }
    
    public void extendPath(String path) throws MalformedURLException {
    	path = "/" + path.replaceAll("\\\\", "/");
        addURL(new URL("jar:file://" + path + "!/"));
    }
    
    public ArrayList<String> getNewModules(ArrayList<String> oldModules) {
    	ArrayList<String> newModules = new ArrayList<String>();
    	for(String module: availableModules.keySet()) {
    		if(!oldModules.contains(module)) {
    			newModules.add(module);
    		}
    	}
    	return newModules;
    }
    
    public ArrayList<String> getLostModules(ArrayList<String> oldModules) {
    	ArrayList<String> lostModules = new ArrayList<String>();
    	for(String module: oldModules) {
    		if(!availableModules.keySet().contains(module)) {
    			lostModules.add(module);
    		}
    	}
    	return lostModules;
    }
    
    public ArrayList<String> findAvailableModules(ArrayList<String> known) {
    	return getNewModules(known);
    }
    
    public ArrayList<String> enableModules(ArrayList<String> enabled) {
    	return getLostModules(enabled);
    }
    
    public ArrayList<String> getEnabledModules() {
    	return enabledModules;
    }
}