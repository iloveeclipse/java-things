/*******************************************************************************
 * Copyright (c) 2014 Andrey Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributor:  Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package org.eclipse.jgit.ignore2.tests;

import static org.junit.Assert.assertTrue;

import java.util.*;

import org.eclipse.jgit.ignore.IgnoreRule;
import org.eclipse.jgit.ignore2.FastIgnoreRule;
import org.junit.*;
import org.junit.runners.MethodSorters;

import com.carrotsearch.junitbenchmarks.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@BenchmarkOptions(benchmarkRounds = 2000, warmupRounds = 1000)
public class PerformanceTest extends AbstractBenchmark {

	static List<String> longPaths = Arrays.asList(
			"0_abcdefghijklmnopqrstuvwxyz",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz/3_abcdefghijklmnopqrstuvwxyz",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz/3_abcdefghijklmnopqrstuvwxyz/4_abcdefghijklmnopqrstuvwxyz",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz/3_abcdefghijklmnopqrstuvwxyz/4_abcdefghijklmnopqrstuvwxyz/5_abcdefghijklmnopqrstuvwxyz",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz/3_abcdefghijklmnopqrstuvwxyz/4_abcdefghijklmnopqrstuvwxyz/5_abcdefghijklmnopqrstuvwxyz/6",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz/3_abcdefghijklmnopqrstuvwxyz/4_abcdefghijklmnopqrstuvwxyz/5_abcdefghijklmnopqrstuvwxyz/6/7",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz/3_abcdefghijklmnopqrstuvwxyz/4_abcdefghijklmnopqrstuvwxyz/5_abcdefghijklmnopqrstuvwxyz/6/7/8",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz/3_abcdefghijklmnopqrstuvwxyz/4_abcdefghijklmnopqrstuvwxyz/5_abcdefghijklmnopqrstuvwxyz/6/7/8/9",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz/3_abcdefghijklmnopqrstuvwxyz/4_abcdefghijklmnopqrstuvwxyz/5_abcdefghijklmnopqrstuvwxyz/6/7/8/9/10",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz/3_abcdefghijklmnopqrstuvwxyz/4_abcdefghijklmnopqrstuvwxyz/5_abcdefghijklmnopqrstuvwxyz/6/7/8/9/10/11",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz/3_abcdefghijklmnopqrstuvwxyz/4_abcdefghijklmnopqrstuvwxyz/5_abcdefghijklmnopqrstuvwxyz/6/7/8/9/10/11/12",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz/3_abcdefghijklmnopqrstuvwxyz/4_abcdefghijklmnopqrstuvwxyz/5_abcdefghijklmnopqrstuvwxyz/6/7/8/9/10/11/12/13",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz/3_abcdefghijklmnopqrstuvwxyz/4_abcdefghijklmnopqrstuvwxyz/5_abcdefghijklmnopqrstuvwxyz/6/7/8/9/10/11/12/13/14",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz/3_abcdefghijklmnopqrstuvwxyz/4_abcdefghijklmnopqrstuvwxyz/5_abcdefghijklmnopqrstuvwxyz/6/7/8/9/10/11/12/13/14/15",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz/3_abcdefghijklmnopqrstuvwxyz/4_abcdefghijklmnopqrstuvwxyz/5_abcdefghijklmnopqrstuvwxyz/6/7/8/9/10/11/12/13/14/15/16",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz/3_abcdefghijklmnopqrstuvwxyz/4_abcdefghijklmnopqrstuvwxyz/5_abcdefghijklmnopqrstuvwxyz/6/7/8/9/10/11/12/13/14/15/16/17",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz/3_abcdefghijklmnopqrstuvwxyz/4_abcdefghijklmnopqrstuvwxyz/5_abcdefghijklmnopqrstuvwxyz/6/7/8/9/10/11/12/13/14/15/16/17/18",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz/3_abcdefghijklmnopqrstuvwxyz/4_abcdefghijklmnopqrstuvwxyz/5_abcdefghijklmnopqrstuvwxyz/6/7/8/9/10/11/12/13/14/15/16/17/18/19",
			"0_abcdefghijklmnopqrstuvwxyz/1_abcdefghijklmnopqrstuvwxyz/2_abcdefghijklmnopqrstuvwxyz/3_abcdefghijklmnopqrstuvwxyz/4_abcdefghijklmnopqrstuvwxyz/5_abcdefghijklmnopqrstuvwxyz/6/7/8/9/10/11/12/13/14/15/16/17/18/19/20"
			);

	static List<String> shortPaths = Arrays.asList(
			"0_a",
			"0_a/1_a",
			"0_a/1_a/2_a",
			"0_a/1_a/2_a/3_a",
			"0_a/1_a/2_a/3_a/4_a",
			"0_a/1_a/2_a/3_a/4_a/5_a",
			"0_a/1_a/2_a/3_a/4_a/5_a/6",
			"0_a/1_a/2_a/3_a/4_a/5_a/6/7",
			"0_a/1_a/2_a/3_a/4_a/5_a/6/7/8",
			"0_a/1_a/2_a/3_a/4_a/5_a/6/7/8/9",
			"0_a/1_a/2_a/3_a/4_a/5_a/6/7/8/9/10",
			"0_a/1_a/2_a/3_a/4_a/5_a/6/7/8/9/10/11",
			"0_a/1_a/2_a/3_a/4_a/5_a/6/7/8/9/10/11/12",
			"0_a/1_a/2_a/3_a/4_a/5_a/6/7/8/9/10/11/12/13",
			"0_a/1_a/2_a/3_a/4_a/5_a/6/7/8/9/10/11/12/13/14",
			"0_a/1_a/2_a/3_a/4_a/5_a/6/7/8/9/10/11/12/13/14/15",
			"0_a/1_a/2_a/3_a/4_a/5_a/6/7/8/9/10/11/12/13/14/15/16",
			"0_a/1_a/2_a/3_a/4_a/5_a/6/7/8/9/10/11/12/13/14/15/16/17",
			"0_a/1_a/2_a/3_a/4_a/5_a/6/7/8/9/10/11/12/13/14/15/16/17/18",
			"0_a/1_a/2_a/3_a/4_a/5_a/6/7/8/9/10/11/12/13/14/15/16/17/18/19",
			"0_a/1_a/2_a/3_a/4_a/5_a/6/7/8/9/10/11/12/13/14/15/16/17/18/19/20"
			);

	/** long paths, matching without wildcards */
	static List<String> simplePatterns;
	static {
		simplePatterns = new ArrayList<String>();
		for (String path : longPaths) {
			simplePatterns.add(path);
		}
	}

	/** long paths, matching with 1 wildcard */
	static List<String> longPatterns;
	static {
		longPatterns = new ArrayList<String>();
		for (String path : longPaths) {
			longPatterns.add(path.replaceAll("0_", "*"));
		}
	}

	/** short paths, matching with 1 wildcard */
	static List<String> shortPatterns;
	static {
		shortPatterns = new ArrayList<String>();
		for (String path : shortPaths) {
			shortPatterns.add(path.replaceAll("0_", "*"));
		}
	}

	@Test
	public void testOldRuleShort() {
		for (int i = 0; i < shortPatterns.size(); i++) {
			String pattern = shortPatterns.get(i);
			IgnoreRule r1 = new IgnoreRule(pattern);
			for (int j = i; j < shortPaths.size(); j++) {
				String path = shortPaths.get(j);
				boolean match = r1.isMatch(path, false);
				assertTrue(path, match);
			}
		}
	}

	@Test
	public void testOldRuleSimple() {
		for (int i = 0; i < simplePatterns.size(); i++) {
			String pattern = simplePatterns.get(i);
			IgnoreRule r1 = new IgnoreRule(pattern);
			for (int j = i; j < longPaths.size(); j++) {
				String path = longPaths.get(j);
				boolean match = r1.isMatch(path, false);
				assertTrue(path, match);
			}
		}
	}

	@Test
	public void testOldRuleLong() {
		for (int i = 0; i < longPatterns.size(); i++) {
			String pattern = longPatterns.get(i);
			IgnoreRule r1 = new IgnoreRule(pattern);
			for (int j = i; j < longPaths.size(); j++) {
				String path = longPaths.get(j);
				boolean match = r1.isMatch(path, false);
				assertTrue(path, match);
			}
		}
	}

	@Test
	public void testFastRuleShort() {
		for (int i = 0; i < shortPatterns.size(); i++) {
			String pattern = shortPatterns.get(i);
			FastIgnoreRule r1 = new FastIgnoreRule(pattern);
			for (int j = i; j < shortPaths.size(); j++) {
				String path = shortPaths.get(j);
				boolean match = r1.isMatch(path, false);
				assertTrue(path, match);
			}
		}
	}

	@Test
	public void testFastRuleSimple() {
		for (int i = 0; i < simplePatterns.size(); i++) {
			String pattern = simplePatterns.get(i);
			FastIgnoreRule r1 = new FastIgnoreRule(pattern);
			for (int j = i; j < longPaths.size(); j++) {
				String path = longPaths.get(j);
				boolean match = r1.isMatch(path, false);
				assertTrue(path, match);
			}
		}
	}

	@Test
	public void testFastRuleLong() {
		for (int i = 0; i < longPatterns.size(); i++) {
			String pattern = longPatterns.get(i);
			FastIgnoreRule r1 = new FastIgnoreRule(pattern);
			for (int j = i; j < longPaths.size(); j++) {
				String path = longPaths.get(j);
				boolean match = r1.isMatch(path, false);
				assertTrue(path, match);
			}
		}
	}

}
