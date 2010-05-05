package demerit;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * A class loader which will load modules during development.
 * Checks for modules based on the currently known packages.
 */
public class DevelopmentClassLoader extends ExtendedClassLoader {
	
	public ArrayList<String> findAvailableModules(ArrayList<String> known) {
		try {
			String packageName = Core.rootPackage + ".headers";
			ArrayList<Class<?>> classes = getClassesForPackage(packageName);
			for(Class<?> c: classes) {
				if(Header.class.isAssignableFrom(c)) {
					String officialName = c.getName().substring(packageName.length()+1);
					Core.debug("DevCLD Found: " + officialName);
					availableModules.put(officialName, officialName);
				}
			}
		}
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
    	return super.findAvailableModules(known);
    }
    
    public ArrayList<String> enableModules(ArrayList<String> enabled) {
    	for(String moduleName: enabled) {
    		if(availableModules.containsKey(moduleName)) {
    			enabledModules.add(moduleName);
    		}
    	}
    	return super.enableModules(enabled);
    }
    
    private ArrayList<Class<?>> getClassesForPackage(String pckgname) throws ClassNotFoundException {
		ArrayList<File> directories = new ArrayList<File>();
		try {
			String path = pckgname.replace('.', '/');
			Enumeration<URL> resources = this.getResources(path);
			while (resources.hasMoreElements()) {
				directories.add(new File(URLDecoder.decode(resources.nextElement().getPath(), "UTF-8")));
			}
		}
		catch (Exception ex) {
			throw new ClassNotFoundException("Could not find any classes for " + pckgname);
		}
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        for(File directory : directories) {
        	if (directory.exists()) {
        		String[] files = directory.list();
        		for (String file : files) {
        			if (file.endsWith(".class")) {
        				classes.add(Class.forName(pckgname + '.' + file.substring(0, file.length() - 6)));
        			}
        		}
        	}
        	else {
        		throw new ClassNotFoundException(pckgname + " (" + directory.getPath() + ") does not appear to be a valid package");
        	}
        }
        return classes;
    }
}
