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
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.checks.util.BNDSourceUtil;
import com.liferay.source.formatter.checks.util.SourceUtil;
import com.liferay.source.formatter.util.FileUtil;

/**
 * @author Alan Huang
 */
public class GradleDependenciesUnusedCheck extends BaseFileCheck {

	private static Map<String, String> _getModuleInformationMap(File portalDir)
		throws IOException {

		if (portalDir == null) {
			return Collections.emptyMap();
		}

		final Map<String, String> moduleInformationMap = new TreeMap<>();

		final Map<String, List<String>> modulePackageMap = new TreeMap<>();

		Files.walkFileTree(
			portalDir.toPath(), EnumSet.noneOf(FileVisitOption.class), 15,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String dirName = String.valueOf(dirPath.getFileName());

					if (ArrayUtil.contains(_SKIP_DIR_NAMES, dirName)) {
						return FileVisitResult.SKIP_SUBTREE;
					}

					Path path = dirPath.resolve(".gitrepo");

					if (Files.exists(path)) {
						return FileVisitResult.SKIP_SUBTREE;
					}

					Path bndPath = dirPath.resolve("bnd.bnd");

					if (!Files.exists(bndPath)) {
						return FileVisitResult.CONTINUE;
					}

					String bndContent = FileUtil.read(bndPath.toFile());

					String bundleSymbolicName = _getBundleSymbolicName(
						bndContent, SourceUtil.getAbsolutePath(bndPath));

					if (bundleSymbolicName == null) {
						return FileVisitResult.SKIP_SUBTREE;
					}

//					String bundleVersion = BNDSourceUtil.getDefinitionValue(
//						bndContent, "Bundle-Version");
//
//					if (Validator.isNotNull(bundleVersion)) {
//						moduleInformationMap.put(
//							"bundle.version[" + bundleSymbolicName + "]",
//							bundleVersion);
//					}
//
					String absolutePath = SourceUtil.getAbsolutePath(dirPath);

					int x = absolutePath.indexOf("/modules/");

					List<String> packageList = new ArrayList<>();
					
					try {
						packageList = _getPackageList(dirPath);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					modulePackageMap.put(bundleSymbolicName, null);
					if (x != -1) {
						moduleInformationMap.put(
							"project.name[" + bundleSymbolicName + "]",
							StringUtil.replace(
								absolutePath.substring(x + 8), CharPool.SLASH,
								CharPool.COLON));
					}

					
					return FileVisitResult.SKIP_SUBTREE;
				}

			});

		return moduleInformationMap;
	}

	private static List<String> _getPackageList(Path dirPath) throws Exception {
		List<String> packageList = new ArrayList<>();

		final List<String> sourceFiles = new ArrayList<>();

//		File path = new File(absolutePath.substring(0, absolutePath.lastIndexOf("/")));
		
		Files.walkFileTree(
				dirPath,
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
//										return true;
										try {
											String content = FileUtil.read(file);
											
											Matcher matcher = 
											int a = 0;
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

									}

									return false;
								}

							});

//						if (!ArrayUtil.isEmpty(files)) {
//							sourceFiles.addAll(ListUtil.fromArray(files));
//						}

						

						return FileVisitResult.CONTINUE;
					}

				});
		return sourceFiles;
//		return packageList;
	}
	private static String _getBundleSymbolicName(
		String bndContent, String absolutePath) {

		if (absolutePath.endsWith("/portal-impl/bnd.bnd")) {
			return "com.liferay.portal.impl";
		}

		if (absolutePath.endsWith("/portal-kernel/bnd.bnd")) {
			return "com.liferay.portal.kernel";
		}

		if (absolutePath.endsWith("/portal-test-integration/bnd.bnd")) {
			return "com.liferay.portal.test.integration";
		}

		if (absolutePath.endsWith("/portal-test/bnd.bnd")) {
			return "com.liferay.portal.test";
		}

		if (absolutePath.endsWith("/portal-support-tomcat/bnd.bnd")) {
			return "com.liferay.support.tomcat";
		}

		if (absolutePath.endsWith("/util-bridges/bnd.bnd")) {
			return "com.liferay.util.bridges";
		}

		if (absolutePath.endsWith("/util-java/bnd.bnd")) {
			return "com.liferay.util.java";
		}

		if (absolutePath.endsWith("/util-slf4j/bnd.bnd")) {
			return "com.liferay.util.slf4j";
		}

		if (absolutePath.endsWith("/util-taglib/bnd.bnd")) {
			return "com.liferay.util.taglib";
		}

		String bundleSymbolicName = BNDSourceUtil.getDefinitionValue(
			bndContent, "Bundle-SymbolicName");

		if (Validator.isNotNull(bundleSymbolicName) &&
			bundleSymbolicName.startsWith("com.liferay.")) {

			return bundleSymbolicName;
		}

		return null;
	}

	private List<File> _getSourceFiles(String absolutePath) throws IOException {

		final List<File> sourceFiles = new ArrayList<>();

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

									if (fileName.endsWith(".java") || fileName.endsWith(".jsp") || fileName.endsWith(".jspf")) {
										return true;
									}

									return false;
								}

							});

						if (!ArrayUtil.isEmpty(files)) {
							sourceFiles.addAll(ListUtil.fromArray(files));
						}

						

						return FileVisitResult.CONTINUE;
					}

				});
		return sourceFiles;
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

		Map<String, String> moduleInformationMap = _getModuleInformationMap(
				getPortalDir());

		
		
		Map<String, String> projectNamesMap = _getProjectNamesMap(
				absolutePath);

		final List<File> javaFiles = _getSourceFiles(absolutePath);

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
	private static final String[] _SKIP_DIR_NAMES = {
			".git", ".gradle", ".idea", ".m2", ".settings", "bin", "build",
			"classes", "dependencies", "node_modules", "node_modules_cache",
			"private", "sdk", "sql", "src", "test-classes", "test-coverage",
			"test-results", "tmp"
		};

}