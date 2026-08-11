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

		if (_matchesMethodCall(detailAST, variableName)) {
			return;
		}

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

			if (typeName.equals(firstGenericTypeName)) {
				if (!variableName.endsWith(typeName)) {
					log(
						detailAST, _MSG_INCORRECT_ENDING_VARIABLE_1,
						variableName, typeName);
				}

				return;
			}

			if (variableName.endsWith("Collection") ||
				variableName.endsWith("List") || variableName.endsWith("Set")) {

				log(
					detailAST, _MSG_INCORRECT_ENDING_VARIABLE_2, variableName,
					typeName);

				return;
			}

			if ((firstGenericTypeName.length() == 1) ||
				ArrayUtil.contains(
					_PRIMITIVE_WRAPPER_NAMES, firstGenericTypeName) ||
				firstGenericTypeName.equals("Array") ||
				firstGenericTypeName.equals("Collection") ||
				firstGenericTypeName.equals("List") ||
				firstGenericTypeName.equals("Set") ||
				firstGenericTypeName.equals("Map") ||
				firstGenericTypeName.equals("Class") ||
				firstGenericTypeName.equals("Dictionary") ||
				firstGenericTypeName.equals("Object") ||
				firstGenericTypeName.equals("Serializable") ||
				firstGenericTypeName.equals("String") ||
				firstGenericTypeName.equals("Tuple")) {

				return;
			}

			String absolutePath = getAbsolutePath();

			if ((absolutePath.contains("/journal/") &&
				 firstGenericTypeName.startsWith("Journal")) ||
				(absolutePath.contains("/knowledge-base/") &&
				 firstGenericTypeName.startsWith("KB")) ||
				(absolutePath.contains("/message-boards/") &&
				 firstGenericTypeName.startsWith("MB"))) {

				return;
			}

			String expectedVariableNameSuffix = _getExpectedVariableNameSuffix(
				firstGenericTypeName, typeName);

			if (variableName.matches(
					"(?i).*" + expectedVariableNameSuffix + "[0-9]*") ||
				(firstGenericTypeName.equals("ObjectValuePair") &&
				 variableName.matches("(?i).*OVPs[0-9]*"))) {

				return;
			}

			log(
				detailAST, _MSG_INCORRECT_ENDING_VARIABLE_1, variableName,
				expectedVariableNameSuffix, expectedVariableNameSuffix);
		}
	}

	private String _getExpectedVariableNameSuffix(
		String firstGenericTypeName, String typeName) {

		String lastWord = _getLastWord(firstGenericTypeName);

		if (lastWord.equals("Data") || lastWord.equals("Preferences") ||
			lastWord.equals("Settings") || lastWord.equals("Values") ||
			lastWord.equals("Variables")) {

			return lastWord + typeName;
		}

		lastWord = StringUtil.toLowerCase(lastWord);

		JSONObject irregularPluralNounsJSONObject =
			_getIrregularPluralNounsJSONObject();

		String pluralNoun = irregularPluralNounsJSONObject.getString(lastWord);

		if (Validator.isBlank(pluralNoun)) {
			pluralNoun = TextFormatter.formatPlural(lastWord);
		}

		return StringUtil.upperCaseFirstLetter(pluralNoun);
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

	private String _getLastWord(String s) {
		int x = s.length();

		do {
			x = x - 1;

			char c = s.charAt(x);

			if (Character.isUpperCase(c)) {
				break;
			}
		}
		while (x > 0);

		return s.substring(x);
	}

	private String _getVariableName(DetailAST variableDefinitionDetailAST) {
		DetailAST nameDetailAST = variableDefinitionDetailAST.findFirstToken(
			TokenTypes.IDENT);

		return nameDetailAST.getText();
	}

	private boolean _matchesMethodCall(
		DetailAST detailAST, String variableName) {

		DetailAST assignDetailAST = detailAST.findFirstToken(TokenTypes.ASSIGN);

		if (assignDetailAST == null) {
			return false;
		}

		DetailAST firstChildDetailAST = assignDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.EXPR)) {

			return false;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if (firstChildDetailAST == null) {
			return false;
		}

		DetailAST methodCallDetailAST = null;

		if (firstChildDetailAST.getType() == TokenTypes.TYPECAST) {
			methodCallDetailAST = firstChildDetailAST.findFirstToken(
				TokenTypes.METHOD_CALL);
		}
		else if (firstChildDetailAST.getType() == TokenTypes.METHOD_CALL) {
			methodCallDetailAST = firstChildDetailAST;
		}

		if (methodCallDetailAST == null) {
			return false;
		}

		String methodName = getMethodName(methodCallDetailAST);

		if (!methodName.matches("_?get.*")) {
			return false;
		}

		int x = methodName.indexOf("get");

		String s = methodName.substring(x + 3);

		if (!Validator.isBlank(s) &&
			variableName.matches("(?i).*" + s + "[0-9]*")) {

			return true;
		}

		DetailAST firstParameterExprDetailAST = getFirstParameterExprDetailAST(
			methodCallDetailAST);

		if (firstParameterExprDetailAST == null) {
			return false;
		}

		firstChildDetailAST = firstParameterExprDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.STRING_LITERAL) {
			return false;
		}

		String parameter = firstChildDetailAST.getText();

		if (variableName.matches(
				"(?i).*" + StringUtil.unquote(parameter) + "[0-9]*")) {

			return true;
		}

		return false;
	}

	private static final String _MSG_INCORRECT_ENDING_VARIABLE_1 =
		"variable.incorrect.ending.1";

	private static final String _MSG_INCORRECT_ENDING_VARIABLE_2 =
		"variable.incorrect.ending.2";

	private static final String[] _PRIMITIVE_WRAPPER_NAMES = {
		"BigDecimal", "BigInteger", "Boolean", "Byte", "Character", "Double",
		"Float", "Integer", "Long", "Short"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		CollectionVariableNameCheck.class);

	private JSONObject _irregularPluralNounsJSONObject;

}