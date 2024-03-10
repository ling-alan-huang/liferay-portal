/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.util;

import com.liferay.petra.string.StringBundler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alan Huang
 */
public class GradleDependency implements Comparable<GradleDependency> {

	public static final Comparator<GradleDependency>
		COMPARATOR_LAST_LINE_NUMBER_DESC = Comparator.comparingInt(
			GradleDependency::getLastLineNumber
		).reversed();

	public GradleDependency(
		String configuration, Map<String, String> argumentsMap,
		boolean hasArgumentList, int lineNumber, int lastLineNumber) {

		_configuration = configuration;
		_argumentsMap = argumentsMap;
		_hasArgumentList = hasArgumentList;
		_lineNumber = lineNumber;
		_lastLineNumber = lastLineNumber;

		_group = "";
		_name = "";
		_version = "";
	}

	public GradleDependency(
		String configuration, String group, String name, String version) {

		this(configuration, group, name, version, -1, -1);
	}

	public GradleDependency(
		String configuration, String group, String name, String version,
		int lineNumber, int lastLineNumber) {

		_configuration = configuration;
		_group = group;
		_name = name;
		_version = version;
		_lineNumber = lineNumber;
		_lastLineNumber = lastLineNumber;
	}

	@Override
	public int compareTo(GradleDependency gradleDependency) {
		String string = toString();

		return string.compareTo(gradleDependency.toString());
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof GradleDependency)) {
			return false;
		}

		GradleDependency gradleDependency = (GradleDependency)object;

		if (Objects.equals(_configuration, gradleDependency._configuration) &&
			Objects.equals(_group, gradleDependency._group) &&
			Objects.equals(_name, gradleDependency._name)) {

			return true;
		}

		return false;
	}

	public Map<String, String> getArgumentsMap() {
		return _argumentsMap;
	}

	public String getConfiguration() {
		return _configuration;
	}

	public List<GradleDependency> getGradleDependencyList() {
		return _gradleDependencyList;
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

	public List<GradleDependency> gradleDependencyList() {
		return _gradleDependencyList;
	}

	public boolean hasArgumentList() {
		return _hasArgumentList;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_configuration, _argumentsMap);
	}

	public void setAargumentsMap(Map<String, String> argumentsMap) {
		_argumentsMap = argumentsMap;
	}

	public void setConfiguration(String configuration) {
		_configuration = configuration;
	}

	public void setGradleDependencyList(
		List<GradleDependency> gradleDependencyList) {

		_gradleDependencyList = gradleDependencyList;
	}

	public void setGroup(String group) {
		_group = group;
	}

	public void setHasArgumentList(boolean hasArgumentList) {
		_hasArgumentList = hasArgumentList;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setVersion(String version) {
		_version = version;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append(_configuration);

		if (_hasArgumentList) {
			sb.append("(");
		}
		else {
			sb.append(" ");
		}

		for (Map.Entry<String, String> entry : _argumentsMap.entrySet()) {
			sb.append(entry.getKey());
			sb.append(": ");
			sb.append(entry.getValue());
			sb.append(", ");
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		if (_hasArgumentList) {
			sb.append(") {");
			sb.append("\n");

			for (GradleDependency gradleDependency : _gradleDependencyList) {
				sb.append("\t");
				sb.append(gradleDependency.toString());
				sb.append("\n");
			}

			sb.append("}");
		}

		return sb.toString();
	}

	private Map<String, String> _argumentsMap;
	private String _configuration;
	private List<GradleDependency> _gradleDependencyList = new ArrayList<>();
	private String _group;
	private boolean _hasArgumentList;
	private int _lastLineNumber;
	private int _lineNumber;
	private String _name;
	private String _version;

}