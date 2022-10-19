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

package com.liferay.language.security.scan;

import com.liferay.language.security.scan.util.AntiSamyUtil;
import com.liferay.language.security.scan.util.StringEscapeUtils;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.tools.ArgumentsUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

/**
 * @author Seiphon Wang
 */
public class LanguageSecurityScan {

	public static void main(String[] args) {
		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		LanguageSecurityScanArges languageSecurityScanArges =
			new LanguageSecurityScanArges();

		languageSecurityScanArges.setBaseDirName(
			GetterUtil.getString(
				arguments.get("scan.base.dir"),
				LanguageSecurityScanArges.BASE_DIR_NAME));

		languageSecurityScanArges.setScanAll(
			GetterUtil.getBoolean(
				arguments.get("scan.all"), LanguageSecurityScanArges.SCAN_ALL));

		LanguageSecurityScan languageSecurityScan = new LanguageSecurityScan(
			languageSecurityScanArges);

		languageSecurityScan.scan();
	}

	public LanguageSecurityScan(
		LanguageSecurityScanArges languageSecurityScanArges) {

		_languageSecurityScanArges = languageSecurityScanArges;
	}

	public List<File> getSantizedFiles() {
		return _sanitizedFiles;
	}

	public void scan() {
		long startTime = System.currentTimeMillis();

		List<File> fileList = new ArrayList<>();

		try {
			fileList = _getAllLanguageProperties(
				_languageSecurityScanArges.getBaseDirName());
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}

		ExecutorService executorService = Executors.newFixedThreadPool(12);

		List<Future<Void>> futures = new ArrayList<>();

		for (File file : fileList) {
			Future<Void> future = executorService.submit(
				new Callable<Void>() {

					@Override
					public Void call() {
						try {
							_sanitizeProperites(file);
						}
						catch (Exception exception) {
							exception.printStackTrace();

							// add log here

						}

						return null;
					}

				});

			futures.add(future);
		}

		for (Future<Void> future : futures) {
			try {
				future.get();
			}
			catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		executorService.shutdown();

		long endTime = System.currentTimeMillis();

		System.out.println(
			"total using time： " + ((endTime - startTime) / 1000) + " m");
	}

	private List<File> _getAllLanguageProperties(String baseDirName)
		throws Exception {

		List<File> fileList = new ArrayList<>();

		Files.walkFileTree(
			Paths.get(baseDirName),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String dirName = String.valueOf(dirPath.getFileName());

					if (dirName.startsWith(".") ||
						ArrayUtil.contains(_SKIP_DIR_NAMES, dirName)) {

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
						Path file, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String fileName = String.valueOf(file.getFileName());

					if (fileName.endsWith(".properties") &&
						fileName.startsWith("Language")) {

						fileList.add(file.toFile());
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return fileList;
	}

	private Properties _readProperties(File file) throws IOException {
		Properties properties = new Properties();

		if (file.exists()) {
			try (FileInputStream fileInputStream = new FileInputStream(file)) {
				properties.load(fileInputStream);
			}
		}

		return properties;
	}

	private void _sanitizeProperites(File file)
		throws IOException, PolicyException, ScanException {

		Properties properties = _readProperties(file);

		Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();

		for (Map.Entry<Object, Object> entry : entrySet) {
			String value = (String)entry.getValue();

			String sanitizedValue = AntiSamyUtil.scan(value);

			sanitizedValue = StringEscapeUtils.unEscapeSpecialCharactors(
				sanitizedValue);

			if (!value.equals(sanitizedValue)) {
				System.out.println(value);
				System.out.println(sanitizedValue);
				System.out.println(file.toString());
				_sanitizedFiles.add(file);
			}
		}
	}

	private static final String[] _SKIP_DIR_NAMES = {
		".git", ".github", ".gradle", ".idea", ".m2", ".settings", "bin",
		"build", "classes", "dependencies", "node_modules",
		"node_modules_cache", "sql", "test-classes", "test-coverage",
		"test-results", "tmp"
	};

	private static final List<File> _sanitizedFiles = new ArrayList<>();

	private final LanguageSecurityScanArges _languageSecurityScanArges;

}