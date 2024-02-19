/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import java.util.List;

import com.liferay.source.formatter.util.GradleBuildFile;
import com.liferay.source.formatter.util.GradleDependency;

/**
 * @author Alan Huang
 */
public class GradleDependenciesCheckNew extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		GradleBuildFile gradleBuildFile = new GradleBuildFile(content);

		List<GradleDependency> gradleDependencies = gradleBuildFile.getGradleDependencies();
		
		for (GradleDependency gradleDependency :
			gradleDependencies) {

			String gradleDependencyConfiguration =
				gradleDependency.getConfiguration();
			String gradleDependencyGroup = gradleDependency.getGroup();
			String gradleDependencyName = gradleDependency.getName();

			int a = 0;
		}


		return content;
	}


}