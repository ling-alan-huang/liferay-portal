/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.util;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.liferay.petra.string.StringBundler;

/**
 * @author Alan Huang
 */
public class GradleDependencyMethod implements Comparable<GradleDependencyMethod> {

	public static final Comparator<GradleDependencyMethod>
		COMPARATOR_LAST_LINE_NUMBER_DESC = Comparator.comparingInt(
			GradleDependencyMethod::getLastLineNumber
		).reversed();

	public GradleDependencyMethod(
		String mehtodName, Map<String, String> paramtersMap, boolean hasArgumentList) {

		this(mehtodName, paramtersMap, hasArgumentList, -1, -1);
	}

	public GradleDependencyMethod(
		String methodName, Map<String, String> paramtersMap,
		boolean hasArgumentList,
		int lineNumber, int lastLineNumber) {

		_methodName = methodName;
		_paramtersMap = paramtersMap;
		_hasArgumentList = hasArgumentList;
	}

	@Override
	public int compareTo(GradleDependencyMethod gradleDependency) {
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
		if (Objects.equals(_methodName, gradleDependency._methodName)) {

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

	public List<GradleDependencyMethod> getGradleDependencyMethodList() {
		return _gradleDependencyMethodList;
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

	public void setGradleDependencyMethodList(List<GradleDependencyMethod> gradleDependencyMethodList) {
		_gradleDependencyMethodList = gradleDependencyMethodList;
	}

	public String toGAVString() {
		StringBundler sb = new StringBundler(_paramtersMap.size() * 2);

		sb.append(_methodName);
		sb.append(" ");

		for (Map.Entry<String, String> entry : _paramtersMap.entrySet()) {
			sb.append(entry.getKey());
			sb.append(": ");
			sb.append(entry.getValue());
			sb.append(", ");
		}
		
		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
		
	}

	@Override
	public String toString() {
		return toGAVString();
	}

	private String _methodName;
	private int _lastLineNumber;
	private int _lineNumber;
	private Map<String, String> _paramtersMap;
	private boolean _hasArgumentList;
	private List<GradleDependencyMethod> _gradleDependencyMethodList;
}