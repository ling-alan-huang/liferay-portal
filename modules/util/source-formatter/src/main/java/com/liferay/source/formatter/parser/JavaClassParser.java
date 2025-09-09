/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.parser;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.checkstyle.util.CheckstyleUtil;
import com.liferay.source.formatter.checkstyle.util.DetailASTUtil;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaClassParser {

	public static List<JavaClass> parseAnonymousClasses(String content)
		throws IOException, ParseException {

		return parseAnonymousClasses(content, null, Collections.emptyList());
	}

	public static List<JavaClass> parseAnonymousClasses(
			String content, String packageName, List<String> importNames)
            throws IOException, ParseException {

		List<JavaClass> anonymousClasses = new ArrayList<>();

		FileText fileText = new FileText(null, CheckstyleUtil.getLines(content));

		FileContents fileContents = new FileContents(fileText);

        DetailAST rootDetailAST;
		
        try {
            rootDetailAST = com.puppycrawl.tools.checkstyle.JavaParser.parse(fileContents);
        } catch (CheckstyleException checkstyleException) {
            throw new RuntimeException(checkstyleException);
        }

        List<DetailAST> leteralNewDetailASTList =
				DetailASTUtil.getAllChildTokens(
						rootDetailAST, false, TokenTypes.LITERAL_NEW);

		for (DetailAST leteralNewDetailAST : leteralNewDetailASTList) {
			DetailAST objBlockDetailAST = leteralNewDetailAST.findFirstToken(
					TokenTypes.OBJBLOCK);
			
			if (objBlockDetailAST == null) {
				continue;
			}

			DetailAST nameDetailAST = leteralNewDetailAST.findFirstToken(
					TokenTypes.IDENT);

			String classContent = _getJavaTermContent(fileContents, leteralNewDetailAST.getLineNo(),
					getEndLineNumber(leteralNewDetailAST));

			JavaClass anonymousClass = _parseJavaClass(
					JavaTerm.ACCESS_MODIFIER_PRIVATE, false, classContent, leteralNewDetailAST.getLineNo(),
					StringPool.BLANK, importNames, false,
					false, false, false, false, false, false,
					packageName, false, fileContents,leteralNewDetailAST);

			List<DetailAST> childDetailASTList =
					DetailASTUtil.getAllChildTokens(
							objBlockDetailAST, false, TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF,
							TokenTypes.STATIC_INIT, TokenTypes.VARIABLE_DEF);


			for (DetailAST childDetailAST : childDetailASTList) {
				String javaTermContent = _getJavaTermContent(fileContents, childDetailAST.getLineNo(),
						getEndLineNumber(childDetailAST));

				JavaTerm javaTerm = _getJavaTerm(
						packageName, importNames, javaTermContent,
						childDetailAST, fileContents);
				
				if (javaTerm == null) {
					throw new ParseException(
							"Parsing error at line \"" + childDetailAST.getLineNo() +
									"\"");
				}
				anonymousClass.addChildJavaTerm(javaTerm);

			}

			anonymousClasses.add(anonymousClass);
		}


		return anonymousClasses;
	}

	private static String _getJavaTermContent(
			FileContents fileContents, int start, int end) {

		StringBundler sb = new StringBundler();

		for (int i = start; i <= end; i++) {
			sb.append(fileContents.getLine(i - 1));
			sb.append("\n");
		}

		return sb.toString();
	}
	
	
	public static JavaClass parseJavaClass(String fileName, String content) throws IOException, ParseException {
		
		FileText fileText = new FileText(null, CheckstyleUtil.getLines(content));

		FileContents fileContents = new FileContents(fileText);

        DetailAST rootDetailAST;
		
        try {
            rootDetailAST = com.puppycrawl.tools.checkstyle.JavaParser.parse(fileContents);
        } catch (CheckstyleException checkstyleException) {
            throw new RuntimeException(checkstyleException);
        }

        DetailAST siblingDetailAST = rootDetailAST.getNextSibling();
		
		while (true) {
			if (siblingDetailAST == null) {
				break;
			}

			if (siblingDetailAST.getType() == TokenTypes.CLASS_DEF ||
					siblingDetailAST.getType() == TokenTypes.ENUM_DEF ||
					siblingDetailAST.getType() == TokenTypes.INTERFACE_DEF) {

				break;
			}
			siblingDetailAST = siblingDetailAST.getNextSibling();
		}

		boolean isEnum = false;
		boolean isInterface = false;

		if (siblingDetailAST.getType() == TokenTypes.ENUM_DEF) {
			isEnum = true;
		} else if (siblingDetailAST.getType() == TokenTypes.INTERFACE_DEF) {
			isInterface = true;
		}
		
		boolean isAbstract = false;
		boolean isFinal = false;
		boolean isStrictfp = false;
		boolean nonsealed = false;
		boolean sealed = false;

		DetailAST modifiersDetailAST =
				siblingDetailAST.findFirstToken(TokenTypes.MODIFIERS);

		if (modifiersDetailAST.branchContains(TokenTypes.ABSTRACT)) {
			isAbstract = true;
		}

		if (modifiersDetailAST.branchContains(TokenTypes.FINAL)) {
			isFinal = true;
		}

		if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_NON_SEALED)) {
			nonsealed = true;
		}

		if (modifiersDetailAST.branchContains(TokenTypes.STRICTFP)) {
			isStrictfp = true;
		}

		if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_SEALED)) {
			sealed = true;
		}

		String accessModifier = JavaTerm.ACCESS_MODIFIER_DEFAULT;

		if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_PRIVATE)) {
			accessModifier = JavaTerm.ACCESS_MODIFIER_PRIVATE;
		}
		else if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_PROTECTED)) {
			accessModifier = JavaTerm.ACCESS_MODIFIER_PROTECTED;
		}
		else if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_PUBLIC)) {
			accessModifier = JavaTerm.ACCESS_MODIFIER_PUBLIC;
		}

		DetailAST nameDetailAST = siblingDetailAST.findFirstToken(
				TokenTypes.IDENT);

		String className = nameDetailAST.getText();

		String classContent = _getJavaTermContent(fileContents, siblingDetailAST.getLineNo(),
				getEndLineNumber(siblingDetailAST));

		JavaClass javaClass = _parseJavaClass(
				accessModifier, false, classContent, siblingDetailAST.getLineNo(),
				className, JavaSourceUtil.getImportNames(content), isAbstract,
				isEnum, isFinal, isInterface, false, isStrictfp, nonsealed,
				JavaSourceUtil.getPackageName(content), sealed, fileContents,siblingDetailAST);

		_parseExtendsImplementsPermits(javaClass, siblingDetailAST);

		return javaClass;
			

		
	}
	private static int getEndLineNumber(DetailAST detailAST) {

		int endLineNumber = detailAST.getLineNo();

		for (DetailAST childDetailAST :
				DetailASTUtil.getAllChildTokens(detailAST, true, DetailASTUtil.ALL_TYPES)) {

			if (childDetailAST.getLineNo() > endLineNumber) {
				endLineNumber = childDetailAST.getLineNo();
			}
		}

		return endLineNumber;
	}



	private static String _getAnonymousClassContent(
		String content, int start, boolean genericClass) {

		int x = start;

		if (genericClass) {
			while (true) {
				x = content.indexOf('>', x + 1);

				if (x == -1) {
					return null;
				}

				int level = ToolsUtil.getLevel(
					content.substring(start, x + 1), "<", ">");

				if (level == 0) {
					break;
				}
			}

			if (!Objects.equals(content.charAt(x + 1), '(')) {
				return null;
			}
		}

		while (true) {
			x = content.indexOf(')', x + 1);

			if (x == -1) {
				return null;
			}

			if (ToolsUtil.getLevel(content.substring(start, x + 1), "(", ")") ==
					0) {

				break;
			}
		}

		String s = StringUtil.trim(content.substring(x + 1));

		if (!s.startsWith("{\n")) {
			return null;
		}

		while (true) {
			x = content.indexOf('}', x + 1);

			if (x == -1) {
				return null;
			}

			String anonymousClassContent = content.substring(start, x + 1);

			if (ToolsUtil.getLevel(anonymousClassContent, "{", "}") == 0) {
				return anonymousClassContent;
			}
		}
	}



	private static JavaTerm _getJavaTerm(
			String packageName, List<String> importNames,
			String javaTermContent, DetailAST detailAST, FileContents fileContents)
		throws IOException, ParseException {

		boolean isAbstract = false;
		boolean isFinal = false;
		boolean isStatic = false;
		boolean isStrictfp = false;
		boolean nonsealed = false;
		boolean sealed = false;

		DetailAST modifiersDetailAST =
				detailAST.findFirstToken(TokenTypes.MODIFIERS);

		if (modifiersDetailAST.branchContains(TokenTypes.ABSTRACT)) {
			isAbstract = true;
		}

		if (modifiersDetailAST.branchContains(TokenTypes.FINAL)) {
			isFinal = true;
		}

		if (modifiersDetailAST.branchContains(TokenTypes.STATIC_INIT)) {
			isStatic = true;
		}

		if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_NON_SEALED)) {
			nonsealed = true;
		}

		if (modifiersDetailAST.branchContains(TokenTypes.STRICTFP)) {
			isStrictfp = true;
		}

		if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_SEALED)) {
			sealed = true;
		}

		String accessModifier = JavaTerm.ACCESS_MODIFIER_DEFAULT;

		if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_PRIVATE)) {
			accessModifier = JavaTerm.ACCESS_MODIFIER_PRIVATE;
		}
		else if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_PROTECTED)) {
			accessModifier = JavaTerm.ACCESS_MODIFIER_PROTECTED;
		}
		else if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_PUBLIC)) {
			accessModifier = JavaTerm.ACCESS_MODIFIER_PUBLIC;
		}

		String content = _getJavaTermContent(fileContents, detailAST.getLineNo(),
				getEndLineNumber(detailAST));
		String name = _getName(detailAST.findFirstToken(TokenTypes.IDENT));

		if (detailAST.getType() == TokenTypes.CTOR_DEF) {
			return new JavaConstructor(
					accessModifier, content, isAbstract, isFinal, isStatic,
					detailAST.getLineNo(), name);
		}

		if (detailAST.getType() == TokenTypes.METHOD_DEF) {
			return new JavaMethod(
				accessModifier, content, isAbstract, isFinal, isStatic,
				detailAST.getLineNo(), name);
		}

		if (detailAST.getType() == TokenTypes.STATIC_INIT) {
			return new JavaStaticBlock(
					content,detailAST.getLineNo());
		}

		if (detailAST.getType() == TokenTypes.VARIABLE_DEF) {
			return new JavaVariable(
					accessModifier, content, isAbstract, isFinal, isStatic,
					detailAST.getLineNo(), name);
		}


		return null;
	}

	private static int _getJavaTermEndLineNumber(
		String classContent, int lineNumber) {

		int x = SourceUtil.getLineStartPos(classContent, lineNumber);

		String s = classContent.substring(x);

		Matcher matcher = _javaTermEndPattern.matcher(s);

		while (matcher.find()) {
			String javaTermContent = s.substring(0, matcher.end());

			if ((ToolsUtil.getLevel(javaTermContent, "(", ")") == 0) &&
				(ToolsUtil.getLevel(javaTermContent, "{", "}") == 0)) {

				return lineNumber + StringUtil.count(javaTermContent, "\n") - 1;
			}
		}

		return -1;
	}

	private static int _getMatchingEndLineNumber(
		String classContent, int lineNumber, String increaseLevelString,
		String decreaseLevelString) {

		int level = 0;

		while (true) {
			level += ToolsUtil.getLevel(
				SourceUtil.getLine(classContent, lineNumber),
				increaseLevelString, decreaseLevelString);

			if (level == 0) {
				return lineNumber;
			}

			lineNumber++;
		}
	}

	private static String _getVariableName(String line) {
		int x = line.indexOf(CharPool.EQUAL);
		int y = line.lastIndexOf(CharPool.SPACE);

		if (x != -1) {
			line = line.substring(0, x);
			line = StringUtil.trim(line);

			y = line.lastIndexOf(CharPool.SPACE);

			return line.substring(y + 1);
		}

		if (line.endsWith(StringPool.SEMICOLON)) {
			return line.substring(y + 1, line.length() - 1);
		}

		return StringPool.BLANK;
	}

	private static String _getName(DetailAST detailAST) {
		if (detailAST.getType() == TokenTypes.IDENT) {
			return detailAST.getText();
		}
		
		else if (detailAST.getType() == TokenTypes.DOT) {
			FullIdent fullIdent = FullIdent.createFullIdent(detailAST);

			return fullIdent.getText();

		}

		return null;
	}

	private static String[] _getNames(DetailAST detailAST) {
		List<String> names = new ArrayList<>();

		DetailAST childDetailAST = detailAST.getFirstChild();

		while (true) {
			if (childDetailAST == null) {
				break;
			}

			names.add(_getName(childDetailAST)) ;

			childDetailAST = childDetailAST.getNextSibling();
		}
		
		return names.toArray(
				new String[0]);
		
	}

	private static JavaClass _parseExtendsImplementsPermits(
			JavaClass javaClass, DetailAST detailAST)
	{

		DetailAST extendsClauseDetailAST =
				detailAST.findFirstToken(TokenTypes.EXTENDS_CLAUSE);
		
		if (extendsClauseDetailAST != null) {
			String[] extendedClassNames = _getNames(extendsClauseDetailAST.getFirstChild());

			javaClass.addExtendedClassNames(extendedClassNames);
		}

		DetailAST implementsClauseDetailAST =
				detailAST.findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);

		if (implementsClauseDetailAST != null) {
			String[] implementedClassNames = _getNames(implementsClauseDetailAST.getFirstChild());

			javaClass.addImplementedClassNames(implementedClassNames);
		}


		DetailAST permitsClauseDetailAST =
				detailAST.findFirstToken(TokenTypes.PERMITS_CLAUSE);

		if (permitsClauseDetailAST != null) {
			String[] permitsClassNames = _getNames(permitsClauseDetailAST.getFirstChild());

			javaClass.addPermittedClassNames(permitsClassNames);
		}


		return javaClass;
	}

//	private static JavaClass _parseExtendsImplementsPermits(
//			JavaClass javaClass, String s)
//		throws ParseException {
//
//		if (ToolsUtil.getLevel(s, "<", ">") != 0) {
//			throw new ParseException("Parsing error around class declaration");
//		}
//
//		outerLoop:
//		while (true) {
//			int x = s.indexOf("<");
//
//			if (x == -1) {
//				break;
//			}
//
//			int y = x;
//
//			while (true) {
//				y = s.indexOf(">", y + 1);
//
//				if (ToolsUtil.getLevel(s.substring(x, y + 1), "<", ">") == 0) {
//					s = StringUtil.trim(s.substring(0, x) + s.substring(y + 1));
//
//					continue outerLoop;
//				}
//			}
//		}
//
//		Matcher matcher = _permitsPattern.matcher(s);
//
//		if (matcher.find()) {
//			javaClass.addPermittedClassNames(
//				StringUtil.split(s.substring(matcher.end())));
//
//			s = s.substring(0, matcher.start());
//		}
//
//		s = StringUtil.trim(s);
//
//		matcher = _implementsPattern.matcher(s);
//
//		if (matcher.find()) {
//			javaClass.addImplementedClassNames(
//				StringUtil.split(s.substring(matcher.end())));
//
//			s = s.substring(0, matcher.start());
//		}
//
//		s = StringUtil.trim(s);
//
//		if (s.startsWith("extends")) {
//			javaClass.addExtendedClassNames(StringUtil.split(s.substring(7)));
//		}
//
//		return javaClass;
//	}

	private static JavaClass _parseJavaClass(
			String accessModifier, boolean anonymous, String classContent,
			int classLineNumber, String className, List<String> importNames,
			boolean isAbstract, boolean isEnum, boolean isFinal,
			boolean isInterface, boolean isStatic, boolean isStrictfp,
			boolean nonsealed, String packageName, boolean sealed, FileContents fileContents, DetailAST detailAST)
		throws IOException, ParseException {

		JavaClass javaClass = new JavaClass(
			accessModifier, anonymous, classContent, importNames, isAbstract,
			isFinal, isInterface, isStatic, isStrictfp, classLineNumber,
			className, nonsealed, packageName, sealed);

		DetailAST objBlockDetailAST = detailAST.findFirstToken(
				TokenTypes.OBJBLOCK);

		if (objBlockDetailAST == null) {
			return  null;
		}

		List<DetailAST> childDetailASTList = 
				DetailASTUtil.getAllChildTokens(
						objBlockDetailAST, false, TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF, 
						TokenTypes.STATIC_INIT, TokenTypes.VARIABLE_DEF);
		
		for (DetailAST childDetailAST : childDetailASTList) {

			String javaTermContent = _getJavaTermContent(fileContents, childDetailAST.getLineNo(),
					getEndLineNumber(childDetailAST));

			JavaTerm javaTerm = _getJavaTerm(
					packageName, importNames, javaTermContent,
					childDetailAST, fileContents);

			if (javaTerm == null) {
				throw new ParseException(
						"Parsing error at line \"" + childDetailAST.getLineNo() +
								"\"");
			}
			javaClass.addChildJavaTerm(javaTerm);

		}

		
		return javaClass;

	}

	private static final Pattern _anonymousClassPattern = Pattern.compile(
		"\\snew [\\w\\.\t\n]+(\\(|\\<)");
	private static final Pattern _implementsPattern = Pattern.compile(
		"(\\A|\\s)implements\\s");
	private static final Pattern _javaTermEndPattern = Pattern.compile(
		"[;}]\\s*?\n");
	private static final Pattern _javaTermStartLinePattern = Pattern.compile(
		".*?[{;]\\s*?\n", Pattern.DOTALL);
	private static final Pattern _permitsPattern = Pattern.compile(
		"(\\A|\\s)permits\\s");

}