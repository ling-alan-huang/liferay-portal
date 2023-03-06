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

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.checkstyle.util.CheckstyleUtil;
import com.liferay.source.formatter.util.FileUtil;

import com.puppycrawl.tools.checkstyle.JavaParser;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.io.File;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class InstanceInitializerCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.INSTANCE_INIT};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		try {
			_checkVariableInitStyling(detailAST);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.OBJBLOCK) {
			return;
		}

		parentDetailAST = parentDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.LITERAL_NEW) {
			return;
		}

		DetailAST childDetailAST = detailAST.getFirstChild();

		if (childDetailAST.getType() != TokenTypes.SLIST) {
			return;
		}

		List<DetailAST> exprDetailASTList = getAllChildTokens(
			childDetailAST, false, TokenTypes.EXPR);

		if (exprDetailASTList.size() < 2) {
			return;
		}

		_checkAttributeOrder(exprDetailASTList);
	}

	private void _checkAttributeOrder(List<DetailAST> exprDetailASTList) {
		String previousVariableName = null;
		String previousMethodName = null;

		for (DetailAST exprDetailAST : exprDetailASTList) {
			DetailAST childDetailAST = exprDetailAST.getFirstChild();

			if (childDetailAST.getType() == TokenTypes.ASSIGN) {
				String variableName = getName(childDetailAST);

				if (Validator.isNotNull(
						getTypeName(
							getVariableTypeDetailAST(
								childDetailAST, variableName, false),
							false))) {

					continue;
				}

				if ((previousVariableName != null) &&
					(previousVariableName.compareToIgnoreCase(variableName) >
						0)) {

					log(
						exprDetailAST, _MSG_ASSIGN_ORDER_INCORRECT,
						variableName, previousVariableName,
						childDetailAST.getLineNo());
				}
				else if (Validator.isNotNull(previousMethodName)) {
					log(
						exprDetailAST, _MSG_MOVE_ASSIGN_BEFORE_METHOD_CALL,
						variableName, previousMethodName,
						childDetailAST.getLineNo());
				}

				previousVariableName = variableName;
			}
			else if (childDetailAST.getType() == TokenTypes.METHOD_CALL) {
				String methodName = getName(childDetailAST);

				if (Validator.isNull(methodName) ||
					!methodName.matches("set[A-Z].+")) {

					continue;
				}

				if ((previousMethodName != null) &&
					(previousMethodName.compareToIgnoreCase(methodName) > 0)) {

					log(
						exprDetailAST, _MSG_METHOD_CALL_ORDER_INCORRECT,
						methodName, previousMethodName,
						childDetailAST.getLineNo());
				}

				previousMethodName = methodName;
			}
		}
	}

	private void _checkIfStatement(
		DetailAST detailAST, DetailAST classDefDetailAST,
		DetailAST instanceInitDetailAST) {

		DetailAST sListDetailAST = detailAST.findFirstToken(TokenTypes.SLIST);

		DetailAST childDetailAST = sListDetailAST.getFirstChild();

		DetailAST assignDetailAST = null;

		while (childDetailAST != null) {
			int tokenType = childDetailAST.getType();

			if ((tokenType == TokenTypes.SEMI) ||
				(tokenType == TokenTypes.RCURLY)) {

				childDetailAST = childDetailAST.getNextSibling();

				continue;
			}

			if (tokenType != TokenTypes.EXPR) {
				return;
			}

			assignDetailAST = childDetailAST.getFirstChild();

			break;
		}

		if ((assignDetailAST == null) ||
			(assignDetailAST.getType() != TokenTypes.ASSIGN)) {

			return;
		}

		String assignName = getName(assignDetailAST);

		if (_checkIsLocalVariable(
				assignDetailAST, getStartLineNumber(instanceInitDetailAST),
				assignName) ||
			!_checkSameAssign(sListDetailAST, assignName)) {

			return;
		}

		if (_checkSupportsFunction(classDefDetailAST, assignName)) {
			log(
				detailAST, _MSG_SIMPLY_BY_CALL_LAMBDA,
				getStartLineNumber(detailAST), getEndLineNumber(detailAST),
				StringUtil.upperCaseFirstLetter(assignName));
		}
	}

	private boolean _checkIsLocalVariable(
		DetailAST assignDetailAST, int startLineNumber, String variableName) {

		DetailAST previousDetailAST = assignDetailAST.getParent();

		while (true) {
			if (previousDetailAST.getType() == TokenTypes.INSTANCE_INIT) {
				if (getStartLineNumber(previousDetailAST) == startLineNumber) {
					return false;
				}

				return true;
			}

			if ((previousDetailAST.getType() == TokenTypes.VARIABLE_DEF) &&
				StringUtil.equals(getName(previousDetailAST), variableName)) {

				return true;
			}

			DetailAST previousSiblingDetailAST =
				previousDetailAST.getPreviousSibling();

			if (previousSiblingDetailAST != null) {
				previousDetailAST = previousSiblingDetailAST;

				continue;
			}

			previousDetailAST = previousDetailAST.getParent();
		}
	}

	private void _checkMethodCall(
		DetailAST detailAST, DetailAST classDefDetailAST) {

		String methodName = getMethodName(detailAST);

		if (!methodName.matches("set[A-Z]\\w+")) {
			return;
		}

		DetailAST eListDetailAST = detailAST.findFirstToken(TokenTypes.ELIST);

		if (eListDetailAST.getChildCount() > 1) {
			return;
		}

		DetailAST eListChildDetailAST = eListDetailAST.getFirstChild();

		if (eListChildDetailAST == null) {
			return;
		}

		if (eListChildDetailAST.getType() == TokenTypes.LAMBDA) {
			DetailAST parametersDetailAST = eListChildDetailAST.findFirstToken(
				TokenTypes.PARAMETERS);

			if ((parametersDetailAST == null) ||
				(parametersDetailAST.getChildCount() != 0)) {

				return;
			}

			DetailAST exprDetailAST = eListChildDetailAST.findFirstToken(
				TokenTypes.EXPR);

			if (exprDetailAST == null) {
				return;
			}
		}

		String variableName = StringUtil.lowerCaseFirstLetter(
			methodName.substring(3));

		if (_checkVariableDirectAssignment(
				variableName, classDefDetailAST, methodName)) {

			log(detailAST, _MSG_DIRECT_ASSIGN, variableName, methodName);
		}
	}

	private boolean _checkSameAssign(DetailAST detailAST, String assignName) {
		if (Validator.isNull(assignName)) {
			return false;
		}

		while (detailAST != null) {
			if (detailAST.getType() == TokenTypes.SLIST) {
				break;
			}

			DetailAST firstChilddetailAST = detailAST.findFirstToken(
				TokenTypes.SLIST);

			if (firstChilddetailAST == null) {
				detailAST = detailAST.getFirstChild();
			}
			else {
				detailAST = firstChilddetailAST;
			}
		}

		if (detailAST == null) {
			return false;
		}

		DetailAST childDetailAST = detailAST.getFirstChild();

		int childCount = 0;

		while (childDetailAST != null) {
			if ((childDetailAST.getType() == TokenTypes.SEMI) ||
				(childDetailAST.getType() == TokenTypes.RCURLY)) {

				childDetailAST = childDetailAST.getNextSibling();

				continue;
			}

			childCount++;

			if ((childCount > 1) ||
				(childDetailAST.getType() != TokenTypes.EXPR)) {

				return false;
			}

			DetailAST exprChildDetailAST = childDetailAST.getFirstChild();

			if (exprChildDetailAST.getType() != TokenTypes.ASSIGN) {
				return false;
			}

			String variableName = getName(exprChildDetailAST);

			if (!StringUtil.equals(variableName, assignName)) {
				return false;
			}

			childDetailAST = childDetailAST.getNextSibling();
		}

		detailAST = detailAST.getNextSibling();

		if (detailAST != null) {
			return _checkSameAssign(detailAST, assignName);
		}

		return true;
	}

	private boolean _checkSupportsFunction(
		DetailAST detailAST, String variableName) {

		List<DetailAST> methodDefDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.METHOD_DEF);

		for (DetailAST methodDefDetailAST : methodDefDetailASTs) {
			String methodName = getName(methodDefDetailAST);

			if (!StringUtil.equals(
					"set" + StringUtil.upperCaseFirstLetter(variableName),
					methodName)) {

				continue;
			}

			DetailAST modifiersDetailAST = methodDefDetailAST.findFirstToken(
				TokenTypes.MODIFIERS);

			DetailAST modifiersFirstChildDetailAST =
				modifiersDetailAST.getLastChild();

			if ((modifiersFirstChildDetailAST != null) &&
				(modifiersFirstChildDetailAST.getType() ==
					TokenTypes.LITERAL_PRIVATE)) {

				continue;
			}

			List<DetailAST> parameterDefDetailASTs = getParameterDefs(
				methodDefDetailAST);

			if (ListUtil.isEmpty(parameterDefDetailASTs) ||
				(parameterDefDetailASTs.size() > 1)) {

				continue;
			}

			String parameterType = getTypeName(
				parameterDefDetailASTs.get(0), false);

			if (StringUtil.equals(parameterType, "UnsafeSupplier")) {
				return true;
			}
		}

		return false;
	}

	private boolean _checkVariableDirectAssignment(
		String variableName, DetailAST detailAST, String methodName) {

		DetailAST objBlockDetailAST = detailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		List<DetailAST> methodDefinitionDetailASTs = getAllChildTokens(
			objBlockDetailAST, false, TokenTypes.METHOD_DEF);

		boolean containMethod = false;

		for (DetailAST methodDefinitionDetailAST : methodDefinitionDetailASTs) {
			if (StringUtil.equals(
					getName(methodDefinitionDetailAST), methodName)) {

				containMethod = true;

				break;
			}
		}

		if (!containMethod) {
			return false;
		}

		DetailAST variableDefinitionDetailAST = getVariableDefinitionDetailAST(
			detailAST, variableName);

		if (variableDefinitionDetailAST == null) {
			return false;
		}

		DetailAST modifiersDetailAST =
			variableDefinitionDetailAST.findFirstToken(TokenTypes.MODIFIERS);

		DetailAST modifiersFirstChildDetailAST =
			modifiersDetailAST.getLastChild();

		if ((modifiersFirstChildDetailAST != null) &&
			(modifiersFirstChildDetailAST.getType() ==
				TokenTypes.LITERAL_PRIVATE)) {

			return false;
		}

		return true;
	}

	private void _checkVariableInitStyling(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		while ((parentDetailAST != null) &&
			   (parentDetailAST.getType() != TokenTypes.LITERAL_NEW)) {

			parentDetailAST = parentDetailAST.getParent();
		}

		if (parentDetailAST == null) {
			return;
		}

		DetailAST iDentDetailAST = parentDetailAST.findFirstToken(
			TokenTypes.IDENT);

		if (iDentDetailAST == null) {
			return;
		}

		List<String> importNames = getImportNames(detailAST);

		String className = iDentDetailAST.getText();
		String packageName = null;

		for (String importName : importNames) {
			if (importName.endsWith("." + className)) {
				className = importName;
				packageName = importName.substring(
					0, importName.lastIndexOf("."));

				break;
			}
		}

		if (StringUtil.equals(className, iDentDetailAST.getText()) ||
			!_isSameAppDTO(packageName, className)) {

			return;
		}

		DetailAST rootDetailAST = _getRootDetailAST(className);

		if ((rootDetailAST == null) ||
			!StringUtil.equals(packageName, getPackageName(rootDetailAST))) {

			return;
		}

		DetailAST sListDetailAST = detailAST.findFirstToken(TokenTypes.SLIST);

		DetailAST classDefDetailAST = _getClassDefDetailAST(rootDetailAST);

		DetailAST childDetailAST = sListDetailAST.getFirstChild();

		while (childDetailAST != null) {
			int tokenType = childDetailAST.getType();

			if ((tokenType == TokenTypes.SEMI) ||
				(tokenType == TokenTypes.RCURLY)) {

				childDetailAST = childDetailAST.getNextSibling();

				continue;
			}

			if (tokenType == TokenTypes.EXPR) {
				DetailAST grandChildDetailAST = childDetailAST.getFirstChild();

				if (grandChildDetailAST.getType() == TokenTypes.METHOD_CALL) {
					_checkMethodCall(grandChildDetailAST, classDefDetailAST);
				}
			}

			if (tokenType == TokenTypes.LITERAL_IF) {
				_checkIfStatement(childDetailAST, classDefDetailAST, detailAST);
			}

			childDetailAST = childDetailAST.getNextSibling();
		}
	}

	private DetailAST _getClassDefDetailAST(DetailAST rootDetailAST) {
		DetailAST nextDetailAST = rootDetailAST.getNextSibling();

		while (nextDetailAST != null) {
			if (nextDetailAST.getType() == TokenTypes.CLASS_DEF) {
				return nextDetailAST;
			}

			nextDetailAST = nextDetailAST.getNextSibling();
		}

		return null;
	}

	private String _getClassPath(String className) {
		String absolutePath = StringUtil.replace(
			getAbsolutePath(), CharPool.BACK_SLASH, CharPool.SLASH);

		int pos = absolutePath.lastIndexOf("/src/main");

		if (pos == -1) {
			return null;
		}

		absolutePath = absolutePath.substring(0, pos);

		pos = absolutePath.lastIndexOf("/");

		if (pos == -1) {
			return null;
		}

		String currentFolderName = absolutePath.substring(pos + 1);

		if (Validator.isNull(currentFolderName) ||
			!currentFolderName.endsWith("-impl")) {

			return null;
		}

		return StringBundler.concat(
			absolutePath.substring(0, pos + 1),
			StringUtil.replaceFirst(
				currentFolderName, "impl", "api",
				currentFolderName.lastIndexOf("-impl")),
			"/src/main/java/",
			StringUtil.replace(className, CharPool.PERIOD, CharPool.SLASH),
			".java");
	}

	private DetailAST _getRootDetailAST(String className) {
		String classPath = _getClassPath(className);

		if (Validator.isNull(classPath)) {
			return null;
		}

		File file = new File(classPath);

		if (!file.exists()) {
			return null;
		}

		try {
			String content = FileUtil.read(file);

			FileText fileText = new FileText(
				file, CheckstyleUtil.getLines(content));

			FileContents fileContents = new FileContents(fileText);

			return JavaParser.parse(fileContents);
		}
		catch (Exception exception) {
			return null;
		}
	}

	private boolean _isSameAppDTO(String packageName, String className) {
		Matcher matcher = _appRootNamePattern.matcher(packageName);

		if (matcher.find()) {
			return className.startsWith(matcher.group());
		}

		return false;
	}

	private static final String _MSG_ASSIGN_ORDER_INCORRECT =
		"assign.incorrect.order";

	private static final String _MSG_DIRECT_ASSIGN = "direct.assign";

	private static final String _MSG_METHOD_CALL_ORDER_INCORRECT =
		"method.call.incorrect.order";

	private static final String _MSG_MOVE_ASSIGN_BEFORE_METHOD_CALL =
		"assign.move.before.method.call";

	private static final String _MSG_SIMPLY_BY_CALL_LAMBDA =
		"simply.by.call.lambda";

	private static final Pattern _appRootNamePattern = Pattern.compile(
		"com\\.liferay\\.\\w+\\.");

}