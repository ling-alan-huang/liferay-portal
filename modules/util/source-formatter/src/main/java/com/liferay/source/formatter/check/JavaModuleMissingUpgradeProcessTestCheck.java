/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.GitUtil;
import com.liferay.source.formatter.SourceFormatterArgs;
import com.liferay.source.formatter.check.util.BNDSourceUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.processor.SourceProcessor;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class JavaModuleMissingUpgradeProcessTestCheck extends BaseFileCheck {

	@Override
	public boolean isModuleSourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!absolutePath.contains("/upgrade/") ||
			absolutePath.contains("-test/") ||
			(!absolutePath.contains("-service/src/main/") &&
			 !absolutePath.contains("-web/src/main/"))) {

			return content;
		}

		SourceProcessor sourceProcessor = getSourceProcessor();

		SourceFormatterArgs sourceFormatterArgs =
			sourceProcessor.getSourceFormatterArgs();

		for (String currentBranchAddedFileNames :
				_getCurrentBranchAddedFileName(sourceFormatterArgs)) {

			if (!absolutePath.endsWith(currentBranchAddedFileNames)) {
				continue;
			}

			if (!_hasExtendedJavaClass(
					absolutePath, content, "UpgradeProcess")) {

				return content;
			}

			String testFileName = StringUtil.replace(
				absolutePath,
				new String[] {"-service/src/main/", "-web/src/main/"},
				new String[] {
					"-test/src/testIntegration/", "-test/src/testIntegration/"
				});

			int x = testFileName.lastIndexOf("/");

			testFileName = StringUtil.insert(testFileName, "/test", x);

			testFileName = StringUtil.insert(
				testFileName, "Test", testFileName.length() - 5);

			File file = new File(testFileName);

			if (!file.exists()) {
				addMessage(
					fileName, "Test class does not exist: " + testFileName);
			}

			break;
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

	private synchronized List<String> _getCurrentBranchAddedFileName(
			SourceFormatterArgs sourceFormatterArgs)
		throws Exception {

		if (_currentBranchAddedFileNames != null) {
			return _currentBranchAddedFileNames;
		}

		_currentBranchAddedFileNames = GitUtil.getCurrentBranchAddedFileNames(
			sourceFormatterArgs.getBaseDirName(),
			sourceFormatterArgs.getGitWorkingBranchName());

		return _currentBranchAddedFileNames;
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
	private List<String> _currentBranchAddedFileNames;

}