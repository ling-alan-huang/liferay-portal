/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Alan Huang
 */
public class JavaMockitoChainedCallsOrderCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		int x = -1;

		while (true) {
			x = content.indexOf("Mockito.when(", x + 1);

			if (x == -1) {
				return content;
			}

			int y = _getClosingParenthesisPosition(
				content, content.indexOf("(", x));

			if (y == -1) {
				return content;
			}

			List<String> chainedMethodCalls = new ArrayList<>();

			int z = y + 1;

			while (content.startsWith(".then", z)) {
				int closingParenthesisPosition = _getClosingParenthesisPosition(
					content, content.indexOf("(", z));

				if (closingParenthesisPosition == -1) {
					return content;
				}

				chainedMethodCalls.add(
					content.substring(z, closingParenthesisPosition + 1));

				z = closingParenthesisPosition + 1;
			}

			if ((chainedMethodCalls.size() < 2) ||
				(content.charAt(z) != CharPool.SEMICOLON)) {

				continue;
			}

			List<String> sortedChainedMethodCalls = new ArrayList<>(
				chainedMethodCalls);

			sortedChainedMethodCalls.sort(
				Comparator.comparing(
					chainedMethodCall -> chainedMethodCall.substring(
						1, chainedMethodCall.indexOf("("))));

			if (chainedMethodCalls.equals(sortedChainedMethodCalls)) {
				continue;
			}

			content = StringBundler.concat(
				content.substring(0, y + 1),
				StringUtil.merge(sortedChainedMethodCalls, ""),
				content.substring(z));
		}
	}

	private int _getClosingParenthesisPosition(String content, int start) {
		int x = start;

		while (true) {
			x = content.indexOf(")", x + 1);

			if ((x == -1) || (getLevel(content.substring(start, x + 1)) == 0)) {
				return x;
			}
		}
	}

}