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

package com.liferay.source.formatter.checks;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.JavaImportsFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alan Huang
 */
public class JavaTestMissingInitializeKernelUtilClassTestRuleCheck
	extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (!absolutePath.contains("/test/") &&
			!fileName.contains("Test.java")) {

			return content;
		}

		List<String> imports = _getImports(content);

		if (imports.contains(
				"com.liferay.portal.kernel.test.rule.NewEnvTestRule") ||
			imports.contains(
				"com.liferay.portal.test.rule.AspectJNewEnvTestRule")) {

			return content;
		}

		if ((content.indexOf("@RunWith(PowerMockRunner.class)") != -1) ||
			(content.indexOf("@RunWith(MockitoJUnitRunner.class)") != -1) ||
			((content.indexOf("@RunWith(Arquillian.class)") != -1) &&
			 absolutePath.contains("/testIntegration/"))) {

			return content;
		}

		if (!imports.contains(
				"com.liferay.portal.kernel.test.rule." +
					"InitializeKernelUtilClassTestRule")) {

			content = _addInitializeKernelUtilClassTestRule(content);

			content = _removeUnneededMethods(content);
		}

		return content;
	}

	private String _addInitializeKernelUtilClassTestRule(String content) {
		int x = content.indexOf(StringPool.NEW_LINE + "import ");

		content = StringUtil.insert(
			content,
			StringPool.NEW_LINE +
				"import com.liferay.portal.kernel.test.rule." +
					"InitializeKernelUtilClassTestRule;",
			x);

		x = content.lastIndexOf("}");

		return StringUtil.insert(
			content,
			StringBundler.concat(
				StringPool.NEW_LINE, StringPool.TAB, "@ClassRule",
				StringPool.NEW_LINE, StringPool.TAB,
				"public static InitializeKernelUtilClassTestRule ",
				"initializeKernelUtilClassTestRule = ",
				"InitializeKernelUtilClassTestRule.INSTANCE;",
				StringPool.NEW_LINE),
			x);
	}

	private List<String> _getImports(String content) {
		List<String> imports = new ArrayList<>();

		String[] importLines = StringUtil.splitLines(
			JavaImportsFormatter.getImports(content));

		for (String importLine : importLines) {
			if (Validator.isNotNull(importLine)) {
				imports.add(importLine.substring(7, importLine.length() - 1));
			}
		}

		return imports;
	}

	private String _removeUnneededMethods(String content) {
		
		content.replaceFirst("\n\t+fileUtil.setFile", replacement)
		return content;
	}

}