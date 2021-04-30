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

package com.liferay.source.formatter.checks;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.util.FileUtil;

/**
 * @author Alan Huang
 */
public class GradleDependenciesUnusedCheck extends BaseFileCheck {

	private List<File> _getJavaFiles(String absolutePath) throws IOException {

		final List<File> javaFiles = new ArrayList<>();

		File path = new File(absolutePath.substring(0, absolutePath.lastIndexOf("/")));
		
		Files.walkFileTree(
				path.toPath(),
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(
							Path dirPath, BasicFileAttributes basicFileAttributes)
						throws IOException {

						File dirFile = dirPath.toFile();

//						File packageinfoFile = new File(dirFile, "packageinfo");
//
//						if (packageinfoFile.exists()) {
//							return FileVisitResult.CONTINUE;
//						}
						String dirName = String.valueOf(dirPath.getFileName());

						if (!dirPath.toString().contains("/src/")) {
							return FileVisitResult.CONTINUE;
						}

						File[] files = dirFile.listFiles(
							new FileFilter() {

								@Override
								public boolean accept(File file) {
									if (!file.isFile()) {
										return false;
									}

									String fileName = file.getName();

									if (fileName.endsWith(".java")) {
										return true;
									}

									return false;
								}

							});

						if (!ArrayUtil.isEmpty(files)) {
							javaFiles.addAll(ListUtil.fromArray(files));
						}

						

						return FileVisitResult.CONTINUE;
					}

				});
		return javaFiles;
	}
	
	
	private void _checkUnusedDependencies(String fileName, String content, Map<String, String> projectNamesMap, List<File> javaFiles) throws IOException {
//		List<String> dependencyNames = new ArrayList<>();
		
		Matcher matcher = _dependencyNamePattern1.matcher(content);

		String dependencyName = null;
		
		while (matcher.find()) {
//			dependencyNames.add(matcher.group(1));
			dependencyName = matcher.group(1);
			
			if (_isUnusedDependency(fileName, javaFiles, dependencyName)) {
				addMessage(
						fileName,
						StringBundler.concat(
								"Remove dependency '", dependencyName,
								"' since it is unused"));
			}
		}
		
		matcher = _dependencyNamePattern2.matcher(content);
		
		String projectName = null;

		while (matcher.find()) {

			projectName = matcher.group(1);
			
			for (Map.Entry<String, String> entry : projectNamesMap.entrySet()) {
				dependencyName = null;

				String attributeValue = entry.getValue();
				
				if (attributeValue.equals(projectName)) {
					dependencyName = entry.getKey();
					
					break;
				}

			}
			
			if (Validator.isNull(dependencyName)) {
				continue;
			}
			
			if (_isUnusedDependency(fileName, javaFiles, dependencyName)) {
				addMessage(
						fileName,
						StringBundler.concat(
								"Remove dependency '", projectName,
								"' since it is unused"));
			}
		}
	}
	
	private boolean _isUnusedDependency(String fileName, List<File> javaFiles, String dependencyName) throws IOException {
		File javaFile = null;
		
		for (int i = 0; i < javaFiles.size(); i++) {
			javaFile = javaFiles.get(i);
			
			String fileContent = FileUtil.read(javaFile);
			
			if (fileContent.indexOf("import " + dependencyName) != -1) {
				break;
			}
			
			if (i == javaFiles.size() - 1) {
				return true;
			}
		}
		
		return false;

		
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		Map<String, String> projectNamesMap = _getProjectNamesMap(
				absolutePath);

		final List<File> javaFiles = _getJavaFiles(absolutePath);

		_checkUnusedDependencies(fileName, content, projectNamesMap, javaFiles);
		int a = 0;
		
		return content;
	}

	private synchronized Map<String, String> _getProjectNamesMap(
			String absolutePath)
		throws IOException {

		if (_projectNamesMap != null) {
			return _projectNamesMap;
		}

		_projectNamesMap = new HashMap<>();

		String content = getModulesPropertiesContent(absolutePath);

		if (Validator.isNull(content)) {
			return _projectNamesMap;
		}

		List<String> lines = ListUtil.fromString(content);

		for (String line : lines) {
			String[] array = StringUtil.split(line, StringPool.EQUAL);

			if (array.length != 2) {
				continue;
			}

			String key = array[0];

			if (key.startsWith("project.name[")) {
				_projectNamesMap.put(
					key.substring(13, key.length() - 1), array[1]);
			}
		}

		return _projectNamesMap;
	}

	private Map<String, String> _projectNamesMap;

	private static final Pattern _dependencyNamePattern1 = Pattern.compile(
			"compileOnly group: \".+?\", name: \"(.+?)\"");
	private static final Pattern _dependencyNamePattern2 = Pattern.compile(
			"compileOnly project\\(\"(.+)?\"\\)");

}