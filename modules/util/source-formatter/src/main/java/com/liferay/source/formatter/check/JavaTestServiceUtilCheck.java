/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Seiphon Wang
 */
public class JavaTestServiceUtilCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!fileName.endsWith("Test.java")) {
			return content;
		}

		List<String> importNames = JavaSourceUtil.getImportNames(content);

		Map<String, String> serviceUtilFileMap = _getServiceUtilFileMap();

		Set<String> serviceUtilFileNames = serviceUtilFileMap.keySet();

		for (String importName : importNames) {
			if (!serviceUtilFileNames.contains(importName)) {
				continue;
			}

			String className = importName.substring(
				importName.lastIndexOf(".") + 1);

			File file = new File(fileName);

			JavaClass javaClass = JavaClassParser.parseJavaClass(
				fileName, FileUtil.read(file));

			List<JavaTerm> childJavaTerms = javaClass.getChildJavaTerms();

			for (JavaTerm javaTerm : childJavaTerms) {
				if (!javaTerm.isJavaMethod()) {
					continue;
				}

				String javaMethodContent = javaTerm.getContent();

				if (javaTerm.hasAnnotation() &&
					!javaMethodContent.contains("@Test")) {

					continue;
				}

				Pattern serviceUtilMethodCallPattern = Pattern.compile(
					className + ".(\\w+)\\(");

				Matcher matcher = serviceUtilMethodCallPattern.matcher(
					javaMethodContent);

				while (matcher.find()) {
					String methodName = matcher.group(1);

					List<String> parameters = JavaSourceUtil.getParameterNames(
						JavaSourceUtil.getMethodCall(
							javaMethodContent, matcher.start()));

					String serviceFileName = serviceUtilFileMap.get(importName);

					File serviceFile = new File(serviceFileName);

					JavaClass serviceJavaClass = JavaClassParser.parseJavaClass(
						serviceFileName, FileUtil.read(serviceFile));

					List<JavaTerm> serviceClassChildJavaTerms =
						serviceJavaClass.getChildJavaTerms();

					for (JavaTerm serviceClassChildJavaTerm :
							serviceClassChildJavaTerms) {

						if (!serviceClassChildJavaTerm.isJavaMethod()) {
							continue;
						}

						JavaMethod javaMethod =
							(JavaMethod)serviceClassChildJavaTerm;

						if (methodName.equals(javaMethod.getName())) {
							Pattern methodPattern = Pattern.compile(
								methodName + "\\s*\\(([^)]*)\\)",
								Pattern.MULTILINE);

							Matcher methodMatcher = methodPattern.matcher(
								javaMethod.getContent());

							if (methodMatcher.find()) {
								String parameterString = methodMatcher.group(1);

								String[] methodParameters =
									parameterString.split(",");

								if (parameters.size() ==
										methodParameters.length) {

									int methodLineNumber =
										SourceUtil.getLineNumber(
											javaMethodContent, matcher.start());

									int lineNumber =
										javaTerm.getLineNumber() +
											methodLineNumber - 1;

									addMessage(
										fileName,
										StringBundler.concat(
											"Please use @Inject for ",
											serviceJavaClass.getName(),
											" rather than method '", className,
											".", methodName, "'."),
										lineNumber);
								}
							}
						}
					}
				}
			}
		}

		return content;
	}

	private synchronized Map<String, String> _getServiceUtilFileMap()
		throws Exception {

		if (_serviceUtilFileMap != null) {
			return _serviceUtilFileMap;
		}

		_serviceUtilFileMap = new HashMap<>();

		File portalDir = getPortalDir();

		List<String> serviceUtilFiles = SourceFormatterUtil.scanForFileNames(
			portalDir.getCanonicalPath(),
			new String[] {"**/*ServiceUtil.java"});

		for (String serviceUtilFileName : serviceUtilFiles) {
			serviceUtilFileName = StringUtil.replace(
				serviceUtilFileName, CharPool.BACK_SLASH, CharPool.SLASH);

			String serviceFileName = StringUtil.replace(
				serviceUtilFileName, "ServiceUtil", "Service");

			File file = new File(serviceFileName);

			if (!file.exists()) {
				continue;
			}

			String packageName = JavaSourceUtil.getPackageName(
				FileUtil.read(file));

			String className = JavaSourceUtil.getClassName(serviceUtilFileName);

			_serviceUtilFileMap.put(
				packageName + "." + className, serviceFileName);
		}

		return _serviceUtilFileMap;
	}

	private Map<String, String> _serviceUtilFileMap;

}