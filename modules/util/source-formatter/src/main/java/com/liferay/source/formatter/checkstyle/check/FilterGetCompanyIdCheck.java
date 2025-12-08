/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.util.FileUtil;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.io.File;
import java.util.List;
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

	protected boolean isLiferayFilter(String absolutePath, String content) {
		Pattern pattern = Pattern.compile(
				" class " + JavaSourceUtil.getClassName(absolutePath) +
						"\\s+extends\\s+([\\w.]+) ");

		Matcher matcher = pattern.matcher(content);

		if (!matcher.find()) {
			return false;
		}

		String extendedClassName = matcher.group(1);

		if (extendedClassName.equals("UpgradeProcess")) {
			List<String> importNames = getImportNames(detailAST);
			if (importNames.contains()) {
				
			}
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
				getBundleSymbolicNamesMap(absolutePath));

		if (file == null) {
			return false;
		}

		return isUpgradeProcess(file.getAbsolutePath(), FileUtil.read(file));
	}
	@Override
	protected void doVisitToken(DetailAST detailAST) {
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

			String methodCallClassName = names.get(0);
			String methodCallMethodName = names.get(1);

			if (methodCallMethodName.equals("getCompanyId")) {
				continue;
			}
			
			if (!methodCallClassName.equals("PortalImpl") &&
					!methodCallClassName.equals("PortalInstances") &&
					!methodCallClassName.equals("PortalUtil")) {
				
				continue;
			}

			log(
					methodCallDetailAST, _MSG_AVOID_GET_COMPANY_ID_CALL,
					methodCallClassName);

		}

	}


	private static final String _MSG_AVOID_GET_COMPANY_ID_CALL =
		"get.company.id.call.avoid";


}