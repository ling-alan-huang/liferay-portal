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

package com.liferay.language.security.scan.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.liferay.language.security.scan.LanguageSecurityScan;
import com.liferay.language.security.scan.LanguageSecurityScanArges;

/**
 * @author Seiphon Wang
 */
public class LanguageSecurityScanTask extends Task {

	@Override
	public void execute() throws BuildException {
		Project project = getProject();

		LanguageSecurityScan languageSecurityScan =
			new LanguageSecurityScan(_languageSecurityScanArges);

		languageSecurityScan.scan();

		project.addIdReference(
			LanguageSecurityScanArges.OUTPUT_KEY_SANTIZED_FILES,
			languageSecurityScan.getSantizedFiles());

		super.execute();
	}

	public void setBaseDirName(String baseDirName) {
		_languageSecurityScanArges.setBaseDirName(baseDirName);
	}

	public void setScanAll(boolean scanAll) {
		_languageSecurityScanArges.setScanAll(scanAll);
	}

	private final LanguageSecurityScanArges _languageSecurityScanArges =
		new LanguageSecurityScanArges();
}