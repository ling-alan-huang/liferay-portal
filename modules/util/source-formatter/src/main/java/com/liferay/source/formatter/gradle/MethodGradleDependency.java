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
public class MethodGradleDependency extends GradleDependency {

	public MethodGradleDependency(String configuration, String group,
		String name, String version, String methodName) {

		super(configuration, group, name, version);

		_methodName = methodName;
	}

	public MethodGradleDependency(String configuration, String group,
		String name, String version, int lineNumber, int lastLineNumber,
		String methodName) {

		super(configuration, group, name, version, lineNumber, lastLineNumber);

		_methodName = methodName;
	}

	public String getMethodName() {
		return _methodName;
	}

	@Override
	public String toString() {
		return MessageFormat.format(
			"{0} {1}()", getConfiguration(), _methodName);
	}



	private String _methodName;
}
