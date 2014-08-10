/*******************************************************************************
 * Copyright (c) 2014 Andrey Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributor:  Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package de.loskutov.gitignore;

import static de.loskutov.gitignore.Strings.stripTrailing;

import org.eclipse.jgit.errors.InvalidPatternException;

public class FastIgnoreRule {

	private static final char PATH_SEPARATOR = '/';
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
			isDirectory = pattern.charAt(pattern.length() - 1) == PATH_SEPARATOR;
			if(isDirectory) {
				pattern = stripTrailing(pattern, PATH_SEPARATOR);
				if(pattern.isEmpty()){
					this.matcher = NO_MATCH;
					return;
				}
			}
			IgnoreMatcher m;
			try {
				m = PathMatcher.createPathMatcher(pattern,  Character.valueOf(PATH_SEPARATOR), isDirectory);
			} catch (InvalidPatternException e) {
				// TODO Auto-generated catch block
				m = NO_MATCH;
			}
			this.matcher = m;
		}
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

	public boolean getNegation() {
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
			sb.append(PATH_SEPARATOR);
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
