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
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.SourceFormatterExcludes;
import com.liferay.source.formatter.checkstyle.util.CheckstyleUtil;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import com.puppycrawl.tools.checkstyle.JavaParser;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private boolean _checkIsLocalVariable(
		DetailAST assignDetailAST, int startLineNumber, String variableName) {

		DetailAST previousDetailAST = assignDetailAST.getParent();

		while (true) {
			if (previousDetailAST.getType() == TokenTypes.INSTANCE_INIT) {
				if (getStartLineNumber(previousDetailAST) == startLineNumber) {
					return true;
				}

				return false;
			}

			if ((previousDetailAST.getType() == TokenTypes.VARIABLE_DEF) &&
				StringUtil.equals(getName(previousDetailAST), variableName)) {

				return false;
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
				modifiersDetailAST.getFirstChild();

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

	private void _checkVariableInitStyling(DetailAST detailAST)
		throws Exception {

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
			!className.startsWith("com.liferay") ||
			className.startsWith("com.liferay.portal.kernel")) {

			return;
		}

		List<String> classNames = _getClassNames();

		String classPath = null;

		className = className.replaceAll("\\.", "/") + ".java";

		for (String curClassName : classNames) {
			if (curClassName.endsWith(className)) {
				classPath = curClassName;

				break;
			}
		}

		if (Validator.isNull(classPath)) {
			return;
		}

		DetailAST rootDetailAST = _getRootDetailAST(classPath);

		if ((rootDetailAST == null) ||
			!StringUtil.equals(packageName, getPackageName(rootDetailAST))) {

			return;
		}

		List<DetailAST> assignDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.ASSIGN);

		DetailAST sListDetailAST = detailAST.findFirstToken(TokenTypes.SLIST);

		DetailAST classDefDetailAST = _getClassDefDetailAST(rootDetailAST);

		for (DetailAST assignDetailAST : assignDetailASTs) {
			String variableName = getName(assignDetailAST);

			if (!_checkIsLocalVariable(
					assignDetailAST, getStartLineNumber(detailAST),
					variableName)) {

				continue;
			}

			DetailAST ifDetailAST = _getIfDetailAST(
				assignDetailAST, getStartLineNumber(sListDetailAST));

			if (ifDetailAST == null) {
				continue;
			}

			if (_checkSupportsFunction(classDefDetailAST, variableName)) {
				log(
					ifDetailAST, _MSG_SIMPLY_BY_CALL_LAMBDA,
					getStartLineNumber(ifDetailAST),
					getEndLineNumber(ifDetailAST),
					StringUtil.upperCaseFirstLetter(variableName));
			}
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

	private synchronized List<String> _getClassNames() throws Exception {
		String absolutePath = StringUtil.replace(
			getAbsolutePath(), CharPool.BACK_SLASH, CharPool.SLASH);

		int pos = absolutePath.lastIndexOf("/src/main");

		if (pos == -1) {
			return Collections.emptyList();
		}

		String currentFolderName = _getFolderName(
			absolutePath.substring(0, pos));

		if (Validator.isNull(currentFolderName)) {
			return Collections.emptyList();
		}

		pos = absolutePath.lastIndexOf("/", pos - 1);

		if (pos == -1) {
			return Collections.emptyList();
		}

		String currentFolderPath = absolutePath.substring(0, pos);

		if (_filePathMap.containsKey(currentFolderPath)) {
			return _filePathMap.get(currentFolderPath);
		}

		String parentFolderName = _getFolderName(
			absolutePath.substring(0, pos));

		if (Validator.isNull(parentFolderName) ||
			!currentFolderName.startsWith(parentFolderName)) {

			return Collections.emptyList();
		}

		List<String> files = SourceFormatterUtil.scanForFiles(
			absolutePath.substring(0, pos + 1), new String[0],
			new String[] {"**/com/liferay/**/*.java"},
			new SourceFormatterExcludes(), false);

		List<String> classNames = new ArrayList<>();

		for (String file : files) {
			classNames.add(
				StringUtil.replace(file, CharPool.BACK_SLASH, CharPool.SLASH));
		}

		_filePathMap.put(currentFolderPath, classNames);

		return classNames;
	}

	private String _getFolderName(String absolutePath) {
		int pos = absolutePath.lastIndexOf("/");

		if (pos == -1) {
			return null;
		}

		return absolutePath.substring(pos + 1);
	}

	private DetailAST _getIfDetailAST(
		DetailAST assignDetailAST, int sListStartLineNumber) {

		DetailAST parentDetailAST = assignDetailAST.getParent();

		while (parentDetailAST != null) {
			if (parentDetailAST.getType() != TokenTypes.SLIST) {
				parentDetailAST = parentDetailAST.getParent();

				continue;
			}

			if (getStartLineNumber(parentDetailAST) == sListStartLineNumber) {
				return null;
			}

			DetailAST sListDetailAST = parentDetailAST;

			parentDetailAST = parentDetailAST.getParent();

			DetailAST grandParentDetailAST = parentDetailAST.getParent();

			if (grandParentDetailAST == null) {
				return null;
			}

			if ((parentDetailAST.getType() != TokenTypes.LITERAL_IF) ||
				(grandParentDetailAST.getType() != TokenTypes.SLIST) ||
				(getStartLineNumber(grandParentDetailAST) !=
					sListStartLineNumber) ||
				!_checkSameAssign(sListDetailAST, getName(assignDetailAST))) {

				parentDetailAST = parentDetailAST.getParent();

				continue;
			}

			return parentDetailAST;
		}

		return null;
	}

	private DetailAST _getRootDetailAST(String fileName) {
		try {
			File file = new File(fileName);

			String content = FileUtil.read(file);

			FileText fileText = new FileText(
				file, CheckstyleUtil.getLines(content));

			FileContents fileContents = new FileContents(fileText);

			return JavaParser.parse(fileContents);
		}
		catch (CheckstyleException | IOException exception) {
			return null;
		}
	}

	private static final String _MSG_ASSIGN_ORDER_INCORRECT =
		"assign.incorrect.order";

	private static final String _MSG_METHOD_CALL_ORDER_INCORRECT =
		"method.call.incorrect.order";

	private static final String _MSG_MOVE_ASSIGN_BEFORE_METHOD_CALL =
		"assign.move.before.method.call";

	private static final String _MSG_SIMPLY_BY_CALL_LAMBDA =
		"simply.by.call.lambda";

	private final Map<String, List<String>> _filePathMap = new HashMap<>();

}