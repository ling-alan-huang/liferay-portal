/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.source.formatter.check.util.BNDSourceUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.util.FileUtil;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Alan Huang
 */
public class FilterGetCompanyIdCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	protected boolean isBaseFilter(String absolutePath, String content) {
		Pattern pattern = Pattern.compile(
				" class " + JavaSourceUtil.getClassName(absolutePath) +
						"\\s+extends\\s+([\\w.]+) ");

		Matcher matcher = pattern.matcher(content);

		if (!matcher.find()) {
			return false;
		}

		String extendedClassName = matcher.group(1);

		if (extendedClassName.equals("BaseFilter")) {
			return true;
		}

		pattern = Pattern.compile("\nimport (.*\\." + extendedClassName + ");");

		matcher = pattern.matcher(content);

		if (matcher.find()) {
			extendedClassName = matcher.group(1);
		}

		if (!extendedClassName.contains(StringPool.PERIOD)) {
			extendedClassName =
					JavaSourceUtil.getPackageName(content) + StringPool.PERIOD +
							extendedClassName;
		}

		if (!extendedClassName.startsWith("com.liferay.")) {
			return false;
		}

		File file = JavaSourceUtil.getJavaFile(
				extendedClassName, SourceUtil.getRootDirName(absolutePath),
				_getBundleSymbolicNamesMap(absolutePath));

		if (file == null) {
			return false;
		}

		return isBaseFilter(file.getAbsolutePath(), FileUtil.read(file));
	}
	private synchronized Map<String, String> _getBundleSymbolicNamesMap(
			String absolutePath) {

		if (_bundleSymbolicNamesMap == null) {
			_bundleSymbolicNamesMap = BNDSourceUtil.getBundleSymbolicNamesMap(
					_getRootDirName(absolutePath));
		}

		return _bundleSymbolicNamesMap;
	}
	private volatile String _rootDirName;
	private synchronized String _getRootDirName(String absolutePath) {
		if (_rootDirName != null) {
			return _rootDirName;
		}

		_rootDirName = SourceUtil.getRootDirName(absolutePath);

		return _rootDirName;
	}

	private volatile Map<String, String> _bundleSymbolicNamesMap;
	@Override
	protected void doVisitToken(DetailAST detailAST) {

		FileContents fileContents = getFileContents();

		FileText fileText = fileContents.getText();

		if (!isBaseFilter(getAbsolutePath(), (String)fileText.getFullText())) {
			return;
		}
		
		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
				detailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			DetailAST dotDetailAST = methodCallDetailAST.findFirstToken(
					TokenTypes.DOT);

			if (dotDetailAST == null) {
				continue;
			}

			List<String> names = getNames(dotDetailAST, false);

			if (names.size() != 2) {
				continue;
			}

			String methodCallMethodName = names.get(1);
			if (!methodCallMethodName.equals("getCompany") &&
					!methodCallMethodName.equals("getCompanyId")) {
				continue;
			}

			String methodCallClassName = names.get(0);
			
			if (methodCallClassName.equals("PortalInstances") ||
					methodCallClassName.equals("PortalUtil")) {
				log(
						methodCallDetailAST, _MSG_AVOID_GET_COMPANY_ID_CALL,
						methodCallClassName);
				
				continue;

			}
			
			String typeName = getVariableTypeName(methodCallDetailAST, methodCallClassName, true);

			if ((typeName == null) || !typeName.equals("PortalImpl")) {
				continue;
			}

			log(
					methodCallDetailAST, _MSG_AVOID_GET_COMPANY_ID_CALL,
					methodCallClassName);


		}

	}


	private static final String _MSG_AVOID_GET_COMPANY_CALL =
		"get.company.call.avoid";
	private static final String _MSG_AVOID_GET_COMPANY_ID_CALL =
		"get.company.id.call.avoid";


}