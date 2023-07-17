/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.defaults.internal.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.file.Path;

import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Peter Shin
 */
public class CopyrightUtil {

	public static String getCopyright(File projectDir) throws IOException {
		return _getCopyright(projectDir);
	}

	public static boolean isCommercial(File projectDir) throws IOException {
		Path projectPath = projectDir.toPath();

		if (projectPath == null) {
			return false;
		}

		Path absolutePath = projectPath.toAbsolutePath();

		absolutePath = absolutePath.normalize();

		String absoluteFileName = absolutePath.toString();

		absoluteFileName = absoluteFileName.replace('\\', '/');

		if (absoluteFileName.contains("/modules/dxp/apps/") ||
			absoluteFileName.contains("/modules/private/apps/")) {

			return true;
		}

		File dir = absolutePath.toFile();

		while (dir != null) {
			File file = new File(dir, "gradle.properties");

			if (file.exists()) {
				Properties properties = new Properties();

				properties.load(new FileInputStream(file));

				if (properties.containsKey("project.path.prefix")) {
					String s = properties.getProperty("project.path.prefix");

					if (s.startsWith(":dxp:apps") ||
						s.startsWith(":private:apps")) {

						return true;
					}

					return false;
				}
			}

			dir = dir.getParentFile();
		}

		return false;
	}

	private static String _getCopyright(File projectDir) throws IOException {
		ClassLoader classLoader = CopyrightUtil.class.getClassLoader();

		String name = "copyright.txt";

		if (isCommercial(projectDir)) {
			name = "copyright-commercial.txt";
		}

		InputStream inputStream = classLoader.getResourceAsStream(
			"com/liferay/gradle/plugins/defaults/dependencies/" + name);

		BufferedReader bufferedReader = new BufferedReader(
			new InputStreamReader(inputStream));

		Stream<String> stream = bufferedReader.lines();

		return stream.collect(Collectors.joining("\n"));
	}

}