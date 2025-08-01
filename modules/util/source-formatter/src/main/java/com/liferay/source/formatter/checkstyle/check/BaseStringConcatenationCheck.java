/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.source.formatter.checkstyle.util.CheckstyleUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public abstract class BaseStringConcatenationCheck extends BaseCheck {

	protected void checkCombineOperand(
		DetailAST literalStringDetailAST, DetailAST operandDetailAST) {

		if (operandDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		FullIdent fullIdent = FullIdent.createFullIdent(operandDetailAST);

		String text = fullIdent.getText();

		if ((text.startsWith("CharPool.") || text.startsWith("StringPool.")) &&
			!text.endsWith(".DEFAULT_CHARSET_NAME") &&
			!text.endsWith(".DELETE") && !text.endsWith(".NO_BREAK_SPACE")) {

			log(
				literalStringDetailAST, _MSG_COMBINE_STRING,
				getStringValue(literalStringDetailAST), text);
		}
	}

	protected void checkLiteralStringBreaks(
		DetailAST rightHandOperandDetailAST, String line1, String line2,
		String value1, String value2) {

		if (!value1.matches("^(.*([ /\\.,]))?(\\w+)$") ||
			!value2.matches("^(\\w+)(([ /\\.,]).*)?$")) {

			return;
		}

		int lineLength1 = CommonUtil.lengthExpandedTabs(
			line1, line1.length(), getTabWidth());

		int x = _getStringBreakPos(value2, getMaxLineLength() - lineLength1);

		if (x != -1) {
			log(
				rightHandOperandDetailAST, MSG_MOVE_LITERAL_STRING,
				value2.substring(0, x + 1), "previous");

			return;
		}

		x = _getLastStringBreakPos(value1);

		if (x != -1) {
			int lineLength2 = CommonUtil.lengthExpandedTabs(
				line2, line2.length(), getTabWidth());

			String s = value1.substring(x);

			if ((lineLength2 + s.length()) <= getMaxLineLength()) {
				log(
					rightHandOperandDetailAST, MSG_MOVE_LITERAL_STRING, s,
					"next");
			}
		}
	}

	protected void checkLiteralStringStartAndEndCharacter(
		String literalString1, String literalString2, int lineNumber) {

		if (literalString1.endsWith(StringPool.SLASH)) {
			log(
				lineNumber, _MSG_INVALID_END_CHARACTER,
				literalString1.charAt(literalString1.length() - 1));
		}
	}

	protected int getMaxLineLength() {
		return GetterUtil.getInteger(
			getAttributeValue(CheckstyleUtil.MAX_LINE_LENGTH_KEY));
	}

	protected int getStringBreakPos(String s1, String s2, int i) {
//		if (s2.startsWith(StringPool.SLASH)) {
//			int pos = s2.lastIndexOf(StringPool.SLASH, i);
//
//			if (pos > 0) {
//				return pos - 1;
//			}
//
//			return -1;
//		}
//
//		if (s1.endsWith(StringPool.DASH)) {
//			return Math.max(
//				s2.lastIndexOf(StringPool.DASH, i - 1),
//				s2.lastIndexOf(StringPool.SPACE, i - 1));
//		}
//
//		if (s1.endsWith(StringPool.PERIOD)) {
//			return Math.max(
//				s2.lastIndexOf(StringPool.PERIOD, i - 1),
//				s2.lastIndexOf(StringPool.SPACE, i - 1));
//		}
//
//		if (s1.endsWith(StringPool.SPACE) || !s1.matches(".*\\\\w")) {
//			return s2.lastIndexOf(StringPool.SPACE, i - 1);
//		}

//		i = i - 1;


		// \\u[a-z]{4}:ua123
		// \b\f\n\r\t\"\'\\

		String s = s2.substring(0, i);

		if
		int a = 0;

		return i;
//		while (true) {
//			if (i == -1) {
//				return -1;
//			}
//
//			String s = s2.substring(0, i);
//
//			int a = 0;
////			char c =  s2.charAt(i);
////
////			if (c == '/' || c == '\\' || c == '"') {
////				i = i - 1;
////				continue;
////			}
////
////			if (!Character.isLetterOrDigit(c)) {
////				return i;
////			}
////
////			i = i - 1;
//		}

	}
//	public class RegexEndsWith {
//		public static void main(String[] args) {
//			String text = "exampleText";
//			String patternString = "ext$"; // 如果你想检查字符串是否以"ext"结束
//
//			Pattern pattern = Pattern.compile(patternString);
//			Matcher matcher = pattern.matcher(text);
//
//			if (matcher.find()) { // 使用find()而不是matches()，因为我们要检查结尾
//				System.out.println("字符串以指定模式结束。");
//			} else {
//				System.out.println("字符串不以指定模式结束。");
//			}
//
//			matche
//		}
//	}
	protected String getStringValue(DetailAST stringLiteralDetailAST) {
		String stringValue = stringLiteralDetailAST.getText();

		return stringValue.substring(1, stringValue.length() - 1);
	}

	protected static final String MSG_COMBINE_LITERAL_STRINGS =
		"literal.string.combine";

	protected static final String MSG_INCORRECT_PLUS = "plus.incorrect";

	protected static final String MSG_MOVE_LITERAL_STRING =
		"literal.string.move";

	private int _getLastStringBreakPos(String s) {
		int x = Math.max(
			s.lastIndexOf(StringPool.DASH),
			Math.max(
				s.lastIndexOf(StringPool.PERIOD),
				s.lastIndexOf(StringPool.SPACE)));

		if (x == -1) {
			return s.lastIndexOf(StringPool.SLASH);
		}

		return Math.max(x + 1, s.lastIndexOf(StringPool.SLASH));
	}

	private int _getStringBreakPos(String s, int i) {
		int x = Math.max(
			s.lastIndexOf(StringPool.DASH, i - 1),
			Math.max(
				s.lastIndexOf(StringPool.PERIOD, i - 1),
				s.lastIndexOf(StringPool.SPACE, i - 1)));

		int y = s.lastIndexOf(StringPool.SLASH, i);

		if (y > 0) {
			return Math.max(x, y - 1);
		}

		return x;
	}

	private static final String _MSG_COMBINE_STRING = "string.combine";

	private static final String _MSG_INVALID_END_CHARACTER =
		"end.character.invalid";

}