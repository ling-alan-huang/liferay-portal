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

import com.google.common.base.Objects;

import com.liferay.source.formatter.upgrade.GradleDependency;

import java.text.MessageFormat;

/**
 * @author Seiphon Wang
 */
public class ExternalGradleDependency extends GradleDependency {

	public ExternalGradleDependency(
		String configuration, String classifier, String ext, String group,
		String name, String version) {

		super(configuration, group, name, version);

		_classifier = classifier;
		_ext = ext;
	}

	public ExternalGradleDependency(
		String configuration, String classifier, String ext, String group,
		String name, String version, int lineNumber, int lastLineNumber) {

		super(configuration, group, name, version, lineNumber, lastLineNumber);

		_classifier = classifier;
		_ext = ext;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof ExternalGradleDependency) {
			ExternalGradleDependency externalGradleDependency =
				(ExternalGradleDependency)object;

			if (Objects.equal(
					_classifier, externalGradleDependency.getClassifier()) &&
				Objects.equal(_ext, externalGradleDependency.getExt()) &&
				Objects.equal(
					getConfiguration(),
					externalGradleDependency.getConfiguration()) &&
				Objects.equal(
					getGroup(), externalGradleDependency.getGroup()) &&
				Objects.equal(getName(), externalGradleDependency.getName())) {

				return true;
			}

			return false;
		}

		return super.equals(object);
	}

	public String getClassifier() {
		return _classifier;
	}

	public String getExt() {
		return _ext;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(
			getConfiguration(), _classifier, _ext, getGroup(), getName());
	}

	@Override
	public String toString() {
		if (getVersion() == null) {
			return MessageFormat.format(
				"{0} classifier: \"{1}\", ext: \"{2}\", group: \"{3}\", " +
					"name: \"{4}\"",
				getConfiguration(), _classifier, _ext, getGroup(), getName());
		}

		return MessageFormat.format(
			"{0} classifier: \"{1}\", ext: \"{2}\", group: \"{3}\", name: " +
				"\"{4}\", version: \"{5}\"",
			getConfiguration(), _classifier, _ext, getGroup(), getName(),
			getVersion());
	}

	private String _classifier;
	private String _ext;

}