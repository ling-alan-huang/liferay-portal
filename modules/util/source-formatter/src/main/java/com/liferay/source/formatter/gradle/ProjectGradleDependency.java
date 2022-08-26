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

import java.text.MessageFormat;

import java.util.Objects;

/**
 * @author Seiphon Wang
 */
public class ProjectGradleDependency extends GradleDependency {

	public ProjectGradleDependency(
		String configuration, String group, String name, String version,
		int lineNumber, int lastLineNumber, String path,
		String otherConfiguration, String methodName, String methodArgs) {

		super(configuration, group, name, version, lineNumber, lastLineNumber);

		_path = path;
		_otherConfiguration = otherConfiguration;
		_methodName = methodName;
		_methodArgs = methodArgs;
	}

	public ProjectGradleDependency(
		String configuration, String group, String name, String version,
		String path, String otherConfiguration, String methodName,
		String methodArgs) {

		super(configuration, group, name, version);

		_path = path;
		_otherConfiguration = otherConfiguration;
		_methodName = methodName;
		_methodArgs = methodArgs;
	}

	public String getMethodArgs() {
		return _methodArgs;
	}

	public String getMethodName() {
		return _methodName;
	}

	public String getOtherConfiguration() {
		return _otherConfiguration;
	}

	public String getPath() {
		return _path;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getConfiguration(), _path);
	}

	@Override
	public String toString() {
		if (_otherConfiguration == null) {
			StringBundler sb = new StringBundler(
				MessageFormat.format(
					"{0} project(\"{1}\")", getConfiguration(), _path));

			if (_methodName != null) {
				sb.append(".");
				sb.append(_methodName);
				sb.append("(\"");
				sb.append(_methodArgs);
				sb.append("\")");
			}

			return sb.toString();
		}

		StringBundler sb = new StringBundler(
			MessageFormat.format(
				"{0} project(path: \"{1}\", configuration: \"{2}\")",
				getConfiguration(), _path, _otherConfiguration));

		if (_methodName != null) {
			sb.append(".");
			sb.append(_methodName);
			sb.append("(\"");
			sb.append(_methodArgs);
			sb.append("\")");
		}

		return sb.toString();
	}

	private final String _methodArgs;
	private final String _methodName;
	private final String _otherConfiguration;
	private final String _path;

}