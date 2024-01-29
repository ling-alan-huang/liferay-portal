/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.delivery.client.dto.v1_0.Language;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class LanguageResourceTest extends BaseLanguageResourceTestCase {

	@Override
	@Test
	public void testGetAssetLibraryLanguagesPage() throws Exception {
		Long assetLibraryId =
			testGetAssetLibraryLanguagesPage_getAssetLibraryId();

		Page<Language> page = languageResource.getAssetLibraryLanguagesPage(
			assetLibraryId);

		long totalCount = page.getTotalCount();

		Assert.assertEquals(
			_getAvailableLocalesSize(assetLibraryId), totalCount);

		GroupTestUtil.updateDisplaySettings(
			testDepotEntry.getGroupId(),
			Arrays.asList(
				LocaleUtil.US, LocaleUtil.CHINA, LocaleUtil.FRANCE,
				LocaleUtil.SPAIN),
			LocaleUtil.US);

		page = languageResource.getAssetLibraryLanguagesPage(assetLibraryId);

		Language language = page.fetchFirstItem();

		Assert.assertEquals(
			LocaleUtil.US, LocaleUtil.fromLanguageId(language.getId()));
		Assert.assertTrue(language.getMarkedAsDefault());

		Assert.assertEquals(4, totalCount);
		assertValid(page);
	}

	@Override
	@Test
	public void testGetSiteLanguagesPage() throws Exception {
		Long siteId = testGetSiteLanguagesPage_getSiteId();

		Page<Language> page = languageResource.getSiteLanguagesPage(siteId);

		long totalCount = page.getTotalCount();

		Assert.assertEquals(_getAvailableLocalesSize(siteId), totalCount);

		GroupTestUtil.updateDisplaySettings(
			siteId, Arrays.asList(LocaleUtil.US), LocaleUtil.US);

		page = languageResource.getSiteLanguagesPage(siteId);

		Language language = page.fetchFirstItem();

		Assert.assertEquals(
			LocaleUtil.US, LocaleUtil.fromLanguageId(language.getId()));
		Assert.assertTrue(language.getMarkedAsDefault());

		Assert.assertEquals(1, totalCount);
		assertValid(page);
	}

	@Override
	@Test
	public void testGraphQLGetSiteLanguagesPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"languages",
			HashMapBuilder.<String, Object>put(
				"siteKey", "\"" + testGetSiteLanguagesPage_getSiteId() + "\""
			).build(),
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject languagesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/languages");

		Assert.assertEquals(
			_getAvailableLocalesSize(testGetSiteLanguagesPage_getSiteId()),
			languagesJSONObject.get("totalCount"));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"name"};
	}

	private int _getAvailableLocalesSize(Long siteId) {
		Set<Locale> availableLocales = LanguageUtil.getAvailableLocales(siteId);

		return availableLocales.size();
	}

}