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

package com.liferay.language.security.scanner.ant;

import com.liferay.language.security.scanner.LanguageSecurityScanner;
import com.liferay.language.security.scanner.LanguageSecurityScannerArges;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * @author Seiphon Wang
 */
public class LanguageSecurityScannerTask extends Task {

	@Override
	public void execute() throws BuildException {
		Project project = getProject();

		LanguageSecurityScanner languageSecurityScanner =
			new LanguageSecurityScanner(_languageSecurityScannerArges);

		languageSecurityScanner.scan();

		project.addIdReference(
			LanguageSecurityScannerArges.OUTPUT_KEY_SANTIZED_FILES,
			languageSecurityScanner.getSantizedFiles());

		super.execute();
	}

	public void setBaseDirName(String baseDirName) {
		_languageSecurityScannerArges.setBaseDirName(baseDirName);
	}

	public void setScanAll(boolean scanAll) {
		_languageSecurityScannerArges.setScanAll(scanAll);
	}

	private final LanguageSecurityScannerArges _languageSecurityScannerArges =
		new LanguageSecurityScannerArges();

}