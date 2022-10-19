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

/**
 * @author Seiphon Wang
 */
public class LanguageSecurityScanArges {

	public static final String BASE_DIR_NAME = "./";
	public static final boolean SCAN_ALL = false;
	public static final String OUTPUT_KEY_SANTIZED_FILES =
		"language.security.scan.santized.files";

	public String getBaseDirName() {
		return _baseDirName;
	}

	public boolean isScanAll() {
		return _scanAll;
	}

	public void setBaseDirName(String baseDirName) {
		_baseDirName = baseDirName;
	}

	public void setScanAll(boolean scanAll) {
		_scanAll = scanAll;
	}

	private String _baseDirName = BASE_DIR_NAME;

	private boolean _scanAll = SCAN_ALL;

}