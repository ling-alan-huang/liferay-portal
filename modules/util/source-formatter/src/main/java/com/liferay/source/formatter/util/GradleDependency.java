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

package com.liferay.source.formatter.util;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.liferay.petra.string.StringBundler;

/**
 * @author Seiphon Wang
 */
public class GradleDependency {

	public GradleDependency(
			String configuration, String group, String name, String version, int lineNumber, int lastLineNumber,
			List<GradleDependency> arguments) {

			_configuration = configuration;
			_group = group;
			_name = name;
			_version = version;
			_lineNumber = lineNumber;
			_lastLineNumber = lastLineNumber;
			_arguments = arguments;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			if (obj == null) {
				return false;
			}

			if (getClass() != obj.getClass()) {
				return false;
			}

			GradleDependency other = (GradleDependency)obj;

			if (_configuration == null) {
				if (other._configuration != null) {
					return false;
				}
			}
			else if (!_configuration.equals(other._configuration)) {
				return false;
			}

			if (_group == null) {
				if (other._group != null) {
					return false;
				}
			}
			else if (!_group.equals(other._group)) {
				return false;
			}

			if (_name == null) {
				if (other._name != null) {
					return false;
				}
			}
			else if (!_name.equals(other._name)) {
				return false;
			}

			if (_version == null) {
				if (other._version != null) {
					return false;
				}
			}
			else if (!_version.equals(other._version)) {
				return false;
			}

			if (_arguments == null) {
				if (other._arguments != null) {
					return false;
				}
			}
			else {
				List<GradleDependency> otherArguments = other._arguments;

				if (_arguments.size() != otherArguments.size()) {
					return false;
				}

				Stream<GradleDependency> otherArgumentStream = otherArguments.stream();

				Stream<GradleDependency> argumentStream = _arguments.stream();

				return argumentStream.map(
					argument -> argument.toString()
				).sorted(
				).collect(
					Collectors.joining()
				).equals(
					otherArgumentStream.map(
						argument -> argument.toString()
					).sorted(
					).collect(
						Collectors.joining()
					)
				);
			}

			return true;
		}

		public List<GradleDependency> getArguments() {
			return _arguments;
		}

		public String getConfiguration() {
			return _configuration;
		}

		public String getGroup() {
			return _group;
		}

		public int getLastLineNumber() {
			return _lastLineNumber;
		}

		public int getLineNumber() {
			return _lineNumber;
		}

		public String getName() {
			return _name;
		}

		public String getVersion() {
			return _version;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;

			result = prime * result + ((_group == null) ? 0 : _group.hashCode());
			result = prime * result + ((_name == null) ? 0 : _name.hashCode());
			result = prime * result + ((_version == null) ? 0 : _version.hashCode());
			result = prime * result + ((_arguments == null) ? 0 : _arguments.hashCode());

			return result;
		}

		public void setArguments(List<GradleDependency> arguments) {
			_arguments = arguments;
		}

		public void setConfiguration(String configuration) {
			_configuration = configuration;
		}

		public void setGroup(String group) {
			_group = group;
		}

		public void setLastLineNumber(int lastLineNumber) {
			_lastLineNumber = lastLineNumber;
		}

		public void setName(String name) {
			_name = name;
		}

		public void setVersion(String version) {
			_version = version;
		}

		@Override
		public String toString() {
			if (_arguments != null && _arguments.size() > 0) {
				StringBundler sb = new StringBundler();

				sb.append(MessageFormat.format("{0}(group: {1}, name: {2}, version: {3})", _configuration, _group, _name, _version));
				sb.append(" {\n");

				for (GradleDependency argument : _arguments) {
					sb.append("\t");
					sb.append(MessageFormat.format("{0} group: {1}, name: {2}, version: {3}",
							argument.getConfiguration(), argument.getGroup(), argument.getName(), argument.getVersion()));

					sb.append("\n");
				}

				sb.append("}");

				return sb.toString();
			}

			return MessageFormat.format("{0} group: {1}, name: {2}, version: {3}", _configuration, _group, _name, _version);
		}

		private List<GradleDependency> _arguments;
		private String _configuration;
		private String _group;
		private int _lastLineNumber;
		private int _lineNumber;
		private String _name;
		private String _version;

}
