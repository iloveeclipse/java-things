package de.loskutov.gitignore;

import java.util.*;
import java.util.regex.Pattern;

import org.eclipse.jgit.errors.InvalidPatternException;

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

	final static List<String> POSIX_CHAR_CLASSES = Arrays.asList(
			//[:alnum:] [:alpha:] [:blank:] [:cntrl:]
			"alnum",  "alpha",  "blank",  "cntrl",
			//[:digit:] [:graph:] [:lower:] [:print:]
			"digit",  "graph",  "lower",  "print",
			//[:punct:] [:space:] [:upper:] [:xdigit:]
			"punct",  "space",  "upper",  "xdigit",
			//[:word:] XXX I don't see it in http://man7.org/linux/man-pages/man7/glob.7.html
			// but this was in org.eclipse.jgit.fnmatch.GroupHead.java ???
			"word"
			);
	final static List<String> JAVA_CHAR_CLASSES = Arrays.asList(
			"\\p{Alnum}",  "\\p{Alpha}",  "\\p{Blank}",  "\\p{Cntrl}",
			"\\p{Digit}",  "\\p{Graph}",  "\\p{Lower}",  "\\p{Print}",
			"\\p{Punct}",  "\\p{Space}",  "\\p{Upper}",  "\\p{XDigit}",
			"\\w"
			);

	/**
	 * Conversion from glob to Java regex following two sources: <li>
	 * http://man7.org/linux/man-pages/man7/glob.7.html <li>
	 * org.eclipse.jgit.fnmatch.FileNameMatcher.java Seems that there are
	 * various ways to define what "glob" can be.
	 *
	 * @return Java regex pattern corresponding to given glob pattern
	 *
	 * @throws InvalidPatternException
	 */
	static Pattern convertGlob(String pattern) throws InvalidPatternException {
		StringBuilder sb = new StringBuilder(pattern.length());

		int in_brackets = 0;
		boolean ignoreLastBracket = false;
		boolean in_char_class = false;
		// 6 is the length of the longest posix char class "xdigit"
		char[] charClass = new char[6];

		for (int i = 0; i < pattern.length(); i++) {
			char c = pattern.charAt(i);
			switch (c) {

			case '*':
				if(in_brackets > 0) {
					sb.append(c);
				} else {
					sb.append('.').append(c);
				}
				break;

			case '.':
				if(in_brackets > 0) {
					sb.append(c);
				} else {
					sb.append('\\').append('.');
				}
				break;

			case '?':
				if(in_brackets > 0) {
					sb.append(c);
				} else {
					sb.append('.');
				}
				break;

			case ':':
				if(in_brackets > 0) {
					if(sb.charAt(sb.length()-1) == '[') {
						in_char_class = true;
					}
				}
				sb.append(':');
				break;

			case '-':
				if(in_brackets > 0) {
					if(pattern.charAt(i + 1) == ']') {
						sb.append('\\').append(c);
					} else {
						sb.append(c);
					}
				} else {
					sb.append('-');
				}
				break;

			case '\\':
				if(in_brackets > 0 && (pattern.charAt(i + 1) == ']' || pattern.charAt(i + 1) == '[')) {
					ignoreLastBracket = true;
				}
				sb.append(c);
				break;

			case '[':
				if(in_brackets > 0) {
					if(sb.charAt(sb.length()-1) == '['
							|| pattern.charAt(i + 1) == ':'){
						in_brackets ++;
						sb.append('[');
						ignoreLastBracket = false;
					} else {
						sb.append('\\').append('[');
						ignoreLastBracket = true;
					}
				} else {
					in_brackets ++;
					sb.append('[');
					ignoreLastBracket = false;
				}
				break;

			case ']':
				if(in_brackets > 0) {
					if((sb.charAt(sb.length()-1) == '[' && !ignoreLastBracket)
							||sb.charAt(sb.length()-1) == '^') {
						sb.append('\\');
						sb.append(']');
						ignoreLastBracket = true;
					} else {
						in_brackets --;
						ignoreLastBracket = false;
						if(!in_char_class) {
							sb.append(']');
						} else {
							in_char_class = false;
							String charCl = checkPosixCharClass(charClass);
							// delete last [:: chars and set the pattern
							if(charCl != null){
								sb.setLength(sb.length() - 3);
								sb.append(charCl);
							}
							reset(charClass);
						}
					}
				} else {
					sb.append('\\').append(']');
					ignoreLastBracket = true;
				}
				break;

			case '!':
				if(in_brackets > 0) {
					if(sb.charAt(sb.length()-1) == '[') {
						sb.append('^');
					} else {
						sb.append(c);
					}
				} else {
					sb.append(c);
				}
				break;

			default:
				if(in_char_class){
					setNext(charClass, c);
				} else{
					sb.append(c);
				}
				break;
			}
		}
		if(in_brackets > 0){
			throw new InvalidPatternException("Not closed bracket?", pattern);
		}
		return Pattern.compile(sb.toString());
	}

	private static void setNext(char[] buffer, char c){
		for (int i = 0; i < buffer.length; i++) {
			if(buffer[i] == 0){
				buffer[i] = c;
				break;
			}
		}
	}

	private static void reset(char[] buffer){
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = 0;
		}
	}

	private static String checkPosixCharClass(char[] buffer){
		for (int i = 0; i < POSIX_CHAR_CLASSES.size(); i++) {
			String clazz = POSIX_CHAR_CLASSES.get(i);
			boolean match = true;
			for (int j = 0; j < clazz.length(); j++) {
				if(buffer[j] != clazz.charAt(j)){
					match = false;
					break;
				}
			}
			if(match) {
				return JAVA_CHAR_CLASSES.get(i);
			}
		}
		return null;
	}

}