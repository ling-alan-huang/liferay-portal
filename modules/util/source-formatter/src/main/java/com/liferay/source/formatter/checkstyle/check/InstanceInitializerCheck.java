/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.BNDSourceUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.util.FileUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.io.File;

import java.util.List;
import java.util.Map;
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

		_checkVariableAssign(
			detailAST, exprDetailASTList, parentDetailAST, getAbsolutePath());

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

	private void _checkVariableAssign(
		DetailAST detailAST, List<DetailAST> exprDetailASTList,
		DetailAST parentDetailAST, String absolutePath) {

		DetailAST identDetailAST = parentDetailAST.findFirstToken(
			TokenTypes.IDENT);

		String className = null;
		String packageName = null;

		if (identDetailAST == null) {
			DetailAST dotDetailAST = parentDetailAST.findFirstToken(
				TokenTypes.DOT);

			if (dotDetailAST == null) {
				return;
			}

			FullIdent fullIdent = FullIdent.createFullIdent(dotDetailAST);

			className = fullIdent.getText();

			packageName = className.substring(0, className.lastIndexOf("."));
		}
		else {
			className = identDetailAST.getText();

			List<String> importNames = getImportNames(detailAST);

			for (String importName : importNames) {
				if (importName.endsWith("." + className)) {
					className = importName;
					packageName = importName.substring(
						0, importName.lastIndexOf("."));

					break;
				}
			}

			if (StringUtil.equals(className, identDetailAST.getText())) {
				return;
			}
		}

		if (!_isSameAppDTO(getPackageName(detailAST), className)) {
			return;
		}

		File javaFile = JavaSourceUtil.getJavaFile(
			getFullyQualifiedTypeName(
				identDetailAST.getText(), detailAST, false),
			_getRootDirName(absolutePath),
			_getBundleSymbolicNamesMap(absolutePath));

		if (javaFile == null) {
			return;
		}

		JavaClass javaClass = null;

		try {
			javaClass = JavaClassParser.parseJavaClass(
				SourceUtil.getAbsolutePath(javaFile), FileUtil.read(javaFile));

			if (javaClass == null) {
				return;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		for (DetailAST exprDetailAST : exprDetailASTList) {
			DetailAST childDetailAST = exprDetailAST.getFirstChild();

			if (childDetailAST.getType() != TokenTypes.METHOD_CALL) {
				continue;
			}

			DetailAST elistDetailAST = childDetailAST.findFirstToken(
				TokenTypes.ELIST);

			DetailAST firstChildDetailAST = elistDetailAST.getFirstChild();

			if ((firstChildDetailAST == null) ||
				(firstChildDetailAST.getType() != TokenTypes.EXPR)) {

				continue;
			}

			String methodName = getMethodName(childDetailAST);

			if (!methodName.matches("set[A-Z]\\w*")) {
				continue;
			}

			String variableName = StringUtil.lowerCaseFirstLetter(
				methodName.substring(3));

			Pattern pattern = Pattern.compile(
				"\\s(\\S+)\\s+(\\S+\\.)?" + variableName);

			for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
				if (!javaTerm.isJavaVariable() || javaTerm.isPrivate()) {
					continue;
				}

				Matcher matcher = pattern.matcher(javaTerm.getContent());

				if (matcher.find()) {
					log(
						detailAST, _MSG_USE_ASSIGN_INSTEAD, javaTerm.getName(),
						methodName);
				}
			}
		}
	}

	private synchronized Map<String, String> _getBundleSymbolicNamesMap(
		String absolutePath) {

		if (_bundleSymbolicNamesMap == null) {
			_bundleSymbolicNamesMap = BNDSourceUtil.getBundleSymbolicNamesMap(
				_getRootDirName(absolutePath));
		}

		return _bundleSymbolicNamesMap;
	}

	private synchronized String _getRootDirName(String absolutePath) {
		if (_rootDirName != null) {
			return _rootDirName;
		}

		_rootDirName = SourceUtil.getRootDirName(absolutePath);

		return _rootDirName;
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

	private static final String _MSG_METHOD_CALL_ORDER_INCORRECT =
		"method.call.incorrect.order";

	private static final String _MSG_MOVE_ASSIGN_BEFORE_METHOD_CALL =
		"assign.move.before.method.call";

	private static final String _MSG_SIMPLY_BY_CALL_LAMBDA =
		"simply.by.call.lambda";

	private static final String _MSG_USE_ASSIGN_INSTEAD = "assign.use.instead";

	private static final Log _log = LogFactoryUtil.getLog(
		InstanceInitializerCheck.class);

	private static final Pattern _appRootNamePattern = Pattern.compile(
		"com\\.liferay\\.\\w+\\.");

	private volatile Map<String, String> _bundleSymbolicNamesMap;
	private volatile String _rootDirName;

}