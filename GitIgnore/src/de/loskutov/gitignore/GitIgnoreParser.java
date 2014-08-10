/*******************************************************************************
 * Copyright (c) 2014 Andrey Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributor:  Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package de.loskutov.gitignore;

import org.eclipse.jgit.errors.InvalidPatternException;

public class GitIgnoreParser {

	public static void main(String[] args) {
		if(args.length < 2 || args.length % 2 != 0){
			System.out.println("Usage: (<ignore pattern> <path to test>)+");
			return;
		}
		for (int i = 0; i < args.length; i++) {
			String pattern = args[i];
			String path = args[++i];
			boolean match = createRule(pattern).isMatch(path, true);
			System.out.println(path + " is " + (match? "ignored" : "not ignored") + " via " + pattern );
		}
	}

	public static FastIgnoreRule createRule(String pattern) {
		return new FastIgnoreRule(pattern);
	}

	public static IgnoreMatcher createMatcher(String pattern, boolean dirOnly) throws InvalidPatternException {
		pattern = pattern.trim();
		// ignore possible leading and trailing slash
		int slash = pattern.indexOf('/', 1);
		if(slash > 0 && slash < pattern.length() - 1){
			return new PathMatcher(pattern, dirOnly);
		}
		return PathMatcher.createMatcher(pattern, dirOnly);
	}
}
