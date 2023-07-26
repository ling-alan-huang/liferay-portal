/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.BNDSourceUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.JavaVariable;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Qi Zhang
 */
public class JavaAnnotationAttributeCheck extends JavaAnnotationsCheck {

	protected void checkAttributeComponentNameValue(
			String fileName, String absolutePath, JavaClass javaClass,
			String annotation, String attributeName)
		throws Exception {

		String targetAttributeValue = getAnnotationAttributeValue(
			annotation, attributeName);

		List<String> ignoreTargetAttributeValues = getAttributeValues(
			_IGNORE_TARGET_ATTRIBUTE_VALUES_KEY, absolutePath);

		if ((targetAttributeValue == null) ||
			ignoreTargetAttributeValues.contains(targetAttributeValue)) {

			return;
		}

		String componentName = _getComponentName(
			absolutePath, javaClass, targetAttributeValue);

		if (componentName == null) {
			return;
		}

		if (componentName.contains("+")) {
			componentName = componentName.replaceAll("[^\\w\\.]", "");
		}

		if (componentName.contains("*")) {
			addMessage(
				fileName,
				"Do not use globs for the 'component.name'. Use the fully " +
					"qualified name of the component class.");

			return;
		}

		if (!componentName.startsWith("com.liferay")) {
			return;
		}

		JavaClass componentJavaClass = _getJavaClass(
			absolutePath, componentName);

		if (componentJavaClass != null) {
			List<String> importNames = componentJavaClass.getImportNames();

			if (componentJavaClass.hasAnnotation("Component") &&
				importNames.contains(
					"org.osgi.service.component.annotations.Component")) {

				return;
			}
		}

		addMessage(
			fileName,
			"The value '" + componentName + "' is not a valid OSGi component");
	}

	@Override
	protected String formatAnnotation(
			String fileName, String absolutePath, JavaClass javaClass,
			String fileContent, String annotation, String indent)
		throws Exception {

		String trimmedAnnotation = StringUtil.trim(annotation);

		if (trimmedAnnotation.startsWith("@Inject(")) {
			checkAttributeComponentNameValue(
				fileName, absolutePath, javaClass, annotation, "filter");
		}

		return annotation;
	}

	private Map<String, String> _getBundleSymbolicNamesMap(
		String absolutePath) {

		Map<String, String> bundleSymbolicNamesMap = _bundleSymbolicNamesMap;

		if (bundleSymbolicNamesMap == null) {
			bundleSymbolicNamesMap = BNDSourceUtil.getBundleSymbolicNamesMap(
				_getRootDirName(absolutePath));

			_bundleSymbolicNamesMap = bundleSymbolicNamesMap;
		}

		return bundleSymbolicNamesMap;
	}

	private String _getComponentName(
			String absolutePath, JavaClass javaClass,
			String targetAttributeValue)
		throws Exception {

		Matcher classConstantMatcher = _classConstantPattern.matcher(
			targetAttributeValue);

		if (!classConstantMatcher.find()) {
			Matcher componentNameMatcher = _componentNamePattern.matcher(
				targetAttributeValue);

			if (componentNameMatcher.find()) {
				return componentNameMatcher.group(1);
			}

			return null;
		}

		String classConstantName = classConstantMatcher.group(1);
		JavaClass classConstantJavaClass = javaClass;

		if (classConstantMatcher.groupCount() == 2) {
			String className = classConstantMatcher.group(1);

			String fullyQualifiedName =
				javaClass.getPackageName() + "." + className;

			for (String importName : javaClass.getImportNames()) {
				if (importName.endsWith(className)) {
					fullyQualifiedName = importName;

					break;
				}
			}

			classConstantName = classConstantMatcher.group(2);
			classConstantJavaClass = _getJavaClass(
				absolutePath, fullyQualifiedName);
		}

		if (classConstantJavaClass != null) {
			for (JavaTerm javaTerm :
					classConstantJavaClass.getChildJavaTerms()) {

				if (!javaTerm.isJavaVariable()) {
					continue;
				}

				JavaVariable javaVariable = (JavaVariable)javaTerm;

				if (classConstantName.equals(javaVariable.getName())) {
					Matcher componentNameMatcher =
						_componentNamePattern.matcher(
							javaVariable.getContent());

					if (componentNameMatcher.find()) {
						return componentNameMatcher.group(1);
					}

					return null;
				}
			}
		}

		return null;
	}

	private JavaClass _getJavaClass(
			String absolutePath, String fullyQualifiedName)
		throws Exception {

		JavaClass javaClass = _javaClassMap.get(fullyQualifiedName);

		if (javaClass == null) {
			File javaFile = JavaSourceUtil.getJavaFile(
				fullyQualifiedName, _getRootDirName(absolutePath),
				_getBundleSymbolicNamesMap(absolutePath));

			if (javaFile != null) {
				javaClass = JavaClassParser.parseJavaClass(
					javaFile.getName(), FileUtil.read(javaFile));

				_javaClassMap.put(fullyQualifiedName, javaClass);
			}
		}

		return javaClass;
	}

	private String _getRootDirName(String absolutePath) {
		String rootDirName = _rootDirName;

		if (rootDirName == null) {
			rootDirName = JavaSourceUtil.getRootDirName(absolutePath);

			_rootDirName = rootDirName;
		}

		return rootDirName;
	}

	private static final String _IGNORE_TARGET_ATTRIBUTE_VALUES_KEY =
		"ignoreTargetAttributeValues";

	private static final Pattern _classConstantPattern = Pattern.compile(
		"^([A-Z]\\w+)\\.?([A-Z]\\w+)$");
	private static final Pattern _componentNamePattern = Pattern.compile(
		"(\\(?)component\\.name=");

	private volatile Map<String, String> _bundleSymbolicNamesMap;
	private final Map<String, JavaClass> _javaClassMap =
		new ConcurrentHashMap<>();
	private volatile String _rootDirName;

}