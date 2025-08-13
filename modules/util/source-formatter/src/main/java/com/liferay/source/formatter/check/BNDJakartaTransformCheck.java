/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import aQute.bnd.header.Attrs;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import aQute.bnd.header.Parameters;

/**
 * @author Alan Huang
 */
public class BNDJakartaTransformCheck extends BaseJakartaTransformCheck {


	private String _replaceProvideCapability(String content, String absolutePath) throws IOException {

		int x = content.indexOf("Provide-Capability:");

		if (x == -1) {
			return content;
		}

		Map<String, String> jakartaTransformOSGiContractsMap =
				_getJakartaTransformOSGiContractsMap();

		Properties properties = new Properties();

		properties.load(new StringReader(content));

		for (Object object : properties.keySet()) {
			String propertyKey = (String)object;

			if (!propertyKey.equals("Provide-Capability")) {
				continue;
			}

			String provideCapability = properties.getProperty(propertyKey);

			Parameters parameters = new Parameters(provideCapability);

			for (Map.Entry<String, Attrs> entry : parameters.entrySet()) {
				String parameterKey = entry.getKey();

				if (!parameterKey.startsWith("osgi.contract")) {
					continue;
				}

				Attrs attrs = entry.getValue();

				String osgiContract = attrs.get("osgi.contract");

				if (osgiContract == null) {
					String filter = attrs.get("filter:");

					if (filter == null) {
						continue;
					}

					osgiContract = filter.replaceFirst(".*osgi.contract=(\\w+).*", "$1");

					if (osgiContract == null) {
						continue;
					}

					String newContract = jakartaTransformOSGiContractsMap.get(osgiContract);

					if (newContract == null) {
						continue;
					}

					String[] values = newContract.split(":");

					String replacement = filter.replaceFirst(osgiContract, values[0]);

					attrs.put("filter:", replacement);

					replacement = filter.replaceFirst("(.+\\(version=)([\\d.]+)(\\).*)", "$1" + values[1] + "$3");

					attrs.put("filter:", replacement);

					continue;

				}

				String newContract = jakartaTransformOSGiContractsMap.get(osgiContract);

				if (newContract == null) {
					continue;
				}

				String[] values = newContract.split(":");

				attrs.put("osgi.contract", values[0]);

				String uses = attrs.get("uses:");

				if (uses != null) {
					attrs.put("version", values[1]);

				}
			}

			properties.setProperty(propertyKey, parameters.toString());

		}

		Map<String, String> newProperties = new TreeMap<>();

		List<String> propertyNames = new ArrayList<>(
				properties.stringPropertyNames());

//		propertyNames.sort(null);

		Collections.sort(propertyNames, new HeaderComparator());

		for (String propertyName : propertyNames) {
			newProperties.put(propertyName, properties.getProperty(propertyName));
		}

		StringBundler sb1 = new StringBundler();

		for (String propertyName : propertyNames) {
			sb1.append(propertyName);
			sb1.append(":");

			Parameters parameters = new Parameters(newProperties.get(propertyName));

			for (Map.Entry<String, Attrs> entry1 : parameters.entrySet()) {
				StringBundler sb2 = new StringBundler();

				String parameterKey = entry1.getKey();

				Attrs attrs = entry1.getValue();

				if (attrs.size() != 0) {

				}

				if (attrs.size() == 0) {
					sb2.append(" ");
					sb2.append(parameterKey);
					sb2.append("\n");
				}
				else {
					for (Map.Entry<String, String> entry2 : attrs.entrySet()) {
						sb2.append("\t\t");
						sb2.append(entry2.getKey());
						sb2.append("=");
						sb2.append(entry2.getValue());
						sb2.append(";\\\n");
					}

				}
				sb2.append(",\\\n");
				int a = 0;
			}
		}

		return content;
	}

	private class HeaderComparator implements Comparator<String> {

		@Override
		public int compare(String header1, String header2) {
			if (header1.startsWith(StringPool.DASH) ^
					header2.startsWith(StringPool.DASH)) {

				return -header1.compareTo(header2);
			}

			String headerName1 = StringUtil.extractFirst(
					header1, StringPool.COLON);
			String headerName2 = StringUtil.extractFirst(
					header2, StringPool.COLON);

			if ((headerName1 != null) && (headerName2 != null)) {
				return headerName1.compareTo(headerName2);
			}

			return header1.compareTo(header2);
		}

	}
	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) throws IOException {

		content = _replaceProvideCapability(content, absolutePath);
		content = replace(content);

		return replaceTaglibURIs(content);
	}

	private synchronized Map<String, String>
	_getJakartaTransformOSGiContractsMap()
			throws IOException {

		if (_jakartaTransformOSGiContractsMap != null) {
			return _jakartaTransformOSGiContractsMap;
		}

		_jakartaTransformOSGiContractsMap = new HashMap<>();

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
				"dependencies/jakarta-transform-osgi-contracts.txt");

		if (inputStream == null) {
			return Collections.emptyMap();
		}

		String[] lines = StringUtil.splitLines(StringUtil.read(inputStream));

		for (String line : lines) {
			String[] parts = line.split("=");

			_jakartaTransformOSGiContractsMap.put(parts[0], parts[1]);
		}

		return _jakartaTransformOSGiContractsMap;
	}
	private Map<String, String> _jakartaTransformOSGiContractsMap;

}