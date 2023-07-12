/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.index.creation.model.listener;

import com.liferay.osgi.util.service.Snapshot;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingIndexCreator;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingIndexReader;
import com.liferay.portal.search.tuning.rankings.web.internal.index.name.RankingIndexName;
import com.liferay.portal.search.tuning.rankings.web.internal.index.name.RankingIndexNameBuilder;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(service = ModelListener.class)
public class RankingIndexCreationCompanyModelListener
	extends BaseModelListener<Company> {

	@Override
	public void onAfterCreate(Company company) {
		SearchEngineInformation searchEngineInformation =
			_searchEngineInformationSnapshot.get();

		if (Objects.equals(searchEngineInformation.getVendorString(), "Solr")) {
			return;
		}

		RankingIndexName rankingIndexName = _getRankingIndexName(company);

		if (_rankingIndexReader.isExists(rankingIndexName)) {
			return;
		}

		_rankingIndexCreator.create(rankingIndexName);
	}

	@Override
	public void onBeforeRemove(Company company) {
		SearchEngineInformation searchEngineInformation =
			_searchEngineInformationSnapshot.get();

		if (Objects.equals(searchEngineInformation.getVendorString(), "Solr")) {
			return;
		}

		RankingIndexName rankingIndexName = _getRankingIndexName(company);

		if (!_rankingIndexReader.isExists(rankingIndexName)) {
			return;
		}

		_rankingIndexCreator.delete(rankingIndexName);
	}

	private RankingIndexName _getRankingIndexName(Company company) {
		return _rankingIndexNameBuilder.getRankingIndexName(
			company.getCompanyId());
	}

	private static final Snapshot<SearchEngineInformation>
		_searchEngineInformationSnapshot = new Snapshot<>(
			RankingIndexCreationCompanyModelListener.class,
			SearchEngineInformation.class, null, true);

	@Reference
	private RankingIndexCreator _rankingIndexCreator;

	@Reference
	private RankingIndexNameBuilder _rankingIndexNameBuilder;

	@Reference
	private RankingIndexReader _rankingIndexReader;

}