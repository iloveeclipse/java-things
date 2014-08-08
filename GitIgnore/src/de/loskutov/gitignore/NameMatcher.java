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

	private final boolean beginning;
	private CharSequence subPattern;

	NameMatcher(String pattern){
		this.pattern = pattern;
		beginning = pattern.charAt(0) == '/';

		if(!beginning) {
			this.subPattern = pattern;
		} else {
			this.subPattern = pattern.subSequence(1, pattern.length());
		}
	}

	public boolean isBeginning() {
		return beginning;
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
		while(!beginning && nextSlash != s.length());
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

	boolean matches(String segment, int startIncl, int endExcl){
		if(subPattern.length() != (endExcl - startIncl)){
			return false;
		}
		for (int i = 0; i < subPattern.length(); i++) {
			char c1 = subPattern.charAt(i);
			char c2 = segment.charAt(i + startIncl);
			if(c1 != c2){
				return false;
			}
		}
		return true;
	}

}
