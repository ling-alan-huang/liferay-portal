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

import java.net.URL;
import java.sql.Time;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;

public class LanguageSecurityScan {

	public static void main(String[] args) {
		ClassLoader classLoader = LanguageSecurityScan.class.getClassLoader();

		URL antiSamyUrl = classLoader.getResource("antisamy-liferay.xml");

		String antisamyPath = antiSamyUrl.getFile();

		try {
			Policy policy = Policy.getInstance(antisamyPath);

			AntiSamy antisamy = new AntiSamy();

			CleanResults cleanResults = antisamy.scan(
				"<script>alert()</script>", policy);

			String test = cleanResults.getCleanHTML();

			double scanTime = cleanResults.getScanTime();

			System.out.println(test);
		}
		catch (Exception e) {
		}
	}

}