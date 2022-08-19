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
import java.util.List;

import com.liferay.petra.string.StringBundler;
import com.liferay.source.formatter.upgrade.GradleDependency;

/**
 * @author Seiphon Wang
 */
public class FileGradleDependency extends GradleDependency {

	public FileGradleDependency(String configuration, String group, String name,
		String version, List<String> files, String fileTreePath) {

		super(configuration, group, name, version);

		_files = files;
		_fileTreePath = fileTreePath;
	}

	public FileGradleDependency(String configuration, String group, String name,
			String version, int lineNumber, int lastLineNumber) {

		super(configuration, group, name, version, lineNumber, lastLineNumber);
	}

	public List<String> getFiles() {
		return _files;
	}

	public String getFileTreePath() {
		return _fileTreePath;
	}

	@Override
	public String toString() {
		if (_files != null && !_files.isEmpty()) {
			StringBundler sb = new StringBundler();

			sb.append(getConfiguration());
			sb.append(" files(");

			for (int i = 0; i < _files.size(); i++) {
				sb.append("\'");
				sb.append(_files.get(i));
				sb.append("\'");

				if (1 != (_files.size() - 1)) {
					sb.append(", ");
				}
			}

			sb.append(")");

			return sb.toString();
		}

		return MessageFormat.format("{0} fileTree(\'{1}\')", getConfiguration(),
			_fileTreePath);
	}

	private List<String> _files;
	private String _fileTreePath;
}
