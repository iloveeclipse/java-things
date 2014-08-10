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

	final boolean beginning;
	final String subPattern;

	NameMatcher(String pattern, boolean dirOnly){
		super(pattern, dirOnly);
		beginning = pattern.isEmpty()? false : pattern.charAt(0) == '/';
		if(!beginning) {
			this.subPattern = pattern;
		} else {
			this.subPattern = pattern.substring(1, pattern.length());
		}
	}

	@Override
	public boolean isBeginning() {
		return beginning;
	}

	/**
	 * @param path string which is not empty and is trimmed
	 */
	@Override
	public boolean matches(String path, boolean dirOnly) {
		int end = 0;
		int firstChar = 0;
		do {
			firstChar = getFirstNotSlash(path, end);
			end = getFirstSlash(path, firstChar);
			boolean match = matches(path, firstChar, end, dirOnly);
			if(match){
				return isDirectory? end > 0 && end != path.length() : true;
			}
		}
		while(!beginning && end != path.length());
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

	@Override
	public boolean matches(String segment, int startIncl, int endExcl, boolean dirOnly){
		if(subPattern.length() != (endExcl - startIncl)){
			return false;
		}
		int i = 0;
		for (; i < subPattern.length(); i++) {
			char c1 = subPattern.charAt(i);
			char c2 = segment.charAt(i + startIncl);
			if(c1 != c2){
				return false;
			}
		}
		return true;
	}

}
