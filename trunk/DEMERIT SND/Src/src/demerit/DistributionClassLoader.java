package demerit;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The class loader which will dynamically load modules during release.
 * Checks for modules stored as JAR files and add's their location to the classpath
 */
public class DistributionClassLoader extends ExtendedClassLoader {
	
	public ArrayList<String> findAvailableModules(ArrayList<String> known) {
    	ArrayList<String> paths = findExternalModules();
    	ArrayList<String> officialNames = new ArrayList<String>();
    	for(String path: paths) {
    		String moduleName = identifyExternalModuleName(path);
    		officialNames.add(moduleName);
    		availableModules.put(moduleName, path);
    		System.out.println("DistCLD finds: " + moduleName + " @ " + path);
    	}
    	return super.findAvailableModules(known);
    }
    
    public ArrayList<String> enableModules(ArrayList<String> enabled) {
    	for(String moduleName: enabled) {
    		try {
    			String path = availableModules.get(moduleName);
    			if(path != null) {
    				extendPath(path);
    				enabledModules.add(moduleName);
    			}
			}
    		catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
    	}
    	return super.enableModules(enabled);
    }
    
    private ArrayList<String> findExternalModules() {
		ArrayList<String> assembled = new ArrayList<String>();
		File directory = new File("Modules");
		File[] files = directory.listFiles();
		for(File file: files) {
			if(file.getName().toLowerCase().contains(".jar")) {
				assembled.add(file.getAbsolutePath());
			}
		}
		return assembled;
	}
	
	private String identifyExternalModuleName(String path) {
		String moduleName = null;
		boolean match = false;
		String headerPrefix = Core.rootPackage + ".headers.";
		try {
			ZipFile file = new ZipFile(path);
			Enumeration<ZipEntry> zipEntries = (Enumeration<ZipEntry>)file.entries();
			while(zipEntries.hasMoreElements()) {
				ZipEntry entry = zipEntries.nextElement();
				if(entry.toString().contains(headerPrefix.replace(".", "/")) && entry.toString().contains(".class")) {
					if(!match) {
						moduleName = entry.toString().replace("/", ".").substring(0, entry.toString().indexOf(".class"));
						moduleName = moduleName.substring(headerPrefix.length());
						match = true;
					}
					else {
						moduleName = null;
					}
				}
			}
				
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}		
		return moduleName;
	}
}
