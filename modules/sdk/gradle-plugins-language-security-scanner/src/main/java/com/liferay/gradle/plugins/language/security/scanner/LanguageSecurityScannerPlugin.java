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

package com.liferay.gradle.plugins.language.security.scanner;

import com.liferay.gradle.util.GradleUtil;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.TaskContainer;

/**
 * @author Seiphon Wang
 */
public class LanguageSecurityScannerPlugin implements Plugin<Project> {

	public static final String CONFIGURATION_NAME = "languageSecurityScanner";

	public static final String LANGUAGE_SECURITY_SCAN_TASK_NAME =
		"languageSecurityScan";

	@Override
	public void apply(Project project) {
		Configuration languageSecurityScannerConfiguration =
			addConfigurationLanguageSecurityScanner(project);

		addTaskLanguageSecurityScan(project);

		configureTasksLanguageSecurityScan(
			project, languageSecurityScannerConfiguration);
	}

	protected Configuration addConfigurationLanguageSecurityScanner(
		final Project project) {

		Configuration configuration = GradleUtil.addConfiguration(
			project, CONFIGURATION_NAME);

		configuration.setDescription(
			"Configures Liferay Language Security Scanner for this project.");

		GradleUtil.executeIfEmpty(
			configuration,
			new Action<Configuration>() {

				@Override
				public void execute(Configuration configuration) {
					addDependenciesLanguageSecurityScanner(project);
				}

			});

		return configuration;
	}

	protected void addDependenciesLanguageSecurityScanner(Project project) {
		GradleUtil.addDependency(
			project, CONFIGURATION_NAME, "com.liferay",
			"com.liferay.language.security.scanner", "latest.release");
	}

	protected LanguageSecurityScannerTask addTaskLanguageSecurityScan(
		Project project) {

		LanguageSecurityScannerTask languageSecurityScannerTask =
			GradleUtil.addTask(
				project, LANGUAGE_SECURITY_SCAN_TASK_NAME,
				LanguageSecurityScannerTask.class);

		languageSecurityScannerTask.setDescription(
			"Runs Liferay Language Security Scanner to format files.");

		languageSecurityScannerTask.setGroup("formatting");

		return languageSecurityScannerTask;
	}

	protected void configureTaskLanguageSecurityScanClasspath(
		LanguageSecurityScannerTask languageSecurityScannerTask,
		FileCollection fileCollection) {

		languageSecurityScannerTask.setClasspath(fileCollection);
	}

	protected void configureTasksLanguageSecurityScan(
		Project project,
		final Configuration languageSecurityScannerConfiguration) {

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			LanguageSecurityScannerTask.class,
			new Action<LanguageSecurityScannerTask>() {

				@Override
				public void execute(
					LanguageSecurityScannerTask languageSecurityScannerTask) {

					configureTaskLanguageSecurityScanClasspath(
						languageSecurityScannerTask,
						languageSecurityScannerConfiguration);
				}

			});
	}

}