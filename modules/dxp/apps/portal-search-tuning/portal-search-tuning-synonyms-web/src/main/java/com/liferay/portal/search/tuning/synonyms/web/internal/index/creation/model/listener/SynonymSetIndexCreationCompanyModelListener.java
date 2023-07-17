/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.index.creation.model.listener;

import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexName;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexCreator;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexReader;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(service = ModelListener.class)
public class SynonymSetIndexCreationCompanyModelListener
	extends BaseModelListener<Company> {

	@Override
	public void onAfterCreate(Company company) {
		if (Objects.equals(
				_searchEngineInformation.getVendorString(), "Solr")) {

			return;
		}

		SynonymSetIndexName synonymSetIndexName =
			_synonymSetIndexNameBuilder.getSynonymSetIndexName(
				company.getCompanyId());

		if (_synonymSetIndexReader.isExists(synonymSetIndexName)) {
			return;
		}

		_synonymSetIndexCreator.create(synonymSetIndexName);
	}

	@Override
	public void onBeforeRemove(Company company) {
		if (Objects.equals(
				_searchEngineInformation.getVendorString(), "Solr")) {

			return;
		}

		SynonymSetIndexName synonymSetIndexName =
			_synonymSetIndexNameBuilder.getSynonymSetIndexName(
				company.getCompanyId());

		if (!_synonymSetIndexReader.isExists(synonymSetIndexName)) {
			return;
		}

		_synonymSetIndexCreator.delete(synonymSetIndexName);
	}

	@Reference
	private SearchEngineInformation _searchEngineInformation;

	@Reference
	private SynonymSetIndexCreator _synonymSetIndexCreator;

	@Reference
	private SynonymSetIndexNameBuilder _synonymSetIndexNameBuilder;

	@Reference
	private SynonymSetIndexReader _synonymSetIndexReader;

}