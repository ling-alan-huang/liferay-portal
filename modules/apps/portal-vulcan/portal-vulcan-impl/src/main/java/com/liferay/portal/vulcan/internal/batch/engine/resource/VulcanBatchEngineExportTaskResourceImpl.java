/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.batch.engine.resource;

import com.liferay.headless.batch.engine.resource.v1_0.ExportTaskResource;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(service = VulcanBatchEngineExportTaskResource.class)
public class VulcanBatchEngineExportTaskResourceImpl
	implements VulcanBatchEngineExportTaskResource {

	@Override
	public Object postExportTask(
			String name, String callbackURL, String contentType,
			String fieldNames)
		throws Exception {

		_exportTaskResource.setContextAcceptLanguage(_contextAcceptLanguage);
		_exportTaskResource.setContextCompany(_contextCompany);
		_exportTaskResource.setContextHttpServletRequest(
			_contextHttpServletRequest);
		_exportTaskResource.setContextUriInfo(_contextUriInfo);
		_exportTaskResource.setContextUser(_contextUser);
		_exportTaskResource.setGroupLocalService(_groupLocalService);

		return _exportTaskResource.postExportTask(
			name, contentType, callbackURL,
			_getQueryParameterValue("externalReferenceCode"), fieldNames,
			_getQueryParameterValue("taskItemDelegateName"));
	}

	@Override
	public void setContextAcceptLanguage(AcceptLanguage contextAcceptLanguage) {
		_contextAcceptLanguage = contextAcceptLanguage;
	}

	@Override
	public void setContextCompany(Company contextCompany) {
		_contextCompany = contextCompany;
	}

	@Override
	public void setContextHttpServletRequest(
		HttpServletRequest contextHttpServletRequest) {

		_contextHttpServletRequest = contextHttpServletRequest;
	}

	@Override
	public void setContextUriInfo(UriInfo contextUriInfo) {
		_contextUriInfo = contextUriInfo;
	}

	@Override
	public void setContextUser(User contextUser) {
		_contextUser = contextUser;
	}

	@Override
	public void setGroupLocalService(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	private String _getQueryParameterValue(String queryParameterName) {
		MultivaluedMap<String, String> queryParameters =
			_contextUriInfo.getQueryParameters();

		return queryParameters.getFirst(queryParameterName);
	}

	private AcceptLanguage _contextAcceptLanguage;
	private Company _contextCompany;
	private HttpServletRequest _contextHttpServletRequest;
	private UriInfo _contextUriInfo;
	private User _contextUser;

	@Reference
	private ExportTaskResource _exportTaskResource;

	private GroupLocalService _groupLocalService;

}