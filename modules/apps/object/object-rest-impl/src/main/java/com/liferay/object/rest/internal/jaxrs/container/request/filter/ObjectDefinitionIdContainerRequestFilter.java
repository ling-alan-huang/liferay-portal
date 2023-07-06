/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.jaxrs.container.request.filter;

import com.liferay.object.model.ObjectDefinition;

import java.util.List;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

/**
 * @author Javier Gamarra
 */
@PreMatching
@Provider
public class ObjectDefinitionIdContainerRequestFilter
	implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext containerRequestContext) {
		UriInfo uriInfo = containerRequestContext.getUriInfo();

		UriBuilder uriBuilder = uriInfo.getRequestUriBuilder();

		MultivaluedMap<String, String> queryParameters =
			uriInfo.getQueryParameters();

		queryParameters.add(
			"objectDefinitionId",
			String.valueOf(_objectDefinition.getObjectDefinitionId()));

		for (Map.Entry<String, List<String>> entry :
				queryParameters.entrySet()) {

			uriBuilder.queryParam(entry.getKey(), entry.getValue());
		}

		uriBuilder.queryParam(
			"taskItemDelegateName", _objectDefinition.getOSGiJaxRsName());

		containerRequestContext.setRequestUri(uriBuilder.build());
	}

	@Context
	private ObjectDefinition _objectDefinition;

}