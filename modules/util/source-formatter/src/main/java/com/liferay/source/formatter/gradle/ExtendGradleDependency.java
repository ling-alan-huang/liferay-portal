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

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Seiphon Wang
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