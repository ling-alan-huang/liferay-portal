/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.comparator.PropertyNameComparator;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaVariable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class PropertiesTestFileCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!fileName.endsWith("/test.properties")) {
			return content;
		}

		String rootDirName = SourceUtil.getRootDirName(absolutePath);

		if (!rootDirName.equals(StringPool.BLANK) &&
			absolutePath.equals(rootDirName + "/test.properties")) {

			_checkTestPropertiesOrder(fileName, content);

			if (isAttributeValue(
					_CHECK_TESTRAY_MAIN_COMPONENT_NAME_KEY, absolutePath)) {

				_checkTestrayMainComponentName(fileName, absolutePath, content);
			}

			return _sortTestCategories(
				fileName, content, StringPool.BLANK,
				StringPool.POUND + StringPool.POUND);
		}

		content = _generateProperties(absolutePath, content);
		return content;
	}

	private class PropertiesCategory {

		public PropertiesCategory(String categoryName) {
			_categoryName = _categoryName;
		}

		public String getCategoryName() {
			return _categoryName;
		}

		private List<PropertiesCategory> subPropertiesCategories;

		public Map<String, List<String>> getPropertiesMap() {
			return propertiesMap;
		}

		public List<PropertiesCategory> getSubPropertiesCategories() {
			return subPropertiesCategories;
		}

		private Map<String, List<String>> propertiesMap;

		private String _categoryName;

	}

	private static final String _CATEGORIES_DATA_KEY =
			"categoriesData";


	private String _generateProperties(String absolutePath, String content) throws IOException {
		Map<String, List<String>> propertiesMap = new TreeMap<>(
				new PropertyNameComparator());

		Map<String, List<String>> categoriesDataMap = new HashMap<>();

		List<String> categoriesData = getAttributeValues(
				_CATEGORIES_DATA_KEY, absolutePath);

		for (String categoryData : categoriesData) {
			String parts[] = StringUtil.split(
					categoryData, StringPool.COLON);

			if (parts.length != 2) {
				continue;
			}

			categoriesDataMap.put(parts[0], ListUtil.fromString(parts[1], "->"));

		}


		try (UnsyncBufferedReader unsyncBufferedReader =
				 new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String key = null;
			String line = null;
			String value = null;

			String previousLine = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				line = line.trim();

				if (Validator.isNull(line) ||
						line.startsWith(StringPool.POUND)) {

					previousLine = line;
					continue;
				}

				int x = line.indexOf('=');

				if (x != -1 && previousLine != null && !previousLine.endsWith("\\")) {
					key = line.substring(0, x);

					if (propertiesMap.containsKey(key)) {
						return content;
					}

					value = line.substring(x + 1);

					if ((value != null) && !value.equals("\\")) {
						List<String> list = propertiesMap.get(key);

						if (list == null) {
							list = new ArrayList<>();
						}

						if (value.equals("[\\")) {
							value = StringUtil.removeLast(value, "\\");
						}

						list.add(value);

						propertiesMap.put(key, list);
					}
				}
				else {
					value = line;

					if (value.endsWith(",\\")) {
						value = value.substring(0, value.length() - 2);
					}

					if (key == null) {
						return content;
					}

					List<String> list = propertiesMap.get(key);

					if (list == null) {
						list = new ArrayList<>();
					}

					list.add(value);

					propertiesMap.put(key, list);
				}

				previousLine = line;

			}
		}

		for (Map.Entry<String, List<String>> entry1 :
				propertiesMap.entrySet()) {

			String propertyKey = entry1.getKey();

			for (Map.Entry<String, List<String>> entry2 :
					categoriesDataMap.entrySet()) {

				String propertyKeyInCategory = entry2.getKey();

				if (propertyKey.startsWith(propertyKeyInCategory)) {

				}
			}

		}
				return content;
	}


	private void _checkTestPropertiesOrder(String fileName, String content) {
		String commentCategory = null;
		String commentPrefix = null;
		String previousLine = null;
		String previousPropertyKey = null;

		String[] lines = content.split("\n");

		int lineNumber = 0;

		for (String line : lines) {
			lineNumber++;

			if ((line.startsWith("##") || line.startsWith("    #")) &&
				!line.contains("=")) {

				if (commentPrefix == null) {
					commentCategory = line;
					commentPrefix = line;

					continue;
				}

				if (line.startsWith(commentPrefix) && !line.contains("=")) {
					commentCategory += "\n" + line;

					continue;
				}
			}

			if ((commentCategory != null) &&
				commentCategory.startsWith(commentPrefix + "\n") &&
				commentCategory.endsWith("\n" + commentPrefix)) {

				commentCategory = null;
				commentPrefix = null;
				previousLine = null;
				previousPropertyKey = null;
			}

			if ((commentCategory != null) && !Validator.isBlank(line) &&
				!line.startsWith(StringPool.FOUR_SPACES)) {

				addMessage(
					fileName, "Incorrect indentation on line " + lineNumber);

				return;
			}

			int x = line.indexOf('=');

			if ((x == -1) ||
				((previousLine != null) && previousLine.endsWith("\\"))) {

				previousLine = line;

				continue;
			}

			String propertyKey = StringUtil.trimLeading(line.substring(0, x));

			if (propertyKey.startsWith(StringPool.POUND)) {
				propertyKey = propertyKey.substring(1);
			}

			if (Validator.isNull(previousPropertyKey)) {
				previousLine = line;
				previousPropertyKey = propertyKey;

				continue;
			}

			PropertyNameComparator propertyNameComparator =
				new PropertyNameComparator();

			int compare = propertyNameComparator.compare(
				previousPropertyKey, propertyKey);

			if (compare > 0) {
				addMessage(
					fileName,
					StringBundler.concat(
						"Incorrect order of properties: \"", propertyKey,
						"\" should come before \"", previousPropertyKey, "\""),
					lineNumber);
			}

			previousLine = line;
			previousPropertyKey = propertyKey;
		}
	}

	private void _checkTestrayMainComponentName(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!isModulesFile(absolutePath) ||
			absolutePath.contains("/modules/apps/archived/")) {

			return;
		}

		Properties properties = new Properties();

		properties.load(new StringReader(content));

		String testrayMainComponentName = properties.getProperty(
			"testray.main.component.name");

		if (testrayMainComponentName == null) {
			return;
		}

		List<String> testrayAllTeamsComponentNames =
			_getTestrayAllTeamsComponentNames();

		if (!testrayAllTeamsComponentNames.contains(testrayMainComponentName)) {
			addMessage(
				fileName,
				StringBundler.concat(
					"Property value \"", testrayMainComponentName,
					"\" does not exist in \"testray.team.*.component.names\" ",
					"in ", SourceUtil.getRootDirName(absolutePath),
					"/test.properties"));
		}
	}

	private synchronized List<String> _getTestrayAllTeamsComponentNames()
		throws IOException {

		if (_testrayAllTeamsComponentNames != null) {
			return _testrayAllTeamsComponentNames;
		}

		_testrayAllTeamsComponentNames = new ArrayList<>();

		File file = new File(getPortalDir(), "test.properties");

		if (!file.exists()) {
			return _testrayAllTeamsComponentNames;
		}

		Properties properties = new Properties();

		properties.load(new FileInputStream(file));

		List<String> testrayAvailableComponentNames = ListUtil.fromString(
			properties.getProperty("testray.available.component.names"),
			StringPool.COMMA);

		for (String testrayAvailableComponentName :
				testrayAvailableComponentNames) {

			if (!testrayAvailableComponentName.startsWith("${") &&
				!testrayAvailableComponentName.endsWith("}")) {

				continue;
			}

			String testrayTeamComponentName =
				testrayAvailableComponentName.substring(
					2, testrayAvailableComponentName.length() - 1);

			List<String> testrayTeamComponentNames = ListUtil.fromString(
				properties.getProperty(testrayTeamComponentName),
				StringPool.COMMA);

			if (ListUtil.isEmpty(testrayTeamComponentNames)) {
				continue;
			}

			_testrayAllTeamsComponentNames.addAll(testrayTeamComponentNames);
		}

		return _testrayAllTeamsComponentNames;
	}

	private String _sortTestCategories(
		String fileName, String content, String indent, String pounds) {

		String indentWithPounds = indent + pounds;

		CommentComparator comparator = new CommentComparator();

		Pattern pattern = Pattern.compile(
			StringBundler.concat(
				"((?<=\\A|\n\n)", indentWithPounds, "\n", indentWithPounds,
				"( .+)\n", indentWithPounds, "\n\n[\\s\\S]*?)(?=(\n\n",
				indentWithPounds, "\n|\\Z))"));

		Matcher matcher = pattern.matcher(content);

		String previousProperties = null;
		String previousPropertiesComment = null;
		int previousPropertiesStartPosition = -1;

		while (matcher.find()) {
			String properties = matcher.group(1);
			String propertiesComment = matcher.group(2);
			int propertiesStartPosition = matcher.start();

			if (pounds.length() == 2) {
				String newProperties = _sortTestCategories(
					fileName, properties, indent + StringPool.FOUR_SPACES,
					StringPool.POUND);

				if (!newProperties.equals(properties)) {
					return StringUtil.replaceFirst(
						content, properties, newProperties,
						propertiesStartPosition);
				}
			}

			if (Validator.isNull(previousProperties)) {
				previousProperties = properties;
				previousPropertiesComment = propertiesComment;
				previousPropertiesStartPosition = propertiesStartPosition;

				continue;
			}

			int value = comparator.compare(
				previousPropertiesComment, propertiesComment);

			if (value > 0) {
				content = StringUtil.replaceFirst(
					content, properties, previousProperties,
					propertiesStartPosition);
				content = StringUtil.replaceFirst(
					content, previousProperties, properties,
					previousPropertiesStartPosition);

				return content;
			}

			previousProperties = properties;
			previousPropertiesComment = propertiesComment;
			previousPropertiesStartPosition = propertiesStartPosition;
		}

		return content;
	}

	private static final String _CHECK_TESTRAY_MAIN_COMPONENT_NAME_KEY =
		"checkTestrayMainComponentName";

	private List<String> _testrayAllTeamsComponentNames;

	private class CommentComparator extends NaturalOrderStringComparator {

		@Override
		public int compare(String comment1, String comment2) {
			if (comment1.equals(" Default")) {
				return -1;
			}

			if (comment2.equals(" Default")) {
				return 1;
			}

			return super.compare(comment1, comment2);
		}

	}

}