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

package com.liferay.language.security.scanner;

import com.liferay.language.security.scanner.util.AntiSamyUtil;
import com.liferay.language.security.scanner.util.StringEscapeUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.tools.ArgumentsUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.URL;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

/**
 * @author Seiphon Wang
 */
public class LanguageSecurityScanner {

	public static void main(String[] args) {
		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		LanguageSecurityScannerArges languageSecurityScanArges =
			new LanguageSecurityScannerArges();

		languageSecurityScanArges.setBaseDirName(
			GetterUtil.getString(
				arguments.get("scan.base.dir"),
				LanguageSecurityScannerArges.BASE_DIR_NAME));

		languageSecurityScanArges.setScanAll(
			GetterUtil.getBoolean(
				arguments.get("scan.all"),
				LanguageSecurityScannerArges.SCAN_ALL));

		LanguageSecurityScanner languageSecurityScanner =
			new LanguageSecurityScanner(languageSecurityScanArges);

		try {
			long startTime = System.currentTimeMillis();

			languageSecurityScanner.scan();

			String baseDirString = GetterUtil.getString(
				arguments.get("scan.base.dir"),
				LanguageSecurityScannerArges.BASE_DIR_NAME);

			File baseDir = new File(baseDirString);

			File resultFile = new File(
				baseDir.getParent(), "language-security-scan-result.log");

			FileWriter fileWriter = new FileWriter(resultFile);

			PrintWriter printWriter = new PrintWriter(resultFile);

			for (int i = 0; i < _languageSecurityMessages.size(); i++) {
				LanguageSecurityMessage languageSecurityMessage =
					_languageSecurityMessages.get(i);

				printWriter.println(
					(i + 1) + ": " + languageSecurityMessage.toString());
				System.out.println(
					(i + 1) + ": " + languageSecurityMessage.toString());

				printWriter.flush();
				fileWriter.flush();
			}

			long endTime = System.currentTimeMillis();

			printWriter.println(
				"total using time： " + ((endTime - startTime) / 1000) + " m");
			printWriter.println(
				"total " + ((endTime - startTime) / 1000) + " items");

			printWriter.flush();
			fileWriter.flush();
			printWriter.close();
			fileWriter.close();
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public LanguageSecurityScanner(
		LanguageSecurityScannerArges languageSecurityScanArges) {

		ClassLoader classLoader =
			LanguageSecurityScanner.class.getClassLoader();

		URL antiSamyURL = classLoader.getResource("antisamy-liferay.xml");

		String antsamyPath = antiSamyURL.getFile();

		Policy policy = null;

		try {
			policy = Policy.getInstance(antsamyPath);
		}
		catch (PolicyException policyException) {
			policyException.printStackTrace();
		}

		_policy = policy;
		_languageSecurityScannerArges = languageSecurityScanArges;
	}

	public List<File> getSantizedFiles() {
		return _sanitizedFiles;
	}

	public void scan() {
		List<File> fileList = new ArrayList<>();

		try {
			fileList = _getAllLanguageProperties(
				_languageSecurityScannerArges.getBaseDirName());
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}

		ExecutorService executorService = Executors.newFixedThreadPool(12);

		List<Future<List<LanguageSecurityMessage>>> futures =
			new CopyOnWriteArrayList<>();

		for (File file : fileList) {
			Future<List<LanguageSecurityMessage>> future =
				executorService.submit(
					new Callable<List<LanguageSecurityMessage>>() {

						@Override
						public List<LanguageSecurityMessage> call() {
							List<LanguageSecurityMessage>
								languageSecurityMessages =
									new CopyOnWriteArrayList<>();

							try {
								languageSecurityMessages = _sanitizeProperites(
									file);
							}
							catch (Exception exception) {
								exception.printStackTrace();
							}

							return languageSecurityMessages;
						}

					});

			futures.add(future);
		}

		for (Future<List<LanguageSecurityMessage>> future : futures) {
			try {
				_languageSecurityMessages.addAll(future.get());
			}
			catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		executorService.shutdown();

		while (!executorService.isTerminated()) {
			try {
				Thread.sleep(20);
			}
			catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}
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

					if ((fileName.endsWith(".properties") &&
						 fileName.startsWith("Language")) ||
						(fileName.endsWith(".properties") &&
						 fileName.startsWith("bundle"))) {

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

	private List<LanguageSecurityMessage> _sanitizeProperites(File file)
		throws IOException {

		List<LanguageSecurityMessage> languageSecurityMessages =
			new CopyOnWriteArrayList<>();

		Properties properties = _readProperties(file);

		Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();

		for (Map.Entry<Object, Object> entry : entrySet) {
			String originalValue = (String)entry.getValue();

			String sanitizedValue = originalValue;
			String value = originalValue;

			try {
				sanitizedValue = StringEscapeUtil.unEscape(
					AntiSamyUtil.sanitize(_policy, originalValue));

				value = StringEscapeUtil.unEscape(originalValue);
			}
			catch (ScanException scanException) {
			}
			catch (PolicyException policyException) {
				policyException.printStackTrace();
			}

			if (!sanitizedValue.equals(value)) {
				Matcher matcher = _tagPattern.matcher(value);

				Set<String> matchedTags = new HashSet<>();

				while (matcher.find()) {
					matchedTags.add(matcher.group());
				}

				if (!matchedTags.isEmpty()) {
					if (matchedTags.contains("<br />")) {
						value = value.replaceAll("<br />", "<br>");
					}

					if (matchedTags.contains("<a {0}>")) {
						value = value.replaceAll("<a \\{0\\}>", "<a>");
					}

					if (sanitizedValue.equals(value)) {
						continue;
					}

					boolean existUnknowTag = false;

					for (String tag : matchedTags) {
						if (!ArrayUtil.contains(_ALLOWED_TAGS, tag)) {
							existUnknowTag = true;

							break;
						}
					}

					if (existUnknowTag) {
						value = StringEscapeUtil.escapeTag(value);

						try {
							sanitizedValue = AntiSamyUtil.sanitize(
								_policy, value);
						}
						catch (PolicyException policyException) {
							policyException.printStackTrace();
						}
						catch (ScanException scanException) {
						}

						sanitizedValue = StringEscapeUtil.unEscapeQuot(
							sanitizedValue);

						if (sanitizedValue.equals(value)) {
							continue;
						}
					}
				}

				LanguageSecurityMessage languageSecurityMessage =
					new LanguageSecurityMessage(
						(String)entry.getKey(), file, originalValue,
						StringEscapeUtil.unEscapeTag(sanitizedValue));

				languageSecurityMessages.add(languageSecurityMessage);

				_sanitizedFiles.add(file);
			}
		}

		return languageSecurityMessages;
	}

	private static final String[] _ALLOWED_TAGS = {
		"<code>", "</code>", "<em>", "</em>", "<strong>", "</strong>", "<a>",
		"</a>", "<br>", "</br>"
	};

	private static final String[] _SKIP_DIR_NAMES = {
		".git", ".github", ".gradle", ".idea", ".m2", ".settings", "bin",
		"build", "classes", "dependencies", "node_modules",
		"node_modules_cache", "sql", "test-classes", "test-coverage",
		"test-results", "tmp"
	};

	private static final List<LanguageSecurityMessage>
		_languageSecurityMessages = new CopyOnWriteArrayList<>();
	private static final List<File> _sanitizedFiles =
		new CopyOnWriteArrayList<>();
	private static final Pattern _tagPattern = Pattern.compile("<.+?>");

	private final LanguageSecurityScannerArges _languageSecurityScannerArges;
	private final Policy _policy;

}