package demerit;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IMPLEMENTATION NOT FINISHED
 * Will be used to dynamically generate descriptive text such as:
 * Multi-language information, the release name of the program, the current time, etc.
 */
public final class DynamicString {
	final private String original;
	final private HashMap<String, String> replacements;
	
	DynamicString(String original, HashMap<String, String> replacements) {
		this.original = original;
		this.replacements = replacements;
	}
	
	public String getReplacement() {
		String replaced = "";
		
		Pattern pattern = Pattern.compile("<!--[A-Z]*?-->");
		Matcher matcher = pattern.matcher(original);
		
		Integer start = 0;
		while(matcher.find()) {
			Integer end = matcher.start();
			replaced += original.substring(start, end);
			replaced += valueForPattern(matcher.group());
			start = matcher.end();
		}
		replaced += original.substring(start);
		
		return replaced;
	}
	
	private String valueForPattern(String pattern) {
		pattern = pattern.substring(4, pattern.length()-3);
		String entry = replacements.get(pattern);
		if(entry == null) {
			entry = "?";
		}
		return entry;
	}
}
