/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.gradle.plugins.language.security.scanner;

import java.util.ArrayList;
import java.util.List;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.JavaExec;

/**
 * @author Seiphon Wang
 */
public class LanguageSecurityScannerTask extends JavaExec {

	public LanguageSecurityScannerTask() {
		setMain(
			"com.liferay.language.security.scanner.LanguageSecurityScanner");
	}

	@Override
	public void exec() {
		setArgs(getCompleteArgs());

		super.exec();
	}

	@Input
	public boolean isScanAll() {
		return _scanAll;
	}

	public void setScanAll(boolean scanALl) {
		_scanAll = scanALl;
	}

	protected List<String> getCompleteArgs() {
		List<String> args = new ArrayList<>(getArgs());

		args.add("scan.all=" + isScanAll());

		return args;
	}

	private boolean _scanAll = true;

}