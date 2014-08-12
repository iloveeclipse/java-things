/*
 * Copyright (C) 2014, Andrey Loskutov <loskutov@gmx.de>
 * and other copyright owners as documented in the project's IP log.
 *
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Distribution License v1.0 which
 * accompanies this distribution, is reproduced below, and is
 * available at http://www.eclipse.org/org/documents/edl-v10.php
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 * - Neither the name of the Eclipse Foundation, Inc. nor the
 *   names of its contributors may be used to endorse or promote
 *   products derived from this software without specific prior
 *   written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.eclipse.jgit.ignore;

import static org.eclipse.jgit.ignore.internal.Strings.split;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.util.Arrays;

import org.eclipse.jgit.ignore.FastIgnoreRule;
import org.eclipse.jgit.ignore.IgnoreRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@SuppressWarnings("deprecation")
@RunWith(Parameterized.class)
public class FastIgnoreRuleTest {

	@Parameters(name = "JGit? {0}")
	public static Iterable<Boolean[]> data() {
		return Arrays.asList(new Boolean[][] { { Boolean.FALSE },
				{ Boolean.TRUE } });
	}

	@Parameter
	public Boolean useJGitRule;

	@Test
	public void testSimpleCharClass() {
		assertMatched("[a]", "a");
		assertMatched("[a]", "a/");
		assertMatched("[a]", "a/b");

		assertMatched("[a]", "b/a");
		assertMatched("[a]", "b/a/");
		assertMatched("[a]", "b/a/b");

		assertMatched("[a]", "/a/");
		assertMatched("[a]", "/a/b");

		assertMatched("[a]", "c/a/b");
		assertMatched("[a]", "c/b/a");

		assertMatched("/[a]", "a");
		assertMatched("/[a]", "a/");
		assertMatched("/[a]", "a/b");
		assertMatched("/[a]", "/a");
		assertMatched("/[a]", "/a/");
		assertMatched("/[a]", "/a/b");

		assertMatched("[a]/", "a/");
		assertMatched("[a]/", "a/b");
		assertMatched("[a]/", "/a/");
		assertMatched("[a]/", "/a/b");

		assertMatched("/[a]/", "a/");
		assertMatched("/[a]/", "a/b");
		assertMatched("/[a]/", "/a/");
		assertMatched("/[a]/", "/a/b");
	}

	@Test
	public void testCharClass() {
		assertMatched("[v-z]", "x");
		assertMatched("[v-z]", "x/");
		assertMatched("[v-z]", "x/b");

		assertMatched("[v-z]", "b/x");
		assertMatched("[v-z]", "b/x/");
		assertMatched("[v-z]", "b/x/b");

		assertMatched("[v-z]", "/x/");
		assertMatched("[v-z]", "/x/b");

		assertMatched("[v-z]", "c/x/b");
		assertMatched("[v-z]", "c/b/x");

		assertMatched("/[v-z]", "x");
		assertMatched("/[v-z]", "x/");
		assertMatched("/[v-z]", "x/b");
		assertMatched("/[v-z]", "/x");
		assertMatched("/[v-z]", "/x/");
		assertMatched("/[v-z]", "/x/b");

		assertMatched("[v-z]/", "x/");
		assertMatched("[v-z]/", "x/b");
		assertMatched("[v-z]/", "/x/");
		assertMatched("[v-z]/", "/x/b");

		assertMatched("/[v-z]/", "x/");
		assertMatched("/[v-z]/", "x/b");
		assertMatched("/[v-z]/", "/x/");
		assertMatched("/[v-z]/", "/x/b");
	}

	@Test
	public void testDotAsterisk() {
		assertMatched("*.a", ".a");
		assertMatched("*.a", "/.a");
		assertMatched("*.a", "a.a");
		assertMatched("*.a", "/b.a");
		assertMatched("*.a", "b.a");
		assertMatched("*.a", "/a/b.a");
		assertMatched("*.a", "/b/.a");
	}

	@Test
	public void testDotAsteriskDoNotMatch() {
		assertNotMatched("*.a", ".ab");
		assertNotMatched("*.a", "/.ab");
		assertNotMatched("*.a", "/b.ba");
		assertNotMatched("*.a", "a.ab");
		assertNotMatched("*.a", "/b.ab");
		assertNotMatched("*.a", "b.ab");
		assertNotMatched("*.a", "/a/b.ab");
		assertNotMatched("*.a", "/b/.ab");
	}

	@Test
	public void testAsteriskDotMatch() {
		assertMatched("a.*", "a.");
		assertMatched("a.*", "a./");
		assertMatched("a.*", "a.b");

		assertMatched("a.*", "b/a.b");
		assertMatched("a.*", "b/a.b/");
		assertMatched("a.*", "b/a.b/b");

		assertMatched("a.*", "/a.b/");
		assertMatched("a.*", "/a.b/b");

		assertMatched("a.*", "c/a.b/b");
		assertMatched("a.*", "c/b/a.b");

		assertMatched("/a.*", "a.b");
		assertMatched("/a.*", "a.b/");
		assertMatched("/a.*", "a.b/b");
		assertMatched("/a.*", "/a.b");
		assertMatched("/a.*", "/a.b/");
		assertMatched("/a.*", "/a.b/b");

		assertMatched("/a.*/b", "a.b/b");
		assertMatched("/a.*/b", "/a.b/b");
		assertMatched("/a.*/b", "/a.bc/b");
		assertMatched("/a.*/b", "/a./b");
	}

	@Test
	public void testAsterisk() {
		assertMatched("a*", "a");
		assertMatched("a*", "a/");
		assertMatched("a*", "ab");

		assertMatched("a*", "b/ab");
		assertMatched("a*", "b/ab/");
		assertMatched("a*", "b/ab/b");

		assertMatched("a*", "b/abc");
		assertMatched("a*", "b/abc/");
		assertMatched("a*", "b/abc/b");

		assertMatched("a*", "/abc/");
		assertMatched("a*", "/abc/b");

		assertMatched("a*", "c/abc/b");
		assertMatched("a*", "c/b/abc");

		assertMatched("/a*", "abc");
		assertMatched("/a*", "abc/");
		assertMatched("/a*", "abc/b");
		assertMatched("/a*", "/abc");
		assertMatched("/a*", "/abc/");
		assertMatched("/a*", "/abc/b");

		assertMatched("/a*/b", "abc/b");
		assertMatched("/a*/b", "/abc/b");
		assertMatched("/a*/b", "/abcd/b");
		assertMatched("/a*/b", "/a/b");
	}

	@Test
	public void testQuestionmark() {
		assertMatched("a?", "ab");
		assertMatched("a?", "ab/");

		assertMatched("a?", "b/ab");
		assertMatched("a?", "b/ab/");
		assertMatched("a?", "b/ab/b");

		assertMatched("a?", "/ab/");
		assertMatched("a?", "/ab/b");

		assertMatched("a?", "c/ab/b");
		assertMatched("a?", "c/b/ab");

		assertMatched("/a?", "ab");
		assertMatched("/a?", "ab/");
		assertMatched("/a?", "ab/b");
		assertMatched("/a?", "/ab");
		assertMatched("/a?", "/ab/");
		assertMatched("/a?", "/ab/b");

		assertMatched("/a?/b", "ab/b");
		assertMatched("/a?/b", "/ab/b");
	}

	@Test
	public void testQuestionmarkDoNotMatch() {
		assertNotMatched("a?", "a/");
		assertNotMatched("a?", "abc");
		assertNotMatched("a?", "abc/");

		assertNotMatched("a?", "b/abc");
		assertNotMatched("a?", "b/abc/");

		assertNotMatched("a?", "/abc/");
		assertNotMatched("a?", "/abc/b");

		assertNotMatched("a?", "c/abc/b");
		assertNotMatched("a?", "c/b/abc");

		assertNotMatched("/a?", "abc");
		assertNotMatched("/a?", "abc/");
		assertNotMatched("/a?", "abc/b");
		assertNotMatched("/a?", "/abc");
		assertNotMatched("/a?", "/abc/");
		assertNotMatched("/a?", "/abc/b");

		assertNotMatched("/a?/b", "abc/b");
		assertNotMatched("/a?/b", "/abc/b");
		assertNotMatched("/a?/b", "/a/b");
	}

	@Test
	public void testSimplePatterns() {
		assertMatched("a", "a");
		assertMatched("a", "a/");
		assertMatched("a", "a/b");

		assertMatched("a", "b/a");
		assertMatched("a", "b/a/");
		assertMatched("a", "b/a/b");

		assertMatched("a", "/a/");
		assertMatched("a", "/a/b");

		assertMatched("a", "c/a/b");
		assertMatched("a", "c/b/a");

		assertMatched("/a", "a");
		assertMatched("/a", "a/");
		assertMatched("/a", "a/b");
		assertMatched("/a", "/a");
		assertMatched("/a", "/a/");
		assertMatched("/a", "/a/b");

		assertMatched("a/", "a/");
		assertMatched("a/", "a/b");
		assertMatched("a/", "/a/");
		assertMatched("a/", "/a/b");

		assertMatched("/a/", "a/");
		assertMatched("/a/", "a/b");
		assertMatched("/a/", "/a/");
		assertMatched("/a/", "/a/b");

	}

	@Test
	public void testSimplePatternsDoNotMatch() {
		assertNotMatched("ab", "a");
		assertNotMatched("abc", "a/");
		assertNotMatched("abc", "a/b");

		assertNotMatched("a", "ab");
		assertNotMatched("a", "ba");
		assertNotMatched("a", "aa");

		assertNotMatched("a", "b/ab");
		assertNotMatched("a", "b/ba");

		assertNotMatched("a", "b/ba");
		assertNotMatched("a", "b/ab");

		assertNotMatched("a", "b/ba/");
		assertNotMatched("a", "b/ba/b");

		assertNotMatched("a", "/aa");
		assertNotMatched("a", "aa/");
		assertNotMatched("a", "/aa/");

		assertNotMatched("/a", "b/a");
		assertNotMatched("/a", "/b/a/");

		assertNotMatched("a/", "a");
		assertNotMatched("a/", "b/a");

		assertNotMatched("/a/", "a");
		assertNotMatched("/a/", "/a");
		assertNotMatched("/a/", "b/a");
	}

	@Test
	public void testSegments() {
		assertMatched("/a/b", "a/b");
		assertMatched("/a/b", "/a/b");
		assertMatched("/a/b", "/a/b/");
		assertMatched("/a/b", "/a/b/c");

		assertMatched("a/b", "a/b");
		assertMatched("a/b", "/a/b");
		assertMatched("a/b", "/a/b/");
		assertMatched("a/b", "/a/b/c");

		assertMatched("a/b/", "a/b/");
		assertMatched("a/b/", "/a/b/");
		assertMatched("a/b/", "/a/b/c");
	}

	@Test
	public void testSegmentsDoNotMatch() {
		assertNotMatched("a/b", "/a/bb");
		assertNotMatched("a/b", "/aa/b");
		assertNotMatched("a/b", "a/bb");
		assertNotMatched("a/b", "aa/b");
		assertNotMatched("a/b", "c/aa/b");
		assertNotMatched("a/b", "c/a/bb");
		assertNotMatched("a/b/", "/a/b");
		assertNotMatched("/a/b/", "/a/b");
		assertNotMatched("/a/b", "c/a/b");
		assertNotMatched("/a/b/", "c/a/b");
		assertNotMatched("/a/b/", "c/a/b/");

		// XXX why is it like this????
		assertNotMatched("a/b", "c/a/b");
		assertNotMatched("a/b", "c/a/b/");
		assertNotMatched("a/b", "c/a/b/c");
		assertNotMatched("a/b/", "c/a/b/");
		assertNotMatched("a/b/", "c/a/b/c");
	}

	@SuppressWarnings("boxing")
	@Test
	public void testWildmatch() {
		if (useJGitRule)
			System.err
					.println("IgnoreRule can't understand wildmatch rules, skipping testWildmatch!");

		Boolean assume = useJGitRule;
		assertMatched("**/a/b", "a/b", assume);
		assertMatched("**/a/b", "c/a/b", assume);
		assertMatched("**/a/b", "c/d/a/b", assume);
		assertMatched("**/**/a/b", "c/d/a/b", assume);

		assertMatched("a/b/**", "a/b", assume);
		assertMatched("a/b/**", "a/b/c", assume);
		assertMatched("a/b/**", "a/b/c/d/", assume);
		assertMatched("a/b/**/**", "a/b/c/d", assume);

		assertMatched("**/a/**/b", "c/d/a/b", assume);
		assertMatched("**/a/**/b", "c/d/a/e/b", assume);
		assertMatched("**/**/a/**/**/b", "c/d/a/e/b", assume);

		assertMatched("a/**/b", "a/b", assume);
		assertMatched("a/**/b", "a/c/b", assume);
		assertMatched("a/**/b", "a/c/d/b", assume);
		assertMatched("a/**/**/b", "a/c/d/b", assume);

		assertMatched("a/**/b/**/c", "a/c/b/d/c", assume);
		assertMatched("a/**/**/b/**/**/c", "a/c/b/d/c", assume);
	}

	@Test
	public void testWildmatchDoNotMatch() {
		assertNotMatched("**/a/b", "a/c/b");
		assertNotMatched("a/**/b", "a/c/bb");
	}

	@SuppressWarnings("unused")
	@Test
	public void testSimpleRules() {
		try {
			new FastIgnoreRule(null);
			fail("Illegal input allowed!");
		} catch (IllegalArgumentException e) {
			// expected
		}
		assertFalse(new FastIgnoreRule("/").isMatch("/", false));
		assertFalse(new FastIgnoreRule("//").isMatch("//", false));
		assertFalse(new FastIgnoreRule("#").isMatch("#", false));
		assertFalse(new FastIgnoreRule("").isMatch("", false));
		assertFalse(new FastIgnoreRule(" ").isMatch(" ", false));
	}

	@Test
	public void testSplit() {
		try {
			split("/", '/').toArray();
			fail("should not allow single slash");
		} catch (IllegalStateException e) {
			// expected
		}

		assertArrayEquals(new String[] { "a", "b" }, split("a/b", '/')
				.toArray());
		assertArrayEquals(new String[] { "a", "b/" }, split("a/b/", '/')
				.toArray());
		assertArrayEquals(new String[] { "/a", "b" }, split("/a/b", '/')
				.toArray());
		assertArrayEquals(new String[] { "/a", "b/" }, split("/a/b/", '/')
				.toArray());
		assertArrayEquals(new String[] { "/a", "b", "c" }, split("/a/b/c", '/')
				.toArray());
		assertArrayEquals(new String[] { "/a", "b", "c/" },
				split("/a/b/c/", '/').toArray());
	}

	public void assertMatched(String pattern, String path, Boolean... assume) {
		boolean match = match(pattern, path);
		String result = path + " is " + (match ? "ignored" : "not ignored")
				+ " via '" + pattern + "' rule";
		if (!match)
			System.err.println(result);
		if (assume.length == 0 || !assume[0].booleanValue())
			assertTrue("Expected a match for: " + pattern + " with: " + path,
					match);
		else
			assumeTrue("Expected a match for: " + pattern + " with: " + path,
					match);

		if (pattern.startsWith("!"))
			pattern = pattern.substring(1);
		else
			pattern = "!" + pattern;
		match = match(pattern, path);
		if (assume.length == 0 || !assume[0].booleanValue())
			assertFalse("Expected no match for: " + pattern + " with: " + path,
					match);
		else
			assumeFalse("Expected no match for: " + pattern + " with: " + path,
					match);
	}

	public void assertNotMatched(String pattern, String path, Boolean... assume) {
		boolean match = match(pattern, path);
		String result = path + " is " + (match ? "ignored" : "not ignored")
				+ " via '" + pattern + "' rule";
		if (match)
			System.err.println(result);
		if (assume.length == 0 || !assume[0].booleanValue())
			assertFalse("Expected no match for: " + pattern + " with: " + path,
					match);
		else
			assumeFalse("Expected no match for: " + pattern + " with: " + path,
					match);

		if (pattern.startsWith("!"))
			pattern = pattern.substring(1);
		else
			pattern = "!" + pattern;
		match = match(pattern, path);
		if (assume.length == 0 || !assume[0].booleanValue())
			assertTrue("Expected a match for: " + pattern + " with: " + path,
					match);
		else
			assumeTrue("Expected a match for: " + pattern + " with: " + path,
					match);
	}

	/**
	 * Check for a match. If target ends with "/", match will assume that the
	 * target is meant to be a directory.
	 *
	 * @param pattern
	 *            Pattern as it would appear in a .gitignore file
	 * @param target
	 *            Target file path relative to repository's GIT_DIR
	 * @return Result of {@link FastIgnoreRule#isMatch(String, boolean)}
	 */
	private boolean match(String pattern, String target) {
		boolean isDirectory = target.endsWith("/");
		if (useJGitRule.booleanValue()) {
			IgnoreRule r = new IgnoreRule(pattern);
			// If speed of this test is ever an issue, we can use a presetRule
			// field
			// to avoid recompiling a pattern each time.
			boolean match = r.isMatch(target, isDirectory);
			if (r.getNegation())
				match = !match;
			return match;
		}
		FastIgnoreRule r = new FastIgnoreRule(pattern);
		// If speed of this test is ever an issue, we can use a presetRule field
		// to avoid recompiling a pattern each time.
		boolean match = r.isMatch(target, isDirectory);
		if (r.getNegation())
			match = !match;
		return match;
	}
}
