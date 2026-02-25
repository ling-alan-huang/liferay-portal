/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.source.formatter.SourceFormatterExcludes;
import com.liferay.source.formatter.check.util.BNDSourceUtil;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;
import java.io.IOException;

import java.util.List;

/**
 * @author Alan Huang
 */
public class BNDUnnamedCheck extends BaseFileCheck {

	@Override
	public boolean isModuleSourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!fileName.endsWith("/bnd.bnd")) {
			return content;
		}

		String webContextPath = BNDSourceUtil.getDefinitionValue(
			content, "Web-ContextPath");

		if (webContextPath != null) {
			return content;
		}

		int x = absolutePath.lastIndexOf(CharPool.SLASH);

		List<String> javaFileNames = SourceFormatterUtil.scanForFileNames(
			absolutePath.substring(0, x + 1), new String[0],
			new String[] {"**/*.java"}, new SourceFormatterExcludes(), false);

		for (String javaFileName : javaFileNames) {
			String javaFileContent = FileUtil.read(new File(javaFileName));

			for (String className : _CLASS_NAMES) {
				if (!javaFileContent.contains("extends " + className)) {
					continue;
				}

				addMessage(fileName, fileName);
			}
		}

		return content;
	}

	private static final String[] _CLASS_NAMES = {"MVCPortlet"};

}