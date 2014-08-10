/*******************************************************************************
 * Copyright (c) 2014 Andrey Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributor:  Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package de.loskutov.gitignore;

import static de.loskutov.gitignore.Strings.*;

import java.util.*;

import org.eclipse.jgit.errors.InvalidPatternException;

public class PathMatcher extends AbstractMatcher {

	private static final WildMatcher WILD = new WildMatcher();
	private final List<AbstractMatcher> matchers;

	PathMatcher(String pattern, boolean dirOnly) throws InvalidPatternException {
		super(pattern, dirOnly);
		matchers = createMatchers(split(pattern), dirOnly);
	}

	static private List<AbstractMatcher> createMatchers(List<String> segments, boolean dirOnly) throws InvalidPatternException {
		List<AbstractMatcher> matchers = new ArrayList<AbstractMatcher>(segments.size());
		for (int i = 0; i < segments.size(); i++) {
			String segment = segments.get(i);
			if(WildMatcher.WILDMATCH.equals(segment)){
				// collapse wildmatchers following each other: **/** is same as **
				if(matchers.size() == 0 || matchers.get(matchers.size() - 1) != WILD) {
					matchers.add(WILD);
				}
			} else if(isWildCard(segment)){
				if(i != segments.size() - 1) {
					matchers.add(new WildCardMatcher(segment, false));
				} else {
					matchers.add(new WildCardMatcher(segment, dirOnly));
				}
			} else {
				if(i != segments.size() - 1) {
					matchers.add(new NameMatcher(segment, false));
				} else {
					matchers.add(new NameMatcher(segment, dirOnly));
				}
			}
		}
		return matchers;
	}

	@Override
	public boolean matches(String path, boolean dirOnly) {
		return matches(path, 0, path.length(), dirOnly);
	}

	@Override
	public boolean matches(String segment, int startIncl, int endExcl, boolean dirOnly) {
		return iterate(segment, startIncl, endExcl, dirOnly);
	}

	boolean iterate(final String path, final int startIncl, final int endExcl, boolean dirOnly){
		int matcher = 0;
		int right = startIncl;
		boolean match = false;
		int lastWildmatch = -1;
		while (true) {
			int left = right;
			right = path.indexOf('/', right);
			if(right == -1) {
				if(left < endExcl){
					match = matches(matcher, path, left, endExcl, dirOnly);
				}
				if(match){
					if(matcher == matchers.size() - 2 && matchers.get(matcher + 1).isWildmatch()){
						// ** can match *nothing*: a/b/** match also a/b
						return true;
					}
					if(matcher < matchers.size() - 1 && matchers.get(matcher).isWildmatch()){
						// ** can match *nothing*: a/**/b match also a/b
						matcher ++;
						match = matches(matcher, path, left, endExcl, dirOnly);
					} else
						if(matchers.get(matcher).isDirectory){
							return false;
						}
				}
				return match && matcher + 1 == matchers.size();
			}
			if(right - left > 0) {
				match = matches(matcher, path, left, right, dirOnly);
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
				if(lastWildmatch != -1){
					matcher = lastWildmatch + 1;
				} else {
					return false;
				}
			}
			right ++;
		}
	}

	boolean matches(int matcherIdx, String path, int startIncl, int endExcl, boolean dirOnly){
		AbstractMatcher matcher = matchers.get(matcherIdx);
		return matcher.matches(path, startIncl, endExcl, dirOnly);
	}


}
