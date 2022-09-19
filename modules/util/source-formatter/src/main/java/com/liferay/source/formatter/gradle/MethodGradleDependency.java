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

package com.liferay.source.formatter.gradle;

import com.liferay.petra.string.StringBundler;
import com.liferay.source.formatter.upgrade.GradleDependency;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Seiphon Wang
 */
public class MethodGradleDependency extends GradleDependency {

	public MethodGradleDependency(
		String configuration, String group, String name, String version,
		int lineNumber, int lastLineNumber, String methodName, String variable,
		List<String> argumentList, Map<String, String> argumentMap) {

		super(configuration, group, name, version, lineNumber, lastLineNumber);

		_methodName = methodName;
		_variable = variable;
		_argumentList = argumentList;
		_argumentMap = argumentMap;
	}

	public MethodGradleDependency(
		String configuration, String group, String name, String version,
		String methodName, String variable, List<String> argumentList,
		Map<String, String> argumentMap) {

		super(configuration, group, name, version);

		_methodName = methodName;
		_variable = variable;
		_argumentList = argumentList;
		_argumentMap = argumentMap;
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

		if (_variable != null) {
			sb.append(_variable);
			sb.append(".");
		}

		sb.append(_methodName);
		sb.append("(");

		if (_argumentList != null) {
			for (int i = 0; i < _argumentList.size(); i++) {
				sb.append(_argumentList.get(i));

				if (i != (_argumentList.size() - 1)) {
					sb.append(", ");
				}
			}
		}

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
	private final Map<String, String> _argumentMap;
	private final String _methodName;
	private final String _variable;

}