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

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Qi Zhang
 */
public class HTMLIndentationCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!fileName.endsWith(".html")) {
			return content;
		}

		List<HtmlTag> htmlTags = _getHtmlTags(fileName, content);

		if (htmlTags == null) {
			return content;
		}

		return _formatHTMLIndentation(htmlTags, content);
	}

	private boolean _afterTextIsNewLine(
		List<HtmlTag> htmlTags, int currentIndex) {

		if ((currentIndex + 2) >= htmlTags.size()) {
			return false;
		}

		HtmlTag htmlTag = htmlTags.get(currentIndex + 1);

		TagType tagType = htmlTag.getTagType();

		if (tagType.compareTo(TagType.OPEN) != 0) {
			return false;
		}

		int originLineNumber = htmlTag.getLineNumber();

		htmlTag = htmlTags.get(currentIndex + 2);

		tagType = htmlTag.getTagType();

		if ((tagType.compareTo(TagType.FULL) == 0) ||
			(tagType.compareTo(TagType.OPEN) == 0)) {

			htmlTag.setNewLine(true);

			return true;
		}
		else if ((tagType.compareTo(TagType.NO_LABEL_TAG) == 0) ||
				 (tagType.compareTo(TagType.TEXT_LINE) == 0)) {

			int nextTagOriLineNumber = htmlTag.getLineNumber();

			if (originLineNumber != nextTagOriLineNumber) {
				htmlTag.setNewLine(true);

				return true;
			}
		}

		return false;
	}

	private String _formatHTMLIndentation(
		List<HtmlTag> htmlTags, String content) {

		int leadTabCount = 0;

		Stack<HtmlTag> htmlTagStack = new Stack<>();

		StringBundler sb = new StringBundler();

		boolean endWithNewLine = false;
		boolean inStyleOrScript = false;
		boolean newLineBeforeCloseTag = false;

		for (int i = 0; i < htmlTags.size(); i++) {
			HtmlTag htmlTag = htmlTags.get(i);

			String tagName = htmlTag.getTagName();
			String tagContent = htmlTag.getTagContent();
			TagType tagType = htmlTag.getTagType();
			int originLineNumber = htmlTag.getLineNumber();

			if (tagType.compareTo(TagType.OPEN) == 0) {
				if (endWithNewLine) {
					if (StringUtil.equals(tagName, "else") ||
						StringUtil.equals(tagName, "elseif")) {

						leadTabCount--;
					}

					sb.append(_makeLeadTab(leadTabCount));
				}

				sb.append(tagContent);

				htmlTag.setLeadTabCount(leadTabCount);

				if (StringUtil.equals(tagName, "style") ||
					StringUtil.equals(tagName, "script") ||
					StringUtil.equals(tagName, "pre")) {

					inStyleOrScript = true;
				}

				htmlTag.setNewLine(false);

				if (!StringUtil.equals(tagName, "else") &&
					!StringUtil.equals(tagName, "elseif")) {

					htmlTagStack.push(htmlTag);
				}
			}
			else if (tagType.compareTo(TagType.CLOSE) == 0) {
				HtmlTag curHtmlTag = htmlTagStack.pop();

				String curTagName = curHtmlTag.getTagName();

				if (!StringUtil.equals(curTagName, tagName)) {
					return content;
				}

				leadTabCount--;

				if (StringUtil.equals(tagName, "if") ||
					StringUtil.equals(tagName, "style") ||
					StringUtil.equals(tagName, "script") ||
					StringUtil.equals(tagName, "pre")) {

					inStyleOrScript = false;
					leadTabCount = curHtmlTag.getLeadTabCount();
				}

				newLineBeforeCloseTag = curHtmlTag.getNewLine();

				if (newLineBeforeCloseTag) {
					String tmpContent = sb.toString();

					if (!tmpContent.endsWith("\n")) {
						sb.append(StringPool.NEW_LINE);
					}

					sb.append(_makeLeadTab(leadTabCount));
				}

				sb.append(tagContent);
			}
			else if (tagType.compareTo(TagType.SPACE) == 0) {
				sb.append(StringPool.NEW_LINE);
			}
			else if (tagType.compareTo(TagType.NO_LABEL_TAG) == 0) {
				if (endWithNewLine) {
					sb.append(_makeLeadTab(leadTabCount));
				}

				if (tagContent.contains("\n")) {
					String[] lines = StringUtil.splitLines(tagContent);

					int difference =
						leadTabCount - htmlTag.getOriginalTabCount();

					for (int j = 0; j < lines.length; j++) {
						String line = lines[j];

						if (j == 0) {
							sb.append(_makeLeadTab(leadTabCount));
						}
						else if (j == (lines.length - 1)) {
							sb.append("\n");
							sb.append(_makeLeadTab(leadTabCount));
						}
						else {
							sb.append("\n");
							sb.append(_makeLeadTab(difference));
						}

						sb.append(line);
					}
				}
				else {
					sb.append(tagContent);
				}
			}
			else {
				if (endWithNewLine && !inStyleOrScript) {
					sb.append(_makeLeadTab(leadTabCount));
				}

				sb.append(tagContent);
			}

			if (i >= (htmlTags.size() - 1)) {
				continue;
			}

			endWithNewLine = false;

			if (tagType.compareTo(TagType.OPEN) == 0) {
				leadTabCount++;
			}
			else if (tagType.compareTo(TagType.SPACE) == 0) {
				endWithNewLine = true;

				continue;
			}

			HtmlTag nextHtmlTag = htmlTags.get(i + 1);

			TagType nextTagType = nextHtmlTag.getTagType();

			if (nextTagType.compareTo(TagType.CLOSE) == 0) {
				continue;
			}

			if (nextTagType.compareTo(TagType.SPACE) == 0) {
				htmlTag.setNewLine(true);
				sb.append(StringPool.NEW_LINE);

				endWithNewLine = true;
			}
			else if ((((tagType.compareTo(TagType.OPEN) == 0) ||
					   (tagType.compareTo(TagType.FULL) == 0)) &&
					  ((nextTagType.compareTo(TagType.FULL) == 0) ||
					   (nextTagType.compareTo(TagType.OPEN) == 0))) ||
					 ((tagType.compareTo(TagType.CLOSE) == 0) &&
					  newLineBeforeCloseTag)) {

				String nextTagName = nextHtmlTag.getTagName();

				if (StringUtil.equals(nextTagName, "br") &&
					(tagType.compareTo(TagType.FULL) == 0)) {

					continue;
				}

				htmlTag.setNewLine(true);
				sb.append(StringPool.NEW_LINE);

				endWithNewLine = true;
			}
			else {
				int nextTagOriLineNumber = nextHtmlTag.getLineNumber();

				if ((originLineNumber != nextTagOriLineNumber) ||
					_afterTextIsNewLine(htmlTags, i)) {

					htmlTag.setNewLine(true);
					sb.append(StringPool.NEW_LINE);

					endWithNewLine = true;
				}
			}
		}

		content = sb.toString();

		if (content.endsWith("\n")) {
			content = content.substring(0, content.length() - 1);
		}

		return content;
	}

	private List<HtmlTag> _getHtmlTags(String fileName, String content)
		throws Exception {

		List<HtmlTag> htmlTags = new ArrayList<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = null;

			int lineNumber = 0;
			int tagLevel = 0;
			int multiTagStartLineNumber = 0;

			boolean inTag = false;
			boolean inStyleOrScript = false;
			boolean inNoLabelTag = false;

			Stack<HtmlTag> htmlTagStack = new Stack<>();
			String targetChar = null;
			StringBundler multiLineTagSB = null;

			HtmlTag htmlTag = null;

			outLoop:
			while ((line = unsyncBufferedReader.readLine()) != null) {
				lineNumber++;

				if (Validator.isNull(line) && !inNoLabelTag) {
					htmlTag = new HtmlTag();

					htmlTag.setTagType(TagType.SPACE);
					htmlTag.setLineNumber(lineNumber);

					htmlTags.add(htmlTag);

					continue;
				}

				int leadTabCount = getLeadingTabCount(line);

				String trimmedLine = StringUtil.trimLeading(line);

				int x = -1;

				while (true) {
					if (inStyleOrScript &&
						!(trimmedLine.contains("</style>") ||
						  trimmedLine.contains("</script>") ||
						  trimmedLine.contains("</pre>"))) {

						htmlTag = new HtmlTag();

						htmlTag.setTagContent(line);
						htmlTag.setTagType(TagType.TEXT_LINE);
						htmlTag.setOriginalTabCount(leadTabCount);
						htmlTag.setLineNumber(lineNumber);

						htmlTags.add(htmlTag);

						continue outLoop;
					}

					if (inNoLabelTag) {
						while (true) {
							x = trimmedLine.indexOf(">", x + 1);

							if (x == -1) {
								multiLineTagSB.append("\n");
								multiLineTagSB.append(line);

								continue outLoop;
							}

							multiLineTagSB.append("\n");
							multiLineTagSB.append(
								trimmedLine.substring(0, x + 1));

							int level = getLevel(
								multiLineTagSB.toString(), "<", ">");

							if (level != 0) {
								continue;
							}

							htmlTag.setTagContent(multiLineTagSB.toString());
							multiLineTagSB = new StringBundler();

							htmlTags.add(htmlTag);

							multiTagStartLineNumber = 0;

							line = trimmedLine.substring(x + 1);

							trimmedLine = StringUtil.trimLeading(line);

							leadTabCount = 0;
							inNoLabelTag = false;

							break;
						}

						continue;
					}

					if (inTag) {
						multiLineTagSB.append(" ");

						while (true) {
							x = trimmedLine.indexOf(targetChar, x + 1);

							if (x == -1) {
								multiLineTagSB.append(trimmedLine);

								continue outLoop;
							}

							multiLineTagSB.append(
								trimmedLine.substring(0, x + 1));

							int level = getLevel(
								multiLineTagSB.toString(),
								new String[] {"<", "["},
								new String[] {">", "]"});

							if (level != 0) {
								continue;
							}

							trimmedLine = trimmedLine.substring(x + 1);
							leadTabCount = 0;

							trimmedLine = StringUtil.insert(
								trimmedLine, multiLineTagSB.toString(), 0);
							multiLineTagSB = new StringBundler();

							inTag = false;

							break;
						}

						continue;
					}

					if (trimmedLine.startsWith("<") ||
						trimmedLine.startsWith("[")) {

						if (trimmedLine.startsWith("<")) {
							targetChar = ">";
						}
						else {
							targetChar = "]";
						}

						x = trimmedLine.indexOf(targetChar, x + 1);

						boolean noLabelTag = false;

						if (trimmedLine.startsWith("<!") ||
							trimmedLine.startsWith("<%") ||
							trimmedLine.startsWith("<?")) {

							noLabelTag = true;
						}

						htmlTag = new HtmlTag();

						htmlTag.setOriginalTabCount(leadTabCount);
						htmlTag.setLineNumber(lineNumber);

						if (x == -1) {
							multiTagStartLineNumber = lineNumber;

							multiLineTagSB = new StringBundler();

							multiLineTagSB.append(trimmedLine);

							if (noLabelTag) {
								htmlTag.setTagType(TagType.NO_LABEL_TAG);
								inNoLabelTag = true;
							}
							else {
								inTag = true;
							}

							break;
						}

						String tag = trimmedLine.substring(0, x + 1);

						int level = getLevel(
							tag, new String[] {"<", "["},
							new String[] {">", "]"});

						if (level != 0) {
							continue;
						}

						if (multiTagStartLineNumber != 0) {
							htmlTag.setLineNumber(multiTagStartLineNumber);

							multiTagStartLineNumber = 0;
						}

						htmlTag.setTagContent(tag);
						htmlTag.setTagType(TagType.TEXT_LINE);

						if (tag.startsWith("[#recover")) {
							htmlTag.setTagType(TagType.FULL);
						}

						String tagName = _getTagName(tag);

						if (noLabelTag) {
							htmlTag.setTagType(TagType.NO_LABEL_TAG);
						}
						else if (tag.equals("</" + tagName + ">") ||
								 tag.equals("[/#" + tagName + "]")) {

							if (StringUtil.equals(tagName, "style") ||
								StringUtil.equals(tagName, "script") ||
								StringUtil.equals(tagName, "pre")) {

								inStyleOrScript = false;
							}

							if (htmlTagStack.isEmpty()) {
								addMessage(
									fileName,
									StringBundler.concat(
										"Extra close tag '", tagName,
										"' in no.", lineNumber),
									lineNumber);

								return null;
							}

							HtmlTag curHtmlTag = htmlTagStack.pop();

							String curTagName = curHtmlTag.getTagName();

							if (!StringUtil.equals(curTagName, tagName)) {
								addMessage(
									fileName,
									StringBundler.concat(
										"Error closing '", curTagName,
										"' tag matches before line No.",
										curHtmlTag.getLineNumber()),
									lineNumber);

								return null;
							}

							htmlTag.setLevel(tagLevel--);
							htmlTag.setTagName(tagName);
							htmlTag.setTagType(TagType.CLOSE);
						}
						else if ((tag.startsWith("<" + tagName) &&
								  !tag.endsWith("/>")) ||
								 (tag.startsWith("[#" + tagName) &&
								  !tag.endsWith("/]") &&
								  ArrayUtil.contains(
									  _NEED_NEW_LINE_TAGS, tagName))) {

							if (StringUtil.equals(tagName, "style") ||
								StringUtil.equals(tagName, "script") ||
								StringUtil.equals(tagName, "pre")) {

								inStyleOrScript = true;
							}

							htmlTag.setTagName(tagName);
							htmlTag.setTagType(TagType.OPEN);
							htmlTag.setLineNumber(lineNumber);

							if (!StringUtil.equals(tagName, "else") &&
								!StringUtil.equals(tagName, "elseif")) {

								htmlTagStack.push(htmlTag);

								htmlTag.setLevel(++tagLevel);
							}
							else {
								if (htmlTagStack.isEmpty()) {
									addMessage(
										fileName,
										StringBundler.concat(
											"Extra close tag '", tagName,
											"' in No.", lineNumber),
										lineNumber);

									return null;
								}

								HtmlTag ifHtmlTag = htmlTagStack.peek();

								String ifHtmlTagName = ifHtmlTag.getTagName();

								if (StringUtil.equals(ifHtmlTagName, "if")) {
									htmlTag.setLevel(ifHtmlTag.getLevel());
								}
								else {
									return null;
								}
							}
						}
						else if ((tag.startsWith("<" + tagName) &&
								  tag.endsWith("/>")) ||
								 (tag.startsWith("[#" + tagName) &&
								  tag.endsWith("/]") &&
								  ArrayUtil.contains(
									  _NEED_NEW_LINE_TAGS, tagName))) {

							htmlTag.setTagName(tagName);
							htmlTag.setTagType(TagType.FULL);
						}

						htmlTags.add(htmlTag);

						if ((x + 1) == trimmedLine.length()) {
							break;
						}

						line = trimmedLine.substring(x + 1);

						trimmedLine = StringUtil.trimLeading(line);

						leadTabCount = 0;

						x = -1;

						continue;
					}

					x = 0;

					for (char e : line.toCharArray()) {
						if ((e == '<') || (e == '[')) {
							break;
						}

						x++;
					}

					if ((x == 0) || Validator.isNull(trimmedLine)) {
						break;
					}

					htmlTag = new HtmlTag();

					htmlTag.setOriginalTabCount(leadTabCount);
					htmlTag.setTagType(TagType.TEXT_LINE);
					htmlTag.setTagContent(line.substring(leadTabCount, x));
					htmlTag.setLineNumber(lineNumber);

					htmlTags.add(htmlTag);

					line = line.substring(x);

					trimmedLine = StringUtil.trim(line);

					leadTabCount = 0;

					x = -1;

					if (Validator.isNull(trimmedLine)) {
						break;
					}
				}
			}

			if (!htmlTagStack.isEmpty()) {
				HtmlTag topHtmlTag = htmlTagStack.pop();

				addMessage(
					fileName,
					StringBundler.concat(
						"Not close tag '", topHtmlTag.getTagName(), "' in No.",
						topHtmlTag.getLineNumber()),
					lineNumber);
			}

			if ((tagLevel != 0) || inTag || inNoLabelTag) {
				return null;
			}
		}

		return htmlTags;
	}

	private String _getTagName(String line) {
		char[] charArray = line.toCharArray();

		int startIndex = -1;

		for (int i = 0; i < charArray.length; i++) {
			char e = charArray[i];

			String tmp = String.valueOf(e);

			if (tmp.matches("\\$|\\w") && (startIndex == -1)) {
				startIndex = i;
			}

			if ((e == CharPool.SPACE) ||
				(line.startsWith("<") && (e == CharPool.GREATER_THAN)) ||
				(line.startsWith("[") && (e == CharPool.CLOSE_BRACKET))) {

				if (startIndex == -1) {
					return null;
				}

				return line.substring(startIndex, i);
			}
		}

		return null;
	}

	private String _makeLeadTab(int leadTabCount) {
		if (leadTabCount == 0) {
			return "";
		}

		StringBundler sb = new StringBundler(leadTabCount);

		for (int i = 0; i < leadTabCount; i++) {
			sb.append(StringPool.TAB);
		}

		return sb.toString();
	}

	private static final String[] _NEED_NEW_LINE_TAGS = {
		"assign", "attempt", "list", "if", "else", "elseif"
	};

	private static class HtmlTag {

		public int getLeadTabCount() {
			return _leadTabCount;
		}

		public int getLevel() {
			return _level;
		}

		public int getLineNumber() {
			return _lineNumber;
		}

		public boolean getNewLine() {
			return _newLine;
		}

		public int getOriginalTabCount() {
			return _originalTabCount;
		}

		public String getTagContent() {
			return _tagContent;
		}

		public String getTagName() {
			return _tagName;
		}

		public TagType getTagType() {
			return _tagType;
		}

		public void setLeadTabCount(int leadTabCount) {
			_leadTabCount = leadTabCount;
		}

		public void setLevel(int level) {
			_level = level;
		}

		public void setLineNumber(int lineNumber) {
			_lineNumber = lineNumber;
		}

		public void setNewLine(boolean newLine) {
			_newLine = newLine;
		}

		public void setOriginalTabCount(int originalTabCount) {
			_originalTabCount = originalTabCount;
		}

		public void setTagContent(String tagContent) {
			_tagContent = tagContent;
		}

		public void setTagName(String tagName) {
			_tagName = tagName;
		}

		public void setTagType(TagType tagType) {
			_tagType = tagType;
		}

		private int _leadTabCount;
		private int _level;
		private int _lineNumber;
		private boolean _newLine;
		private int _originalTabCount;
		private String _tagContent;
		private String _tagName;
		private TagType _tagType;

	}

	private enum TagType {

		CLOSE, FULL, NO_LABEL_TAG, OPEN, SPACE, TEXT_LINE

	}

}