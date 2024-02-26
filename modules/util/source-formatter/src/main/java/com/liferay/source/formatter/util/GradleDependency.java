/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.liferay.petra.string.StringBundler;

/**
 * @author Alan Huang
 */
public class GradleDependency implements Comparable<GradleDependency> {

	public static final Comparator<GradleDependency>
		COMPARATOR_LAST_LINE_NUMBER_DESC = Comparator.comparingInt(
			GradleDependency::getLastLineNumber
		).reversed();

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
		
		public void setConfiguration(String configuration) {
			_configuration = configuration;
		}

		public void setGroup(String group) {
			_group = group;
		}

		public void setName(String name) {
			_name = name;
		}

		public void setVersion(String version) {
			_version = version;
		}
	public GradleDependency(
		String mehtodName, Map<String, String> paramtersMap, boolean hasArgumentList) {

		this(mehtodName, paramtersMap, hasArgumentList, -1, -1);
		_configuration = "";
		_group = "";
		_name = "";
		_version = "";
	}

	public GradleDependency(
		String methodName, Map<String, String> paramtersMap,
		boolean hasArgumentList,
		int lineNumber, int lastLineNumber) {

		_methodName = methodName;
		_paramtersMap = paramtersMap;
		_hasArgumentList = hasArgumentList;
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

		if (!(object instanceof GradleDependencyMethod)) {
			return false;
		}

		GradleDependencyMethod gradleDependency = (GradleDependencyMethod)object;

//		if (Objects.equals(_methodName, gradleDependency._methodName) &&
//			Objects.equals(_group, gradleDependency._group) &&
//			Objects.equals(_name, gradleDependency._name)) {
		if (Objects.equals(_methodName, _gradleDependencyList)) {

			return true;
		}

		return false;
	}

	public String getMethodName() {
		return _methodName;
	}

	public Map<String, String> getParamtersMap() {
		return _paramtersMap;
	}

	public int getLastLineNumber() {
		return _lastLineNumber;
	}

	public int getLineNumber() {
		return _lineNumber;
	}

	public boolean hasArgumentList() {
		return _hasArgumentList;
	}

	public List<GradleDependency> gradleDependencyList() {
		return _gradleDependencyList;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_methodName, _paramtersMap);
	}

	public void setMethodName(String methodName) {
		_methodName = methodName;
	}

	public void setParamtersMap(Map<String, String> paramtersMap) {
		_paramtersMap = paramtersMap;
	}

	public void setHasArgumentList(boolean hasArgumentList) {
		_hasArgumentList = hasArgumentList;
	}

	public void setGradleDependencyList(List<GradleDependency> gradleDependencyList) {
		_gradleDependencyList = gradleDependencyList;
	}

	public String toGAVString() {
		StringBundler sb = new StringBundler(_paramtersMap.size() * 2);

		sb.append(_methodName);
		if (_hasArgumentList) {
			sb.append("(");
		}
		else {
			sb.append(" ");
		}

		for (Map.Entry<String, String> entry : _paramtersMap.entrySet()) {
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
	public String getConfiguration() {
		return _configuration;
	}
	public List<GradleDependency> getGradleDependencyList() {
		return _gradleDependencyList;
	}

	public String getGroup() {
		return _group;
	}
	public String getName() {
		return _name;
	}

	public String getVersion() {
		return _version;
	}

	@Override
	public String toString() {
		return toGAVString();
	}

	private String _configuration;
	private String _group;
	private String _name;
	private String _version;

	
	private String _methodName;
	private int _lastLineNumber;
	private int _lineNumber;
	private Map<String, String> _paramtersMap;
	private boolean _hasArgumentList;
	private List<GradleDependency> _gradleDependencyList =  new ArrayList<>();
}