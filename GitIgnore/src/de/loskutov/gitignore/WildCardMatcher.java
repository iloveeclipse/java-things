/*******************************************************************************
 * Copyright (c) 2014 Andrey Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributor:  Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package de.loskutov.gitignore;

import java.util.regex.Pattern;

public class WildCardMatcher extends NameMatcher {

	final Pattern p;
	WildCardMatcher(String pattern) {
		super(pattern);
		p = convert(subPattern);
	}

	private static Pattern convert(String subPattern) {
		String pat = subPattern.replaceAll("\\*", ".*");
		return Pattern.compile(pat);
	}

	@Override
	public boolean matches(String segment, int startIncl, int endExcl){
		return p.matcher(segment.subSequence(startIncl, endExcl)).matches();
	}
}
