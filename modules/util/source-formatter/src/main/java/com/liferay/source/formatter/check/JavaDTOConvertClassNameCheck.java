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

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.util.FileUtil;

import java.io.IOException;

/**
 * @author Qi Zhang
 */
public class JavaDTOConvertClassNameCheck extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws IOException {

		JavaClass javaClass = (JavaClass)javaTerm;

		_checkConvertClassName(absolutePath, fileName, javaClass);

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private void _checkConvertClassName(
		String absolutePath, String fileName, JavaClass javaClass) {

		if (javaClass.isAbstract() || javaClass.isInterface()) {
			return;
		}

		String className = javaClass.getName();

		if (!javaClass.isJavaClass() || !className.matches("\\w+Converter") ||
			ListUtil.isNotEmpty(javaClass.getExtendedClassNames()) ||
			ListUtil.isNotEmpty(javaClass.getImplementedClassNames())) {

			return;
		}

		int methodCount = 0;
		String methodName = null;
		String returnType = null;
		JavaTerm curJavaTerm = null;

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaMethod() &&
				!childJavaTerm.isJavaVariable()) {

				return;
			}

			if (childJavaTerm.isJavaVariable()) {
				continue;
			}

			String name = childJavaTerm.getName();

			if (name.matches("to(\\w+)")) {
				if (!childJavaTerm.isStatic()) {
					return;
				}

				if (curJavaTerm == null) {
					curJavaTerm = childJavaTerm;
				}
				else {
					JavaSignature javaSignature = curJavaTerm.getSignature();

					if (StringUtil.equals(name, curJavaTerm.getName()) &&
						StringUtil.equals(
							returnType, javaSignature.getReturnType())) {

						continue;
					}
				}

				methodCount++;

				JavaSignature javaSignature = childJavaTerm.getSignature();

				returnType = javaSignature.getReturnType();

				methodName = name;
			}
			else {
				if (!childJavaTerm.isPrivate()) {
					return;
				}
			}
		}

		if ((methodCount > 1) || Validator.isNull(methodName)) {
			return;
		}

		String path;
		String targetName = returnType + "Util";

		String message = "Modify file name to '" + targetName + "'.";

		if (absolutePath.endsWith("/converter/" + className + ".java")) {
			String rootPath = absolutePath.substring(
				0,
				absolutePath.lastIndexOf("/converter/" + javaClass.getName()));

			path = StringBundler.concat(
				rootPath, "/util/", targetName, ".java");

			message =
				message.substring(0, message.length() - 1) +
					", and move file away 'converter' package.";
		}
		else {
			String rootPath = absolutePath.substring(
				0, absolutePath.lastIndexOf(className + ".java"));

			path = rootPath + targetName + ".java";
		}

		if (FileUtil.exists(path)) {
			return;
		}

		addMessage(fileName, message);

		if (!StringUtil.equals(methodName, "to" + returnType)) {
			addMessage(
				fileName,
				StringBundler.concat(
					"Modify method ", methodName, " to 'to", returnType, "'."));
		}
	}

}