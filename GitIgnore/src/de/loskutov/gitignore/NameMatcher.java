/*******************************************************************************
 * Copyright (c) 2014 Andrey Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributor:  Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package de.loskutov.gitignore;

public class NameMatcher extends AbstractMatcher {

	private final boolean firstSegmentOnly;
	private String segment;

	NameMatcher(String pattern){
		this.pattern = pattern;
		firstSegmentOnly = pattern.charAt(0) == '/';

		if(!firstSegmentOnly) {
			this.segment = pattern;
		} else {
			if(firstSegmentOnly){
				this.segment = pattern.substring(1, pattern.length());
			}
		}
	}

	/**
	 * @param s string which is not empty and is trimmed
	 */
	@Override
	public boolean matches(String s) {
		int nextSlash = 0;
		int firstChar = 0;
		do {
			firstChar = getFirstNotSlash(s, nextSlash);
			nextSlash = getFirstSlash(s, firstChar);
			boolean match = matches(s, firstChar, nextSlash);
			if(match){
				return true;
			}
		}
		while(!firstSegmentOnly && nextSlash != s.length());
		return false;
	}

	private static int getFirstNotSlash(String s, int start) {
		int slash = s.indexOf('/', start);
		return slash == start? start + 1 : start;
	}

	private static int getFirstSlash(String s, int start) {
		int slash = s.indexOf('/', start);
		return slash == -1? s.length() : slash;
	}

	boolean matches(String s2, int startIncl, int endExcl){
		if(segment.length() != (endExcl - startIncl)){
			return false;
		}
		for (int i = 0; i < segment.length(); i++) {
			char c1 = segment.charAt(i);
			char c2 = s2.charAt(i + startIncl);
			if(c1 != c2){
				return false;
			}
		}
		return true;
	}

}
