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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.ParseException;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;
import java.io.IOException;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class JavaModuleTestCheck extends BaseJavaTermCheck {

	@Override
	public boolean isModuleSourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws IOException, ParseException {

		String content = javaTerm.getContent();

		if (!fileName.contains("/test/") &&
			!fileName.contains("/testIntegration/")) {

			return content;
		}

		JavaClass javaClass = (JavaClass)javaTerm;

		if ((javaClass.getParentJavaClass() != null) ||
			javaClass.isAnonymous()) {

			return content;
		}

		_checkMissingOverride(fileName, javaTerm);

		String className = javaClass.getName();

		_checkTestClassName(fileName, javaClass, className);

		String packageName = javaClass.getPackageName();

		if (className.endsWith("Test") &&
			packageName.startsWith("com.liferay")) {

			_checkTestPackage(fileName, absolutePath, content, packageName);
		}

		return content;
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private boolean _checkHasParentMethod(
		JavaClass parentJavaClass, JavaTerm javaTerm) {

		JavaSignature javaSignature = javaTerm.getSignature();

		List<JavaParameter> javaParameters = javaSignature.getParameters();

		String methodName = javaTerm.getName();

		outerLoop:
		for (JavaTerm methodJavaTerm : parentJavaClass.getChildJavaTerms()) {
			if (!methodJavaTerm.isJavaMethod() || !methodJavaTerm.isPublic() ||
				!StringUtil.equals(methodName, methodJavaTerm.getName())) {

				continue;
			}

			JavaSignature curJavaSignature = methodJavaTerm.getSignature();

			List<JavaParameter> curJavaParameters =
				curJavaSignature.getParameters();

			if (curJavaParameters.size() != javaParameters.size()) {
				continue;
			}

			for (int i = 0; i < curJavaParameters.size(); i++) {
				JavaParameter curJavaParameter = curJavaParameters.get(i);
				JavaParameter javaParameter = javaParameters.get(i);

				if (!StringUtil.equals(
						curJavaParameter.getParameterType(true),
						javaParameter.getParameterType(true))) {

					continue outerLoop;
				}
			}

			return true;
		}

		return false;
	}

	private void _checkMissingOverride(String fileName, JavaTerm javaTerm)
		throws IOException, ParseException {

		if (!fileName.endsWith("ResourceTest.java")) {
			return;
		}

		JavaClass javaClass = (JavaClass)javaTerm;

		List<String> extendedClassNames = javaClass.getExtendedClassNames();

		List<String> importNames = javaClass.getImportNames();

		JavaClass parentJavaClass = null;

		for (String extendedClassName : extendedClassNames) {
			if (extendedClassName.endsWith("ResourceTestCase")) {
				for (String importName : importNames) {
					if (importName.endsWith(extendedClassName)) {
						return;
					}
				}

				parentJavaClass = _getJavaClass(fileName, extendedClassName);

				break;
			}
		}

		if (parentJavaClass == null) {
			return;
		}

		for (JavaTerm curJavaTerm : javaClass.getChildJavaTerms()) {
			if (!curJavaTerm.isJavaMethod() ||
				curJavaTerm.hasAnnotation("Override") ||
				!curJavaTerm.hasAnnotation("Test") || curJavaTerm.isStatic()) {

				continue;
			}

			if (_checkHasParentMethod(parentJavaClass, curJavaTerm)) {
				addMessage(
					fileName,
					StringBundler.concat(
						"Test method '", curJavaTerm.getName(),
						"' must hava @override annotation"));
			}
		}
	}

	private void _checkTestClassName(
		String fileName, JavaClass javaClass, String className) {

		if (className.endsWith("Test")) {
			if (javaClass.isAbstract()) {
				addMessage(
					fileName,
					"Class name ending with 'Test' should not be abstract");
			}
			else if (javaClass.isInterface()) {
				addMessage(
					fileName, "Interface name should not end with 'Test'");
			}
		}

		if (!_hasTestMethod(javaClass)) {
			if (className.endsWith("Test")) {
				for (String extendedClassNames :
						javaClass.getExtendedClassNames()) {

					if (extendedClassNames.endsWith("Test") ||
						extendedClassNames.endsWith("TestCase")) {

						return;
					}
				}

				addMessage(
					fileName,
					StringBundler.concat(
						"'", className,
						"' is not a real test class and therefore should not ",
						"end with 'Test'"));
			}

			return;
		}

		if (javaClass.isAbstract()) {
			if (!className.matches("Base.*TestCase")) {
				addMessage(
					fileName,
					"Abstract class name should match 'Base.*TestCase'");
			}
		}
		else if (!className.endsWith("Test")) {
			addMessage(fileName, "Test class should end with 'Test'");
		}
	}

	private void _checkTestPackage(
		String fileName, String absolutePath, String content,
		String packageName) {

		if (absolutePath.contains("/src/testIntegration/java/") ||
			absolutePath.contains("/test/integration/")) {

			if (content.contains("@RunWith(Arquillian.class)") &&
				content.contains("import org.powermock.")) {

				addMessage(
					fileName, "Do not use PowerMock inside Arquillian tests");
			}

			if (!packageName.endsWith(".test")) {
				addMessage(
					fileName,
					"Module integration test must be under a test subpackage");
			}
		}
		else if ((absolutePath.contains("/test/unit/") ||
				  absolutePath.contains("/src/test/java/")) &&
				 packageName.endsWith(".test")) {

			addMessage(
				fileName,
				"Module unit test should not be under a test subpackage");
		}
	}

	private JavaClass _getJavaClass(String fileName, String className)
		throws IOException, ParseException {

		int index = fileName.lastIndexOf(StringPool.SLASH);

		if (index == -1) {
			return null;
		}

		String path = fileName.substring(0, index + 1) + className + ".java";

		File file = new File(path);

		if (!file.exists()) {
			return null;
		}

		return JavaClassParser.parseJavaClass(path, FileUtil.read(file));
	}

	private boolean _hasTestMethod(JavaClass javaClass) {
		for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
			if (((javaTerm instanceof JavaMethod) &&
				 javaTerm.hasAnnotation(
					 "After", "AfterClass", "Before", "BeforeClass", "Test")) ||
				((javaTerm instanceof JavaClass) &&
				 _hasTestMethod((JavaClass)javaTerm))) {

				return true;
			}
		}

		return false;
	}

}