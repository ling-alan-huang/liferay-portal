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

package com.liferay.source.formatter.check;

/**
 * @author Alan Huang
 */
public class ArchiveExpansionCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if ((fileName.contains("/modules/") ||
			 fileName.contains("/portal-web/")) &&
			(fileName.contains("/test/") || fileName.contains("/tests/")) &&
			fileName.contains("/dependencies/") &&
			!fileName.contains("/testIntegration/")) {

			addMessage(
				fileName,
				"Do not add archive files for tests, they must be expanded");
		}

		return content;
	}

}