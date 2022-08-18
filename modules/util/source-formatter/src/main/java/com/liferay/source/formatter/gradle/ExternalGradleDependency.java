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

/**
 * @author Seiphon Wang
 */
public class ExternalGradleDependency extends GradleDependency {

	public ExternalGradleDependency(String classifier, String ext,
		String configuration, String group, String name, String version,
		int lineNumber, int lastLineNumber) {

		super(configuration, group, name, version, lineNumber, lastLineNumber);

		_classifier = classifier;
		_ext = ext;
	}

	public ExternalGradleDependency(String classifier, String ext,
		String configuration, String group, String name, String version) {

		super(configuration, group, name, version);
	}

	public String getClassifier() {
		return _classifier;
	}

	public String getExt() {
		return _ext;
	}

	private String _classifier;
	private String _ext;
}
