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

import com.liferay.source.formatter.upgrade.GradleDependency;

import java.text.MessageFormat;

import java.util.Objects;

/**
 * @author Seiphon Wang
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