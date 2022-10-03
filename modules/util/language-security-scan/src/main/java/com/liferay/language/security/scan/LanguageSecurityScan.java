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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

import java.util.Properties;
import java.util.Set;

import com.liferay.language.security.scan.util.AntiSamyUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.tools.ArgumentsUtil;

/**
 * @author Seiphon Wang
 */
public class LanguageSecurityScan {

	public static void main(String[] args) {
		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		String baseDirName = GetterUtil.getString(
			arguments.get("scan.base.dir"),
			LanguageSecurityScanArges.BASE_DIR_NAME);

		File baseDir = new File(baseDirName);

		if (!baseDir.exists()) {
			return;
		}

		// will be deleted
		long startTime = System.currentTimeMillis();

		_recursiveDirectory(baseDir);

		long middleTime = System.currentTimeMillis();

		ExecutorService executorService = Executors.newFixedThreadPool(20);

		List<Future<Void>> futures = new ArrayList<>();

		for (File file : _fileList) {
			Future<Void> future = executorService.submit(
					new Callable<Void>() {

						@Override
						public Void call() {
							try {
								_sanitizeProperites(file);
							}
							catch (Exception exception) {
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		executorService.shutdown();

		long endTime = System.currentTimeMillis();

		// will be deleted
		System.out.println("recursive time： " + (middleTime - startTime) / 1000 + " m");
		System.out.println("total using time： " + (endTime - startTime) / 1000 + " m");
	}

	private static void _sanitizeProperites(File file)
		throws IOException, PolicyException, ScanException {

		Properties properties = _readProperties(file);

		Set<Entry<Object, Object>> entrySet = properties.entrySet();

		for (Entry<Object, Object> entry : entrySet) {
			String value = (String)entry.getValue();

			String sanitizedValue = AntiSamyUtil.scan(value);

			if (!value.equals(sanitizedValue)) {
				System.out.println(value);
				System.out.println(sanitizedValue);
			}
		}
	}

	private static void _recursiveDirectory(File file) {
		String fileNameString = file.getName();

		if (file.isDirectory()) {
			if (fileNameString.startsWith(".") ||
				_ingnoredFolderNameList.contains(fileNameString)) {

				return;
			}

			File[] children = file.listFiles();

			for (File child : children) {
				_recursiveDirectory(child);
			}
		}
		else {
			if (file.getName().endsWith(".properties") &&
				file.getName().startsWith("Language")) {

				_fileList.add(file);
			}
		}
	}

	private static Properties _readProperties(File file) throws IOException {
		Properties properties = new Properties();

		if (file.exists()) {
			try (FileInputStream fileInputStream = new FileInputStream(file)) {
				properties.load(fileInputStream);
			}
		}

		return properties;
	}

	private static List<File> _fileList = new ArrayList<>();

	private static List<String> _ingnoredFolderNameList =
		Arrays.asList("bin", "build", "classes", "lib");
}