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
import com.liferay.portal.tools.ToolsUtil;

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

		List<HtmlTag> htmlTags = _getHtmlTags(fileName, content);

		if (htmlTags == null) {
			return content;
		}

		return _formatHTMLIndentation(htmlTags, content);
	}

	private String _formatHTMLIndentation(
		List<HtmlTag> htmlTags, String content) {

		int leadTabCount = 0;

		Stack<HtmlTag> htmlTagStack = new Stack<>();

		StringBundler sb = new StringBundler();

		for (int i = 0; i < htmlTags.size(); i++) {
			HtmlTag htmlTag = htmlTags.get(i);

			String tagName = htmlTag.getTagName();
			String tagContent = htmlTag.getTagContent();

			TagType tagType = htmlTag.getTagType();

			if (tagType.compareTo(TagType.OPEN) == 0) {
				if (StringUtil.equals(tagName, "else") ||
					StringUtil.equals(tagName, "elseif")) {

					leadTabCount--;
				}

				sb.append(_makeLeadTab(leadTabCount));
				sb.append(tagContent);

				htmlTag.setLeadTabCount(leadTabCount);

				int childTagCount = _getChildTagCount(htmlTags, i);

				if (childTagCount == -1) {
					return content;
				}

				htmlTag.setNewLine(false);

				if (childTagCount > 1) {
					htmlTag.setNewLine(true);
					sb.append(StringPool.NEW_LINE);
					leadTabCount++;
				}
				else if (childTagCount == 1) {
					HtmlTag nextHtmlTag = htmlTags.get(i + 1);

					TagType nextTagType = nextHtmlTag.getTagType();

					if (nextTagType.compareTo(TagType.TEXT_LINE) == 0) {
						String nextTagContent = nextHtmlTag.getTagContent();

						if ((tagContent.length() + nextTagContent.length()) >
								80) {

							htmlTag.setNewLine(true);
							sb.append(StringPool.NEW_LINE);
							leadTabCount++;
						}
						else {
							sb.append(nextHtmlTag.getTagContent());
							i++;
						}
					}
					else if (nextTagType.compareTo(TagType.FULL) == 0) {
						htmlTag.setNewLine(true);
						sb.append(StringPool.NEW_LINE);
						leadTabCount++;
					}
				}

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

				if (curHtmlTag.getNewLine()) {
					String tmpContent = sb.toString();

					if (!tmpContent.endsWith("\n")) {
						sb.append(StringPool.NEW_LINE);
					}

					if (StringUtil.equals(tagName, "if")) {
						leadTabCount = curHtmlTag.getLeadTabCount();
					}
					else {
						leadTabCount--;
					}

					sb.append(_makeLeadTab(leadTabCount));
				}

				sb.append(tagContent);
				sb.append(StringPool.NEW_LINE);
			}
			else if (tagType.compareTo(TagType.SPACE) == 0) {
				sb.append(StringPool.NEW_LINE);
			}
			else {
				sb.append(_makeLeadTab(leadTabCount));
				sb.append(tagContent);
				sb.append(StringPool.NEW_LINE);
			}
		}

		content = sb.toString();

		if (content.endsWith("\n")) {
			content = content.substring(0, content.length() - 1);
		}

		return content;
	}

	private int _getChildTagCount(List<HtmlTag> htmlTags, int index) {
		HtmlTag openHtmlTag = htmlTags.get(index);

		String tagName = openHtmlTag.getTagName();
		int level = openHtmlTag.getLevel();

		for (int i = index + 1; i < htmlTags.size(); i++) {
			HtmlTag htmlTag = htmlTags.get(i);

			TagType curTagType = htmlTag.getTagType();
			int curTagLevel = htmlTag.getLevel();
			String curTagName = htmlTag.getTagName();

			if (StringUtil.equals(tagName, "else") ||
				StringUtil.equals(tagName, "elseif")) {

				if (((StringUtil.equals(curTagName, "else") ||
					  StringUtil.equals(curTagName, "elseif")) &&
					 (curTagType.compareTo(TagType.OPEN) == 0)) ||
					(StringUtil.equals(curTagName, "if") &&
					 (curTagType.compareTo(TagType.CLOSE) == 0) &&
					 (curTagLevel == level))) {

					return i - index - 1;
				}
			}
			else {
				if (StringUtil.equals(curTagName, tagName) &&
					(curTagLevel == level) &&
					(curTagType.compareTo(TagType.CLOSE) == 0)) {

					return i - index - 1;
				}
			}
		}

		return -1;
	}

	private int _getCurPos(
		String content, String line, int lineNumber, int startPos,
		int difference) {

		return getLineStartPos(content, lineNumber) + getLeadingTabCount(line) +
			startPos + difference;
	}

	private List<HtmlTag> _getHtmlTags(String fileName, String content)
		throws Exception {

		List<HtmlTag> htmlTags = new ArrayList<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = null;

			int difference = 0;
			int lineNumber = 0;
			int tagLevel = 0;

			boolean inTag = false;
			Stack<HtmlTag> htmlTagStack = new Stack<>();
			String targetChar = null;
			StringBundler multiLineTagSB = null;

			HtmlTag htmlTag = null;

			outLoop:
			while ((line = unsyncBufferedReader.readLine()) != null) {
				lineNumber++;

				if (Validator.isNull(line)) {
					htmlTag = new HtmlTag();

					htmlTag.setTagType(TagType.SPACE);

					htmlTags.add(htmlTag);

					continue;
				}

				String trimmedLine = StringUtil.trimLeading(line);

				int x = -1;

				while (true) {
					if (inTag) {
						multiLineTagSB.append(" ");

						String multiLineTag = multiLineTagSB.toString();

						difference = -multiLineTag.length();

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

							if (ToolsUtil.isInsideQuotes(
									content,
									_getCurPos(
										content, line, lineNumber, x, 0)) ||
								(level != 0)) {

								continue;
							}

							trimmedLine = trimmedLine.substring(x + 1);

							trimmedLine = StringUtil.insert(
								trimmedLine, multiLineTagSB.toString(), 0);

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

						if (x == -1) {
							multiLineTagSB = new StringBundler();

							multiLineTagSB.append(trimmedLine);

							inTag = true;

							break;
						}

						String tag = trimmedLine.substring(0, x + 1);

						String tagName = _getTagName(tag);

						int level = getLevel(
							tag, new String[] {"<", "["},
							new String[] {">", "]"});

						if (ToolsUtil.isInsideQuotes(
								content,
								_getCurPos(
									content, line, lineNumber, x,
									difference)) ||
							(level != 0)) {

							continue;
						}

						htmlTag = new HtmlTag();

						htmlTag.setTagContent(tag);

						if (tag.equals("</" + tagName + ">") ||
							tag.equals("[/#" + tagName + "]")) {

							HtmlTag curHtmlTag = htmlTagStack.pop();

							String curTagName = curHtmlTag.getTagName();

							if (!StringUtil.equals(curTagName, tagName)) {
								addMessage(
									fileName,
									StringBundler.concat(
										"Error closing '",
										curHtmlTag.getTagName(),
										"' tag matches before line no.",
										lineNumber),
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

							htmlTag.setTagName(tagName);
							htmlTag.setTagType(TagType.OPEN);

							if (!StringUtil.equals(tagName, "else") &&
								!StringUtil.equals(tagName, "elseif")) {

								htmlTagStack.push(htmlTag);

								htmlTag.setLevel(++tagLevel);
							}
							else {
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
						else {
							htmlTag.setTagType(TagType.FULL);
						}

						htmlTags.add(htmlTag);

						if ((x + 1) == trimmedLine.length()) {
							break;
						}

						trimmedLine = StringUtil.trim(
							trimmedLine.substring(x + 1));
						difference = difference + x + 1;

						x = -1;

						continue;
					}

					x = 0;

					for (char e : trimmedLine.toCharArray()) {
						if ((e == '<') || (e == '[')) {
							break;
						}

						x++;
					}

					htmlTag = new HtmlTag();

					htmlTag.setTagType(TagType.TEXT_LINE);
					htmlTag.setTagContent(trimmedLine.substring(0, x));

					htmlTags.add(htmlTag);

					trimmedLine = StringUtil.trim(trimmedLine.substring(x));
					difference = difference + x;

					x = -1;

					if (Validator.isNull(trimmedLine)) {
						break;
					}
				}

				difference = 0;
			}

			if ((tagLevel != 0) || inTag) {
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

				return line.substring(startIndex, i);
			}
		}

		return null;
	}

	private String _makeLeadTab(int leadTabCount) {
		StringBundler sb = new StringBundler(leadTabCount);

		for (int i = 0; i < leadTabCount; i++) {
			sb.append(StringPool.TAB);
		}

		return sb.toString();
	}

	private static final String[] _NEED_NEW_LINE_TAGS = {
		"attempt", "list", "if", "else", "elseif"
	};

	private static class HtmlTag {

		public int getLeadTabCount() {
			return _leadTabCount;
		}

		public int getLevel() {
			return _level;
		}

		public boolean getNewLine() {
			return _newLine;
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

		public void setNewLine(boolean newLine) {
			_newLine = newLine;
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
		private boolean _newLine;
		private String _tagContent;
		private String _tagName;
		private TagType _tagType;

	}

	private enum TagType {

		CLOSE, FULL, OPEN, SPACE, TEXT_LINE

	}

}