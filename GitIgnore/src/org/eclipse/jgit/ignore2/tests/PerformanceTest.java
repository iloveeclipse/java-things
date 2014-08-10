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
import org.eclipse.jgit.ignore2.*;
import org.junit.*;
import org.junit.runners.MethodSorters;

import com.carrotsearch.junitbenchmarks.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@BenchmarkOptions(benchmarkRounds = 2000, warmupRounds = 1000)
public class PerformanceTest extends AbstractBenchmark {

	static List<String> longPaths = Arrays.asList(
			"0_abcdefghijklmnopqrstuvwxyz_/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/3_abcdefghijklmnopqrstuvwxyz_/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/3_abcdefghijklmnopqrstuvwxyz_/4_abcdefghijklmnopqrstuvwxyz_/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/3_abcdefghijklmnopqrstuvwxyz_/4_abcdefghijklmnopqrstuvwxyz_/5_abcdefghijklmnopqrstuvwxyz_/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/3_abcdefghijklmnopqrstuvwxyz_/4_abcdefghijklmnopqrstuvwxyz_/5_abcdefghijklmnopqrstuvwxyz_/6/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/3_abcdefghijklmnopqrstuvwxyz_/4_abcdefghijklmnopqrstuvwxyz_/5_abcdefghijklmnopqrstuvwxyz_/6/7/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/3_abcdefghijklmnopqrstuvwxyz_/4_abcdefghijklmnopqrstuvwxyz_/5_abcdefghijklmnopqrstuvwxyz_/6/7/8/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/3_abcdefghijklmnopqrstuvwxyz_/4_abcdefghijklmnopqrstuvwxyz_/5_abcdefghijklmnopqrstuvwxyz_/6/7/8/9/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/3_abcdefghijklmnopqrstuvwxyz_/4_abcdefghijklmnopqrstuvwxyz_/5_abcdefghijklmnopqrstuvwxyz_/6/7/8/9/10/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/3_abcdefghijklmnopqrstuvwxyz_/4_abcdefghijklmnopqrstuvwxyz_/5_abcdefghijklmnopqrstuvwxyz_/6/7/8/9/10/11/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/3_abcdefghijklmnopqrstuvwxyz_/4_abcdefghijklmnopqrstuvwxyz_/5_abcdefghijklmnopqrstuvwxyz_/6/7/8/9/10/11/12/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/3_abcdefghijklmnopqrstuvwxyz_/4_abcdefghijklmnopqrstuvwxyz_/5_abcdefghijklmnopqrstuvwxyz_/6/7/8/9/10/11/12/13/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/3_abcdefghijklmnopqrstuvwxyz_/4_abcdefghijklmnopqrstuvwxyz_/5_abcdefghijklmnopqrstuvwxyz_/6/7/8/9/10/11/12/13/14/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/3_abcdefghijklmnopqrstuvwxyz_/4_abcdefghijklmnopqrstuvwxyz_/5_abcdefghijklmnopqrstuvwxyz_/6/7/8/9/10/11/12/13/14/15/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/3_abcdefghijklmnopqrstuvwxyz_/4_abcdefghijklmnopqrstuvwxyz_/5_abcdefghijklmnopqrstuvwxyz_/6/7/8/9/10/11/12/13/14/15/16/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/3_abcdefghijklmnopqrstuvwxyz_/4_abcdefghijklmnopqrstuvwxyz_/5_abcdefghijklmnopqrstuvwxyz_/6/7/8/9/10/11/12/13/14/15/16/17/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/3_abcdefghijklmnopqrstuvwxyz_/4_abcdefghijklmnopqrstuvwxyz_/5_abcdefghijklmnopqrstuvwxyz_/6/7/8/9/10/11/12/13/14/15/16/17/18/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/3_abcdefghijklmnopqrstuvwxyz_/4_abcdefghijklmnopqrstuvwxyz_/5_abcdefghijklmnopqrstuvwxyz_/6/7/8/9/10/11/12/13/14/15/16/17/18/19/",
			"0_abcdefghijklmnopqrstuvwxyz_/1_abcdefghijklmnopqrstuvwxyz_/2_abcdefghijklmnopqrstuvwxyz_/3_abcdefghijklmnopqrstuvwxyz_/4_abcdefghijklmnopqrstuvwxyz_/5_abcdefghijklmnopqrstuvwxyz_/6/7/8/9/10/11/12/13/14/15/16/17/18/19/20/"
			);
	static List<String> shortPaths = Arrays.asList(
			"0_/",
			"0_/1_/",
			"0_/1_/2_/",
			"0_/1_/2_/3_/",
			"0_/1_/2_/3_/4_/",
			"0_/1_/2_/3_/4_/5_/",
			"0_/1_/2_/3_/4_/5_/6/",
			"0_/1_/2_/3_/4_/5_/6/7/",
			"0_/1_/2_/3_/4_/5_/6/7/8/",
			"0_/1_/2_/3_/4_/5_/6/7/8/9/",
			"0_/1_/2_/3_/4_/5_/6/7/8/9/10/",
			"0_/1_/2_/3_/4_/5_/6/7/8/9/10/11/",
			"0_/1_/2_/3_/4_/5_/6/7/8/9/10/11/12/",
			"0_/1_/2_/3_/4_/5_/6/7/8/9/10/11/12/13/",
			"0_/1_/2_/3_/4_/5_/6/7/8/9/10/11/12/13/14/",
			"0_/1_/2_/3_/4_/5_/6/7/8/9/10/11/12/13/14/15/",
			"0_/1_/2_/3_/4_/5_/6/7/8/9/10/11/12/13/14/15/16/",
			"0_/1_/2_/3_/4_/5_/6/7/8/9/10/11/12/13/14/15/16/17/",
			"0_/1_/2_/3_/4_/5_/6/7/8/9/10/11/12/13/14/15/16/17/18/",
			"0_/1_/2_/3_/4_/5_/6/7/8/9/10/11/12/13/14/15/16/17/18/19/",
			"0_/1_/2_/3_/4_/5_/6/7/8/9/10/11/12/13/14/15/16/17/18/19/20/"
			);

	static List<String> longPatterns;
	static {
		longPatterns = new ArrayList<String>();
		for (String path : longPaths) {
			longPatterns.add(path.replaceAll("_", "*"));
		}
	}
	static List<String> shortPatterns;
	static {
		shortPatterns = new ArrayList<String>();
		for (String path : shortPaths) {
			shortPatterns.add(path.replaceAll("_", "*"));
		}
	}

	@Test
	public void testOldRuleShort() {
		for (int i = 0; i < shortPatterns.size(); i++) {
			String pattern = shortPatterns.get(i);
			IgnoreRule r1 = new IgnoreRule(pattern);
			for (int j = i; j < shortPaths.size(); j++) {
				String path = shortPaths.get(j);
				boolean match = r1.isMatch(path, true);
				assertTrue(pattern + " does not match " + path, match);
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
				boolean match = r1.isMatch(path, true);
				assertTrue(pattern + " does not match " + path, match);
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
				boolean match = r1.isMatch(path, true);
				assertTrue(pattern + " does not match " + path, match);
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
				boolean match = r1.isMatch(path, true);
				assertTrue(pattern + " does not match " + path, match);
			}
		}
	}

}
