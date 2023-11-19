/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.dom4j.Element;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.java.parser.JavaExpression;
import com.liferay.portal.tools.java.parser.JavaSimpleValue;
import com.liferay.portal.tools.java.parser.Position;
import com.liferay.source.formatter.processor.PropertiesSourceProcessor;

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

			content = _sortPortalProperties(absolutePath, content);

			content = _formatPortalProperties(absolutePath, content);
		}

		return content;
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

	private synchronized String _getPortalPropertiesContent(String absolutePath)
		throws IOException {

		if (_portalPortalPropertiesContent != null) {
			return _portalPortalPropertiesContent;
		}

		if (isPortalSource() || isSubrepository()) {
			_portalPortalPropertiesContent = getPortalContent(
				"portal-impl/src/portal.properties", absolutePath);

			if (_portalPortalPropertiesContent == null) {
				_portalPortalPropertiesContent = StringPool.BLANK;
			}

			return _portalPortalPropertiesContent;
		}

		ClassLoader classLoader =
			PropertiesSourceProcessor.class.getClassLoader();

		URL url = classLoader.getResource("portal.properties");

		if (url != null) {
			_portalPortalPropertiesContent = IOUtils.toString(url);
		}
		else {
			_portalPortalPropertiesContent = StringPool.BLANK;
		}

		return _portalPortalPropertiesContent;
	}

	private String _getPropertyCluster(String content, int lineNumber) {
		StringBundler sb = new StringBundler();

		while (true) {
			String line = getLine(content, lineNumber);

			if (Validator.isNull(line)) {
				sb.setIndex(sb.index() - 1);

				return sb.toString();
			}

			sb.append(line);
			sb.append("\n");

			lineNumber++;
		}
	}

	private String _sortPortalProperties(
		String content, int lineNumber, Collection<Integer> positions,
		Map<Integer, Collection<Integer>> propertyClusterPositionsMap) {

		if (propertyClusterPositionsMap.isEmpty()) {
			return content;
		}

		outerLoop:
		for (Map.Entry<Integer, Collection<Integer>> entry :
				propertyClusterPositionsMap.entrySet()) {

			for (int curPosition : entry.getValue()) {
				for (int position : positions) {
					if (curPosition <= position) {
						continue outerLoop;
					}
				}

				int previousLineNumber = entry.getKey();

				String previousPropertyCluster = _getPropertyCluster(
					content, previousLineNumber);

				String propertyCluster = _getPropertyCluster(
					content, lineNumber);

				content = StringUtil.replaceFirst(
					content, propertyCluster, previousPropertyCluster,
					getLineStartPos(content, lineNumber) - 1);
				content = StringUtil.replaceFirst(
					content, previousPropertyCluster, propertyCluster,
					getLineStartPos(content, previousLineNumber) - 1);

				return content;
			}
		}

		return content;
	}

	private String _sortPortalProperties(
		String content, int lineNumber, int pos,
		Map<Integer, Integer> propertyPositionsMap) {

		for (Map.Entry<Integer, Integer> entry :
				propertyPositionsMap.entrySet()) {

			int curPos = entry.getValue();

			if (curPos <= pos) {
				continue;
			}

			int curLineNumber = entry.getKey();

			String curProperty = getLine(content, curLineNumber);

			String property = getLine(content, lineNumber);

			content = StringUtil.replaceFirst(
				content, property, curProperty,
				getLineStartPos(content, lineNumber) - 1);
			content = StringUtil.replaceFirst(
				content, curProperty, property,
				getLineStartPos(content, curLineNumber) - 1);

			return content;
		}

		return content;
	}

	private String _sortPortalProperties(String absolutePath, String content)
		throws IOException {


		////
		if (absolutePath.endsWith("/portal-impl/src/portal.properties")) {
			return content;
		}

		Map<String, List<String>> propertiesMap = new HashMap<>();

		try (FileReader fileReader = new FileReader(new File(absolutePath));
			UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(fileReader)) {

			String key = null;
			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				line = line.trim();

				if (Validator.isNull(line) || line.startsWith(StringPool.POUND)) {
					continue;
				}

				if (line.indexOf('=') >= 0) {
					key = line.substring(0, line.indexOf('='));

					String value = line.substring(line.indexOf('=') + 1);

					if (!Objects.isNull(value) && !value.equals("\\")) {
						List<String> set = propertiesMap.get(key);

						if (set == null) {
							set = new ArrayList<>();
						}

						set.add(value);

						propertiesMap.put(key, set);
					}
				}
				else {
					String value = line;

					if (value.endsWith(",\\")) {
						value = value.substring(0, value.length() - 2);
					}

					if (key == null) {
						return content;
					}

					List<String> set = propertiesMap.get(key);

					if (set == null) {
						set = new ArrayList<>();
					}

					set.add(value);

					propertiesMap.put(key, set);
				}
			}
		}
		
		String portalPropertiesContent = _getPortalPropertiesContent(
				absolutePath);

		Properties portalProperties = new Properties();

		portalProperties.load(new StringReader(portalPropertiesContent));
		
		Map<String, List<String>> portalOSGiEnvironmentPropertiesMap = new TreeMap<>(new NaturalOrderStringComparator());
		Map<String, List<String>> portalPropertiesMap = new TreeMap<>(new NaturalOrderStringComparator());

		Set<Map.Entry<String, List<String>>> set = propertiesMap.entrySet();

		Iterator<Map.Entry<String, List<String>>> iterator = set.iterator();
		

		while(iterator.hasNext()) {
			
			Entry<String, List<String>> properties = iterator.next();
			
			String propertyKey = properties.getKey();
			
//			int pos = _getPortalPropertiesPosition(portalPropertiesContent, propertyName);
	
			if (portalProperties.containsKey(propertyKey) || propertyKey.startsWith("module.framework.")) {
				portalPropertiesMap.put(propertyKey, propertiesMap.get(propertyKey));
				iterator.remove();
			}
			else if (propertyKey.startsWith("configuration.override.com.liferay.")) {
				portalOSGiEnvironmentPropertiesMap.put(propertyKey, propertiesMap.get(propertyKey));
				iterator.remove();
			}
		}
		
		
		////

//		String portalPortalPropertiesContent = _getPortalPropertiesContent(
//			absolutePath);

		return content;
	}

	private class PortalPropertiesComparator extends NaturalOrderStringComparator {

		public PortalPropertiesComparator(String portalPropertiesContent) {
			this._portalPropertiesContent = portalPropertiesContent;
			
			lastModuleFrameworkPosiston = portalPropertiesContent.lastIndexOf("    module.framework.");

			if (lastModuleFrameworkPosiston == -1) {
				lastModuleFrameworkPosiston = portalPropertiesContent.lastIndexOf("    #module.framework.");
			}
			
		}
		
		@Override
		public int compare(String propertyKey1, String propertyKey2) {
			
			int propertyKey1Posiston = _getPortalPropertiesPosition(
					_portalPropertiesContent, propertyKey1);
			int propertyKey2Posiston = _getPortalPropertiesPosition(
					_portalPropertiesContent, propertyKey2);
			
			if (propertyKey1Posiston != -1 && propertyKey2Posiston != -1) {
					return propertyKey1Posiston - propertyKey2Posiston;
			}
			
			if (propertyKey1Posiston == -1) {
				if (propertyKey2.startsWith("module.framework.")) {
					return 1;
				}
				
				
			}
			return 0;
		}

		private int _getPortalPropertiesPosition(String content, String propertyKey) {
			int pos = content.indexOf(StringPool.FOUR_SPACES + propertyKey);
			
			if (pos == -1) {
				pos = content.indexOf(
						StringPool.FOUR_SPACES + StringPool.POUND + propertyKey);
			}
			
			return pos;
		}
	
		
		private final String _portalPropertiesContent;
		private int lastModuleFrameworkPosiston;
	}

	private static final String _ALLOWED_SINGLE_LINE_PROPERTY_KEYS =
		"allowedSingleLinePropertyKeys";

	private String _portalPortalPropertiesContent;

}