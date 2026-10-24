/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shuyang Zhou
 */
public class JavaServiceTrackerCollectionsCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		_checkPropertyKeyOverload(fileName, content);

		return content;
	}

	private void _checkPropertyKeyOverload(String fileName, String content) {
		Matcher matcher = _openValueMapPattern.matcher(content);

		while (matcher.find()) {
			if (ToolsUtil.isInsideQuotes(content, matcher.start())) {
				continue;
			}

			List<String> parameters = JavaSourceUtil.splitParameters(
				JavaSourceUtil.getParameters(
					content.substring(matcher.start())));

			if (parameters.size() != 4) {
				continue;
			}

			String propertyKey = _getPropertyKey(
				_removeWhitespace(parameters.get(3)));

			if ((propertyKey == null) ||
				!_isPropertyKeyFilter(
					_removeWhitespace(parameters.get(2)), propertyKey) ||
				!_isStringKeyedServiceTrackerMap(
					content.substring(0, matcher.start()), content)) {

				continue;
			}

			addMessage(
				fileName,
				"Use the property key overload \"ServiceTrackerMapFactory." +
					matcher.group(1) + "(BundleContext, Class, String)\"",
				getLineNumber(content, matcher.start()));
		}
	}

	private String _getPropertyKey(String serviceReferenceMapper) {
		Matcher matcher = _propertyServiceReferenceMapperPattern.matcher(
			serviceReferenceMapper);

		if (matcher.matches()) {
			return matcher.group(1);
		}

		matcher = _propertyEmittingLambdaPattern.matcher(
			serviceReferenceMapper);

		if (matcher.matches()) {
			return matcher.group(3);
		}

		return null;
	}

	private boolean _isPropertyKeyFilter(
		String filterString, String propertyKey) {

		if (filterString.equals("null")) {
			return true;
		}

		if (propertyKey.startsWith("\"")) {
			return filterString.equals(
				"\"(" + propertyKey.substring(1, propertyKey.length() - 1) +
					"=*)\"");
		}

		return filterString.equals("\"(\"+" + propertyKey + "+\"=*)\"");
	}

	private boolean _isStringKeyedServiceTrackerMap(
		String beforeCall, String content) {

		Matcher matcher = _assignedVariableNamePattern.matcher(beforeCall);

		if (!matcher.find()) {
			return false;
		}

		Pattern pattern = Pattern.compile(
			"\\bServiceTrackerMap\\s*<\\s*String\\s*,[^;=(){}]+>\\s+" +
				matcher.group(1) + "\\s*[;=]");

		matcher = pattern.matcher(content);

		return matcher.find();
	}

	private String _removeWhitespace(String s) {
		return StringUtil.removeChars(s, ' ', '\n', '\t');
	}

	private static final Pattern _assignedVariableNamePattern = Pattern.compile(
		"(\\w+)\\s*=\\s*\\z");
	private static final Pattern _openValueMapPattern = Pattern.compile(
		"(?<![\\w.])ServiceTrackerMapFactory\\.\\s*" +
			"(openMultiValueMap|openSingleValueMap)\\(");
	private static final Pattern _propertyEmittingLambdaPattern =
		Pattern.compile(
			"\\((\\w+),(\\w+)\\)->\\2\\.emit\\(\\(String\\)\\1\\." +
				"getProperty\\((\"[^\"]+\"|[\\w.]+)\\)\\)");
	private static final Pattern _propertyServiceReferenceMapperPattern =
		Pattern.compile(
			"newPropertyServiceReferenceMapper<(?:String,[^()]+)?>\\(" +
				"(\"[^\"]+\"|[\\w.]+)\\)");

}