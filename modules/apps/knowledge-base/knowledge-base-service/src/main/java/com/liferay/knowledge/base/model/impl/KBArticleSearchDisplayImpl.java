/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.model.impl;

import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBArticleSearchDisplay;

import java.util.List;

/**
 * @author Peter Shin
 */
public class KBArticleSearchDisplayImpl implements KBArticleSearchDisplay {

	public KBArticleSearchDisplayImpl(
		List<KBArticle> kbArticles, int total, int[] curStartValues) {

		_kbArticles = kbArticles;
		_total = total;
		_curStartValues = curStartValues;
	}

	@Override
	public int[] getCurStartValues() {
		return _curStartValues;
	}

	@Override
	public List<KBArticle> getKBArticles() {
		return _kbArticles;
	}

	@Override
	public int getTotal() {
		return _total;
	}

	@Override
	public void setCurStartValues(int[] curStartValues) {
		_curStartValues = curStartValues;
	}

	@Override
	public void setKBArticles(List<KBArticle> kbArticles) {
		_kbArticles = kbArticles;
	}

	@Override
	public void setTotal(int total) {
		_total = total;
	}

	private int[] _curStartValues;
	private List<KBArticle> _kbArticles;
	private int _total;

}