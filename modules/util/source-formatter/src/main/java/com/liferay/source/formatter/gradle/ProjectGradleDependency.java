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

import java.text.MessageFormat;

import com.liferay.source.formatter.upgrade.GradleDependency;

/**
 * @author Seiphon Wang
 */
public class ProjectGradleDependency extends GradleDependency {

	public ProjectGradleDependency(
		String configuration, String group, String name, String version,
		int lineNumber,int lastLineNumber, String path,
		String otherConfiguration) {

		super(configuration, group, name, version, lineNumber, lastLineNumber);

		_path = path;
		_otherConfiguration = otherConfiguration;
	}

	public ProjectGradleDependency(
		String configuration, String group, String name, String version,
		String path, String otherConfiguration) {

		super(configuration, group, name, version);

		_path = path;
		_otherConfiguration = otherConfiguration;
	}

	public String getPath() {
		return _path;
	}

	public String getOtherConfiguration () {
		return _otherConfiguration;
	}

	@Override
	public String toString() {
		if (_otherConfiguration != null) {
			return MessageFormat.format(
				"{0} project(\"{1}\")", getConfiguration(), _path);
		}

		return MessageFormat.format(
			"{0} project(path: \'{1}\', configuration: \'{2}\')",
			getConfiguration(), _path, _otherConfiguration);
	}

	private String _path;
	private String _otherConfiguration;
}
