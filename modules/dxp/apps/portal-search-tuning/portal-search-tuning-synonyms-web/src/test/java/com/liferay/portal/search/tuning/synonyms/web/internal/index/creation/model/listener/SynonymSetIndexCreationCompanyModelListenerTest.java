/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.index.creation.model.listener;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.BaseSynonymsWebTestCase;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexCreator;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class SynonymSetIndexCreationCompanyModelListenerTest
	extends BaseSynonymsWebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_synonymSetIndexCreationCompanyModelListener =
			new SynonymSetIndexCreationCompanyModelListener();

		ReflectionTestUtil.setFieldValue(
			_synonymSetIndexCreationCompanyModelListener,
			"_searchEngineInformation", _searchEngineInformation);
		ReflectionTestUtil.setFieldValue(
			_synonymSetIndexCreationCompanyModelListener,
			"_synonymSetIndexCreator", _synonymSetIndexCreator);
		ReflectionTestUtil.setFieldValue(
			_synonymSetIndexCreationCompanyModelListener,
			"_synonymSetIndexNameBuilder", _synonymSetIndexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			_synonymSetIndexCreationCompanyModelListener,
			"_synonymSetIndexReader", synonymSetIndexReader);
	}

	@Test
	public void testOnAfterCreateWithIndexReaderFalse() {
		setUpSynonymSetIndexReader(false);

		_synonymSetIndexCreationCompanyModelListener.onAfterCreate(
			Mockito.mock(Company.class));

		Mockito.verify(
			_synonymSetIndexCreator, Mockito.times(1)
		).create(
			Mockito.any()
		);
	}

	@Test
	public void testOnAfterCreateWithIndexReaderTrue() {
		setUpSynonymSetIndexReader(true);

		_synonymSetIndexCreationCompanyModelListener.onAfterCreate(
			Mockito.mock(Company.class));

		Mockito.verify(
			_synonymSetIndexCreator, Mockito.never()
		).create(
			Mockito.any()
		);
	}

	@Test
	public void testOnBeforeRemoveWithIndexReaderFalse() {
		setUpSynonymSetIndexReader(false);

		_synonymSetIndexCreationCompanyModelListener.onBeforeRemove(
			Mockito.mock(Company.class));

		Mockito.verify(
			_synonymSetIndexCreator, Mockito.never()
		).delete(
			Mockito.any()
		);

		setUpSynonymSetIndexReader(true);
	}

	@Test
	public void testOnBeforeRemoveWithIndexReaderTrue() {
		setUpSynonymSetIndexReader(true);

		_synonymSetIndexCreationCompanyModelListener.onBeforeRemove(
			Mockito.mock(Company.class));

		Mockito.verify(
			_synonymSetIndexCreator, Mockito.times(1)
		).delete(
			Mockito.any()
		);
	}

	private final SearchEngineInformation _searchEngineInformation =
		Mockito.mock(SearchEngineInformation.class);
	private SynonymSetIndexCreationCompanyModelListener
		_synonymSetIndexCreationCompanyModelListener;
	private final SynonymSetIndexCreator _synonymSetIndexCreator = Mockito.mock(
		SynonymSetIndexCreator.class);
	private final SynonymSetIndexNameBuilder _synonymSetIndexNameBuilder =
		Mockito.mock(SynonymSetIndexNameBuilder.class);

}