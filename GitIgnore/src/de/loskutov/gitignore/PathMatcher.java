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

	private static final WildMatcher WILD = new WildMatcher();
	private final List<AbstractMatcher> matchers;

	PathMatcher(String pattern){
		this.pattern = pattern;
		matchers = createMatchers(split(pattern));
	}

	static private List<AbstractMatcher> createMatchers(List<String> segments) {
		List<AbstractMatcher> matchers = new ArrayList<AbstractMatcher>(segments.size());
		for (int i = 0; i < segments.size(); i++) {
			String segment = segments.get(i);
			if(WildMatcher.WILDMATCH.equals(segment)){
				// collapse wildmatchers following each other: **/** is same as **
				if(matchers.size() == 0 || matchers.get(matchers.size() - 1) != WILD) {
					matchers.add(WILD);
				}
			} else if(segment.indexOf('*') != -1){
				matchers.add(new WildCardMatcher(segment));
			} else {
				matchers.add(new NameMatcher(segment));
			}
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
		return matches(path, 0, path.length());
	}

	@Override
	public boolean matches(String segment, int startIncl, int endExcl) {
		return iterate(segment, startIncl, endExcl);
	}

	boolean iterate(final String path, final int startIncl, final int endExcl){
		int matcher = 0;
		int right = startIncl;
		boolean match = false;
		int lastWildmatch = -1;
		while (true) {
			int left = right;
			right = path.indexOf('/', right);
			if(right == -1) {
				if(left < endExcl){
					match = matches(matcher, path, left, endExcl);
				}
				if(match){
					if(matcher == matchers.size() - 2 && matchers.get(matcher + 1).isWildmatch()){
						// ** can match *nothing*: a/b/** match also a/b
						return true;
					}
					if(matcher < matchers.size() - 1 && matchers.get(matcher).isWildmatch()){
						// ** can match *nothing*: a/**/b match also a/b
						matcher ++;
						match = matches(matcher, path, left, endExcl);
					}
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
				if(matchers.get(matcher).isWildmatch()){
					lastWildmatch = matcher;
					// reset index as ** can match *nothing*: a/**/b match also a/b
					right = left - 1;
				}
				matcher ++;
				if (matcher == matchers.size()) {
					return true;
				}
			} else {
				if (matcher == 0 && matchers.get(matcher).isBeginning()) {
					return false;
				}
				if(lastWildmatch != -1){
					matcher = lastWildmatch + 1;
				}
			}
			right ++;
		}
	}

	boolean matches(int matcherIdx, String path, int startIncl, int endExcl){
		AbstractMatcher matcher = matchers.get(matcherIdx);
		return matcher.matches(path, startIncl, endExcl);
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
