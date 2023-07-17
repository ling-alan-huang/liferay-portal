/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.util;

import com.liferay.petra.lang.CentralizedThreadLocal;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Matija Petanjek
 */
public class ItemIndexThreadLocal {

	public static int get(Object item) {
		Map<Object, Integer> itemIndexMap = _itemIndexMap.get();

		return itemIndexMap.get(item);
	}

	public static void put(Object item, int itemIndex) {
		Map<Object, Integer> itemIndexMap = _itemIndexMap.get();

		itemIndexMap.put(item, itemIndex);
	}

	public static void remove() {
		_itemIndexMap.remove();
	}

	private static final ThreadLocal<Map<Object, Integer>> _itemIndexMap =
		new CentralizedThreadLocal<>(
			ItemIndexThreadLocal.class + "._itemIndexMap", HashMap::new);

}