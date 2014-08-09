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

public class FastIgnoreRule {
	private static final NoResultMatcher NO_MATCH = new NoResultMatcher();
	private final IgnoreMatcher matcher;
	private final boolean inverse;
	private final boolean isDirectory;

	public FastIgnoreRule(String pattern) {
		if(pattern == null){
			throw new IllegalArgumentException("Pattern must be not null!");
		}
		pattern = pattern.trim();
		if(pattern.isEmpty()){
			isDirectory = false;
			inverse = false;
			this.matcher = NO_MATCH;
			return;
		}
		inverse = pattern.charAt(0) == '!';
		if(inverse){
			pattern = pattern.substring(1);
			if(pattern.isEmpty()){
				isDirectory = false;
				this.matcher = NO_MATCH;
				return;
			}
		}
		if(pattern.charAt(0) == '#'){
			this.matcher = NO_MATCH;
			isDirectory = false;
		} else {
			isDirectory = pattern.charAt(pattern.length() - 1) == '/';
			if(isDirectory) {
				pattern = stripSlashes(pattern);
				if(pattern.isEmpty()){
					this.matcher = NO_MATCH;
					return;
				}
			}
			this.matcher = createMatcher(pattern, isDirectory);
		}
	}

	private static IgnoreMatcher createMatcher(String pattern, boolean dirOnly) {
		pattern = pattern.trim();
		// ignore possible leading and trailing slash
		int slash = pattern.indexOf('/', 1);
		if(slash > 0 && slash < pattern.length() - 1){
			return new PathMatcher(pattern, dirOnly);
		}
		if(isWildCard(pattern)){
			return new WildCardMatcher(pattern, dirOnly);
		}
		return new NameMatcher(pattern, dirOnly);
	}

	public boolean isMatch(String path, boolean directory) {
		if(path == null){
			return false;
		}
		path = path.trim();
		if(path.isEmpty()){
			return false;
		}
		boolean match = matcher.matches(path, directory);
		return match;
	}

	public boolean dirOnly() {
		return isDirectory;
	}

	public boolean isInverse() {
		return inverse;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (inverse) {
			sb.append('!');
		}
		sb.append(matcher);
		if (isDirectory) {
			sb.append('/');
		}
		return sb.toString();

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (inverse ? 1231 : 1237);
		result = prime * result + (isDirectory ? 1231 : 1237);
		result = prime * result + ((matcher == null) ? 0 : matcher.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof FastIgnoreRule)) {
			return false;
		}

		FastIgnoreRule other = (FastIgnoreRule) obj;
		if(inverse != other.inverse) {
			return false;
		}
		if(isDirectory != other.isDirectory) {
			return false;
		}
		return matcher.equals(other.matcher);
	}

	static final class NoResultMatcher implements IgnoreMatcher {

		@Override
		public boolean matches(String path, boolean dirOnly) {
			return false;
		}

		@Override
		public boolean matches(String segment, int startIncl, int endExcl, boolean dirOnly) {
			return false;
		}

	}
}
