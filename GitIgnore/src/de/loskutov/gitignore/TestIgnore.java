/*******************************************************************************
 * Copyright (c) 2014 Andrey Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributor:  Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package de.loskutov.gitignore;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestIgnore {
	@Test
	public void testSimplePatterns(){
		assertEquals("", matches("a", "a"));
		assertEquals("", matches("/a", "a"));
		assertEquals("", matches("a", "b/a"));
		assertEquals("", matches("a", "b/c/a"));
		assertEquals("", matches("/a", "a/b"));
		assertEquals("", matches("a/b", "c/a/b"));
	}

	String matches(String pattern, String path){
		boolean match = GitIgnoreParser.createRule(pattern).match(path);
		String result = path + " is " + (match? "ignored" : "not ignored") + " via '" + pattern + "' rule";
		System.out.println(result);
		if(match){
			return "";
		}
		return result;
	}
}
