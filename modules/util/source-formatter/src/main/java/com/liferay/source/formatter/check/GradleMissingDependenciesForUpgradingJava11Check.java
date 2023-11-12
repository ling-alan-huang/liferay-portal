/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.SourceFormatterExcludes;
import com.liferay.source.formatter.check.LibraryVulnerabilitiesCheck.SecurityAdvisoryEcosystemEnum;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.GradleBuildFile;
import com.liferay.source.formatter.util.GradleDependency;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class GradleMissingDependenciesForUpgradingJava11Check extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	private void _checkMissingDependencies(String fileName, List<GradleDependency> gradleDependencies, 
			String configuration, String group, String name, String version) {
		
		for (GradleDependency gradleDependency : gradleDependencies) {
			String gradleDependencyConfiguration = gradleDependency.getConfiguration();
			String gradleDependencyGroup = gradleDependency.getGroup();
			String gradleDependencyName = gradleDependency.getName();

			if (Validator.isNull(gradleDependencyConfiguration) ||
					Validator.isNull(gradleDependencyGroup) ||
				Validator.isNull(gradleDependencyName)) {

				continue;
			}

			if (gradleDependencyConfiguration.equals(configuration) &&
					gradleDependencyGroup.equals(group) ||
					gradleDependencyName.equals(name)) {

				return;
			}
			
			addMessage(
					fileName,
					StringBundler.concat(
						"Missing dependency '", configuration, " group: ", 
						StringUtil.quote(group,  StringPool.QUOTE), "name: ", 
						StringUtil.quote(name,  StringPool.QUOTE), ", version: ", 
						StringUtil.quote(version,  StringPool.QUOTE),
						"' for updagding to Java 11"));
		}
	}
	
	
	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		GradleBuildFile gradleBuildFile = new GradleBuildFile(content);

		List<GradleDependency> gradleDependencies =
			gradleBuildFile.getGradleDependencies();

		int x = absolutePath.lastIndexOf(CharPool.SLASH);

		List<String> javaFileNames = SourceFormatterUtil.scanForFileNames(
			absolutePath.substring(0, x + 1), new String[0],
			new String[] {"**/*.java"},
			new SourceFormatterExcludes(), false);
		
		for (String javaFileName : javaFileNames) {
			String javaFileContent = FileUtil.read(new File(javaFileName));
			
			if (javaFileContent.contains("import javax.annotation.")) {
				_checkMissingDependencies(fileName, gradleDependencies, "compileOnly", "javax.annotation", "javax.annotation-api", "1.3.2");
				break;
			}
			if (javaFileContent.contains("import javax.xml.bind.annotation.")) {
				_checkMissingDependencies(fileName, gradleDependencies, "compileOnly", "javax.xml.bind", "jaxb-api", "2.3.0");
				break;
			}
		}

		return content;
	}

	private void _checkDependency(
		String fileName, String content, String dependency,
		String dependencyName, List<String> buildGradleContents) {

		int count = 0;

		for (String buildGradleContent : buildGradleContents) {
			if (!buildGradleContent.contains(dependency)) {
				continue;
			}

			count++;

			if (count > 1) {
				return;
			}
		}

		int lineNumber = getLineNumber(content, content.indexOf(dependency));

		if (count == 0) {
			addMessage(
				fileName,
				StringBundler.concat(
					"Remove dependency '", dependencyName,
					"' since it is not used by any module"),
				lineNumber);
		}
		else {
			addMessage(
				fileName,
				StringBundler.concat(
					"Remove dependency '", dependencyName,
					"' since it is only used by 1 module"),
				lineNumber);
		}
	}

	private List<String> _getBuildGradleContents() throws IOException {
		List<String> buildGradleContents = new ArrayList<>();

		String moduleAppsDirLocation = "modules/apps/";

		for (int i = 0; i < (getMaxDirLevel() - 1); i++) {
			File file = new File(getBaseDirName() + moduleAppsDirLocation);

			if (!file.exists()) {
				moduleAppsDirLocation = "../" + moduleAppsDirLocation;

				continue;
			}

			List<String> buildGradleFileNames =
				SourceFormatterUtil.scanForFileNames(
					getBaseDirName() + moduleAppsDirLocation,
					new String[] {
						"**/required-dependencies/required-dependencies" +
							"/build.gradle"
					},
					new String[] {"**/build.gradle"},
					getSourceFormatterExcludes(), false);

			for (String buildGradleFileName : buildGradleFileNames) {
				buildGradleContents.add(
					FileUtil.read(new File(buildGradleFileName)));
			}

			break;
		}

		return buildGradleContents;
	}

	private static final Pattern _dependencyNamePattern = Pattern.compile(
		"compileOnly group: (\".* name: \"(.*?)\".*)");

}