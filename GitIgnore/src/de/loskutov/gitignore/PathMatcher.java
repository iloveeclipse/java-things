/*******************************************************************************
 * Copyright (c) 2014 Andrey Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributor:  Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package de.loskutov.gitignore;

import java.util.*;

public class PathMatcher extends AbstractMatcher {

	private final List<NameMatcher> matchers;

	PathMatcher(String pattern){
		this.pattern = pattern;
		matchers = createMatchers(split(pattern));
	}

	static private List<NameMatcher> createMatchers(List<String> segments) {
		List<NameMatcher> matchers = new ArrayList<NameMatcher>(segments.size());
		for (int i = 0; i < segments.size(); i++) {
			matchers.add(new NameMatcher(segments.get(i)));
		}
		return matchers;
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

	@Override
	public boolean matches(String path) {
		return iterate(path);
	}

	boolean iterate(String path){
		int matcher = 0;
		int right = 0;
		boolean match = false;
		while (true) {
			int left = right;
			right = path.indexOf('/', right);
			if(right == -1) {
				if(left < path.length()){
					match = matches(matcher, path, left, path.length());
				}
				return match && matcher + 1 == matchers.size();
			}
			if(right - left > 0) {
				match = matches(matcher, path, left, right);
			} else {
				// path starts with slash???
				right ++;
				continue;
			}
			if(match){
				matcher ++;
				if (matcher == matchers.size()) {
					return true;
				}
			} else if(matcher == 0 && matchers.get(matcher).isBeginning()){
				return false;
			}
			right ++;
		}
	}

	boolean matches(int matcher, String path, int startIncl, int endExcl){
		NameMatcher nameMatcher = matchers.get(matcher);
		return nameMatcher.matches(path, startIncl, endExcl);
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
}
