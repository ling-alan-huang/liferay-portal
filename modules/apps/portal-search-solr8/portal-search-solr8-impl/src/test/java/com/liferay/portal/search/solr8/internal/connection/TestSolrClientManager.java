/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal.connection;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.search.solr8.configuration.SolrConfiguration;
import com.liferay.portal.search.solr8.internal.http.BasicAuthPoolingHttpClientFactory;

import java.util.Collections;
import java.util.Map;

/**
 * @author Miguel Angelo Caldas Gallindo
 */
public class TestSolrClientManager extends SolrClientManager {

	public TestSolrClientManager(Map<String, Object> configurationProperties)
		throws Exception {

		BasicAuthPoolingHttpClientFactory httpClientFactory =
			new BasicAuthPoolingHttpClientFactory() {
				{
					activate(Collections.<String, Object>emptyMap());
				}
			};

		SolrConfiguration solrConfiguration =
			ConfigurableUtil.createConfigurable(
				SolrConfiguration.class, configurationProperties);

		setHttpClientFactory(
			httpClientFactory,
			Collections.singletonMap(
				"type", (Object)solrConfiguration.authenticationMode()));

		ReplicatedSolrClientFactory replicatedSolrClientFactory =
			new ReplicatedSolrClientFactory();

		replicatedSolrClientFactory.activate(configurationProperties);

		setSolrClientFactory(
			replicatedSolrClientFactory,
			Collections.singletonMap(
				"type", (Object)solrConfiguration.clientType()));

		activate(configurationProperties);
	}

}