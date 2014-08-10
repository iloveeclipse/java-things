/*******************************************************************************
 * Copyright (c) 2014 Andrey Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributor:  Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package org.eclipse.jgit.fnmatch2;

import static org.eclipse.jgit.fnmatch2.Strings.convertGlob;

import java.util.regex.Pattern;

import org.eclipse.jgit.errors.InvalidPatternException;

public class WildCardMatcher extends NameMatcher {

	final Pattern p;

	WildCardMatcher(String pattern, Character pathSeparator, boolean dirOnly) throws InvalidPatternException {
		super(pattern, pathSeparator, dirOnly);
		p = convertGlob(subPattern);
	}

	@Override
	public boolean matches(String segment, int startIncl, int endExcl, boolean dirOnly) {
		return p.matcher(new SubString(segment, startIncl, endExcl)).matches();
	}

	final static class SubString implements CharSequence {
		private final String parent;
		private final int startIncl;
		private final int length;

		public SubString(String parent, int startIncl, int endExcl) {
			this.parent = parent;
			this.startIncl = startIncl;
			this.length = endExcl - startIncl;
			if (startIncl < 0 || endExcl < 0 || length < 0 || startIncl + length > parent.length()) {
				throw new IndexOutOfBoundsException(
						"Trying to create substring on: " + parent
						+ " with invalid indices: " + startIncl + " / " + endExcl);
			}
		}

		@Override
		public final int length() {
			return length;
		}

		@Override
		public final char charAt(int index) {
			return parent.charAt(startIncl + index);
		}

		@Override
		public final CharSequence subSequence(int start, int end) {
			return new SubString(parent, startIncl + start, startIncl + end);
		}

		@Override
		public final String toString() {
			return parent.substring(startIncl, startIncl + length);
		}
	}
}
