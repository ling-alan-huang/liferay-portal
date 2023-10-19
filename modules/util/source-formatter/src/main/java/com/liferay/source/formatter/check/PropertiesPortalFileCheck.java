/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.processor.PropertiesSourceProcessor;

import java.io.IOException;

import java.net.URL;

import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * @author Hugo Huijser
 */
public class PropertiesPortalFileCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (((isPortalSource() || isSubrepository()) &&
			 fileName.matches(".*/portal(-[^-/]+)*\\.properties")) ||
			(!isPortalSource() && !isSubrepository() &&
			 fileName.endsWith("portal.properties"))) {

			_checkPropertiesOrder(fileName, absolutePath, content);

			content = _formatPortalProperties(absolutePath, content);
		}

		return content;
	}

	private void _checkPropertiesOrder(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (absolutePath.endsWith("/portal-impl/src/portal.properties")) {
			return;
		}

		String portalPropertiesContent = _getPortalPropertiesContent(
			absolutePath);

		if (Validator.isNull(portalPropertiesContent)) {
			return;
		}

		String portalOSGiConfigurationPropertiesContent =
			_getPortalOSGiConfigurationPropertiesContent(absolutePath);

		int previousPropertyPosition = -1;
		String propertyKey = null;
		String previousPropertyKey = null;

		for (String line : content.split("\n")) {
			propertyKey = _getPropertyKey(line);

			if (propertyKey == null) {
				continue;
			}

			int pos = _getPropertyPosition(
				portalPropertiesContent, propertyKey);

			if (pos != -1) {
				if ((previousPropertyPosition != -1) &&
					(pos < previousPropertyPosition)) {

					addMessage(
						fileName,
						StringBundler.concat(
							"Incorrect order of properties: '",
							previousPropertyKey, "' should come after '",
							propertyKey, "', see the order in ",
							SourceUtil.getRootDirName(absolutePath),
							"/portal-impl/src/portal.properties"));

					return;
				}

				if ((previousPropertyPosition == -1) &&
					(previousPropertyKey != null) &&
					(!previousPropertyKey.startsWith("module.framework.") ||
					 (pos < _getLastModuleFrameworkPos(
						 portalPropertiesContent)))) {

					addMessage(
						fileName,
						StringBundler.concat(
							"Incorrect order of properties: '",
							previousPropertyKey, "' should come after '",
							propertyKey, "', since '", previousPropertyKey,
							"' is not in ",
							SourceUtil.getRootDirName(absolutePath),
							"/portal-impl/src/portal.properties"));

					return;
				}
			}

			if ((pos == -1) && propertyKey.startsWith("module.framework.") &&
				(previousPropertyKey != null)) {

				if (previousPropertyPosition != -1) {
					if (!previousPropertyKey.startsWith("module.framework.") &&
						(previousPropertyPosition > _getLastModuleFrameworkPos(
							portalPropertiesContent))) {

						addMessage(
							fileName,
							StringBundler.concat(
								"Incorrect order of properties: '",
								previousPropertyKey, "' should come after '",
								propertyKey, "'"));

						return;
					}
				}
				else if (!previousPropertyKey.startsWith("module.framework.") ||
						 (previousPropertyKey.startsWith("module.framework.") &&
						  (propertyKey.compareTo(previousPropertyKey) < 0))) {

					addMessage(
						fileName,
						StringBundler.concat(
							"Incorrect order of properties: '",
							previousPropertyKey, "' should come after '",
							propertyKey, "'"));

					return;
				}
			}

			if ((pos == -1) && (previousPropertyPosition == -1) &&
				(propertyKey != null) &&
				!propertyKey.startsWith("module.framework.") &&
				(previousPropertyKey != null) &&
				!previousPropertyKey.startsWith("module.framework.")) {

				int previousKeyPosInPortalOSGiConfigurationProperties =
					_getPropertyPosition(
						portalOSGiConfigurationPropertiesContent,
						previousPropertyKey);
				int keyPosInPortalOSGiConfigurationProperties =
					_getPropertyPosition(
						portalOSGiConfigurationPropertiesContent, propertyKey);

				if ((previousKeyPosInPortalOSGiConfigurationProperties == -1) &&
					(keyPosInPortalOSGiConfigurationProperties == -1) &&
					(propertyKey.compareTo(previousPropertyKey) < 0)) {

					addMessage(
						fileName,
						StringBundler.concat(
							"Incorrect order of properties: '",
							previousPropertyKey, "' should come after '",
							propertyKey, "'"));

					return;
				}
				else if ((previousKeyPosInPortalOSGiConfigurationProperties !=
							-1) &&
						 (keyPosInPortalOSGiConfigurationProperties != -1) &&
						 (previousKeyPosInPortalOSGiConfigurationProperties >
							 keyPosInPortalOSGiConfigurationProperties)) {

					addMessage(
						fileName,
						StringBundler.concat(
							"Incorrect order of properties: '",
							previousPropertyKey, "' should come after '",
							propertyKey, "', see the order in ",
							SourceUtil.getRootDirName(absolutePath),
							"/portal-impl/src/portal-osgi-configuration.",
							"properties"));

					return;
				}

				if ((previousKeyPosInPortalOSGiConfigurationProperties != -1) &&
					(keyPosInPortalOSGiConfigurationProperties == -1)) {

					addMessage(
						fileName,
						StringBundler.concat(
							"Incorrect order of properties: '", propertyKey,
							"' should come before '", previousPropertyKey,
							"', since '", propertyKey, "' is not in ",
							SourceUtil.getRootDirName(absolutePath),
							"/portal-impl/src/portal-osgi-configuration.",
							"properties"));

					return;
				}
			}

			previousPropertyKey = propertyKey;
			previousPropertyPosition = pos;
		}
	}

	private String _formatPortalProperties(String absolutePath, String content)
		throws IOException {

		List<String> allowedSingleLinePropertyKeys = getAttributeValues(
			_ALLOWED_SINGLE_LINE_PROPERTY_KEYS, absolutePath);

		StringBundler sb = new StringBundler();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				if (line.matches("    [^# ]+?=[^,]+(,[^ ][^,]+)+")) {
					String propertyKey = StringUtil.extractFirst(
						StringUtil.trimLeading(line), "=");

					if (!propertyKey.contains("regex") &&
						!allowedSingleLinePropertyKeys.contains(propertyKey)) {

						line = line.replaceFirst("=", "=\\\\\n        ");

						line = line.replaceAll(",", ",\\\\\n        ");
					}
				}

				sb.append(line);
				sb.append("\n");
			}
		}

		content = sb.toString();

		if (content.endsWith("\n")) {
			content = content.substring(0, content.length() - 1);
		}

		return content;
	}

	private int _getLastModuleFrameworkPos(String content) {
		int pos = content.lastIndexOf("    module.framework.");

		if (pos == -1) {
			pos = content.lastIndexOf("    #module.framework.");
		}

		return pos;
	}

	private synchronized String _getPortalOSGiConfigurationPropertiesContent(
			String absolutePath)
		throws IOException {

		if (_portalOSGiConfigurationPropertiesContent != null) {
			return _portalOSGiConfigurationPropertiesContent;
		}

		if (isPortalSource() || isSubrepository()) {
			_portalOSGiConfigurationPropertiesContent = getPortalContent(
				"portal-impl/src/portal-osgi-configuration.properties",
				absolutePath);

			if (_portalOSGiConfigurationPropertiesContent == null) {
				_portalOSGiConfigurationPropertiesContent = StringPool.BLANK;
			}
		}

		return _portalOSGiConfigurationPropertiesContent;
	}

	private synchronized String _getPortalPropertiesContent(String absolutePath)
		throws IOException {

		if (_portalPropertiesContent != null) {
			return _portalPropertiesContent;
		}

		if (isPortalSource() || isSubrepository()) {
			_portalPropertiesContent = getPortalContent(
				"portal-impl/src/portal.properties", absolutePath);

			if (_portalPropertiesContent == null) {
				_portalPropertiesContent = StringPool.BLANK;
			}

			return _portalPropertiesContent;
		}

		ClassLoader classLoader =
			PropertiesSourceProcessor.class.getClassLoader();

		URL url = classLoader.getResource("portal.properties");

		if (url != null) {
			_portalPropertiesContent = IOUtils.toString(url);
		}
		else {
			_portalPropertiesContent = StringPool.BLANK;
		}

		return _portalPropertiesContent;
	}

	private String _getPropertyKey(String line) {
		String trimmedLine = line.trim();

		if (Validator.isNull(trimmedLine) ||
			trimmedLine.startsWith(StringPool.POUND)) {

			return null;
		}

		int x = trimmedLine.indexOf(CharPool.EQUAL);

		if (x == -1) {
			return null;
		}

		return trimmedLine.substring(0, x);
	}

	private int _getPropertyPosition(String content, String propertyKey) {
		int pos = content.indexOf(StringPool.FOUR_SPACES + propertyKey);

		if (pos == -1) {
			pos = content.indexOf(
				StringPool.FOUR_SPACES + StringPool.POUND + propertyKey);
		}

		return pos;
	}

	private static final String _ALLOWED_SINGLE_LINE_PROPERTY_KEYS =
		"allowedSingleLinePropertyKeys";

	private String _portalOSGiConfigurationPropertiesContent;
	private String _portalPropertiesContent;

}