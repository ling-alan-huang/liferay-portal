/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.util;

import java.text.MessageFormat;

import java.util.Objects;

/**
 * @author Alan Huang
 */
public class OptionalGradleDependency extends GradleDependency {

	public OptionalGradleDependency(
		String configuration, String group, String name, String version) {

		super(configuration, group, name, version);
	}

	public OptionalGradleDependency(
		String configuration, String group, String name, String version,
		int lineNumber, int lastLineNumber) {

		super(configuration, group, name, version, lineNumber, lastLineNumber);
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			getConfiguration(), getGroup(), getName(), "optional");
	}

	@Override
	public String toString() {
		if (getVersion() == null) {
			return MessageFormat.format(
				"{0} group: {1}, name: {2}, optional", getConfiguration(),
				getGroup(), getName());
		}

		return MessageFormat.format(
			"{0} group: {1}, name: {2}, optional, version: {3}",
			getConfiguration(), getGroup(), getName(), getVersion());
	}

}