/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.internal.jaxrs.context.provider;

import com.liferay.headless.builder.application.APIApplication;
import com.liferay.headless.builder.application.provider.APIApplicationProvider;
import com.liferay.headless.builder.internal.util.PathUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Portal;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;

/**
 * @author Luis Miguel Barcos
 */
@Provider
public class APIApplicationContextProvider
	implements ContextProvider<APIApplication> {

	public APIApplicationContextProvider(
		APIApplicationProvider apiApplicationProvider, Portal portal) {

		_apiApplicationProvider = apiApplicationProvider;
		_portal = portal;
	}

	@Override
	public APIApplication createContext(Message message) {
		try {
			HttpServletRequest httpServletRequest =
				(HttpServletRequest)message.getContextualProperty(
					"HTTP.REQUEST");

			return _apiApplicationProvider.fetchAPIApplication(
				PathUtil.sanitize(httpServletRequest.getContextPath()),
				_portal.getCompanyId(httpServletRequest));
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new NotFoundException(exception.getMessage());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		APIApplicationContextProvider.class);

	private final APIApplicationProvider _apiApplicationProvider;
	private final Portal _portal;

}