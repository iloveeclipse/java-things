/*******************************************************************************
 * Copyright (c) 2014 Andrey Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributor:  Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package de.loskutov.gitignore;

public class IgnoreRule {

	private final IgnoreMatcher matcher;
	private final boolean inverse;
	private final boolean isDirectory;

	public IgnoreRule(String pattern) {
		if(pattern == null){
			throw new IllegalArgumentException("Pattern must be not null!");
		}
		pattern = pattern.trim();
		checkEmptyRule(pattern);
		if(pattern.charAt(0) == '#'){
			throw new IllegalArgumentException("Comment is not pattern!");
		}
		inverse = pattern.charAt(0) == '!';
		if(inverse){
			pattern = pattern.substring(1);
			checkEmptyRule(pattern);
		}
		isDirectory = pattern.charAt(pattern.length() - 1) == '/';
		if(isDirectory) {
			pattern = stripSlashes(pattern);
			checkEmptyRule(pattern);
		}
		this.matcher = createMatcher(pattern);
	}

	private static String stripSlashes(String pattern) {
		while(pattern.length() > 0 && pattern.charAt(pattern.length() - 1) == '/'){
			pattern = pattern.substring(0, pattern.length() - 1);
		}
		return pattern;
	}

	private static void checkEmptyRule(String pattern) {
		if(pattern.isEmpty()){
			throw new IllegalArgumentException("Pattern must be not empty!");
		}
	}

	private static IgnoreMatcher createMatcher(String pattern) {
		pattern = pattern.trim();
		// ignore possible leading and trailing slash
		int slash = pattern.indexOf('/', 1);
		if(slash > 0 && slash < pattern.length() - 1){
			return new PathMatcher(pattern);
		}
		if(pattern.indexOf('*') != -1){
			return new WildCardMatcher(pattern);
		}
		return new NameMatcher(pattern);
	}

	public boolean match(String path) {
		if(path == null){
			return updateMatch(false);
		}
		path = path.trim();
		if(path.isEmpty()){
			return updateMatch(false);
		}
		boolean match = matcher.matches(path);
		return updateMatch(match);
	}

	private boolean updateMatch(boolean result){
		return !inverse? result : !result;
	}

	public boolean isDirectory() {
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
		if (!(obj instanceof IgnoreRule)) {
			return false;
		}

		IgnoreRule other = (IgnoreRule) obj;
		if(inverse != other.inverse) {
			return false;
		}
		if(isDirectory != other.isDirectory) {
			return false;
		}
		return matcher.equals(other.matcher);
	}

}
