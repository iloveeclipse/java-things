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

	private IgnoreMatcher matcher;

	public IgnoreRule(String pattern) {
		if(pattern == null){
			throw new IllegalArgumentException("Pattern must be not null!");
		}
		this.matcher = createMatcher(pattern);
	}

	private IgnoreMatcher createMatcher(String pattern) {
		pattern = pattern.trim();
		int containsSlash = pattern.indexOf('/');
		if(containsSlash > 0){
			return new PathMatcher(pattern);
		}
		return new NameMatcher(pattern);
	}

	public boolean match(String path) {
		if(path == null){
			return false;
		}
		path = path.trim();
		if(path.isEmpty()){
			return false;
		}
		return matcher.matches(path);
	}

	@Override
	public String toString() {
		return matcher.toString();
	}

	@Override
	public int hashCode() {
		return matcher.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof IgnoreRule)) {
			return false;
		}
		return matcher.equals(((IgnoreRule) obj).matcher);
	}

}
