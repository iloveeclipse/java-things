/*******************************************************************************
 * Copyright (c) 2014 Andrey Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributor:  Andrey Loskutov - initial API and implementation
 *******************************************************************************/
package de.loskutov.gitignore;

public class PathMatcher extends AbstractMatcher {

	PathMatcher(String pattern){
		this.pattern = pattern;
	}

	@Override
	public boolean matches(String s) {
		if(s.length() == 0){
			return false;
		}
		return pattern.equals(s);
	}

}
