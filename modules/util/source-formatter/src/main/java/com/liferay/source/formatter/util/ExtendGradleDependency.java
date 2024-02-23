/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.util;

import com.liferay.petra.string.StringBundler;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Alan Huang
 */
public class ExtendGradleDependency extends GradleDependency {

	public ExtendGradleDependency(
		String configuration, String group, String name, String version,
		int lineNumber, int lastLineNumber, Map<String, String> keyValues) {

		super(configuration, group, name, version, lineNumber, lastLineNumber);

		_keyValues = keyValues;
	}

	public ExtendGradleDependency(
		String configuration, String group, String name, String version,
		Map<String, String> keyValues) {

		super(configuration, group, name, version);

		_keyValues = keyValues;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append(getConfiguration());
		sb.append(" ");

		Set<String> keySet = new TreeSet<>(_keyValues.keySet());

		Object[] keys = keySet.toArray();

		for (int i = 0; i < keys.length; i++) {
			sb.append(keys[i]);
			sb.append(": ");
			sb.append(_keyValues.get(keys[i]));

			if (i != (keys.length - 1)) {
				sb.append(", ");
			}
		}

		return sb.toString();
	}

	private final Map<String, String> _keyValues;

}