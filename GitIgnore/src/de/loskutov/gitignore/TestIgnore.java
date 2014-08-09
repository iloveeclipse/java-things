/*******************************************************************************
 * Copyright (c) 2014 Andrey Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributor:  Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package de.loskutov.gitignore;


import static de.loskutov.gitignore.Strings.split;
import static org.junit.Assert.*;

import org.junit.Test;

public class TestIgnore {

	@Test
	public void testSimpleCharClass(){
		assertEquals("", matches("[a]", "a"));
		assertEquals("", matches("[a]", "a/"));
		assertEquals("", matches("[a]", "a/b"));

		assertEquals("", matches("[a]", "b/a"));
		assertEquals("", matches("[a]", "b/a/"));
		assertEquals("", matches("[a]", "b/a/b"));

		assertEquals("", matches("[a]", "/a/"));
		assertEquals("", matches("[a]", "/a/b"));

		assertEquals("", matches("[a]", "c/a/b"));
		assertEquals("", matches("[a]", "c/b/a"));

		assertEquals("", matches("/[a]", "a"));
		assertEquals("", matches("/[a]", "a/"));
		assertEquals("", matches("/[a]", "a/b"));
		assertEquals("", matches("/[a]", "/a"));
		assertEquals("", matches("/[a]", "/a/"));
		assertEquals("", matches("/[a]", "/a/b"));

		assertEquals("", matches("[a]/", "a/"));
		assertEquals("", matches("[a]/", "a/b"));
		assertEquals("", matches("[a]/", "/a/"));
		assertEquals("", matches("[a]/", "/a/b"));

		assertEquals("", matches("/[a]/", "a/"));
		assertEquals("", matches("/[a]/", "a/b"));
		assertEquals("", matches("/[a]/", "/a/"));
		assertEquals("", matches("/[a]/", "/a/b"));
	}

	@Test
	public void testCharClass(){
		assertEquals("", matches("[v-z]", "x"));
		assertEquals("", matches("[v-z]", "x/"));
		assertEquals("", matches("[v-z]", "x/b"));

		assertEquals("", matches("[v-z]", "b/x"));
		assertEquals("", matches("[v-z]", "b/x/"));
		assertEquals("", matches("[v-z]", "b/x/b"));

		assertEquals("", matches("[v-z]", "/x/"));
		assertEquals("", matches("[v-z]", "/x/b"));

		assertEquals("", matches("[v-z]", "c/x/b"));
		assertEquals("", matches("[v-z]", "c/b/x"));

		assertEquals("", matches("/[v-z]", "x"));
		assertEquals("", matches("/[v-z]", "x/"));
		assertEquals("", matches("/[v-z]", "x/b"));
		assertEquals("", matches("/[v-z]", "/x"));
		assertEquals("", matches("/[v-z]", "/x/"));
		assertEquals("", matches("/[v-z]", "/x/b"));

		assertEquals("", matches("[v-z]/", "x/"));
		assertEquals("", matches("[v-z]/", "x/b"));
		assertEquals("", matches("[v-z]/", "/x/"));
		assertEquals("", matches("[v-z]/", "/x/b"));

		assertEquals("", matches("/[v-z]/", "x/"));
		assertEquals("", matches("/[v-z]/", "x/b"));
		assertEquals("", matches("/[v-z]/", "/x/"));
		assertEquals("", matches("/[v-z]/", "/x/b"));
	}

	@Test
	public void testDotAsterisk(){
		assertEquals("", matches("*.a", ".a"));
		assertEquals("", matches("*.a", "/.a"));
		assertEquals("", matches("*.a", "a.a"));
		assertEquals("", matches("*.a", "/b.a"));
		assertEquals("", matches("*.a", "b.a"));
		assertEquals("", matches("*.a", "/a/b.a"));
		assertEquals("", matches("*.a", "/b/.a"));
	}

	@Test
	public void testDotAsteriskDoNotMatch(){
		assertEquals("", notMatches("*.a", ".ab"));
		assertEquals("", notMatches("*.a", "/.ab"));
		assertEquals("", notMatches("*.stp", "/test.astp"));
		assertEquals("", notMatches("*.a", "a.ab"));
		assertEquals("", notMatches("*.a", "/b.ab"));
		assertEquals("", notMatches("*.a", "b.ab"));
		assertEquals("", notMatches("*.a", "/a/b.ab"));
		assertEquals("", notMatches("*.a", "/b/.ab"));
	}

	@Test
	public void testAsteriskDotMatch(){
		assertEquals("", matches("a.*", "a."));
		assertEquals("", matches("a.*", "a./"));
		assertEquals("", matches("a.*", "a.b"));

		assertEquals("", matches("a.*", "b/a.b"));
		assertEquals("", matches("a.*", "b/a.b/"));
		assertEquals("", matches("a.*", "b/a.b/b"));

		assertEquals("", matches("a.*", "/a.b/"));
		assertEquals("", matches("a.*", "/a.b/b"));

		assertEquals("", matches("a.*", "c/a.b/b"));
		assertEquals("", matches("a.*", "c/b/a.b"));

		assertEquals("", matches("/a.*", "a.b"));
		assertEquals("", matches("/a.*", "a.b/"));
		assertEquals("", matches("/a.*", "a.b/b"));
		assertEquals("", matches("/a.*", "/a.b"));
		assertEquals("", matches("/a.*", "/a.b/"));
		assertEquals("", matches("/a.*", "/a.b/b"));

		assertEquals("", matches("/a.*/b", "a.b/b"));
		assertEquals("", matches("/a.*/b", "/a.b/b"));
		assertEquals("", matches("/a.*/b", "/a.bc/b"));
		assertEquals("", matches("/a.*/b", "/a./b"));
	}

	@Test
	public void testAsterisk(){
		assertEquals("", matches("a*", "a"));
		assertEquals("", matches("a*", "a/"));
		assertEquals("", matches("a*", "ab"));

		assertEquals("", matches("a*", "b/ab"));
		assertEquals("", matches("a*", "b/ab/"));
		assertEquals("", matches("a*", "b/ab/b"));

		assertEquals("", matches("a*", "b/abc"));
		assertEquals("", matches("a*", "b/abc/"));
		assertEquals("", matches("a*", "b/abc/b"));

		assertEquals("", matches("a*", "/abc/"));
		assertEquals("", matches("a*", "/abc/b"));

		assertEquals("", matches("a*", "c/abc/b"));
		assertEquals("", matches("a*", "c/b/abc"));

		assertEquals("", matches("/a*", "abc"));
		assertEquals("", matches("/a*", "abc/"));
		assertEquals("", matches("/a*", "abc/b"));
		assertEquals("", matches("/a*", "/abc"));
		assertEquals("", matches("/a*", "/abc/"));
		assertEquals("", matches("/a*", "/abc/b"));

		assertEquals("", matches("/a*/b", "abc/b"));
		assertEquals("", matches("/a*/b", "/abc/b"));
		assertEquals("", matches("/a*/b", "/abcd/b"));
		assertEquals("", matches("/a*/b", "/a/b"));
	}

	@Test
	public void testQuestionmark(){
		assertEquals("", matches("a?", "ab"));
		assertEquals("", matches("a?", "ab/"));

		assertEquals("", matches("a?", "b/ab"));
		assertEquals("", matches("a?", "b/ab/"));
		assertEquals("", matches("a?", "b/ab/b"));

		assertEquals("", matches("a?", "/ab/"));
		assertEquals("", matches("a?", "/ab/b"));

		assertEquals("", matches("a?", "c/ab/b"));
		assertEquals("", matches("a?", "c/b/ab"));

		assertEquals("", matches("/a?", "ab"));
		assertEquals("", matches("/a?", "ab/"));
		assertEquals("", matches("/a?", "ab/b"));
		assertEquals("", matches("/a?", "/ab"));
		assertEquals("", matches("/a?", "/ab/"));
		assertEquals("", matches("/a?", "/ab/b"));

		assertEquals("", matches("/a?/b", "ab/b"));
		assertEquals("", matches("/a?/b", "/ab/b"));
	}

	@Test
	public void testQuestionmarkDoNotMatch(){
		assertEquals("", notMatches("a?", "a/"));
		assertEquals("", notMatches("a?", "abc"));
		assertEquals("", notMatches("a?", "abc/"));

		assertEquals("", notMatches("a?", "b/abc"));
		assertEquals("", notMatches("a?", "b/abc/"));

		assertEquals("", notMatches("a?", "/abc/"));
		assertEquals("", notMatches("a?", "/abc/b"));

		assertEquals("", notMatches("a?", "c/abc/b"));
		assertEquals("", notMatches("a?", "c/b/abc"));

		assertEquals("", notMatches("/a?", "abc"));
		assertEquals("", notMatches("/a?", "abc/"));
		assertEquals("", notMatches("/a?", "abc/b"));
		assertEquals("", notMatches("/a?", "/abc"));
		assertEquals("", notMatches("/a?", "/abc/"));
		assertEquals("", notMatches("/a?", "/abc/b"));

		assertEquals("", notMatches("/a?/b", "abc/b"));
		assertEquals("", notMatches("/a?/b", "/abc/b"));
		assertEquals("", notMatches("/a?/b", "/a/b"));
	}

	@Test
	public void testSimplePatterns(){
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

		assertEquals("", matches("a/", "a/"));
		assertEquals("", matches("a/", "a/b"));
		assertEquals("", matches("a/", "/a/"));
		assertEquals("", matches("a/", "/a/b"));

		assertEquals("", matches("/a/", "a/"));
		assertEquals("", matches("/a/", "a/b"));
		assertEquals("", matches("/a/", "/a/"));
		assertEquals("", matches("/a/", "/a/b"));

	}

	@Test
	public void testSimplePatternsDoNotMatch(){
		assertEquals("", notMatches("ab", "a"));
		assertEquals("", notMatches("abc", "a/"));
		assertEquals("", notMatches("abc", "a/b"));

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

		assertEquals("", notMatches("a/", "a"));
		assertEquals("", notMatches("a/", "b/a"));
		assertEquals("", notMatches("/a/", "a"));
		assertEquals("", notMatches("/a/", "/a"));
		assertEquals("", notMatches("/a/", "b/a"));
	}

	@Test
	public void testSegments(){
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

		assertEquals("", matches("a/b/", "a/b/"));
		assertEquals("", matches("a/b/", "/a/b/"));
		assertEquals("", matches("a/b/", "/a/b/c"));

		assertEquals("", matches("a/b/", "c/a/b/"));
		assertEquals("", matches("a/b/", "c/a/b/c"));
	}

	@Test
	public void testSegmentsDoNotMatch(){
		assertEquals("", notMatches("a/b", "/a/bb"));
		assertEquals("", notMatches("a/b", "/aa/b"));
		assertEquals("", notMatches("a/b", "a/bb"));
		assertEquals("", notMatches("a/b", "aa/b"));
		assertEquals("", notMatches("a/b", "c/aa/b"));
		assertEquals("", notMatches("a/b", "c/a/bb"));
		assertEquals("", notMatches("a/b/", "/a/b"));
		assertEquals("", notMatches("/a/b/", "/a/b"));
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
		assertFalse(GitIgnoreParser.createRule("#").isMatch("#", false));
	}

	@Test
	public void testSplit(){
		try{
			split("/").toArray();
			fail("should not allow single slash");
		} catch(IllegalStateException e){
			// expected
		}

		assertArrayEquals(new String[]{"a", "b"}, split("a/b").toArray());
		assertArrayEquals(new String[]{"a", "b/"}, split("a/b/").toArray());
		assertArrayEquals(new String[]{"/a", "b"}, split("/a/b").toArray());
		assertArrayEquals(new String[]{"/a", "b/"}, split("/a/b/").toArray());
		assertArrayEquals(new String[]{"/a", "b", "c"}, split("/a/b/c").toArray());
		assertArrayEquals(new String[]{"/a", "b", "c/"}, split("/a/b/c/").toArray());
	}


	String matches(String pattern, String path){
		FastIgnoreRule rule = GitIgnoreParser.createRule(pattern);
		boolean dir = path.endsWith("/");
		boolean match = rule.isMatch(path, dir);
		String result = path + " is " + (match? "ignored" : "not ignored") + " via '" + pattern + "' rule";
		if(match) {
			System.out.println(result);
		} else {
			System.err.println(result);
		}

		if(pattern.endsWith("/")) {
			assertTrue(rule.dirOnly());
		} else {
			assertFalse(rule.dirOnly());
		}

		rule = GitIgnoreParser.createRule("!" + pattern);
		assertTrue(rule.isInverse());
		if(match) {
			assertFalse(!rule.isMatch(path, dir));
		} else {
			assertTrue(!rule.isMatch(path, dir));
		}

		if(match){
			return "";
		}
		return result;
	}

	String notMatches(String pattern, String path){
		FastIgnoreRule rule = GitIgnoreParser.createRule(pattern);
		boolean dir = path.endsWith("/");
		boolean match = rule.isMatch(path, dir);
		String result = path + " is " + (match? "ignored" : "not ignored") + " via '" + pattern + "' rule";
		if(match) {
			System.err.println(result);
		} else {
			System.out.println(result);
		}

		if(pattern.endsWith("/")) {
			assertTrue(rule.dirOnly());
		} else {
			assertFalse(rule.dirOnly());
		}

		rule = GitIgnoreParser.createRule("!" + pattern);
		assertTrue(rule.isInverse());
		if(match) {
			assertFalse(!rule.isMatch(path, dir));
		} else {
			assertTrue(!rule.isMatch(path, dir));
		}

		if(!match){
			return "";
		}
		return result;
	}
}
