/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.tools.GitUtil;
import com.liferay.source.formatter.SourceFormatterArgs;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.processor.SourceProcessor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Seiphon Wang
 */
public class JavaCounterIncrementMethodCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String fileContent)
		throws Exception {

		SourceProcessor sourceProcessor = getSourceProcessor();

		SourceFormatterArgs sourceFormatterArgs =
			sourceProcessor.getSourceFormatterArgs();

		for (String currentBranchAddedFileName :
				_getCurrentBranchAddedFileName(sourceFormatterArgs)) {

			if (absolutePath.endsWith(currentBranchAddedFileName)) {
				Matcher matcher = _counterLocalServiceIncrementPattern.matcher(
					fileContent);

				while (matcher.find()) {
					String incrementMethodCall = JavaSourceUtil.getMethodCall(
						fileContent, matcher.start());

					List<String> parameterList =
						JavaSourceUtil.getParameterList(incrementMethodCall);

					if (parameterList.isEmpty()) {
						addMessage(
							fileName,
							"Use 'counterLocalService.increment" +
								"(Entity.class.getName())' instead of " +
									"'counterLocalService.increment()'",
							getLineNumber(fileContent, matcher.start()));
					}
				}
			}
		}

		return fileContent;
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

	private static final Pattern _counterLocalServiceIncrementPattern =
		Pattern.compile("counterLocalService\\.increment\\(");
	private static List<String> _currentBranchAddedFileNames;

}