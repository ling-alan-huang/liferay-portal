/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import aQute.bnd.header.Attrs;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aQute.bnd.header.Parameters;

/**
 * @author Alan Huang
 */
public class BNDJakartaTransformCheck extends BaseJakartaTransformCheck {

	private String _replaceProvideCapability(String content, String absolutePath) throws IOException {

		int x = content.indexOf("Provide-Capability");

		if (x == -1) {
			return content;
		}

		Map<String, String> jakartaTransformOSGiContractsMap =
				_getJakartaTransformOSGiContractsMap();

		File file = new File(absolutePath);

		Properties properties = new Properties();

		properties.load(new FileInputStream(file));

		String provideCapability = properties.getProperty("Provide-Capability");

		if (provideCapability == null) {
			return content;
		}

		List<String> lines = _splitLines(provideCapability);
		if (!provideCapability.contains("osgi.contract")) {
			return content;
		}

//		Matcher matcher = _osgiContractPattern.matcher(capability);
//
//		if (matcher.find()) {
//
//		}


		Parameters parameters = new Parameters(provideCapability);

		// Modify attributes (example: set foo=bar on all capabilities)
		for (Map.Entry<String, Attrs> entry : parameters.entrySet()) {
			Attrs attrs = entry.getValue();
			attrs.put("foo", "bar"); // Replace or add attribute
			// You can also remove or update other attributes here
		}

		return content;
	}

	private List<String> _splitLines(String value) {
		List<String> lines = new ArrayList<>();

		int previousIndex = 0;
		int index = 0;

		while ((index = value.indexOf(',', index)) != -1) {
			index++;

			if ((_count(value, 0, index, '"') % 2) == 1) {
				continue;
			}

			lines.add(value.substring(previousIndex, index));

			previousIndex = index;
		}

		lines.add(value.substring(previousIndex));

		return lines;
	}
	private int _count(String s, int start, int end, char c) {
		if ((s == null) || s.isEmpty() || ((end - start) < 1)) {
			return 0;
		}

		int count = 0;

		int pos = start;

		while ((pos < end) && ((pos = s.indexOf(c, pos)) != -1)) {
			if (pos < end) {
				count++;
			}

			pos++;
		}

		return count;
	}


	private static final Pattern _osgiContractPattern = Pattern.compile(
			"osgi.contract=\"(\\w+)\"");

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