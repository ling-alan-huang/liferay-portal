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

import com.liferay.petra.string.StringBundler;
import com.liferay.source.formatter.upgrade.GradleDependency;

import java.text.MessageFormat;

import java.util.List;

/**
 * @author Seiphon Wang
 */
public class FileGradleDependency extends GradleDependency {

	public FileGradleDependency(
		String configuration, String group, String name, String version,
		int lineNumber, int lastLineNumber) {

		super(configuration, group, name, version, lineNumber, lastLineNumber);
	}

	public FileGradleDependency(
		String configuration, String group, String name, String version,
		List<String> files, String fileTreePath) {

		super(configuration, group, name, version);

		_files = files;
		_fileTreePath = fileTreePath;
	}

	public List<String> getFiles() {
		return _files;
	}

	public String getFileTreePath() {
		return _fileTreePath;
	}

	@Override
	public int hashCode() {
		if ((_files != null) && !_files.isEmpty()) {
			return Objects.hashCode(getConfiguration(), _files);
		}

		return Objects.hashCode(getConfiguration(), _fileTreePath);
	}

	@Override
	public String toString() {
		if ((_files != null) && !_files.isEmpty()) {
			StringBundler sb = new StringBundler();

			sb.append(getConfiguration());
			sb.append(" files(");

			for (int i = 0; i < _files.size(); i++) {
				sb.append("\'");
				sb.append(_files.get(i));
				sb.append("\'");

				if (i != (_files.size() - 1)) {
					sb.append(", ");
				}
			}

			sb.append(")");

			return sb.toString();
		}

		return MessageFormat.format(
			"{0} fileTree(\'{1}\')", getConfiguration(), _fileTreePath);
	}

	private List<String> _files;
	private String _fileTreePath;

}