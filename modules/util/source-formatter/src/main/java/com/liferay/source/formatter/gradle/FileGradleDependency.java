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

import java.util.List;

/**
 * @author Seiphon Wang
 */
public class FileGradleDependency extends GradleDependency {

	public FileGradleDependency(
		String configuration, String group, String name, String version,
		List<String> files, String fileTreePath, String builtBy, String include,
		String excludes) {

		super(configuration, group, name, version);

		_files = files;
		_fileTreePath = fileTreePath;
		_builtBy = builtBy;
		_include = include;
		_excludes = excludes;
	}

	public FileGradleDependency(
		String configuration, String group, String name, String version,
		List<String> files, String fileTreePath, String builtBy, String include,
		String excludes, int lineNumber, int lastLineNumber) {

		super(configuration, group, name, version, lineNumber, lastLineNumber);

		_files = files;
		_fileTreePath = fileTreePath;
		_builtBy = builtBy;
		_include = include;
		_excludes = excludes;
	}

	public String getBuiltBy() {
		return _builtBy;
	}

	public String getExcludeList() {
		return _excludes;
	}

	public List<String> getFiles() {
		return _files;
	}

	public String getFileTreePath() {
		return _fileTreePath;
	}

	public String getInclude() {
		return _include;
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
				sb.append("\"");
				sb.append(_files.get(i));
				sb.append("\"");

				if (i != (_files.size() - 1)) {
					sb.append(", ");
				}
			}

			sb.append(")");

			return sb.toString();
		}

		StringBundler sb = new StringBundler();

		sb.append(getConfiguration());
		sb.append(" fileTree(");

		if (_fileTreePath != null) {
			if ((_builtBy == null) && (_include == null) &&
				(_excludes == null)) {

				sb.append("\"");
				sb.append(_fileTreePath);
				sb.append("\")");

				return sb.toString();
			}

			if (_builtBy != null) {
				sb.append("builtBy: ");
				sb.append(_builtBy);
				sb.append(", ");
			}

			sb.append("dir: ");

			sb.append(_fileTreePath);

			if (_include != null) {
				sb.append(", include: ");
				sb.append(_include);
			}

			if (_excludes != null) {
				sb.append(", excludes: ");
				sb.append(_excludes);
			}
		}

		sb.append(")");

		return sb.toString();
	}

	private String _builtBy;
	private String _excludes;
	private final List<String> _files;
	private final String _fileTreePath;
	private String _include;

}