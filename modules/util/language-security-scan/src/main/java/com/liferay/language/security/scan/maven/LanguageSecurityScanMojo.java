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

package com.liferay.language.security.scan.maven;

import java.io.File;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.liferay.language.security.scan.LanguageSecurityScan;
import com.liferay.language.security.scan.LanguageSecurityScanArges;

/**
 * Invoke Liferay Language Security Scan to validate language property files.
 *
 * @author Andrea Di Giorgi
 * @goal format
 */
public class LanguageSecurityScanMojo extends AbstractMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		try {
			@SuppressWarnings("rawtypes")
			Map pluginContext = getPluginContext();

			LanguageSecurityScan languageSecurityScan =
				new LanguageSecurityScan(_languageSecurityScanArges);

			languageSecurityScan.scan();

			pluginContext.put(
				LanguageSecurityScanArges.OUTPUT_KEY_SANTIZED_FILES,
				languageSecurityScan.getSantizedFiles());
		}
		catch (Exception exception) {
			throw new MojoExecutionException(exception.getMessage(), exception);
		}
	}

	/**
	 * @parameter
	 */
	public void setBaseDirName(String baseDirName) {
		_languageSecurityScanArges.setBaseDirName(baseDirName);
	}

	/**
	 * @parameter
	 */
	public void setScanAll(boolean scanALl) {
		_languageSecurityScanArges.setScanAll(scanALl);
	}

	/**
	 * @parameter default-value="${project.basedir}"
	 * @readonly
	 */
	protected File baseDir;

	private final LanguageSecurityScanArges _languageSecurityScanArges =
		new LanguageSecurityScanArges();
}