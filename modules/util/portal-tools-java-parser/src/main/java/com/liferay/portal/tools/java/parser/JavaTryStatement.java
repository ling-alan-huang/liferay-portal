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
	public void addResourceSpecification() {
		_hasResourceSpecification = true;
	}

	public void setResourceJavaVariableDefinitions(
		List<JavaVariableDefinition> resourceJavaVariableDefinitions) {

		_resourceJavaVariableDefinitions = resourceJavaVariableDefinitions;
	}

	@Override
	public String toString(
		String indent, String prefix, String suffix, int maxLineLength) {

		if (!_hasResourceSpecification) {
			return StringBundler.concat(indent, prefix, "try", suffix);
		}
//		if (_resourceJavaVariableDefinitions == null) {
//			return StringBundler.concat(indent, prefix, "try", suffix);
//		}

//		StringBundler sb = new StringBundler();
//
//		sb.append(indent);
//
//		indent = "\t" + indent;
//
//		append(
//			sb, _resourceJavaVariableDefinitions, "; ", indent,
//			prefix + "try (", ")" + suffix, maxLineLength);
//
//		return sb.toString();

		String originalIndent = indent;

		StringBundler sb = new StringBundler();

		sb.append(indent);

		indent = "\t" + indent;

		sb.append(indent);
		sb.append(prefix);
		sb.append("try");
		sb.append(" (\n");
		sb.append(NESTED_CODE_BLOCK);
		sb.append("\n");
		sb.append(originalIndent);
		sb.append(")");

		sb.append(suffix);

		return sb.toString();

	}

	protected static final String NESTED_CODE_BLOCK =
			"${JAVA_TRY_STATEMENT_NESTED_CODE_BLOCK}";


	private List<JavaVariableDefinition> _resourceJavaVariableDefinitions;
	private boolean _hasResourceSpecification;

}