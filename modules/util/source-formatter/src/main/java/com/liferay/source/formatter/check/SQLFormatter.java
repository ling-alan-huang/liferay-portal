package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

public class SQLFormatter {
    private static final Set<String> _NON_FUNCTION_NAMES = SetUtil.fromArray("select", "from", "on", "set", "and", "or", "where", "having", "by", "all", "in");

    private static final String INDENT_STRING = "\t";

    public SQLFormatter() {
    }

    public String format(String source) {
        return (new FormatProcess(source)).perform();
    }

    private static class FormatProcess {

        public FormatProcess(String sql) {
            assert sql != null : "SQL to format should not be null";

            this.tokens = new StringTokenizer(sql, "()+*/-=<>'`\"[], \n\r\f\t", true);
        }

        public String perform() {

            while (this.tokens.hasMoreTokens()) {
                this.token = this.tokens.nextToken();
                this.lcToken = this.token.toLowerCase(Locale.ROOT);
                switch (this.lcToken) {
                    case "'":
                    case "\"":
                        String t;
                        do {
                            t = this.tokens.nextToken();
                            this.token = this.token + t;
                        } while (!this.lcToken.equals(t) && this.tokens.hasMoreTokens());

                        this.lcToken = this.token;
                        this.misc();
                        break;
                    case "[":
                        String tt;
                        do {
                            tt = this.tokens.nextToken();
                            this.token = this.token + tt;
                        } while (!"]".equals(tt) && this.tokens.hasMoreTokens());

                        this.lcToken = this.token;
                        this.misc();
                        break;
                    case ",":
                        if (this.afterByOrSetOrFromOrSelect && this.inFunction == 0) {
                            this.commaAfterByOrFromOrSelect();
                        } else {
                            if (this.afterOn && this.inFunction == 0) {
                                this.commaAfterOn();
                                break;
                            }

                            this.misc();

                            if (_afterCreate && inFunction == 1) {
                                _newline();
                            }
                        }
                        break;
                    case "(":
                        this.openParen();
                        break;
                    case ")":
                        this.closeParen();
                        break;
                    case "all":
                        _all();
                        break;
                    case "create":
                        _create();
                        break;
                    case "select":
                        this.select();
                        break;
                    case "update":
                        _update();
                        break;
                    case "insert":
                    case "delete":
                        this._insertOrDelete();
                        break;
                    case "on":
                        this.on();
                        break;
                    case "between":
                        this.afterBetween = true;
                        this.misc();
                        break;
                    case "trim":
                    case "extract":
                        this.afterExtract = true;
                        this.misc();
                        break;
                    case "left":
                    case "right":
                    case "full":
                    case "inner":
                    case "outer":
                    case "cross":
                    case "group":
                    case "order":
                        this.beginNewClause();
                        break;
                    case "from":
                        if (this.afterExtract) {
                            this.misc();
                            this.afterExtract = false;
                            break;
                        }
                    case "where":
                    case "set":
                    case "having":
                    case "by":
                    case "join":
                    case "union":
                    case "intersect":
                        this.endNewClause();
                        break;
                    case "case":
                        this.beginCase();
                        break;
                    case "end":
                        this.endCase();
                        break;
                    case "and":
                        if (this.afterBetween) {
                            this.misc();
                            this.afterBetween = false;
                            break;
                        }
                    case "or":
                    case "when":
                    case "else":
                        this.logical();
                        break;
                    case "index":
                        _afterIndex = true;
                        _afterCreate = false;
                        this.misc();
                        break;
                    default:
                        if (isWhitespace(this.token)) {
                            this.white();
                        } else {
                            this.misc();
                        }
                }

                if (!isWhitespace(this.token)) {
                    this.lastToken = this.lcToken;
                }
            }

            return this.result.toString();
        }

        private void _create() {
            this.misc();

            _afterCreate = true;
        }

        private void _all() {
            this.misc();

            _newline();
        }

        private void commaAfterOn() {
            this.out();
            _subIndent();
            this._newline();
            this.afterOn = false;
            this.afterByOrSetOrFromOrSelect = true;
        }

        private void commaAfterByOrFromOrSelect() {
            this.out();

            if (!_afterUpdate) {
                this._newline();
            }

        }

        private void logical() {
            this.out();

            if (!_afterUpdate) {
                this._newline();
                this.beginLine = true;
            }
        }

        private void endCase() {
            _subIndent();
            this.logical();
        }

        private void on() {

            if (!_afterIndex) {
                ++this.indent;
                this._newline();
            }

            this.afterOn = true;
            this.out();
            this.beginLine = false;
            _afterIndex = false;
        }

        private void beginCase() {
            this.out();
            this.beginLine = false;
            ++this.indent;
        }

        private void misc() {
            this.out();

            this.beginLine = false;

        }

        private void white() {
            if (!this.beginLine) {
                this.lastTokenIsWhite = true;

                if (result.toString().endsWith("(")) {
                    _afterOpenParens.addLast(false);
                }

                this.result.append(" ");
            }

        }

        private void _insertOrDelete() {
            if (this.indent > 1) {
                this.out();
            } else {
                this.out();
                ++this.indent;
                this.beginLine = false;

                if ("insert".equals(this.lcToken)) {
                    this.afterInsert = true;
                }
            }

        }

        private void _update() {
            beginLine = false;
            _afterUpdate = true;

            out();
        }

        private void select() {
            if (afterByOrSetOrFromOrSelect) {
                indent++;
            }

            if (afterInsert || afterByOrSetOrFromOrSelect) {
                _newline();
            }

            this.out();
            ++this.indent;
            this._newline();
            this.parenCounts.addLast(this.parensSinceSelect);
            this.afterByOrFromOrSelects.addLast(this.afterByOrSetOrFromOrSelect);
            this.parensSinceSelect = 0;
            this.afterByOrSetOrFromOrSelect = true;

            afterOn = false;
        }

        private void out() {
            lastTokenIsWhite = false;

            if (result.toString().endsWith("(") || result.toString().endsWith("( ")) {
                _afterOpenParens.addLast(false);
            }

            this.result.append(this.token);
        }

        private void endNewClause() {
            if (StringUtil.equals(lcToken, "join")) {
                _afterJoin = true;
                _joinIndent = indent;
            }

            if (_afterJoin && _parens == 0 && !StringUtil.equals(lcToken, "join") &&
                    !StringUtil.equals(lcToken, "and") && !StringUtil.equals(lcToken, "or") &&
                    _joinIndent != -1) {
                indent = _joinIndent;
                _joinIndent = -1;
                _afterJoin = false;
            }


            if (!this.afterBeginBeforeEnd && !_afterUpdate) {
                _subIndent();

                if (this.afterOn) {
                    _subIndent();
                    this.afterOn = false;
                }

                this._newline();
            }

            this.out();
            if (!"union".equals(this.lcToken) && !"intersect".equals(this.lcToken) && !_afterUpdate) {
                ++this.indent;
            }

            if (!_afterUpdate && !StringUtil.equals(this.lcToken, "union")) {
                this._newline();
            } else {
                beginLine = false;
            }

            this.afterBeginBeforeEnd = false;
            this.afterByOrSetOrFromOrSelect = "by".equals(this.lcToken) || "set".equals(this.lcToken) || "from".equals(this.lcToken);
        }

        private void beginNewClause() {
            if (!this.afterBeginBeforeEnd) {
                if (this.afterOn) {
                    _subIndent();
                    this.afterOn = false;
                }

                _subIndent();
                this._newline();
            }

            this.out();
            this.beginLine = false;
            this.afterBeginBeforeEnd = true;
        }

        private void closeParen() {
            --this.parensSinceSelect;
            _parens--;
            if (this.parensSinceSelect < 0) {
                _subIndent();
                this.parensSinceSelect = this.parenCounts.removeLast() - 1;
                this.afterByOrSetOrFromOrSelect = this.afterByOrFromOrSelects.removeLast();
            }

            boolean afterOpenParen = _afterOpenParens.removeLast();

            if (this.inFunction > 0) {

                if ((_afterCreate) && this.inFunction == 1) {
                    indent--;
                    this._newline();
                } else {
                    if (afterOpenParen) {
                        indent--;
                        this._newline();
                    }
                }

                --this.inFunction;

                this.out();
            } else {
                if (!this.afterByOrSetOrFromOrSelect) {
                    _subIndent();
                    this._newline();
                } else {
                    if (afterOpenParen) {
                        indent--;
                        this._newline();
                    }
                }

                this.out();
            }

            this.beginLine = false;
        }

        private void openParen() {
            if (isFunctionName(this.lastToken) || this.inFunction > 0) {
                ++this.inFunction;
            }

            this.beginLine = false;

            if (this.inFunction > 0) {
                this.out();

                if (((_afterCreate) && this.inFunction == 1) || _NON_FUNCTION_NAMES.contains(lastToken)) {
                    ++this.indent;

                    _newline();
                }
            } else {
                this.out();
                if (!this.afterByOrSetOrFromOrSelect) {
                    ++this.indent;
                    this._newline();
                    this.beginLine = true;
                }
            }

            this.parensSinceSelect++;
            _parens++;
        }

        private static boolean isFunctionName(String tok) {
            if (tok != null && tok.length() != 0) {
                char begin = tok.charAt(0);
                boolean isIdentifier = Character.isJavaIdentifierStart(begin) || '"' == begin;
                return isIdentifier && !SQLFormatter._NON_FUNCTION_NAMES.contains(tok);
            } else {
                return false;
            }
        }

        private static boolean isWhitespace(String token) {
            return " \n\r\f\t".contains(token);
        }

        private void _newline() {

            if (lastTokenIsWhite && this.result.index() > 0) {
                this.result.setIndex(result.index() - 1);
            }

            if (result.toString().endsWith("(")) {
                _afterOpenParens.addLast(true);
            }

            this.result.append("\n");

            for (int i = 0; i < this.indent; i++) {
                this.result.append(INDENT_STRING);
            }

            lastTokenIsWhite = false;
            this.beginLine = true;
        }

        private void _subIndent() {
            if (indent > 0) {
                indent--;
            }
        }

        private boolean beginLine = true;
        private boolean afterBeginBeforeEnd;
        private boolean afterByOrSetOrFromOrSelect;
        private boolean afterOn;
        private boolean afterBetween;
        private boolean afterExtract;
        private boolean afterInsert;
        private int inFunction;
        private int parensSinceSelect;
        private final LinkedList<Integer> parenCounts = new LinkedList<>();
        private final LinkedList<Boolean> afterByOrFromOrSelects = new LinkedList<>();
        int indent = 0;
        private final StringBundler result = new StringBundler();
        private final StringTokenizer tokens;
        private String lastToken;
        private String token;
        private String lcToken;
        private boolean lastTokenIsWhite = false;
        private boolean _afterIndex = false;
        private boolean _afterUpdate = false;
        private boolean _afterCreate = false;
        private boolean _afterJoin = false;

        private int _parens = 0;
        private int _joinIndent = -1;

        private final LinkedList<Boolean> _afterOpenParens = new LinkedList<>();

    }
}
