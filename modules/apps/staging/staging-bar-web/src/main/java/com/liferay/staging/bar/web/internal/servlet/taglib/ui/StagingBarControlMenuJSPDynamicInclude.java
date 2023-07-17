/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.bar.web.internal.servlet.taglib.ui;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Chema Balsas
 */
@Component(
	service = {
		DynamicInclude.class, StagingBarControlMenuJSPDynamicInclude.class
	}
)
public class StagingBarControlMenuJSPDynamicInclude
	extends BaseJSPDynamicInclude {

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		try {
			if (!isShow(httpServletRequest)) {
				return;
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		super.include(httpServletRequest, httpServletResponse, key);
	}

	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		Boolean show = (Boolean)httpServletRequest.getAttribute(_SHOW);

		if (show != null) {
			return show;
		}

		show = _isShow(httpServletRequest);

		httpServletRequest.setAttribute(_SHOW, show);

		return show;
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"com.liferay.product.navigation.taglib#/page.jsp#post");
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/entry.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private boolean _isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (layout.isTypeControlPanel() || !themeDisplay.isShowStagingIcon()) {
			return false;
		}

		String layoutMode = ParamUtil.getString(
			httpServletRequest, "p_l_mode", Constants.VIEW);

		if (layoutMode.equals(Constants.EDIT)) {
			return false;
		}

		return true;
	}

	private static final String _SHOW =
		StagingBarControlMenuJSPDynamicInclude.class + "#_SHOW";

	private static final Log _log = LogFactoryUtil.getLog(
		StagingBarControlMenuJSPDynamicInclude.class);

	@Reference(target = "(osgi.web.symbolicname=com.liferay.staging.bar.web)")
	private ServletContext _servletContext;

}