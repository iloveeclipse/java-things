/*******************************************************************************
 * Copyright (c) 2014 Andrey Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributor:  Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package org.eclipse.jgit.fnmatch2;

public final class WildMatcher extends AbstractMatcher {

	static final String WILDMATCH = "**";

	WildMatcher() {
		super(WILDMATCH, false);
	}

	@Override
	public final boolean matches(String path) {
		return true;
	}

	@Override
	public final boolean matches(String segment, int startIncl, int endExcl) {
		return true;
	}

	@Override
	public final boolean isWildmatch() {
		return true;
	}

}
