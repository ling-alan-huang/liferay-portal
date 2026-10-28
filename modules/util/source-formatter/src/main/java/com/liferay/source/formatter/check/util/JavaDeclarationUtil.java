/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check.util;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shuyang Zhou
 */
public class JavaDeclarationUtil {

	public static ClassDeclaration parseClassDeclaration(
		Set<String> classNames, File file) {

		ClassDeclaration classDeclaration = new ClassDeclaration();

		String content = FileUtil.read(file);

		if (content == null) {
			return classDeclaration;
		}

		String code = splitCode(content, new ArrayList<>());

		Matcher matcher = _packagePattern.matcher(code);

		String packageName = null;

		if (matcher.find()) {
			packageName = matcher.group(1);
		}

		Map<String, String> importNames = new HashMap<>();
		List<String> wildcardImportNames = new ArrayList<>();

		matcher = _importPattern.matcher(code);

		while (matcher.find()) {
			String name = StringUtil.removeChars(
				matcher.group(2), CharPool.SPACE, CharPool.TAB);

			if ((matcher.group(1) == null) && (matcher.group(3) != null)) {
				wildcardImportNames.add(name);

				continue;
			}

			classDeclaration._referencedNames.add(name);

			if ((matcher.group(1) != null) || (matcher.group(3) != null)) {
				continue;
			}

			importNames.put(
				name.substring(name.lastIndexOf(CharPool.PERIOD) + 1), name);
		}

		matcher = _codeQualifiedNamePattern.matcher(code);

		while (matcher.find()) {
			classDeclaration._referencedNames.add(
				StringUtil.removeChars(
					matcher.group(1), CharPool.NEW_LINE, CharPool.SPACE,
					CharPool.TAB));
		}

		String fileName = file.getName();

		String simpleClassName = fileName.substring(
			0, fileName.lastIndexOf(CharPool.PERIOD));

		_parseTypeDeclarations(
			classDeclaration, classNames, code, importNames, packageName,
			simpleClassName, wildcardImportNames);

		matcher = _newTypePattern.matcher(code);

		while (matcher.find()) {
			int index = _getClosingIndex(
				CharPool.CLOSE_PARENTHESIS, matcher.end() - 1,
				CharPool.OPEN_PARENTHESIS, code);

			if (index == -1) {
				continue;
			}

			index = _skipWhitespace(index + 1, code);

			if ((index == code.length()) ||
				(code.charAt(index) != CharPool.OPEN_CURLY_BRACE)) {

				continue;
			}

			classDeclaration._anonymousSuperClassNames.addAll(
				_resolveTypeNames(
					classNames, importNames,
					Collections.singletonList(matcher.group(1)), packageName,
					wildcardImportNames));
		}

		Pattern constructorPattern = Pattern.compile(
			StringBundler.concat(
				"(?:^|[;{}])\\s*(?:@[\\w.$]+(?:\\([^()]*\\))?\\s*)*",
				"((?:(?:public|protected|private)\\s+)?)",
				Pattern.quote(simpleClassName),
				"\\s*\\(([^;{}()]*(?:\\([^()]*\\)[^;{}()]*)*)\\)\\s*",
				"(?:throws\\s+([\\w.$,\\s]+))?\\s*\\{"),
			Pattern.MULTILINE);

		matcher = constructorPattern.matcher(code);

		while (matcher.find()) {
			String modifiers = matcher.group(1);

			if (modifiers.contains("private")) {
				continue;
			}

			List<String> typeNames = _parseParameterTypeNames(matcher.group(2));

			_parseThrowsTypeNames(matcher.group(3), typeNames);

			_putMethodTypeNames(
				classNames, importNames, "<init>",
				classDeclaration._methodTypeNames, packageName, typeNames,
				wildcardImportNames);
		}

		matcher = _methodPattern.matcher(code);

		while (matcher.find()) {
			String modifiers = matcher.group(1);
			String returnTypeName = matcher.group(2);

			if (modifiers.contains("private") ||
				ArrayUtil.contains(
					new String[] {"case", "else", "new", "return", "throw"},
					returnTypeName)) {

				continue;
			}

			List<String> typeNames = _getTypeNames(returnTypeName);

			_parseThrowsTypeNames(matcher.group(5), typeNames);

			typeNames.addAll(_parseParameterTypeNames(matcher.group(4)));

			String methodName = matcher.group(3);

			_putMethodTypeNames(
				classNames, importNames, methodName,
				classDeclaration._methodTypeNames, packageName, typeNames,
				wildcardImportNames);

			classDeclaration._methodCounts.merge(methodName, 1, Integer::sum);

			String terminator = matcher.group(6);

			if (!modifiers.contains("abstract") &&
				(!classDeclaration._interfaceType ||
				 !terminator.equals(StringPool.SEMICOLON) ||
				 modifiers.contains("default") ||
				 modifiers.contains("static"))) {

				continue;
			}

			_putMethodTypeNames(
				classNames, importNames, methodName,
				classDeclaration._abstractMethodTypeNames, packageName,
				typeNames, wildcardImportNames);
		}

		return classDeclaration;
	}

	public static String splitCode(String content, List<String> strings) {
		StringBundler sb = new StringBundler();

		int index = 0;
		int start = 0;

		while (index < content.length()) {
			char c = content.charAt(index);

			int end = -1;
			boolean literal = false;

			if (c == CharPool.QUOTE) {
				if (content.startsWith("\"\"\"", index)) {
					end = content.indexOf("\"\"\"", index + 3);

					if (end != -1) {
						end += 3;
					}
				}

				if (end == -1) {
					end = _getQuotedEnd(true, content, index, c);
				}

				literal = true;
			}
			else if (c == CharPool.APOSTROPHE) {
				end = _getQuotedEnd(false, content, index, c);
				literal = true;
			}
			else if (content.startsWith("//", index)) {
				end = content.indexOf(CharPool.NEW_LINE, index);

				if (end == -1) {
					end = content.length();
				}
			}
			else if (content.startsWith("/*", index)) {
				end = content.indexOf("*/", index + 2);

				if (end != -1) {
					end += 2;
				}
			}

			if (end == -1) {
				index++;

				continue;
			}

			sb.append(content.substring(start, index));

			if (literal) {
				strings.add(content.substring(index, end));

				sb.append("\"\"");
			}
			else {
				sb.append(StringPool.SPACE);
			}

			index = end;
			start = end;
		}

		sb.append(content.substring(start));

		return sb.toString();
	}

	public static class ClassDeclaration {

		public Map<String, Set<String>> getAbstractMethodTypeNames() {
			return _abstractMethodTypeNames;
		}

		public List<String> getAnonymousSuperClassNames() {
			return _anonymousSuperClassNames;
		}

		public List<String> getExtendedClassNames() {
			return _extendedClassNames;
		}

		public Map<String, Integer> getMethodCounts() {
			return _methodCounts;
		}

		public Map<String, Set<String>> getMethodTypeNames() {
			return _methodTypeNames;
		}

		public Map<String, List<String>> getNestedSuperClassNames() {
			return _nestedSuperClassNames;
		}

		public Set<String> getReferencedNames() {
			return _referencedNames;
		}

		public List<String> getSuperClassNames() {
			return _superClassNames;
		}

		public boolean isGeneric() {
			return _generic;
		}

		public boolean isInterfaceType() {
			return _interfaceType;
		}

		private final Map<String, Set<String>> _abstractMethodTypeNames =
			new HashMap<>();
		private final List<String> _anonymousSuperClassNames =
			new ArrayList<>();
		private final List<String> _extendedClassNames = new ArrayList<>();
		private boolean _generic;
		private boolean _interfaceType;
		private final Map<String, Integer> _methodCounts = new HashMap<>();
		private final Map<String, Set<String>> _methodTypeNames =
			new HashMap<>();
		private final Map<String, List<String>> _nestedSuperClassNames =
			new HashMap<>();
		private final Set<String> _referencedNames = new HashSet<>();
		private final List<String> _superClassNames = new ArrayList<>();

	}

	private static int _getClosingIndex(
		char close, int index, char open, String s) {

		int level = 0;

		for (int i = index; i < s.length(); i++) {
			char c = s.charAt(i);

			if (c == open) {
				level++;
			}
			else if ((c == close) && (--level == 0)) {
				return i;
			}
		}

		return -1;
	}

	private static int _getQuotedEnd(
		boolean allowEmpty, String content, int index, char quote) {

		for (int i = index + 1; i < content.length(); i++) {
			char c = content.charAt(i);

			if (c == quote) {
				if (!allowEmpty && (i == (index + 1))) {
					return -1;
				}

				return i + 1;
			}

			if (c == CharPool.NEW_LINE) {
				return -1;
			}

			if (c != CharPool.BACK_SLASH) {
				continue;
			}

			if (((i + 1) == content.length()) ||
				(content.charAt(i + 1) == CharPool.NEW_LINE)) {

				return -1;
			}

			i++;
		}

		return -1;
	}

	private static List<String> _getTypeNames(String s) {
		List<String> typeNames = new ArrayList<>();

		Matcher matcher = _typeSeparatorPattern.matcher(s);

		matcher = _typeNamePattern.matcher(
			matcher.replaceAll(StringPool.SPACE));

		while (matcher.find()) {
			String typeName = matcher.group(1);

			if (_primitiveTypeNames.contains(typeName) ||
				typeName.equals("extends") || typeName.equals("final") ||
				typeName.equals("super")) {

				continue;
			}

			typeNames.add(typeName);
		}

		return typeNames;
	}

	private static List<String> _parseParameterTypeNames(String parameters) {
		List<String> typeNames = new ArrayList<>();

		for (String parameter : _splitTopLevel(parameters)) {
			Matcher matcher = _annotationPattern.matcher(parameter);

			parameter = matcher.replaceAll(StringPool.SPACE);

			parameter = StringUtil.trim(
				StringUtil.replace(
					parameter, new String[] {"final ", "..."},
					new String[] {StringPool.SPACE, StringPool.SPACE}));

			matcher = _parameterPattern.matcher(parameter);

			if (!matcher.matches()) {
				continue;
			}

			typeNames.addAll(_getTypeNames(matcher.group(1)));
		}

		return typeNames;
	}

	private static List<String> _parseSuperTypeNames(
		String header, Pattern pattern) {

		Matcher matcher = pattern.matcher(header);

		if (!matcher.find()) {
			return Collections.emptyList();
		}

		return TransformUtil.transform(
			_splitTopLevel(_trimClause(header.substring(matcher.end()))),
			JavaDeclarationUtil::_stripGenerics);
	}

	private static void _parseThrowsTypeNames(
		String s, List<String> typeNames) {

		if (s == null) {
			return;
		}

		for (String typeName : StringUtil.split(s)) {
			typeName = StringUtil.trim(typeName);

			if (typeName.isEmpty()) {
				continue;
			}

			typeNames.add(typeName);
		}
	}

	private static void _parseTypeDeclarations(
		ClassDeclaration classDeclaration, Set<String> classNames, String code,
		Map<String, String> importNames, String packageName,
		String simpleClassName, List<String> wildcardImportNames) {

		boolean mainType = true;

		Matcher matcher = _typeDeclarationPattern.matcher(code);

		while (matcher.find()) {
			boolean generic = false;
			int index = _skipWhitespace(matcher.end(), code);

			if ((index < code.length()) &&
				(code.charAt(index) == CharPool.LESS_THAN)) {

				generic = true;

				index = _getClosingIndex(
					CharPool.GREATER_THAN, index, CharPool.LESS_THAN, code);

				if (index == -1) {
					continue;
				}

				index = _skipWhitespace(index + 1, code);
			}

			if ((index < code.length()) &&
				(code.charAt(index) == CharPool.OPEN_PARENTHESIS)) {

				index = code.indexOf(CharPool.CLOSE_PARENTHESIS, index);

				if (index == -1) {
					continue;
				}

				index++;
			}

			int end = code.indexOf(CharPool.OPEN_CURLY_BRACE, index);

			if (end == -1) {
				continue;
			}

			String header = code.substring(index, end);

			if (header.contains(StringPool.SEMICOLON)) {
				continue;
			}

			List<String> extendedClassNames = _resolveTypeNames(
				classNames, importNames,
				_parseSuperTypeNames(header, _extendsPattern), packageName,
				wildcardImportNames);
			List<String> implementedClassNames = _resolveTypeNames(
				classNames, importNames,
				_parseSuperTypeNames(header, _implementsPattern), packageName,
				wildcardImportNames);

			String typeName = matcher.group(2);

			if (mainType && typeName.equals(simpleClassName)) {
				mainType = false;

				classDeclaration._extendedClassNames.addAll(extendedClassNames);
				classDeclaration._generic = generic;

				String keyword = matcher.group(1);

				classDeclaration._interfaceType = keyword.endsWith("interface");

				classDeclaration._superClassNames.addAll(extendedClassNames);
				classDeclaration._superClassNames.addAll(implementedClassNames);
			}
			else {
				List<String> nestedSuperClassNames =
					classDeclaration._nestedSuperClassNames.computeIfAbsent(
						typeName, key -> new ArrayList<>());

				nestedSuperClassNames.addAll(extendedClassNames);
				nestedSuperClassNames.addAll(implementedClassNames);
			}
		}
	}

	private static void _putMethodTypeNames(
		Set<String> classNames, Map<String, String> importNames,
		String methodName, Map<String, Set<String>> methodTypeNamesMap,
		String packageName, List<String> typeNames,
		List<String> wildcardImportNames) {

		Set<String> methodTypeNames = methodTypeNamesMap.computeIfAbsent(
			methodName, key -> new HashSet<>());

		for (List<String> candidateNames :
				_resolveNames(
					importNames, typeNames, packageName, wildcardImportNames)) {

			String typeName = _resolveCandidateName(candidateNames, classNames);

			if (typeName == null) {
				continue;
			}

			methodTypeNames.add(typeName);
		}
	}

	private static String _resolveCandidateName(
		List<String> candidateNames, Set<String> classNames) {

		for (String candidateName : candidateNames) {
			if (classNames.contains(candidateName)) {
				return candidateName;
			}
		}

		return null;
	}

	private static List<List<String>> _resolveNames(
		Map<String, String> importNames, List<String> names, String packageName,
		List<String> wildcardImportNames) {

		List<List<String>> candidateNamesList = new ArrayList<>();

		for (String name : names) {
			name = StringUtil.trim(name);

			if (name.isEmpty() || _primitiveTypeNames.contains(name)) {
				continue;
			}

			String[] segments = StringUtil.split(name, CharPool.PERIOD);

			if (segments.length == 0) {
				continue;
			}

			if ((segments.length > 1) && !segments[0].isEmpty() &&
				Character.isLowerCase(segments[0].charAt(0))) {

				for (int i = 0; i < segments.length; i++) {
					if (segments[i].isEmpty() ||
						!Character.isLowerCase(segments[i].charAt(0))) {

						candidateNamesList.add(
							Collections.singletonList(
								StringUtil.merge(
									ArrayUtil.subset(segments, 0, i + 1),
									StringPool.PERIOD)));

						break;
					}
				}

				continue;
			}

			String simpleName = segments[0];

			if (simpleName.isEmpty() ||
				!Character.isUpperCase(simpleName.charAt(0))) {

				continue;
			}

			String importName = importNames.get(simpleName);

			if (importName != null) {
				candidateNamesList.add(Collections.singletonList(importName));

				continue;
			}

			List<String> candidateNames = new ArrayList<>();

			if (packageName != null) {
				candidateNames.add(packageName + "." + simpleName);
			}

			for (String wildcardImportName : wildcardImportNames) {
				candidateNames.add(wildcardImportName + "." + simpleName);
			}

			candidateNames.add("java.lang." + simpleName);

			candidateNamesList.add(candidateNames);
		}

		return candidateNamesList;
	}

	private static List<String> _resolveTypeNames(
		Set<String> classNames, Map<String, String> importNames,
		List<String> names, String packageName,
		List<String> wildcardImportNames) {

		return TransformUtil.transform(
			_resolveNames(importNames, names, packageName, wildcardImportNames),
			candidateNames -> {
				String typeName = _resolveCandidateName(
					candidateNames, classNames);

				if (typeName != null) {
					return typeName;
				}

				if (candidateNames.size() == 1) {
					return candidateNames.get(0);
				}

				return null;
			});
	}

	private static int _skipWhitespace(int index, String s) {
		while ((index < s.length()) &&
			   Character.isWhitespace(s.charAt(index))) {

			index++;
		}

		return index;
	}

	private static List<String> _splitTopLevel(String s) {
		List<String> parts = new ArrayList<>();

		int level = 0;
		int start = 0;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if ((c == CharPool.LESS_THAN) || (c == CharPool.OPEN_PARENTHESIS)) {
				level++;
			}
			else if ((c == CharPool.GREATER_THAN) ||
					 (c == CharPool.CLOSE_PARENTHESIS)) {

				level--;
			}
			else if ((c == CharPool.COMMA) && (level == 0)) {
				parts.add(StringUtil.trim(s.substring(start, i)));

				start = i + 1;
			}
		}

		parts.add(StringUtil.trim(s.substring(start)));

		parts.removeIf(String::isEmpty);

		return parts;
	}

	private static String _stripGenerics(String s) {
		StringBundler sb = new StringBundler();

		int level = 0;
		int start = 0;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (c == CharPool.LESS_THAN) {
				if (level == 0) {
					sb.append(s.substring(start, i));
				}

				level++;
			}
			else if ((c == CharPool.GREATER_THAN) && (level > 0) &&
					 (--level == 0)) {

				start = i + 1;
			}
		}

		if (level == 0) {
			sb.append(s.substring(start));
		}

		return StringUtil.trim(sb.toString());
	}

	private static String _trimClause(String s) {
		Matcher matcher = _clauseEndPattern.matcher(s);

		if (matcher.find()) {
			return s.substring(0, matcher.start());
		}

		return s;
	}

	private static final Pattern _annotationPattern = Pattern.compile(
		"@[\\w.$]+(\\([^)]*\\))?");
	private static final Pattern _clauseEndPattern = Pattern.compile(
		"\\b(?:implements|permits)\\b");
	private static final Pattern _codeQualifiedNamePattern = Pattern.compile(
		"(?<![\\w$.])((?:[a-z_][a-z0-9_]*\\.\\s*)+[A-Z][\\w$]*)");
	private static final Pattern _extendsPattern = Pattern.compile(
		"\\bextends\\s+");
	private static final Pattern _implementsPattern = Pattern.compile(
		"\\bimplements\\s+");
	private static final Pattern _importPattern = Pattern.compile(
		"^[ \\t]*import[ \\t]+(static[ \\t]+)?([\\w$]+(?:[ \\t]*\\.[ \\t]*" +
			"[\\w$]+)*)([ \\t]*\\.[ \\t]*\\*)?",
		Pattern.MULTILINE);
	private static final Pattern _methodPattern = Pattern.compile(
		StringBundler.concat(
			"(?:^|[;{}])\\s*(?:@[\\w.$]+(?:\\([^()]*(?:\\([^()]*\\)[^()]*)*",
			"\\))?\\s*)*((?:(?:public|protected|private|static|final|",
			"abstract|default|synchronized|native|strictfp)\\s+)*)",
			"(?:<(?:[^<>;{}]|<(?:[^<>;{}]|<[^<>;{}]*>)*>)*>\\s+)?",
			"([\\w.$]+(?:\\s*<(?:[^<>;{}()]|<(?:[^<>;{}()]|",
			"<[^<>;{}()]*>)*>)*>)?(?:\\s*\\[\\s*\\])*)\\s+",
			"([a-z_$][\\w$]*)\\s*\\(([^;{}()]*(?:\\([^()]*\\)[^;{}()]*)*)",
			"\\)\\s*(?:throws\\s+([\\w.$,\\s]+))?\\s*([;{])"),
		Pattern.MULTILINE);
	private static final Pattern _newTypePattern = Pattern.compile(
		"\\bnew\\s+([A-Za-z_$][\\w$]*(?:\\.[A-Za-z_$][\\w$]*)*)\\s*" +
			"(?:<[^;(){}]*?>\\s*)?\\(");
	private static final Pattern _packagePattern = Pattern.compile(
		"^[ \\t]*package[ \\t]+([\\w.]+)", Pattern.MULTILINE);
	private static final Pattern _parameterPattern = Pattern.compile(
		"(.*\\S)\\s+\\S+", Pattern.DOTALL);
	private static final Set<String> _primitiveTypeNames = SetUtil.fromArray(
		"boolean", "byte", "char", "double", "float", "int", "long", "short",
		"var", "void");
	private static final Pattern _typeDeclarationPattern = Pattern.compile(
		"(?:^|[;{}\\s])(class|interface|enum|record|@interface)\\s+" +
			"([A-Z_$][\\w$]*)(?![\\w$])",
		Pattern.MULTILINE);
	private static final Pattern _typeNamePattern = Pattern.compile(
		"(?<![\\w$.])([A-Za-z_$][\\w$]*(?:\\.[A-Za-z_$][\\w$]*)*)");
	private static final Pattern _typeSeparatorPattern = Pattern.compile(
		"[<>\\[\\],?&@]");

}