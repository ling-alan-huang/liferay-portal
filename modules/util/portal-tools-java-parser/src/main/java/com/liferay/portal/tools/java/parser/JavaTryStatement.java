/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.java.parser;

import com.liferay.petra.string.StringBundler;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class JavaTryStatement extends BaseJavaTerm {

	public void setResourceJavaVariableDefinitions(
		List<JavaVariableDefinition> resourceJavaVariableDefinitions) {

		_resourceJavaVariableDefinitions = resourceJavaVariableDefinitions;
	}

	@Override
	public String toString(
		String indent, String prefix, String suffix, int maxLineLength) {

		if (_resourceJavaVariableDefinitions == null) {
			return StringBundler.concat(indent, prefix, "try", suffix);
		}

		StringBundler sb = new StringBundler();

		int size = _resourceJavaVariableDefinitions.size();

		for (int i = 0; i < size; i++) {
			JavaVariableDefinition resourceJavaVariableDefinition =
				_resourceJavaVariableDefinitions.get(i);

			appendNewLine(
				sb, resourceJavaVariableDefinition,
				(i == 0) ? indent : indent + "\t",
				(i == 0) ? prefix + "try (" : "",
				(i == (size - 1)) ? ")" + suffix : ";", maxLineLength,
				(i > 0) &&
				resourceJavaVariableDefinition.hasPrecedingBlankLine());
		}

		return sb.toString();
	}

	private List<JavaVariableDefinition> _resourceJavaVariableDefinitions;

}