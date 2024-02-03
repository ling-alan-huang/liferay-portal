/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.BNDSourceUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.ParseException;
import com.liferay.source.formatter.util.FileUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Map;

/**
 * @author Alan Huang
 */
public class RestDTOSetterCallCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/")) {

			return;
		}

		for (DetailAST childDetailAST :
				getAllChildTokens(detailAST, true, TokenTypes.METHOD_CALL)) {

			_checkExprStatement(absolutePath, childDetailAST);
		}
	}

	private void _checkExprStatement(String absolutePath, DetailAST detailAST) {
		DetailAST dotDetailAST = detailAST.findFirstToken(TokenTypes.DOT);

		if (dotDetailAST == null) {
			return;
		}

		String methodName = getMethodName(detailAST);

		if (!methodName.startsWith("set")) {
			return;
		}

		String variableName = getVariableName(detailAST);

		String variableTypeName = getVariableTypeName(
			detailAST, variableName, false, false, true);

		JavaClass javaClass = null;

		try {
			javaClass = _getJavaClass(absolutePath, variableTypeName);
		}
		catch (IOException | ParseException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return;
		}

		if (javaClass == null) {
			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.EXPR) {
			return;
		}

		parentDetailAST = parentDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.SLIST) {
			return;
		}

		parentDetailAST = parentDetailAST.getParent();

		if (parentDetailAST.getType() == TokenTypes.LITERAL_IF) {
			_checkHasReplacableMethodSignature(
				detailAST, methodName, javaClass, true);
		}
		else {
			_checkHasReplacableMethodSignature(
				detailAST, methodName, javaClass, false);
		}
	}

	private void _checkHasReplacableMethodSignature(
		DetailAST detailAST, String methodName, JavaClass javaClass,
		boolean insideIfStatement) {

		for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
			if (!javaTerm.isJavaMethod() || javaTerm.isPrivate()) {
				continue;
			}

			JavaMethod javaMethod = (JavaMethod)javaTerm;

			if (!StringUtil.equals(methodName, javaMethod.getName())) {
				continue;
			}

			JavaSignature javaSignature = javaMethod.getSignature();

			List<JavaParameter> javaParameters = javaSignature.getParameters();

			if (javaParameters.size() != 1) {
				continue;
			}

			JavaParameter javaParameter = javaParameters.get(0);

			String parameterType = javaParameter.getParameterType();

			if (parameterType.startsWith("UnsafeSupplier")) {
				if (insideIfStatement) {
					log(
						detailAST, _MSG_INLINE_IF_STATEMENT, methodName,
						parameterType);
				}
				else {
					log(
						detailAST, _MSG_USE_SET_METHOD_INSTEAD, methodName,
						parameterType);
				}

				return;
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

	private JavaClass _getJavaClass(
			String absolutePath, String fullyQualifiedTypeName)
		throws IOException, ParseException {

		if (fullyQualifiedTypeName == null) {
			return null;
		}

		File javaFile = JavaSourceUtil.getJavaFile(
			fullyQualifiedTypeName, _getRootDirName(absolutePath),
			_getBundleSymbolicNamesMap(absolutePath));

		if (javaFile == null) {
			return null;
		}

		return JavaClassParser.parseJavaClass(
			SourceUtil.getAbsolutePath(javaFile), FileUtil.read(javaFile));
	}

	private synchronized String _getRootDirName(String absolutePath) {
		if (_rootDirName != null) {
			return _rootDirName;
		}

		_rootDirName = SourceUtil.getRootDirName(absolutePath);

		return _rootDirName;
	}

	private static final String _MSG_INLINE_IF_STATEMENT =
		"if.statement.inline";

	private static final String _MSG_USE_SET_METHOD_INSTEAD =
		"set.method.use.instead";

	private static final Log _log = LogFactoryUtil.getLog(
		RestDTOSetterCallCheck.class);

	private volatile Map<String, String> _bundleSymbolicNamesMap;
	private volatile String _rootDirName;

}