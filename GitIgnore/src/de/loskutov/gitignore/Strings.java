package de.loskutov.gitignore;

import java.util.*;
import java.util.regex.Pattern;

public class Strings {

	static String stripSlashes(String pattern) {
		while(pattern.length() > 0 && pattern.charAt(pattern.length() - 1) == '/'){
			pattern = pattern.substring(0, pattern.length() - 1);
		}
		return pattern;
	}

	static boolean hasSegments(String s){
		int slash = s.indexOf('/', 1);
		return slash > 0 && slash != s.length() - 1;
	}

	static int count(String s, char c, boolean ignoreFirstLast){
		int start = 0;
		int count = 0;
		while (true) {
			start = s.indexOf(c, start);
			if(start == -1) {
				break;
			}
			if(!ignoreFirstLast || (start != 0 && start != s.length())) {
				count ++;
			}
			start ++;
		}
		return count;
	}

	static List<String> split(String pattern){
		int count = count(pattern, '/', true);
		if(count < 1){
			throw new IllegalStateException("Pattern must have at least two segments: " + pattern);
		}
		List<String> segments = new ArrayList<String>(count);
		int right = 0;
		while (true) {
			int left = right;
			right = pattern.indexOf('/', right);
			if(right == -1) {
				if(left < pattern.length()){
					segments.add(pattern.substring(left));
				}
				break;
			}
			if(right - left > 0) {
				if(left == 1){
					// leading slash should remain by the first pattern
					segments.add(pattern.substring(left - 1, right));
				} else if(right == pattern.length() - 1){
					// trailing slash should remain too
					segments.add(pattern.substring(left, right + 1));
				} else {
					segments.add(pattern.substring(left, right));
				}
			}
			right ++;
		}
		return segments;
	}

	static boolean isWildCard(String pattern) {
		return pattern.indexOf('*') != -1 || pattern.indexOf('?') != -1
				|| pattern.indexOf('[') != -1
				|| pattern.indexOf(']') != -1;
	}

	static Pattern convert(String subPattern) {
		String pat = subPattern.replaceAll("\\.", "\\\\.");
		pat = pat.replaceAll("\\*", ".*");
		pat = pat.replaceAll("\\?", ".?");
		return Pattern.compile(pat);
	}
}
