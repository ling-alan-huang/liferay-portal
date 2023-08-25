/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peter Shin
 */
public class PropertiesSourceFormatterContentCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (fileName.endsWith("/source-formatter.properties")) {
			content = _checkConvertedKeys(content);
			content = _checkGitLiferayPortalBranch(content);
		}

		_sortByRootSourceFormatter(fileName, content);

		return content;
	}

	private String _checkConvertedKeys(String content) {
		for (String[] array : _CONVERTED_KEYS) {
			content = StringUtil.replace(content, array[0], array[1]);
		}

		return content;
	}

	private String _checkGitLiferayPortalBranch(String content) {
		Matcher matcher = _gitLiferayPortalBranchPattern.matcher(content);

		if (matcher.find()) {
			return StringUtil.replaceFirst(
				content, matcher.group(1), StringPool.BLANK, matcher.start());
		}

		return content;
	}

	private synchronized List<String> _getRootSourceFormatterCategory()
		throws IOException {

		if (_rootSourceFormatterCategory != null) {
			return _rootSourceFormatterCategory;
		}

		_rootSourceFormatterCategory = new ArrayList<>();

		File file = new File(getPortalDir() + "/source-formatter.properties");

		if (!file.exists()) {
			return _rootSourceFormatterCategory;
		}

		String content = FileUtil.read(file);

		Matcher matcher = _categoryCommentPattern.matcher(content);

		while (matcher.find()) {
			_rootSourceFormatterCategory.add(StringUtil.trim(matcher.group(1)));
		}

		return _rootSourceFormatterCategory;
	}

	private void _sortByRootSourceFormatter(String fileName, String content)
		throws IOException {

		String path = getPortalDir().getCanonicalPath();

		path = StringUtil.replace(path, CharPool.BACK_SLASH, CharPool.SLASH);

		if (fileName.equals(path + "/source-formatter.properties")) {
			return;
		}

		List<String> rootSourceFormatterCategory =
			_getRootSourceFormatterCategory();

		if (rootSourceFormatterCategory.isEmpty()) {
			return;
		}

		Matcher matcher = _categoryCommentPattern.matcher(content);

		int preIndex = -1;

		while (matcher.find()) {
			int index = rootSourceFormatterCategory.indexOf(
				StringUtil.trim(matcher.group(1)));

			if (index == -1) {
				continue;
			}

			if ((preIndex != -1) && (index < preIndex)) {
				addMessage(
					fileName,
					StringBundler.concat(
						"Comment category '",
						rootSourceFormatterCategory.get(preIndex), "' and '",
						rootSourceFormatterCategory.get(index),
						"' should follow root source-formatter.properties ",
						"sort"));

				return;
			}

			preIndex = index;
		}
	}

	private static final String[][] _CONVERTED_KEYS = {
		{
			"blob/master/portal-impl/src/source-formatter.properties",
			"blob/master/source-formatter.properties"
		}
	};

	private static final Pattern _categoryCommentPattern = Pattern.compile(
		"##\n##(.+)(\n##.*)+");
	private static final Pattern _gitLiferayPortalBranchPattern =
		Pattern.compile("\\sgit\\.liferay\\.portal\\.branch=(\\\\\\s+)");
	private static List<String> _rootSourceFormatterCategory;

}