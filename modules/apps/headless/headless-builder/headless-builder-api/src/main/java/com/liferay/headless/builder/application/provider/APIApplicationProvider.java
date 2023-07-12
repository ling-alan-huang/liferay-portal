/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.application.provider;

import com.liferay.headless.builder.application.APIApplication;

/**
 * @author Alejandro Tardín
 */
public interface APIApplicationProvider {

	public APIApplication fetchAPIApplication(String baseURL, long companyId)
		throws Exception;

}