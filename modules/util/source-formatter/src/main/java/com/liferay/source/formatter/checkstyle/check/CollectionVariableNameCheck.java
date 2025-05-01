/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Alan Huang
 */
public class CollectionVariableNameCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.PARAMETER_DEF, TokenTypes.RESOURCE,
			TokenTypes.VARIABLE_DEF
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if ((detailAST.findFirstToken(TokenTypes.ELLIPSIS) != null) ||
			AnnotationUtil.containsAnnotation(detailAST, "Deprecated")) {

			return;
		}

		DetailAST typeDetailAST = detailAST.findFirstToken(TokenTypes.TYPE);

		DetailAST firstChildDetailAST = typeDetailAST.getFirstChild();

		if (firstChildDetailAST == null) {
			return;
		}

		String typeName = getTypeName(typeDetailAST, true);

		int x = typeName.indexOf("<");

		if (x == -1) {
			return;
		}

		String variableName = _getVariableName(detailAST);

		_checkVariableNameSuffix(
			detailAST, typeName.substring(x), typeName.substring(0, x),
			variableName);
	}

	private void _checkVariableNameSuffix(
		DetailAST detailAST, String genericTypeNames, String typeName,
		String variableName) {

		if (typeName.equals("Collection") || typeName.equals("List") ||
			typeName.equals("Set")) {

			if (genericTypeNames.contains("[]")) {
				return;
			}

			String firstGenericTypeName = genericTypeNames.substring(
				1, genericTypeNames.length() - 1);

			int x = firstGenericTypeName.indexOf("<");

			if (x != -1) {
				firstGenericTypeName = firstGenericTypeName.substring(0, x);
			}

			int y = firstGenericTypeName.lastIndexOf(".");

			if (y != -1) {
				firstGenericTypeName = firstGenericTypeName.substring(y + 1);
			}

			if ((firstGenericTypeName.length() == 1) ||
				ArrayUtil.contains(
					_PRIMITIVE_WRAPPER_NAMES, firstGenericTypeName) ||
				firstGenericTypeName.equals("Class") ||
				firstGenericTypeName.equals("Dictionary") ||
				firstGenericTypeName.equals("Map") ||
				firstGenericTypeName.equals("Object") ||
				firstGenericTypeName.equals("Serializable") ||
				firstGenericTypeName.equals("String") ||
				firstGenericTypeName.equals("Tuple")) {

				return;
			}

			String expectedVariableNameSuffix = _getExpectedVariableNameSuffix(
				firstGenericTypeName, typeName);

			if (variableName.matches(
					"(?i).*" + expectedVariableNameSuffix + "[0-9]*")) {

				return;
			}

			log(
				detailAST, _MSG_INCORRECT_ENDING_VARIABLE, variableName,
				expectedVariableNameSuffix, expectedVariableNameSuffix);
		}
	}

	private String _getExpectedVariableNameSuffix(
		String firstGenericTypeName, String typeName) {

		int x = firstGenericTypeName.length();

		do {
			x = x - 1;

			char c = firstGenericTypeName.charAt(x);

			if (Character.isUpperCase(c)) {
				break;
			}
		}
		while (x > 0);

		String lastWord = firstGenericTypeName.substring(x);

		if ((typeName.endsWith("Collection") || typeName.endsWith("List") ||
			 typeName.endsWith("Set")) &&
			(lastWord.equals("Data") || lastWord.equals("Preferences") ||
			 lastWord.equals("Settings"))) {

			return lastWord + typeName;
		}

		lastWord = StringUtil.toLowerCase(lastWord);

		JSONObject irregularPluralNounsJSONObject =
			_getIrregularPluralNounsJSONObject();

		String pluralNoun = irregularPluralNounsJSONObject.getString(lastWord);

		if (Validator.isBlank(pluralNoun)) {
			pluralNoun = TextFormatter.formatPlural(lastWord);
		}

		if (x > 0) {
			pluralNoun = StringUtil.upperCaseFirstLetter(pluralNoun);
		}

		return firstGenericTypeName.substring(0, x) + pluralNoun;
	}

	private synchronized JSONObject _getIrregularPluralNounsJSONObject() {
		if (_irregularPluralNounsJSONObject != null) {
			return _irregularPluralNounsJSONObject;
		}

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"dependencies/irregular-plural-nouns.json");

		if (inputStream == null) {
			return new JSONObjectImpl();
		}

		try {
			_irregularPluralNounsJSONObject = new JSONObjectImpl(
				StringUtil.read(inputStream));
		}
		catch (IOException | JSONException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return new JSONObjectImpl();
		}

		return _irregularPluralNounsJSONObject;
	}

	private String _getVariableName(DetailAST variableDefinitionDetailAST) {
		DetailAST nameDetailAST = variableDefinitionDetailAST.findFirstToken(
			TokenTypes.IDENT);

		return nameDetailAST.getText();
	}

	private static final String _MSG_INCORRECT_ENDING_VARIABLE =
		"variable.incorrect.ending";

	private static final String[] _PRIMITIVE_WRAPPER_NAMES = {
		"BigDecimal", "BigInteger", "Boolean", "Byte", "Character", "Double",
		"Float", "Integer", "Long", "Short"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		CollectionVariableNameCheck.class);

	private JSONObject _irregularPluralNounsJSONObject;

}