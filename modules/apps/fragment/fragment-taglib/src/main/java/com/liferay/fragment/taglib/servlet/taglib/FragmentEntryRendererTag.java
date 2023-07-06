/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.taglib.servlet.taglib;

import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryServiceUtil;
import com.liferay.fragment.taglib.internal.servlet.ServletContextUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.taglib.util.IncludeTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author Jürgen Kappler
 */
public class FragmentEntryRendererTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		try {
			_fragmentEntry = FragmentEntryServiceUtil.fetchFragmentEntry(
				_fragmentEntryId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get fragment entry", portalException);
			}

			return SKIP_BODY;
		}

		return super.doStartTag();
	}

	public long getFragmentEntryId() {
		return _fragmentEntryId;
	}

	public void setFragmentEntryId(long fragmentEntryId) {
		_fragmentEntryId = fragmentEntryId;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_fragmentEntry = null;
		_fragmentEntryId = 0;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-fragment:fragment-entry-renderer:fragmentEntry",
			_fragmentEntry);
	}

	private static final String _PAGE = "/fragment_entry_renderer/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentEntryRendererTag.class);

	private FragmentEntry _fragmentEntry;
	private long _fragmentEntryId;

}