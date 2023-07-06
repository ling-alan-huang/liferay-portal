/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.installer;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Igor Beslic
 */
public interface BatchEngineZipUnit<T> {

	public T getBatchEngineConfiguration(Class<T> clazz) throws IOException;

	public InputStream getConfigurationInputStream() throws IOException;

	public String getDataFileName();

	public InputStream getDataInputStream() throws IOException;

	public String getZipFileName();

	public boolean isValid();

}