/*******************************************************************************
 * Copyright (c) 2014 Andrey Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributor:  Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package org.eclipse.jgit.fnmatch2;

import static org.eclipse.jgit.fnmatch2.Strings.*;

import java.util.*;

import org.eclipse.jgit.errors.InvalidPatternException;

public class PathMatcher extends AbstractMatcher {

	private static final WildMatcher WILD = new WildMatcher();
	private final List<AbstractMatcher> matchers;
	private final char slash;

	PathMatcher(String pattern, Character pathSeparator, boolean dirOnly) throws InvalidPatternException {
		super(pattern, dirOnly);
		slash = getPathSeparator(pathSeparator);
		matchers = createMatchers(split(pattern, slash), pathSeparator, dirOnly);
	}

	static private List<AbstractMatcher> createMatchers(List<String> segments, Character pathSeparator, boolean dirOnly) throws InvalidPatternException {
		List<AbstractMatcher> matchers = new ArrayList<AbstractMatcher>(segments.size());
		for (int i = 0; i < segments.size(); i++) {
			String segment = segments.get(i);
			AbstractMatcher matcher = createNameMatcher(segment, pathSeparator, dirOnly);
			if(matcher == WILD && i > 0 && matchers.get(matchers.size() - 1) == WILD){
				// collapse wildmatchers following each other: **/** is same as **
				continue;
			}
			matchers.add(matcher);
		}
		return matchers;
	}

	/**
	 *
	 * @param pattern
	 * @param pathSeparator if this parameter isn't null then this character will not
	 *            match at wildcards(* and ? are wildcards).
	 * @param dirOnly
	 * @return
	 * @throws InvalidPatternException
	 */
	public static IMatcher createPathMatcher(String pattern, Character pathSeparator, boolean dirOnly) throws InvalidPatternException {
		pattern = pattern.trim();
		char slash = Strings.getPathSeparator(pathSeparator);
		// ignore possible leading and trailing slash
		int slashIdx = pattern.indexOf(slash, 1);
		if(slashIdx > 0 && slashIdx < pattern.length() - 1){
			return new PathMatcher(pattern, pathSeparator, dirOnly);
		}
		return createNameMatcher(pattern, pathSeparator, dirOnly);
	}

	public static AbstractMatcher createNameMatcher(String segment, Character pathSeparator, boolean dirOnly) throws InvalidPatternException {
		if(WildMatcher.WILDMATCH.equals(segment)) {
			return WILD;
		}
		if(isWildCard(segment)){
			return new WildCardMatcher(segment, pathSeparator, dirOnly);
		}
		return new NameMatcher(segment, pathSeparator, dirOnly);
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
			right = path.indexOf(slash, right);
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