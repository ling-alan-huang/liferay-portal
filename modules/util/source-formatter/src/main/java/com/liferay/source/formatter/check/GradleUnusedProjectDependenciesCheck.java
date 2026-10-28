/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaDeclarationUtil;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shuyang Zhou
 */
public class GradleUnusedProjectDependenciesCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		int x = absolutePath.lastIndexOf("/modules/");

		if (x == -1) {
			return content;
		}

		String moduleFileName = absolutePath.substring(x + 9);

		int y = moduleFileName.lastIndexOf(CharPool.SLASH);

		if (y == -1) {
			return content;
		}

		File moduleDir = new File(absolutePath.substring(0, x + 9 + y));

		Matcher matcher = _generatedCodePattern.matcher(content);

		if (!_isPortalModule(moduleDir, moduleFileName) || matcher.find()) {
			return content;
		}

		ModuleReferences moduleReferences = null;

		matcher = _projectDependencyPattern.matcher(content);

		while (matcher.find()) {
			String configurationName = matcher.group(1);

			Pattern pattern = Pattern.compile(
				"configurations\\." + configurationName + "(?![A-Za-z])");

			Matcher configurationMatcher = pattern.matcher(content);

			if (configurationMatcher.find()) {
				continue;
			}

			_initProjectIndex(new File(absolutePath.substring(0, x + 8)));

			String projectPath = matcher.group(2);

			ProjectProvider projectProvider = _projectProviders.get(
				StringUtil.replace(
					projectPath.substring(1), CharPool.COLON, CharPool.SLASH));

			if ((projectProvider == null) || projectProvider.isEmpty()) {
				continue;
			}

			if (moduleReferences == null) {
				moduleReferences = _getModuleReferences(moduleDir);
			}

			if (!_isUnused(
					configurationName, moduleReferences, projectProvider)) {

				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"The project dependency \"", projectPath,
					"\" is unused because no source, JSP, taglib, or ",
					"\"bnd.bnd\" reference to it was found"),
				getLineNumber(content, matcher.start(1)));
		}

		return content;
	}

	private void _addClassName(Set<String> classNames, String name) {
		String className = _getTopClassName(name);

		if (className != null) {
			classNames.add(className);
		}
	}

	private void _addOverloadedMethodTypeNames(
		String className, Set<String> typeNames) {

		List<JavaDeclarationUtil.ClassDeclaration> classDeclarations =
			new ArrayList<>();
		Map<String, Integer> methodCounts = new HashMap<>();

		JavaDeclarationUtil.ClassDeclaration classDeclaration =
			_getClassDeclaration(className);

		while ((classDeclaration != null) &&
			   !classDeclaration.isInterfaceType() &&
			   (classDeclarations.size() < 100)) {

			classDeclarations.add(classDeclaration);

			Map<String, Integer> curMethodCounts =
				classDeclaration.getMethodCounts();

			for (Map.Entry<String, Integer> entry :
					curMethodCounts.entrySet()) {

				methodCounts.merge(
					entry.getKey(), entry.getValue(), Integer::sum);
			}

			List<String> extendedClassNames =
				classDeclaration.getExtendedClassNames();

			if (extendedClassNames.isEmpty()) {
				break;
			}

			classDeclaration = _getClassDeclaration(extendedClassNames.get(0));
		}

		for (JavaDeclarationUtil.ClassDeclaration genericClassDeclaration :
				classDeclarations) {

			if (!genericClassDeclaration.isGeneric()) {
				return;
			}

			Map<String, Integer> genericMethodCounts =
				genericClassDeclaration.getMethodCounts();

			for (String methodName : genericMethodCounts.keySet()) {
				if (methodCounts.get(methodName) < 2) {
					continue;
				}

				for (JavaDeclarationUtil.ClassDeclaration curClassDeclaration :
						classDeclarations) {

					Map<String, Set<String>> methodTypeNamesMap =
						curClassDeclaration.getMethodTypeNames();

					Set<String> methodTypeNames = methodTypeNamesMap.get(
						methodName);

					if (methodTypeNames == null) {
						continue;
					}

					typeNames.addAll(methodTypeNames);
				}
			}
		}
	}

	private void _addReferencedClassName(
		String name, ScopeReferences scopeReferences) {

		String className = _getTopClassName(name);

		if (className == null) {
			return;
		}

		scopeReferences.classNames.add(className);

		if (name.length() <= (className.length() + 1)) {
			return;
		}

		String nestedClassName = name.substring(className.length() + 1);

		int index = nestedClassName.indexOf(CharPool.PERIOD);

		if (index != -1) {
			nestedClassName = nestedClassName.substring(0, index);
		}

		Set<String> nestedClassNames =
			scopeReferences.nestedClassNames.computeIfAbsent(
				className, key -> new HashSet<>());

		nestedClassNames.add(nestedClassName);
	}

	private void _addSuperClassNames(
		String className, Set<String> classNames, Set<String> excludedNames) {

		Deque<String> deque = new ArrayDeque<>();

		deque.push(className);

		while (!deque.isEmpty()) {
			for (String superClassName : _getSuperClassNames(deque.pop())) {
				if (excludedNames.contains(superClassName) ||
					!classNames.add(superClassName)) {

					continue;
				}

				deque.push(superClassName);
			}
		}
	}

	private JavaDeclarationUtil.ClassDeclaration _getClassDeclaration(
		String className) {

		JavaDeclarationUtil.ClassDeclaration classDeclaration =
			_classDeclarations.get(className);

		if (classDeclaration != null) {
			return classDeclaration;
		}

		File file = _javaFiles.get(className);

		if (file == null) {
			return null;
		}

		classDeclaration = JavaDeclarationUtil.parseClassDeclaration(
			_classNames, file);

		_classDeclarations.putIfAbsent(className, classDeclaration);

		return classDeclaration;
	}

	private Set<String> _getIndirectClassNames(
		String configurationName, ModuleReferences moduleReferences) {

		Set<String> indirectClassNames =
			moduleReferences.indirectClassNamesMap.get(configurationName);

		if (indirectClassNames != null) {
			return indirectClassNames;
		}

		String[] scopeNames = _scopeNamesMap.get(configurationName);

		Set<String> classNames = new HashSet<>();

		indirectClassNames = new HashSet<>();

		for (String scopeName : scopeNames) {
			ScopeReferences scopeReferences =
				moduleReferences.getScopeReferences(scopeName);

			if (scopeReferences == null) {
				continue;
			}

			for (String className : scopeReferences.classNames) {
				if (!_classNames.contains(className)) {
					continue;
				}

				classNames.add(className);
			}

			indirectClassNames.addAll(scopeReferences.resourceClassNames);
			indirectClassNames.addAll(scopeReferences.stringClassNames);
		}

		Set<String> superClassNames = new HashSet<>();

		for (String className : classNames) {
			_addSuperClassNames(className, superClassNames, classNames);
		}

		indirectClassNames.addAll(superClassNames);

		Set<String> declaredSuperClassNames = new HashSet<>();

		for (String scopeName : scopeNames) {
			ScopeReferences scopeReferences =
				moduleReferences.getScopeReferences(scopeName);

			if (scopeReferences == null) {
				continue;
			}

			for (Map.Entry<String, Set<String>> entry :
					scopeReferences.nestedClassNames.entrySet()) {

				JavaDeclarationUtil.ClassDeclaration classDeclaration =
					_getClassDeclaration(entry.getKey());

				if (classDeclaration == null) {
					continue;
				}

				Map<String, List<String>> nestedSuperClassNamesMap =
					classDeclaration.getNestedSuperClassNames();

				for (String nestedClassName : entry.getValue()) {
					List<String> nestedSuperClassNames =
						nestedSuperClassNamesMap.get(nestedClassName);

					if (nestedSuperClassNames == null) {
						continue;
					}

					for (String nestedSuperClassName : nestedSuperClassNames) {
						indirectClassNames.add(nestedSuperClassName);

						_addSuperClassNames(
							nestedSuperClassName, indirectClassNames,
							Collections.emptySet());
					}
				}
			}

			for (String declaredSuperClassName :
					scopeReferences.declaredSuperClassNames) {

				declaredSuperClassNames.add(declaredSuperClassName);

				_addSuperClassNames(
					declaredSuperClassName, declaredSuperClassNames,
					Collections.emptySet());
			}

			for (String extendedClassName :
					scopeReferences.extendedClassNames) {

				_addOverloadedMethodTypeNames(
					extendedClassName, indirectClassNames);
			}
		}

		for (String declaredSuperClassName : declaredSuperClassNames) {
			JavaDeclarationUtil.ClassDeclaration classDeclaration =
				_getClassDeclaration(declaredSuperClassName);

			if (classDeclaration == null) {
				continue;
			}

			Map<String, Set<String>> abstractMethodTypeNames =
				classDeclaration.getAbstractMethodTypeNames();

			for (Set<String> typeNames : abstractMethodTypeNames.values()) {
				indirectClassNames.addAll(typeNames);
			}
		}

		if (configurationName.equals("compileOnly")) {
			indirectClassNames.addAll(
				_getJSPLoadedClassNames(moduleReferences));
		}

		indirectClassNames.addAll(
			_getReachedClassNames(
				classNames, declaredSuperClassNames, moduleReferences,
				scopeNames, superClassNames));

		moduleReferences.indirectClassNamesMap.put(
			configurationName, indirectClassNames);

		return indirectClassNames;
	}

	private Set<String> _getJSPLoadedClassNames(
		ModuleReferences moduleReferences) {

		ScopeReferences scopeReferences = moduleReferences.getScopeReferences(
			"main");

		if (scopeReferences == null) {
			return Collections.emptySet();
		}

		Set<String> tagClassNames = new HashSet<>();

		for (String[] jspTag : scopeReferences.jspTags) {
			Set<String> uris = scopeReferences.taglibPrefixURIs.get(jspTag[0]);

			if (uris == null) {
				continue;
			}

			for (String uri : uris) {
				Map<String, Set<String>> tagClassNamesMap =
					_tldTagClassNames.get(uri);

				if (tagClassNamesMap == null) {
					continue;
				}

				Set<String> curTagClassNames = tagClassNamesMap.get(jspTag[1]);

				if (curTagClassNames == null) {
					continue;
				}

				tagClassNames.addAll(curTagClassNames);
			}
		}

		Set<String> hierarchyClassNames = new HashSet<>(tagClassNames);

		for (String tagClassName : tagClassNames) {
			_addSuperClassNames(
				tagClassName, hierarchyClassNames, Collections.emptySet());
		}

		Set<String> referencedClassNames = new HashSet<>();

		for (String hierarchyClassName : hierarchyClassNames) {
			JavaDeclarationUtil.ClassDeclaration classDeclaration =
				_getClassDeclaration(hierarchyClassName);

			if (classDeclaration == null) {
				continue;
			}

			for (String referencedName :
					classDeclaration.getReferencedNames()) {

				String className = _getTopClassName(referencedName);

				if ((className == null) ||
					hierarchyClassNames.contains(className)) {

					continue;
				}

				referencedClassNames.add(className);
			}
		}

		for (String referencedClassName :
				new ArrayList<>(referencedClassNames)) {

			_addSuperClassNames(
				referencedClassName, referencedClassNames, hierarchyClassNames);
		}

		referencedClassNames.addAll(hierarchyClassNames);

		return referencedClassNames;
	}

	private ModuleReferences _getModuleReferences(File moduleDir)
		throws IOException {

		ModuleReferences moduleReferences = new ModuleReferences();

		Path moduleDirPath = moduleDir.toPath();

		Files.walkFileTree(
			moduleDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
					Path dirPath, BasicFileAttributes basicFileAttributes) {

					if (dirPath.equals(moduleDirPath)) {
						return FileVisitResult.CONTINUE;
					}

					if (_isSkipDir(dirPath) || _isModuleDir(dirPath)) {
						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
					Path filePath, BasicFileAttributes basicFileAttributes) {

					_scanModuleFile(
						filePath.toFile(), moduleReferences,
						StringUtil.replace(
							String.valueOf(moduleDirPath.relativize(filePath)),
							CharPool.BACK_SLASH, CharPool.SLASH),
						basicFileAttributes.size());

					return FileVisitResult.CONTINUE;
				}

			});

		return moduleReferences;
	}

	private List<String> _getPackageEntries(String value) {
		List<String> entries = new ArrayList<>();

		boolean quoted = false;
		int start = 0;

		for (int i = 0; i <= value.length(); i++) {
			if (i < value.length()) {
				char c = value.charAt(i);

				if (c == CharPool.QUOTE) {
					quoted = !quoted;
				}

				if ((c != CharPool.COMMA) || quoted) {
					continue;
				}
			}

			String entry = StringUtil.trim(value.substring(start, i));

			start = i + 1;

			if (entry.isEmpty()) {
				continue;
			}

			int index = entry.indexOf(CharPool.SEMICOLON);

			if (index != -1) {
				entry = StringUtil.trim(entry.substring(0, index));
			}

			entries.add(entry);
		}

		return entries;
	}

	private String _getPackageName(String className) {
		int index = className.lastIndexOf(CharPool.PERIOD);

		if (index == -1) {
			return StringPool.BLANK;
		}

		return className.substring(0, index);
	}

	private Set<String> _getReachedClassNames(
		Set<String> classNames, Set<String> declaredSuperClassNames,
		ModuleReferences moduleReferences, String[] scopeNames,
		Set<String> superClassNames) {

		Set<String> inheritedMethodNames = new HashSet<>();
		Set<String> methodNames = new HashSet<>();
		Set<String> newTypeNames = new HashSet<>();

		for (String scopeName : scopeNames) {
			ScopeReferences scopeReferences =
				moduleReferences.getScopeReferences(scopeName);

			if (scopeReferences == null) {
				continue;
			}

			inheritedMethodNames.addAll(scopeReferences.inheritedMethodNames);
			methodNames.addAll(scopeReferences.methodNames);
			newTypeNames.addAll(scopeReferences.newTypeNames);
		}

		Set<String> allClassNames = new HashSet<>(classNames);

		allClassNames.addAll(superClassNames);

		Set<String> frontierClassNames = new HashSet<>(allClassNames);

		Set<String> reachedClassNames = new HashSet<>();

		for (int i = 0; i < 3; i++) {
			Set<String> newClassNames = new HashSet<>();

			for (String frontierClassName : frontierClassNames) {
				JavaDeclarationUtil.ClassDeclaration classDeclaration =
					_getClassDeclaration(frontierClassName);

				if (classDeclaration == null) {
					continue;
				}

				boolean constructed = newTypeNames.contains(
					frontierClassName.substring(
						frontierClassName.lastIndexOf(CharPool.PERIOD) + 1));
				boolean inherited = declaredSuperClassNames.contains(
					frontierClassName);

				Map<String, Set<String>> methodTypeNames =
					classDeclaration.getMethodTypeNames();

				for (Map.Entry<String, Set<String>> entry :
						methodTypeNames.entrySet()) {

					String name = entry.getKey();

					if (!methodNames.contains(name) &&
						!(constructed && name.equals("<init>")) &&
						!(inherited && inheritedMethodNames.contains(name))) {

						continue;
					}

					for (String typeName : entry.getValue()) {
						if (allClassNames.contains(typeName) ||
							!reachedClassNames.add(typeName)) {

							continue;
						}

						newClassNames.add(typeName);
					}
				}
			}

			Set<String> expandedClassNames = new HashSet<>();

			for (String newClassName : newClassNames) {
				_addSuperClassNames(
					newClassName, expandedClassNames, allClassNames);
			}

			expandedClassNames.removeAll(reachedClassNames);

			reachedClassNames.addAll(expandedClassNames);

			allClassNames.addAll(newClassNames);
			allClassNames.addAll(expandedClassNames);

			frontierClassNames = newClassNames;

			frontierClassNames.addAll(expandedClassNames);

			if (frontierClassNames.isEmpty()) {
				break;
			}
		}

		return reachedClassNames;
	}

	private List<String> _getSuperClassNames(String className) {
		JavaDeclarationUtil.ClassDeclaration classDeclaration =
			_getClassDeclaration(className);

		if (classDeclaration == null) {
			return Collections.emptyList();
		}

		return classDeclaration.getSuperClassNames();
	}

	private String _getTopClassName(String name) {
		int index = name.indexOf(CharPool.PERIOD);

		while (index != -1) {
			String prefix = name.substring(0, index);

			if (_classNames.contains(prefix)) {
				return prefix;
			}

			index = name.indexOf(CharPool.PERIOD, index + 1);
		}

		if (_classNames.contains(name)) {
			return name;
		}

		index = 0;

		while (index < name.length()) {
			int end = name.indexOf(CharPool.PERIOD, index);

			if (end == -1) {
				end = name.length();
			}

			if ((end > index) && Character.isUpperCase(name.charAt(index))) {
				return name.substring(0, end);
			}

			index = end + 1;
		}

		return null;
	}

	private boolean _hasAny(Set<String> names, Set<String> set) {
		for (String name : names) {
			if (set.contains(name)) {
				return true;
			}
		}

		return false;
	}

	private boolean _hasBNDReference(
		Map<String, String> bndDefinitions, String configurationName,
		ProjectProvider projectProvider) {

		for (String key : _BND_KEYS_RESOURCE) {
			String value = bndDefinitions.get(key);

			if (value == null) {
				continue;
			}

			for (String name : projectProvider.names) {
				Pattern pattern = Pattern.compile(
					"(?<![\\w.\\-])" + Pattern.quote(name) +
						"(?:-\\[|-\\*|-[0-9]|\\.jar|[\\-*\\[])");

				Matcher matcher = pattern.matcher(value);

				if (matcher.find()) {
					return true;
				}
			}
		}

		if (!configurationName.equals("compileOnly") &&
			!configurationName.startsWith("testIntegration")) {

			return false;
		}

		for (String key :
				ArrayUtil.append(_BND_KEYS_IMPORT, _BND_KEYS_PACKAGE)) {

			String value = bndDefinitions.get(key);

			if (value == null) {
				continue;
			}

			for (String entry : _getPackageEntries(value)) {
				if (entry.startsWith(StringPool.EXCLAMATION) ||
					entry.equals(StringPool.STAR)) {

					continue;
				}

				if (_hasPackage(entry, projectProvider)) {
					return true;
				}
			}
		}

		String fragmentHost = bndDefinitions.get("Fragment-Host");

		if ((fragmentHost != null) &&
			(projectProvider.bundleSymbolicName != null)) {

			int index = fragmentHost.indexOf(CharPool.SEMICOLON);

			if (index != -1) {
				fragmentHost = fragmentHost.substring(0, index);
			}

			if (projectProvider.bundleSymbolicName.equals(
					StringUtil.trim(fragmentHost))) {

				return true;
			}
		}

		for (Map.Entry<String, String> entry : bndDefinitions.entrySet()) {
			String key = entry.getKey();

			if (ArrayUtil.contains(_BND_KEYS_IMPORT, key) ||
				ArrayUtil.contains(_BND_KEYS_PACKAGE, key) ||
				ArrayUtil.contains(_BND_KEYS_RESOURCE, key)) {

				continue;
			}

			Matcher matcher = _qualifiedNamePattern.matcher(entry.getValue());

			while (matcher.find()) {
				if (projectProvider.classNames.contains(matcher.group(1))) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean _hasDirectReference(
		ProjectProvider projectProvider, ScopeReferences scopeReferences) {

		if (_hasAny(scopeReferences.classNames, projectProvider.classNames) ||
			_hasAny(scopeReferences.taglibURIs, projectProvider.tldURIs)) {

			return true;
		}

		for (String packageName : scopeReferences.packageNames) {
			if (_isProvidedPackage(packageName, projectProvider)) {
				return true;
			}
		}

		for (String className : scopeReferences.classNames) {
			if (_classNames.contains(className)) {
				continue;
			}

			String packageName = _getPackageName(className);

			if (!packageName.isEmpty() &&
				_isProvidedPackage(packageName, projectProvider)) {

				return true;
			}
		}

		return false;
	}

	private boolean _hasExtension(String[] extensions, String fileName) {
		for (String extension : extensions) {
			if (fileName.endsWith(extension)) {
				return true;
			}
		}

		return false;
	}

	private boolean _hasPackage(
		String packagePattern, ProjectProvider projectProvider) {

		if (!packagePattern.contains(StringPool.STAR)) {
			return _isProvidedPackage(packagePattern, projectProvider);
		}

		for (String packageName : projectProvider.packageNames) {
			if (_isMatchingPackage(packageName, packagePattern)) {
				return true;
			}
		}

		return false;
	}

	private void _indexBND(
		Map<String, String> bndDefinitions, ProjectProvider projectProvider) {

		String bundleSymbolicName = bndDefinitions.get("Bundle-SymbolicName");

		if (bundleSymbolicName != null) {
			int index = bundleSymbolicName.indexOf(CharPool.SEMICOLON);

			if (index != -1) {
				bundleSymbolicName = bundleSymbolicName.substring(0, index);
			}

			projectProvider.bundleSymbolicName = StringUtil.trim(
				bundleSymbolicName);

			projectProvider.names.add(projectProvider.bundleSymbolicName);
		}

		for (String key : new String[] {"-exportcontents", "Export-Package"}) {
			String value = bndDefinitions.get(key);

			if (value == null) {
				continue;
			}

			for (String entry : _getPackageEntries(value)) {
				if (entry.startsWith(StringPool.EXCLAMATION)) {
					continue;
				}

				if (entry.contains(StringPool.OPEN_CURLY_BRACE) ||
					entry.contains(StringPool.STAR)) {

					projectProvider.packagePatterns.add(entry);
				}
				else {
					projectProvider.packageNames.add(entry);
				}
			}
		}
	}

	private void _indexClass(
		Set<String> classNames, File file, ProjectProvider projectProvider,
		String relativePath) {

		int index = relativePath.lastIndexOf(CharPool.PERIOD);

		String className = StringUtil.replace(
			relativePath.substring(0, index), CharPool.SLASH, CharPool.PERIOD);

		String packageName = _getPackageName(className);

		if (projectProvider != null) {
			if (!packageName.isEmpty()) {
				projectProvider.packageNames.add(packageName);
			}

			if (!className.endsWith(".module-info") &&
				!className.endsWith(".package-info")) {

				projectProvider.classNames.add(className);
			}
		}

		if (className.endsWith(".module-info") ||
			className.endsWith(".package-info")) {

			return;
		}

		classNames.add(className);

		if (relativePath.endsWith(".java")) {
			_javaFiles.putIfAbsent(className, file);
		}
	}

	private synchronized void _initProjectIndex(File modulesDir)
		throws IOException {

		if (_classNames != null) {
			return;
		}

		Set<String> classNames = new HashSet<>();

		for (String dirName : _DIR_NAMES_MODULES) {
			File dir = new File(modulesDir, dirName);

			if (!dir.isDirectory()) {
				continue;
			}

			_walkModulesDir(classNames, modulesDir.toPath(), dir.toPath());
		}

		for (String dirName :
				new String[] {
					"portal-impl", "portal-kernel", "portal-test",
					"util-bridges", "util-java", "util-slf4j", "util-taglib"
				}) {

			File dir = new File(modulesDir.getParentFile(), dirName + "/src");

			if (!dir.isDirectory()) {
				continue;
			}

			_walkCoreDir(classNames, dir.toPath());
		}

		_classNames = classNames;
	}

	private boolean _isMatchingPackage(
		String packageName, String packagePattern) {

		if (packagePattern.equals(StringPool.STAR)) {
			return false;
		}

		if (packagePattern.endsWith(".*")) {
			String basePackageName = packagePattern.substring(
				0, packagePattern.length() - 2);

			if (packageName.equals(basePackageName) ||
				packageName.startsWith(basePackageName + ".")) {

				return true;
			}

			return false;
		}

		StringBundler sb = new StringBundler();

		for (char c : packagePattern.toCharArray()) {
			if (c == CharPool.STAR) {
				sb.append(".*");
			}
			else if (c == CharPool.QUESTION) {
				sb.append(CharPool.PERIOD);
			}
			else {
				sb.append(Pattern.quote(String.valueOf(c)));
			}
		}

		return packageName.matches(sb.toString());
	}

	private boolean _isModuleDir(Path dirPath) {
		if (Files.exists(dirPath.resolve("bnd.bnd")) ||
			Files.exists(dirPath.resolve("build.gradle"))) {

			return true;
		}

		return false;
	}

	private boolean _isPortalModule(File moduleDir, String moduleFileName) {
		if (!moduleFileName.contains(StringPool.SLASH) ||
			!ArrayUtil.contains(
				_DIR_NAMES_MODULES,
				moduleFileName.substring(
					0, moduleFileName.indexOf(CharPool.SLASH)))) {

			return false;
		}

		if (moduleFileName.startsWith("apps/") ||
			moduleFileName.startsWith("core/") ||
			moduleFileName.startsWith("dxp/apps/")) {

			return true;
		}

		String[] fileNames = moduleDir.list();

		if (fileNames == null) {
			return false;
		}

		for (String fileName : fileNames) {
			if (fileName.startsWith(".lfrbuild-portal")) {
				return true;
			}
		}

		return false;
	}

	private boolean _isProvidedPackage(
		String packageName, ProjectProvider projectProvider) {

		if (projectProvider.packageNames.contains(packageName)) {
			return true;
		}

		for (String packagePattern : projectProvider.packagePatterns) {
			if (_isMatchingPackage(packageName, packagePattern)) {
				return true;
			}
		}

		return false;
	}

	private boolean _isSkipDir(Path dirPath) {
		return ArrayUtil.contains(
			new String[] {".gradle", "build", "classes", "node_modules"},
			String.valueOf(dirPath.getFileName()));
	}

	private boolean _isUnused(
		String configurationName, ModuleReferences moduleReferences,
		ProjectProvider projectProvider) {

		for (String scopeName : _scopeNamesMap.get(configurationName)) {
			ScopeReferences scopeReferences =
				moduleReferences.getScopeReferences(scopeName);

			if ((scopeReferences != null) &&
				_hasDirectReference(projectProvider, scopeReferences)) {

				return false;
			}
		}

		if (_hasBNDReference(
				moduleReferences.bndDefinitions, configurationName,
				projectProvider) ||
			_hasAny(
				_getIndirectClassNames(configurationName, moduleReferences),
				projectProvider.classNames)) {

			return false;
		}

		return true;
	}

	private Map<String, String> _parseBND(String content) {
		Map<String, String> bndDefinitions = new HashMap<>();

		content = StringUtil.removeSubstring(content, "\\\n");

		for (String line : StringUtil.splitLines(content)) {
			Matcher matcher = _bndDefinitionPattern.matcher(
				StringUtil.trim(line));

			if (!matcher.find()) {
				continue;
			}

			String key = matcher.group(1);
			String value = matcher.group(2);

			String previousValue = bndDefinitions.get(key);

			if (previousValue != null) {
				value = previousValue + StringPool.COMMA + value;
			}

			bndDefinitions.put(key, value);
		}

		return bndDefinitions;
	}

	private void _scanJSP(String content, ScopeReferences scopeReferences) {
		Matcher matcher = _jspCommentPattern.matcher(content);

		content = matcher.replaceAll(StringPool.SPACE);

		matcher = _jspDirectivePattern.matcher(content);

		while (matcher.find()) {
			Map<String, String> attributes = new HashMap<>();

			Matcher attributeMatcher = _jspAttributePattern.matcher(
				matcher.group(2));

			while (attributeMatcher.find()) {
				String value = attributeMatcher.group(3);

				if (value == null) {
					value = attributeMatcher.group(4);
				}

				attributes.put(attributeMatcher.group(1), value);
			}

			String importValue = attributes.get("import");

			if (importValue != null) {
				for (String importName : StringUtil.split(importValue)) {
					importName = StringUtil.trim(importName);

					if (importName.endsWith(".*")) {
						_scanWildcardImport(
							importName.substring(0, importName.length() - 2),
							scopeReferences);
					}
					else if (!importName.isEmpty()) {
						_addReferencedClassName(importName, scopeReferences);
					}
				}
			}

			String directiveName = matcher.group(1);
			String uri = attributes.get("uri");

			if (!directiveName.equals("taglib") || (uri == null)) {
				continue;
			}

			scopeReferences.taglibURIs.add(uri);

			String prefix = attributes.get("prefix");

			if (prefix == null) {
				continue;
			}

			Set<String> uris = scopeReferences.taglibPrefixURIs.computeIfAbsent(
				prefix, key -> new HashSet<>());

			uris.add(uri);
		}

		matcher = _jspUseBeanPattern.matcher(content);

		while (matcher.find()) {
			_addReferencedClassName(matcher.group(1), scopeReferences);
		}

		matcher = _jspTagPattern.matcher(content);

		while (matcher.find()) {
			String prefix = matcher.group(1);

			if (prefix.equals("jsp")) {
				continue;
			}

			scopeReferences.jspTags.add(
				new String[] {prefix, matcher.group(2)});
		}

		StringBundler sb = new StringBundler();

		int index = 0;

		matcher = _jspScriptletPattern.matcher(content);

		while (matcher.find()) {
			sb.append(content.substring(index, matcher.start()));

			List<String> strings = new ArrayList<>();

			_scanJavaCode(
				JavaDeclarationUtil.splitCode(matcher.group(1), strings), true,
				scopeReferences, strings);

			index = matcher.end();
		}

		sb.append(content.substring(index));

		matcher = _jspDirectivePattern.matcher(sb.toString());

		matcher = _qualifiedNamePattern.matcher(
			matcher.replaceAll(StringPool.SPACE));

		while (matcher.find()) {
			_addClassName(scopeReferences.stringClassNames, matcher.group(1));
		}
	}

	private void _scanJavaCode(
		String code, boolean jspScriptlet, ScopeReferences scopeReferences,
		List<String> strings) {

		if (!jspScriptlet) {
			Matcher matcher = _packagePattern.matcher(code);

			if (matcher.find()) {
				String packageName = matcher.group(1);

				scopeReferences.packageNames.add(packageName);

				matcher = _simpleClassNamePattern.matcher(code);

				while (matcher.find()) {
					String className = packageName + "." + matcher.group(1);

					if (!_classNames.contains(className)) {
						continue;
					}

					scopeReferences.classNames.add(className);
				}
			}

			matcher = _importPattern.matcher(code);

			while (matcher.find()) {
				String name = StringUtil.removeChars(
					matcher.group(2), CharPool.SPACE, CharPool.TAB);

				if ((matcher.group(1) == null) && (matcher.group(3) != null)) {
					_scanWildcardImport(name, scopeReferences);
				}
				else {
					_addReferencedClassName(name, scopeReferences);
				}
			}
		}

		Matcher matcher = _codeQualifiedNamePattern.matcher(code);

		while (matcher.find()) {
			_addReferencedClassName(
				StringUtil.removeChars(
					matcher.group(1), CharPool.NEW_LINE, CharPool.SPACE,
					CharPool.TAB),
				scopeReferences);
		}

		matcher = _methodCallPattern.matcher(code);

		while (matcher.find()) {
			String methodName = matcher.group(1);

			if (methodName == null) {
				methodName = matcher.group(2);
			}

			if (methodName != null) {
				scopeReferences.methodNames.add(methodName);
			}
			else if (jspScriptlet) {
				scopeReferences.methodNames.add(matcher.group(3));
			}
			else {
				scopeReferences.inheritedMethodNames.add(matcher.group(3));
			}
		}

		matcher = _newTypePattern.matcher(code);

		while (matcher.find()) {
			String typeName = matcher.group(1);

			scopeReferences.newTypeNames.add(
				typeName.substring(typeName.lastIndexOf(CharPool.PERIOD) + 1));
		}

		for (String s : strings) {
			matcher = _qualifiedNamePattern.matcher(s);

			while (matcher.find()) {
				_addClassName(
					scopeReferences.stringClassNames, matcher.group(1));
			}
		}
	}

	private void _scanModuleFile(
		File file, ModuleReferences moduleReferences, String relativePath,
		long size) {

		String scopeName = "main";

		if (relativePath.startsWith("src/testIntegration/")) {
			scopeName = "testIntegration";
		}
		else if (relativePath.startsWith("src/test/")) {
			scopeName = "test";
		}
		else if (relativePath.startsWith("src/") &&
				 !relativePath.startsWith("src/main/")) {

			return;
		}

		ScopeReferences scopeReferences =
			moduleReferences.scopeReferencesMap.computeIfAbsent(
				scopeName, key -> new ScopeReferences());

		String fileName = file.getName();

		if (_hasExtension(_FILE_EXTENSIONS_JAVA, fileName)) {
			String content = FileUtil.read(file);

			if (content == null) {
				return;
			}

			List<String> strings = new ArrayList<>();

			_scanJavaCode(
				JavaDeclarationUtil.splitCode(content, strings), false,
				scopeReferences, strings);

			if (!fileName.endsWith(".java")) {
				return;
			}

			JavaDeclarationUtil.ClassDeclaration classDeclaration = null;

			if (relativePath.startsWith("src/main/java/")) {
				classDeclaration = _getClassDeclaration(
					StringUtil.replace(
						relativePath.substring(14, relativePath.length() - 5),
						CharPool.SLASH, CharPool.PERIOD));
			}

			if (classDeclaration == null) {
				classDeclaration = JavaDeclarationUtil.parseClassDeclaration(
					_classNames, file);
			}

			Set<String> methodNames = scopeReferences.inheritedMethodNames;

			if (scopeName.equals("main")) {
				methodNames = scopeReferences.methodNames;
			}

			Map<String, Set<String>> methodTypeNames =
				classDeclaration.getMethodTypeNames();

			for (String methodName : methodTypeNames.keySet()) {
				if (methodName.equals("<init>")) {
					continue;
				}

				methodNames.add(methodName);
			}

			List<String> superClassNames = new ArrayList<>(
				classDeclaration.getAnonymousSuperClassNames());

			superClassNames.addAll(classDeclaration.getSuperClassNames());

			Map<String, List<String>> nestedSuperClassNamesMap =
				classDeclaration.getNestedSuperClassNames();

			for (List<String> nestedSuperClassNames :
					nestedSuperClassNamesMap.values()) {

				superClassNames.addAll(nestedSuperClassNames);
			}

			for (String superClassName : superClassNames) {
				scopeReferences.declaredSuperClassNames.add(superClassName);
				scopeReferences.newTypeNames.add(
					superClassName.substring(
						superClassName.lastIndexOf(CharPool.PERIOD) + 1));
			}

			scopeReferences.extendedClassNames.addAll(
				classDeclaration.getAnonymousSuperClassNames());

			if (!classDeclaration.isInterfaceType()) {
				scopeReferences.extendedClassNames.addAll(
					classDeclaration.getExtendedClassNames());
			}
		}
		else if (_hasExtension(
					new String[] {".jsp", ".jspf", ".jspx", ".tag", ".tagf"},
					fileName)) {

			String content = FileUtil.read(file);

			if (content != null) {
				_scanJSP(content, scopeReferences);
			}
		}
		else if (relativePath.equals("bnd.bnd")) {
			String content = FileUtil.read(file);

			if (content != null) {
				moduleReferences.bndDefinitions = _parseBND(content);
			}
		}
		else if (fileName.endsWith(".tld") ||
				 ((_hasExtension(
					 new String[] {
						 ".MF", ".cfg", ".config", ".ftl", ".properties",
						 ".tld", ".tmpl", ".vm", ".xml", ".xsl", ".yaml", ".yml"
					 },
					 fileName) ||
				   relativePath.contains("/META-INF/services/")) &&
				  !fileName.startsWith("Language") && (size <= 2000000))) {

			String content = FileUtil.read(file);

			if (content != null) {
				_scanResource(
					content, fileName, scopeReferences,
					!fileName.endsWith(".tld") &&
					relativePath.contains("/META-INF/services/"));
			}
		}
	}

	private void _scanResource(
		String content, String fileName, ScopeReferences scopeReferences,
		boolean serviceLoader) {

		if (serviceLoader) {
			_addClassName(scopeReferences.resourceClassNames, fileName);

			for (String line : StringUtil.splitLines(content)) {
				int index = line.indexOf(CharPool.POUND);

				if (index != -1) {
					line = line.substring(0, index);
				}

				line = StringUtil.trim(line);

				if (line.isEmpty()) {
					continue;
				}

				_addClassName(scopeReferences.resourceClassNames, line);
			}

			return;
		}

		Matcher matcher = _qualifiedNamePattern.matcher(content);

		while (matcher.find()) {
			_addClassName(scopeReferences.resourceClassNames, matcher.group(1));
		}
	}

	private void _scanTLD(
		String content, ProjectProvider projectProvider, boolean test) {

		Matcher matcher = _tldURIPattern.matcher(content);

		List<String> uris = new ArrayList<>();

		while (matcher.find()) {
			uris.add(matcher.group(1));
		}

		if (projectProvider != null) {
			projectProvider.tldURIs.addAll(uris);
		}

		if (test || uris.isEmpty()) {
			return;
		}

		Map<String, Set<String>> tagClassNamesMap =
			_tldTagClassNames.computeIfAbsent(
				uris.get(0), key -> new HashMap<>());

		int index = content.indexOf("<tag>");

		while (index != -1) {
			int end = content.indexOf("</tag>", index);

			if (end == -1) {
				return;
			}

			String tag = content.substring(index + 5, end);

			Matcher nameMatcher = _tldNamePattern.matcher(tag);

			if (nameMatcher.find()) {
				Set<String> tagClassNames = tagClassNamesMap.computeIfAbsent(
					nameMatcher.group(1), key -> new HashSet<>());

				Matcher classMatcher = _tldTagClassPattern.matcher(tag);

				while (classMatcher.find()) {
					tagClassNames.add(classMatcher.group(1));
				}
			}

			index = content.indexOf("<tag>", end);
		}
	}

	private void _scanWildcardImport(
		String name, ScopeReferences scopeReferences) {

		if (_classNames.contains(name)) {
			scopeReferences.classNames.add(name);

			return;
		}

		String className = _getTopClassName(name);

		if ((className != null) && _classNames.contains(className)) {
			scopeReferences.classNames.add(className);
		}
		else {
			scopeReferences.packageNames.add(name);
		}
	}

	private void _walkCoreDir(Set<String> classNames, Path srcDirPath)
		throws IOException {

		Files.walkFileTree(
			srcDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
					Path filePath, BasicFileAttributes basicFileAttributes) {

					String relativePath = StringUtil.replace(
						String.valueOf(srcDirPath.relativize(filePath)),
						CharPool.BACK_SLASH, CharPool.SLASH);

					if (relativePath.endsWith(".tld")) {
						String content = FileUtil.read(filePath.toFile());

						if (content != null) {
							_scanTLD(content, null, false);
						}
					}
					else if (_hasExtension(
								_FILE_EXTENSIONS_JAVA, relativePath)) {

						_indexClass(
							classNames, filePath.toFile(), null, relativePath);
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private void _walkModulesDir(
			Set<String> classNames, Path modulesDirPath, Path rootDirPath)
		throws IOException {

		Deque<Path> moduleDirPaths = new ArrayDeque<>();
		Deque<ProjectProvider> projectProviders = new ArrayDeque<>();

		Files.walkFileTree(
			rootDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(
					Path dirPath, IOException ioException) {

					if (dirPath.equals(moduleDirPaths.peek())) {
						moduleDirPaths.pop();

						ProjectProvider projectProvider =
							projectProviders.pop();

						_projectProviders.put(
							StringUtil.replace(
								String.valueOf(
									modulesDirPath.relativize(dirPath)),
								CharPool.BACK_SLASH, CharPool.SLASH),
							projectProvider);
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(
					Path dirPath, BasicFileAttributes basicFileAttributes) {

					if (_isSkipDir(dirPath)) {
						return FileVisitResult.SKIP_SUBTREE;
					}

					if (_isModuleDir(dirPath)) {
						moduleDirPaths.push(dirPath);

						ProjectProvider projectProvider = new ProjectProvider();

						projectProvider.names.add(
							String.valueOf(dirPath.getFileName()));

						projectProviders.push(projectProvider);
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
					Path filePath, BasicFileAttributes basicFileAttributes) {

					Path moduleDirPath = moduleDirPaths.peek();

					if (moduleDirPath == null) {
						return FileVisitResult.CONTINUE;
					}

					ProjectProvider projectProvider = projectProviders.peek();

					String relativePath = StringUtil.replace(
						String.valueOf(moduleDirPath.relativize(filePath)),
						CharPool.BACK_SLASH, CharPool.SLASH);

					if (relativePath.equals("bnd.bnd")) {
						String content = FileUtil.read(filePath.toFile());

						if (content != null) {
							_indexBND(_parseBND(content), projectProvider);
						}
					}
					else if (relativePath.endsWith(".tld")) {
						String content = FileUtil.read(filePath.toFile());

						if (content != null) {
							_scanTLD(
								content, projectProvider,
								relativePath.startsWith("src/test/") ||
								relativePath.startsWith(
									"src/testIntegration/"));
						}
					}
					else if (_hasExtension(
								_FILE_EXTENSIONS_JAVA, relativePath)) {

						for (String srcDirName :
								new String[] {
									"src/main/groovy", "src/main/java",
									"src/main/kotlin"
								}) {

							if (relativePath.startsWith(srcDirName + "/")) {
								_indexClass(
									classNames, filePath.toFile(),
									projectProvider,
									relativePath.substring(
										srcDirName.length() + 1));

								break;
							}
						}
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private static final String[] _BND_KEYS_IMPORT = {
		"DynamicImport-Package", "Import-Package"
	};

	private static final String[] _BND_KEYS_PACKAGE = {
		"-conditionalpackage", "-exportcontents", "-includepackage",
		"-privatepackage", "Conditional-Package", "Export-Package",
		"Private-Package"
	};

	private static final String[] _BND_KEYS_RESOURCE = {
		"-classpath", "-includeresource", "Bundle-ClassPath", "Include-Resource"
	};

	private static final String[] _DIR_NAMES_MODULES = {
		"apps", "core", "dxp", "test", "util"
	};

	private static final String[] _FILE_EXTENSIONS_JAVA = {
		".groovy", ".java", ".kt"
	};

	private static final Pattern _bndDefinitionPattern = Pattern.compile(
		"^([-\\w.]+)\\s*[:=]\\s*(.*)$");
	private static final Pattern _codeQualifiedNamePattern = Pattern.compile(
		"(?<![\\w$.])((?:[a-z_][a-z0-9_]*\\.\\s*)+[A-Z][\\w$]*)");
	private static final Pattern _generatedCodePattern = Pattern.compile(
		"\\b(buildWSDL|buildXSD|wsdlBuilder|xsdBuilder)\\b");
	private static final Pattern _importPattern = Pattern.compile(
		"^[ \\t]*import[ \\t]+(static[ \\t]+)?([\\w$]+(?:[ \\t]*\\.[ \\t]*" +
			"[\\w$]+)*)([ \\t]*\\.[ \\t]*\\*)?",
		Pattern.MULTILINE);
	private static final Pattern _jspAttributePattern = Pattern.compile(
		"(\\w+)\\s*=\\s*(\"([^\"]*)\"|'([^']*)')");
	private static final Pattern _jspCommentPattern = Pattern.compile(
		"<%--[\\s\\S]*?--%>");
	private static final Pattern _jspDirectivePattern = Pattern.compile(
		"<%@\\s*(\\w+)([\\s\\S]*?)%>");
	private static final Pattern _jspScriptletPattern = Pattern.compile(
		"<%(?![@-])[=!]?([\\s\\S]*?)%>");
	private static final Pattern _jspTagPattern = Pattern.compile(
		"<([A-Za-z][\\w-]*):([A-Za-z][\\w.-]*)");
	private static final Pattern _jspUseBeanPattern = Pattern.compile(
		"<jsp:useBean[^>]*?\\b(?:class|type)\\s*=\\s*[\"']([\\w.$]+)[\"']");
	private static final Pattern _methodCallPattern = Pattern.compile(
		"\\.\\s*(\\w+)\\s*(?:<[^<>;(){}]*>\\s*)?\\(|::\\s*(\\w+)|" +
			"(?<![\\w$.])([a-z_$][\\w$]*)\\s*\\(");
	private static final Pattern _newTypePattern = Pattern.compile(
		"\\bnew\\s+([A-Za-z_$][\\w$]*(?:\\.[A-Za-z_$][\\w$]*)*)\\s*" +
			"(?:<[^;(){}]*?>\\s*)?\\(");
	private static final Pattern _packagePattern = Pattern.compile(
		"^[ \\t]*package[ \\t]+([\\w.]+)", Pattern.MULTILINE);
	private static final Pattern _projectDependencyPattern = Pattern.compile(
		"\n\t(compileOnly|testCompileOnly|testIntegrationCompileOnly|" +
			"testIntegrationImplementation) project\\(\"(:[^\"]+)\"\\)" +
				"(?=\n)");
	private static final Pattern _qualifiedNamePattern = Pattern.compile(
		"(?<![\\w$.])((?:[a-z_][a-z0-9_]*\\.)+[A-Z][\\w$]*)");
	private static final Map<String, String[]> _scopeNamesMap =
		HashMapBuilder.put(
			"compileOnly", new String[] {"main", "test", "testIntegration"}
		).put(
			"testCompileOnly", new String[] {"test"}
		).put(
			"testIntegrationCompileOnly", new String[] {"testIntegration"}
		).put(
			"testIntegrationImplementation", new String[] {"testIntegration"}
		).build();
	private static final Pattern _simpleClassNamePattern = Pattern.compile(
		"(?<![\\w$.])([A-Z][\\w$]*)");
	private static final Pattern _tldNamePattern = Pattern.compile(
		"<name>\\s*([^<\\s]+)\\s*</name>");
	private static final Pattern _tldTagClassPattern = Pattern.compile(
		"<(?:tag-class|tei-class)>\\s*([\\w.$]+)\\s*</");
	private static final Pattern _tldURIPattern = Pattern.compile(
		"<uri>\\s*([^<\\s]+)\\s*</uri>");

	private final Map<String, JavaDeclarationUtil.ClassDeclaration>
		_classDeclarations = new ConcurrentHashMap<>();
	private volatile Set<String> _classNames;
	private final Map<String, File> _javaFiles = new HashMap<>();
	private final Map<String, ProjectProvider> _projectProviders =
		new HashMap<>();
	private final Map<String, Map<String, Set<String>>> _tldTagClassNames =
		new HashMap<>();

	private static class ModuleReferences {

		public ScopeReferences getScopeReferences(String scopeName) {
			return scopeReferencesMap.get(scopeName);
		}

		public Map<String, String> bndDefinitions = Collections.emptyMap();
		public final Map<String, Set<String>> indirectClassNamesMap =
			new HashMap<>();
		public final Map<String, ScopeReferences> scopeReferencesMap =
			new HashMap<>();

	}

	private static class ProjectProvider {

		public boolean isEmpty() {
			if (classNames.isEmpty() && packageNames.isEmpty() &&
				packagePatterns.isEmpty()) {

				return true;
			}

			return false;
		}

		public String bundleSymbolicName;
		public final Set<String> classNames = new HashSet<>();
		public final Set<String> names = new HashSet<>();
		public final Set<String> packageNames = new HashSet<>();
		public final List<String> packagePatterns = new ArrayList<>();
		public final Set<String> tldURIs = new HashSet<>();

	}

	private static class ScopeReferences {

		public final Set<String> classNames = new HashSet<>();
		public final Set<String> declaredSuperClassNames = new HashSet<>();
		public final Set<String> extendedClassNames = new HashSet<>();
		public final Set<String> inheritedMethodNames = new HashSet<>();
		public final List<String[]> jspTags = new ArrayList<>();
		public final Set<String> methodNames = new HashSet<>();
		public final Map<String, Set<String>> nestedClassNames =
			new HashMap<>();
		public final Set<String> newTypeNames = new HashSet<>();
		public final Set<String> packageNames = new HashSet<>();
		public final Set<String> resourceClassNames = new HashSet<>();
		public final Set<String> stringClassNames = new HashSet<>();
		public final Map<String, Set<String>> taglibPrefixURIs =
			new HashMap<>();
		public final Set<String> taglibURIs = new HashSet<>();

	}

}