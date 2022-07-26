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

package com.liferay.source.formatter.util;

import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author Qi Zhang
 */
public class SQLFormatterUtil {

	public static String format(String source) {
		FormatProcess formatProcess = new FormatProcess(source);

		return formatProcess.perform();
	}

	private static final String _INDENT_STRING = "\t";

	private static final Set<String> _nonfunctionNames = SetUtil.fromArray(
		"select", "from", "on", "set", "and", "or", "where", "having", "by",
		"all", "in");

	private static class FormatProcess {

		public FormatProcess(String sql) {
			assert sql != null : "SQL to format should not be null";

			_tokens = new StringTokenizer(
				sql, "()+*/-=<>'`\"[], \n\r\f\t", true);
		}

		public String perform() {
			while (_tokens.hasMoreTokens()) {
				_token = _tokens.nextToken();

				_lcToken = _token.toLowerCase(LocaleUtil.ROOT);

				if (StringUtil.equals(_lcToken, "'") ||
					StringUtil.equals(_lcToken, "\"")) {

					String t;

					do {
						t = _tokens.nextToken();

						_token = _token + t;
					}
					while (!_lcToken.equals(t) && _tokens.hasMoreTokens());

					_lcToken = _token;
					_misc();
				}
				else if (StringUtil.equals(_lcToken, "[")) {
					String tt;

					do {
						tt = _tokens.nextToken();

						_token = _token + tt;
					}
					while (!StringUtil.equals(tt, "]") &&
						   _tokens.hasMoreTokens());

					_lcToken = _token;
					_misc();
				}
				else if (StringUtil.equals(_lcToken, ",")) {
					if (_afterByOrSetOrFromOrSelect && (_inFunction == 0)) {
						_commaAfterByOrFromOrSelect();
					}
					else {
						if (_afterOn && (_inFunction == 0)) {
							_commaAfterOn();

							break;
						}

						_misc();

						if (_afterCreate && (_inFunction == 1)) {
							_newline();
						}
					}
				}
				else if (StringUtil.equals(_lcToken, "(")) {
					_openParen();
				}
				else if (StringUtil.equals(_lcToken, ")")) {
					_closeParen();
				}
				else if (StringUtil.equals(_lcToken, "all")) {
					_all();
				}
				else if (StringUtil.equals(_lcToken, "create")) {
					_create();
				}
				else if (StringUtil.equals(_lcToken, "select")) {
					_select();
				}
				else if (StringUtil.equals(_lcToken, "update")) {
					_update();
				}
				else if (StringUtil.equals(_lcToken, "insert") ||
						 StringUtil.equals(_lcToken, "delete")) {

					_insertOrDelete();
				}
				else if (StringUtil.equals(_lcToken, "on")) {
					_on();
				}
				else if (StringUtil.equals(_lcToken, "between")) {
					_afterBetween = true;
					_misc();
				}
				else if (StringUtil.equals(_lcToken, "trim") ||
						 StringUtil.equals(_lcToken, "extract")) {

					_afterExtract = true;
					_misc();
				}
				else if (StringUtil.equals(_lcToken, "left") ||
						 StringUtil.equals(_lcToken, "right") ||
						 StringUtil.equals(_lcToken, "full") ||
						 StringUtil.equals(_lcToken, "inner") ||
						 StringUtil.equals(_lcToken, "outer") ||
						 StringUtil.equals(_lcToken, "cross") ||
						 StringUtil.equals(_lcToken, "group") ||
						 StringUtil.equals(_lcToken, "order")) {

					_beginNewClause();
				}
				else if (StringUtil.equals(_lcToken, "from")) {
					if (_afterExtract) {
						_misc();
						_afterExtract = false;
					}
					else {
						_endNewClause();
					}
				}
				else if (StringUtil.equals(_lcToken, "where") ||
						 StringUtil.equals(_lcToken, "set") ||
						 StringUtil.equals(_lcToken, "having") ||
						 StringUtil.equals(_lcToken, "by") ||
						 StringUtil.equals(_lcToken, "join") ||
						 StringUtil.equals(_lcToken, "union") ||
						 StringUtil.equals(_lcToken, "intersect")) {

					_endNewClause();
				}
				else if (StringUtil.equals(_lcToken, "case")) {
					_beginCase();
				}
				else if (StringUtil.equals(_lcToken, "end")) {
					_endCase();
				}
				else if (StringUtil.equals(_lcToken, "and")) {
					if (_afterBetween) {
						_misc();
						_afterBetween = false;
					}
					else {
						_logical();
					}
				}
				else if (StringUtil.equals(_lcToken, "or") ||
						 StringUtil.equals(_lcToken, "when") ||
						 StringUtil.equals(_lcToken, "else")) {

					_logical();
				}
				else if (StringUtil.equals(_lcToken, "index")) {
					_afterIndex = true;
					_afterCreate = false;
					_misc();
				}
				else {
					if (_isWhitespace(_token)) {
						_white();
					}
					else {
						_misc();
					}
				}

				if (!_isWhitespace(_token)) {
					_lastToken = _lcToken;
				}
			}

			return _resultSB.toString();
		}

		private static boolean _isFunctionName(String tok) {
			if ((tok != null) && (tok.length() != 0)) {
				char begin = tok.charAt(0);

				boolean identifier = false;

				if (Character.isJavaIdentifierStart(begin) || ('"' == begin)) {
					identifier = true;
				}

				if (identifier &&
					!SQLFormatterUtil._nonfunctionNames.contains(tok)) {

					return true;
				}

				return false;
			}

			return false;
		}

		private static boolean _isWhitespace(String token) {
			return " \n\r\f\t".contains(token);
		}

		private void _addOpenParensNewLine(boolean newLine) {
			ParensMessage parensMessage = _openParensMessages.getLast();

			parensMessage.setNewLine(newLine);
		}

		private void _all() {
			_misc();

			_newline();
		}

		private void _beginCase() {
			_out();
			_beginLine = false;
			_indent++;
		}

		private void _beginNewClause() {
			if (!_afterBeginBeforeEnd) {
				if (_afterOn) {
					_reduceIndent();
					_afterOn = false;
				}

				_reduceIndent();
				_newline();
			}

			_out();
			_beginLine = false;
			_afterBeginBeforeEnd = true;
		}

		private void _closeParen() {
			_parensSinceSelect--;
			_parens--;

			if (_parensSinceSelect < 0) {
				_parensSinceSelect = _parenCounts.removeLast() - 1;
				_afterByOrSetOrFromOrSelect =
					_afterByOrFromOrSelects.removeLast();
			}

			ParensMessage parensMessage = _openParensMessages.removeLast();

			if (_inFunction > 0) {
				_inFunction--;
			}

			_indent = parensMessage.getIndent();

			if (parensMessage.getNewLine()) {
				_newline();
			}

			_out();

			_beginLine = false;
		}

		private void _commaAfterByOrFromOrSelect() {
			_out();

			if (!_afterUpdate) {
				_newline();
			}
		}

		private void _commaAfterOn() {
			_out();
			_reduceIndent();
			_newline();
			_afterOn = false;
			_afterByOrSetOrFromOrSelect = true;
		}

		private void _create() {
			_misc();

			_afterCreate = true;
		}

		private void _endCase() {
			_reduceIndent();
			_logical();
		}

		private void _endNewClause() {
			if (StringUtil.equals(_lcToken, "join")) {
				_afterJoin = true;
				_joinIndent = _indent;
			}

			if (_afterJoin && (_parens == 0) &&
				!StringUtil.equals(_lcToken, "join") &&
				!StringUtil.equals(_lcToken, "and") &&
				!StringUtil.equals(_lcToken, "or") && (_joinIndent != -1)) {

				_indent = _joinIndent;
				_joinIndent = -1;
				_afterJoin = false;
			}

			if (!_afterBeginBeforeEnd && !_afterUpdate) {
				_reduceIndent();

				if (_afterOn) {
					_reduceIndent();
					_afterOn = false;
				}

				_newline();
			}

			_out();

			if (!StringUtil.equals(_lcToken, "union") &&
				!StringUtil.equals(_lcToken, "intersect") && !_afterUpdate) {

				_indent++;
			}

			if (!_afterUpdate && !StringUtil.equals(_lcToken, "union")) {
				_newline();
			}
			else {
				_beginLine = false;
			}

			_afterBeginBeforeEnd = false;
			_afterByOrSetOrFromOrSelect =
				StringUtil.equals(_lcToken, "by") ||
				StringUtil.equals(_lcToken, "set") ||
				StringUtil.equals(_lcToken, "from");
		}

		private void _insertOrDelete() {
			if (_indent > 1) {
				_out();
			}
			else {
				_out();
				_indent++;
				_beginLine = false;

				if (StringUtil.equals(_lcToken, "insert")) {
					_afterInsert = true;
				}
			}
		}

		private void _logical() {
			_out();

			if (!_afterUpdate) {
				_newline();
				_beginLine = true;
			}
		}

		private void _misc() {
			_out();

			_beginLine = false;
		}

		private void _newline() {
			if (_lastTokenIsWhite && (_resultSB.index() > 0)) {
				_resultSB.setIndex(_resultSB.index() - 1);
			}

			String currentResult = _resultSB.toString();

			if (currentResult.endsWith("(") &&
				!StringUtil.equals(_lcToken, ")")) {

				_addOpenParensNewLine(true);
			}

			_resultSB.append("\n");

			for (int i = 0; i < _indent; i++) {
				_resultSB.append(_INDENT_STRING);
			}

			_lastTokenIsWhite = false;
			_beginLine = true;
		}

		private void _on() {
			if (!_afterIndex) {
				_indent++;
				_newline();
			}

			_afterOn = true;
			_out();
			_beginLine = false;
			_afterIndex = false;
		}

		private void _openParen() {
			if (_isFunctionName(_lastToken) || (_inFunction > 0)) {
				_inFunction++;
			}

			_beginLine = false;

			_openParensMessages.addLast(new ParensMessage(_indent));

			if (_inFunction > 0) {
				_out();

				if ((_afterCreate && (_inFunction == 1)) ||
					_nonfunctionNames.contains(_lastToken)) {

					_indent++;

					_newline();
				}
			}
			else {
				_out();

				if (!_afterByOrSetOrFromOrSelect) {
					_indent++;
					_newline();
					_beginLine = true;
				}
			}

			_parensSinceSelect++;
			_parens++;
		}

		private void _out() {
			_lastTokenIsWhite = false;

			String currentResult = _resultSB.toString();

			if (currentResult.endsWith("(") &&
				!StringUtil.equals(_lcToken, ")")) {

				_addOpenParensNewLine(false);
			}

			_resultSB.append(_token);
		}

		private void _reduceIndent() {
			if (_indent > 0) {
				_indent--;
			}
		}

		private void _select() {
			if (_afterByOrSetOrFromOrSelect) {
				_indent++;
			}

			if (_afterInsert || _afterByOrSetOrFromOrSelect) {
				_newline();
			}

			_afterUpdate = false;
			_out();
			_indent++;
			_newline();
			_parenCounts.addLast(_parensSinceSelect);
			_afterByOrFromOrSelects.addLast(_afterByOrSetOrFromOrSelect);
			_parensSinceSelect = 0;
			_afterByOrSetOrFromOrSelect = true;

			_afterOn = false;
		}

		private void _update() {
			_beginLine = false;
			_afterUpdate = true;

			_out();
		}

		private void _white() {
			if (!_beginLine) {
				_lastTokenIsWhite = true;

				_resultSB.append(" ");
			}
		}

		private boolean _afterBeginBeforeEnd;
		private boolean _afterBetween;
		private final LinkedList<Boolean> _afterByOrFromOrSelects =
			new LinkedList<>();
		private boolean _afterByOrSetOrFromOrSelect;
		private boolean _afterCreate;
		private boolean _afterExtract;
		private boolean _afterIndex;
		private boolean _afterInsert;
		private boolean _afterJoin;
		private boolean _afterOn;
		private boolean _afterUpdate;
		private boolean _beginLine = true;
		private int _indent;
		private int _inFunction;
		private int _joinIndent = -1;
		private String _lastToken;
		private boolean _lastTokenIsWhite;
		private String _lcToken;
		private final LinkedList<ParensMessage> _openParensMessages =
			new LinkedList<>();
		private final LinkedList<Integer> _parenCounts = new LinkedList<>();
		private int _parens;
		private int _parensSinceSelect;
		private final StringBundler _resultSB = new StringBundler();
		private String _token;
		private final StringTokenizer _tokens;

	}

	private static class ParensMessage {

		public ParensMessage(int indent) {
			_indent = indent;

			_newLine = false;
		}

		public int getIndent() {
			return _indent;
		}

		public boolean getNewLine() {
			return _newLine;
		}

		public void setIndent(int indent) {
			_indent = indent;
		}

		public void setNewLine(boolean newLine) {
			_newLine = newLine;
		}

		private int _indent;
		private boolean _newLine;

	}

}