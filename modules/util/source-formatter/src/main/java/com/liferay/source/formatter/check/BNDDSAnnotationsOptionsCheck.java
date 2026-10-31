/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.SourceFormatterExcludes;
import com.liferay.source.formatter.check.util.BNDSourceUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.JavaVariable;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Shuyang Zhou
 */
public class BNDDSAnnotationsOptionsCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!fileName.endsWith("/bnd.bnd") || absolutePath.contains("/src/")) {
			return content;
		}

		String dsAnnotationsOptions = BNDSourceUtil.getDefinitionValue(
			content, "-dsannotations-options");

		if (dsAnnotationsOptions == null) {
			return content;
		}

		List<String> clauses = BNDSourceUtil.getClauses(dsAnnotationsOptions);

		if (!clauses.contains("inherit")) {
			return content;
		}

		File file = new File(absolutePath);

		if (_hasInheritingComponent(absolutePath, file.getParentFile())) {
			return content;
		}

		addMessage(
			fileName,
			"Do not use \"inherit\" in \"-dsannotations-options\", no " +
				"component inherits an @Activate, @Deactivate, @Modified or " +
					"@Reference member",
			getLineNumber(content, content.indexOf("-dsannotations-options:")));

		return content;
	}

	private File _getJavaFile(
		String absolutePath, File moduleDir, String fullyQualifiedClassName) {

		if (moduleDir != null) {
			for (String sourceDirName : _SOURCE_DIR_NAMES) {
				File file = new File(
					moduleDir,
					StringBundler.concat(
						sourceDirName, StringPool.SLASH,
						StringUtil.replace(
							fullyQualifiedClassName, CharPool.PERIOD,
							CharPool.SLASH),
						".java"));

				if (file.exists()) {
					return file;
				}
			}
		}

		String absoluteJavaFileName = _absoluteJavaFileNamesMap.get(
			fullyQualifiedClassName);

		if (absoluteJavaFileName == null) {
			File file = JavaSourceUtil.getJavaFile(
				fullyQualifiedClassName,
				SourceUtil.getRootDirName(absolutePath),
				getBundleSymbolicNamesMap(absolutePath));

			if (file == null) {
				absoluteJavaFileName = StringPool.BLANK;
			}
			else {
				absoluteJavaFileName = SourceUtil.getAbsolutePath(file);
			}

			_absoluteJavaFileNamesMap.put(
				fullyQualifiedClassName, absoluteJavaFileName);
		}

		if (absoluteJavaFileName.isEmpty()) {
			return null;
		}

		return new File(absoluteJavaFileName);
	}

	private File _getModuleDir(String absoluteJavaFileName) {
		for (String sourceDirName : _SOURCE_DIR_NAMES) {
			int index = absoluteJavaFileName.indexOf(
				StringPool.SLASH + sourceDirName + StringPool.SLASH);

			if (index != -1) {
				return new File(absoluteJavaFileName.substring(0, index));
			}
		}

		return null;
	}

	private boolean _hasDSAnnotation(
		JavaTerm javaTerm, List<String> importNames) {

		for (String annotationName : _DS_ANNOTATION_NAMES) {
			String fullyQualifiedAnnotationName =
				"org.osgi.service.component.annotations." + annotationName;

			if (javaTerm.hasAnnotation(fullyQualifiedAnnotationName) ||
				(importNames.contains(fullyQualifiedAnnotationName) &&
				 javaTerm.hasAnnotation(annotationName))) {

				return true;
			}
		}

		return false;
	}

	private boolean _hasDSMembers(
			String absolutePath, File moduleDir, String fullyQualifiedClassName)
		throws IOException {

		File file = _getJavaFile(
			absolutePath, moduleDir, fullyQualifiedClassName);

		if (file == null) {
			if (fullyQualifiedClassName.startsWith("jakarta.") ||
				fullyQualifiedClassName.startsWith("java.") ||
				fullyQualifiedClassName.startsWith("javax.")) {

				return false;
			}

			return true;
		}

		String absoluteJavaFileName = SourceUtil.getAbsolutePath(file);

		Boolean dsMembers = _dsMembersMap.get(absoluteJavaFileName);

		if (dsMembers == null) {
			dsMembers = _hasDSMembers(absolutePath, absoluteJavaFileName);

			_dsMembersMap.put(absoluteJavaFileName, dsMembers);
		}

		return dsMembers;
	}

	private boolean _hasDSMembers(
			String absolutePath, String absoluteJavaFileName)
		throws IOException {

		JavaClass javaClass = _parseJavaClass(
			absoluteJavaFileName,
			FileUtil.read(new File(absoluteJavaFileName)));

		if (javaClass == null) {
			return true;
		}

		for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
			if ((javaTerm instanceof JavaMethod ||
				 javaTerm instanceof JavaVariable) &&
				_hasDSAnnotation(javaTerm, javaClass.getImportNames())) {

				return true;
			}
		}

		return _hasInheritedDSMembers(
			absolutePath, _getModuleDir(absoluteJavaFileName), javaClass);
	}

	private boolean _hasInheritedDSMembers(
			String absolutePath, File moduleDir, JavaClass javaClass)
		throws IOException {

		for (String extendedClassName : javaClass.getExtendedClassNames(true)) {
			int index = extendedClassName.indexOf(CharPool.LESS_THAN);

			if (index != -1) {
				extendedClassName = extendedClassName.substring(0, index);
			}

			if (_hasDSMembers(absolutePath, moduleDir, extendedClassName)) {
				return true;
			}
		}

		return false;
	}

	private boolean _hasInheritingComponent(String absolutePath, File moduleDir)
		throws IOException {

		for (String sourceDirName : _SOURCE_DIR_NAMES) {
			File sourceDir = new File(moduleDir, sourceDirName);

			if (!sourceDir.isDirectory()) {
				continue;
			}

			for (String javaFileName :
					SourceFormatterUtil.scanForFileNames(
						sourceDir.getPath(), new String[0],
						new String[] {"**/*.java"},
						new SourceFormatterExcludes(), false)) {

				String javaContent = FileUtil.read(new File(javaFileName));

				if (!_isComponent(javaContent)) {
					continue;
				}

				JavaClass javaClass = _parseJavaClass(
					javaFileName, javaContent);

				if ((javaClass == null) ||
					_hasInheritedDSMembers(
						absolutePath, moduleDir, javaClass)) {

					return true;
				}
			}
		}

		return false;
	}

	private boolean _isComponent(String javaContent) {
		if (javaContent == null) {
			return false;
		}

		if (javaContent.contains(
				"@org.osgi.service.component.annotations.Component") ||
			(javaContent.contains("@Component") &&
			 javaContent.contains(
				 "import org.osgi.service.component.annotations.Component;"))) {

			return true;
		}

		return false;
	}

	private JavaClass _parseJavaClass(String javaFileName, String javaContent) {
		try {
			return JavaClassParser.parseJavaClass(javaFileName, javaContent);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private static final String[] _DS_ANNOTATION_NAMES = {
		"Activate", "Deactivate", "Modified", "Reference"
	};

	private static final String[] _SOURCE_DIR_NAMES = {
		"src/main/java", "src/testIntegration/java"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		BNDDSAnnotationsOptionsCheck.class);

	private final Map<String, String> _absoluteJavaFileNamesMap =
		new ConcurrentHashMap<>();
	private final Map<String, Boolean> _dsMembersMap =
		new ConcurrentHashMap<>();

}