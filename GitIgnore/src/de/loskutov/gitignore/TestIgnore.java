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
	public void testSimplePatternsMatch(){
		assertEquals("", matches("a", "a"));
		assertEquals("", matches("a", "a/"));
		assertEquals("", matches("a", "a/b"));

		assertEquals("", matches("a", "b/a"));
		assertEquals("", matches("a", "b/a/"));
		assertEquals("", matches("a", "b/a/b"));

		assertEquals("", matches("a", "/a/"));
		assertEquals("", matches("a", "/a/b"));

		assertEquals("", matches("a", "c/a/b"));
		assertEquals("", matches("a", "c/b/a"));

		assertEquals("", matches("/a", "a"));
		assertEquals("", matches("/a", "a/"));
		assertEquals("", matches("/a", "a/b"));
		assertEquals("", matches("/a", "/a"));
		assertEquals("", matches("/a", "/a/"));
		assertEquals("", matches("/a", "/a/b"));
	}

	@Test
	public void testSimplePatternsDoNotMatch(){
		assertEquals("", notMatches("a", "ab"));
		assertEquals("", notMatches("a", "ba"));
		assertEquals("", notMatches("a", "aa"));

		assertEquals("", notMatches("a", "b/ab"));
		assertEquals("", notMatches("a", "b/ba"));

		assertEquals("", notMatches("a", "b/ba"));
		assertEquals("", notMatches("a", "b/ab"));

		assertEquals("", notMatches("a", "b/ba/"));
		assertEquals("", notMatches("a", "b/ba/b"));

		assertEquals("", notMatches("a", "/aa"));
		assertEquals("", notMatches("a", "aa/"));
		assertEquals("", notMatches("a", "/aa/"));

		assertEquals("", notMatches("/a", "b/a"));
		assertEquals("", notMatches("/a", "/b/a/"));
	}

	@Test
	public void testSegmentsMatch(){
		assertEquals("", matches("/a/b", "a/b"));
		assertEquals("", matches("/a/b", "/a/b"));
		assertEquals("", matches("/a/b", "/a/b/"));
		assertEquals("", matches("/a/b", "/a/b/c"));

		assertEquals("", matches("a/b", "a/b"));
		assertEquals("", matches("a/b", "/a/b"));
		assertEquals("", matches("a/b", "/a/b/"));
		assertEquals("", matches("a/b", "/a/b/c"));

		assertEquals("", matches("a/b", "c/a/b"));
		assertEquals("", matches("a/b", "c/a/b/"));
		assertEquals("", matches("a/b", "c/a/b/c"));
	}

	@Test
	public void testSegmentsDoNotMatch(){
		assertEquals("", notMatches("a/b", "/a/bb"));
		assertEquals("", notMatches("a/b", "/aa/b"));
		assertEquals("", notMatches("a/b", "a/bb"));
		assertEquals("", notMatches("a/b", "aa/b"));
		assertEquals("", notMatches("a/b", "c/aa/b"));
		assertEquals("", notMatches("a/b", "c/a/bb"));
		assertEquals("", notMatches("/a/b", "c/a/b"));
		assertEquals("", notMatches("/a/b/", "c/a/b"));
		assertEquals("", notMatches("/a/b/", "c/a/b/"));
	}

	@Test
	public void testWildmatch(){
		assertEquals("", matches("**/a/b", "a/b"));
		assertEquals("", matches("**/a/b", "c/a/b"));
		assertEquals("", matches("**/a/b", "c/d/a/b"));
		assertEquals("", matches("**/**/a/b", "c/d/a/b"));

		assertEquals("", matches("a/b/**", "a/b"));
		assertEquals("", matches("a/b/**", "a/b/c"));
		assertEquals("", matches("a/b/**", "a/b/c/d/"));
		assertEquals("", matches("a/b/**/**", "a/b/c/d"));

		assertEquals("", matches("**/a/**/b", "c/d/a/b"));
		assertEquals("", matches("**/a/**/b", "c/d/a/e/b"));
		assertEquals("", matches("**/**/a/**/**/b", "c/d/a/e/b"));

		assertEquals("", matches("a/**/b", "a/b"));
		assertEquals("", matches("a/**/b", "a/c/b"));
		assertEquals("", matches("a/**/b", "a/c/d/b"));
		assertEquals("", matches("a/**/**/b", "a/c/d/b"));

		assertEquals("", matches("a/**/b/**/c", "a/c/b/d/c"));
		assertEquals("", matches("a/**/**/b/**/**/c", "a/c/b/d/c"));
	}

	@Test
	public void testWildmatchDoNotMatch(){
		assertEquals("", notMatches("**/a/b", "a/c/b"));
		assertEquals("", notMatches("a/**/b", "a/c/bb"));
	}

	@Test
	public void testSimpleRules(){
		try {
			GitIgnoreParser.createRule(null);
			fail("Illegal input allowed!");
		} catch (IllegalArgumentException e) {
			// expected
		}
		try {
			GitIgnoreParser.createRule("");
			fail("Illegal input allowed!");
		} catch (IllegalArgumentException e) {
			// expected
		}
		try {
			GitIgnoreParser.createRule(" ");
			fail("Illegal input allowed!");
		} catch (IllegalArgumentException e) {
			// expected
		}
		try {
			GitIgnoreParser.createRule(" # ");
			fail("Illegal input allowed!");
		} catch (IllegalArgumentException e) {
			// expected
		}
		try {
			GitIgnoreParser.createRule("/");
			fail("Illegal input allowed!");
		} catch (IllegalArgumentException e) {
			// expected
		}
		try {
			GitIgnoreParser.createRule("//");
			fail("Illegal input allowed!");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void testSplit(){
		try{
			PathMatcher.split("/").toArray();
			fail("should not allow single slash");
		} catch(IllegalStateException e){
			// expected
		}

		assertArrayEquals(new String[]{"a", "b"}, PathMatcher.split("a/b").toArray());
		assertArrayEquals(new String[]{"a", "b/"}, PathMatcher.split("a/b/").toArray());
		assertArrayEquals(new String[]{"/a", "b"}, PathMatcher.split("/a/b").toArray());
		assertArrayEquals(new String[]{"/a", "b/"}, PathMatcher.split("/a/b/").toArray());
		assertArrayEquals(new String[]{"/a", "b", "c"}, PathMatcher.split("/a/b/c").toArray());
		assertArrayEquals(new String[]{"/a", "b", "c/"}, PathMatcher.split("/a/b/c/").toArray());
	}


	String matches(String pattern, String path){
		IgnoreRule rule = GitIgnoreParser.createRule(pattern);
		boolean match = rule.match(path);
		String result = path + " is " + (match? "ignored" : "not ignored") + " via '" + pattern + "' rule";
		if(match) {
			System.out.println(result);
		} else {
			System.err.println(result);
		}

		if(pattern.endsWith("/")) {
			assertTrue(rule.isDirectory());
		} else {
			rule = GitIgnoreParser.createRule(pattern + "/");
			if(match) {
				assertTrue(rule.match(path));
			} else {
				assertFalse(rule.match(path));
			}
		}

		rule = GitIgnoreParser.createRule("!" + pattern);
		if(match) {
			assertFalse(rule.match(path));
		} else {
			assertTrue(rule.match(path));
		}

		if(match){
			return "";
		}
		return result;
	}

	String notMatches(String pattern, String path){
		IgnoreRule rule = GitIgnoreParser.createRule(pattern);
		boolean match = rule.match(path);
		String result = path + " is " + (match? "ignored" : "not ignored") + " via '" + pattern + "' rule";
		if(match) {
			System.err.println(result);
		} else {
			System.out.println(result);
		}

		if(pattern.endsWith("/")) {
			assertTrue(rule.isDirectory());
		} else {
			rule = GitIgnoreParser.createRule(pattern + "/");
			if(match) {
				assertTrue(rule.match(path));
			} else {
				assertFalse(rule.match(path));
			}
		}

		rule = GitIgnoreParser.createRule("!" + pattern);
		if(match) {
			assertFalse(rule.match(path));
		} else {
			assertTrue(rule.match(path));
		}

		if(!match){
			return "";
		}
		return result;
	}
}
