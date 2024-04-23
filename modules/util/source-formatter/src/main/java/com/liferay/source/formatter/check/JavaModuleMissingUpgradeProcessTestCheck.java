/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.BNDSourceUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class JavaModuleMissingUpgradeProcessTestCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (!absolutePath.contains("/upgrade/") ||
			!_hasExtendedJavaClass(absolutePath, content, "UpgradeProcess")) {

			return content;
		}

		String testFilePath = StringUtil.replaceFirst(
			absolutePath, "-service/src/main/", "-test/src/testIntegration/");

		int x = testFilePath.lastIndexOf("/");

		testFilePath = StringUtil.insert(testFilePath, "/test", x);

		testFilePath = StringUtil.insert(
			testFilePath, "Test", testFilePath.length() - 5);

		File file = new File(testFilePath);

		if (!file.exists()) {
			addMessage(fileName, "Test class does not exist: " + testFilePath);
		}

		return content;
	}

	private synchronized Map<String, String> _getBundleSymbolicNamesMap(
		String absolutePath) {

		if (_bundleSymbolicNamesMap != null) {
			return _bundleSymbolicNamesMap;
		}

		_bundleSymbolicNamesMap = BNDSourceUtil.getBundleSymbolicNamesMap(
			SourceUtil.getRootDirName(absolutePath));

		return _bundleSymbolicNamesMap;
	}

	private boolean _hasExtendedJavaClass(
		String absolutePath, String content, String className) {

		Matcher matcher = _extendedClassPattern.matcher(content);

		if (!matcher.find()) {
			return false;
		}

		String extendedClassName = matcher.group(1);

		if (extendedClassName.equals(className)) {
			return true;
		}

		Pattern pattern = Pattern.compile(
			"\nimport (.*\\." + extendedClassName + ");");

		matcher = pattern.matcher(content);

		if (matcher.find()) {
			extendedClassName = matcher.group(1);
		}

		if (!extendedClassName.contains(StringPool.PERIOD)) {
			extendedClassName =
				JavaSourceUtil.getPackageName(content) + StringPool.PERIOD +
					extendedClassName;
		}

		if (!extendedClassName.startsWith("com.liferay.")) {
			return false;
		}

		File file = JavaSourceUtil.getJavaFile(
			extendedClassName, SourceUtil.getRootDirName(absolutePath),
			_getBundleSymbolicNamesMap(absolutePath));

		if (file == null) {
			return false;
		}

		return _hasExtendedJavaClass(
			file.getAbsolutePath(), FileUtil.read(file), className);
	}

	private static final Pattern _extendedClassPattern = Pattern.compile(
		"\\sextends\\s+(\\w+)\\W");

	private Map<String, String> _bundleSymbolicNamesMap;

}