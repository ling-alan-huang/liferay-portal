/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Alan Huang
 */
public class MethodGradleDependency extends GradleDependency {

	public MethodGradleDependency(
		String configuration, String variableName, String methodName,
		List<String> argumentList, Map<String, String> argumentMap,
		int lineNumber, int lastLineNumber) {

		super(configuration, argumentMap, false, lineNumber, lastLineNumber);

		_variableName = variableName;
		_methodName = methodName;
		_argumentList = argumentList;
	}

	public String getMethodName() {
		return _methodName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getConfiguration(), _methodName);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append(getConfiguration());
		sb.append(" ");

		if (_variableName != null) {
			sb.append(_variableName);
			sb.append(".");
		}

		sb.append(_methodName);
		sb.append("(");

		//		if (_argumentList != null) {
		//			for (int i = 0; i < _argumentList.size(); i++) {
		//				sb.append(_argumentList.get(i));
		//
		//				if (i != (_argumentList.size() - 1)) {
		//					sb.append(", ");
		//				}
		//			}
		//		}

		sb.append(StringUtil.merge(_argumentList, StringPool.COMMA_AND_SPACE));

		if (_argumentMap != null) {
			Set<String> keySet = new TreeSet<>(_argumentMap.keySet());

			Object[] keys = keySet.toArray();

			for (int i = 0; i < keys.length; i++) {
				sb.append(keys[i]);
				sb.append(": ");
				sb.append(_argumentMap.get(keys[i]));

				if (i != (keys.length - 1)) {
					sb.append(", ");
				}
			}
		}

		sb.append(")");

		return sb.toString();
	}

	private final List<String> _argumentList;
	private Map<String, String> _argumentMap;
	private final String _methodName;
	private final String _variableName;

}