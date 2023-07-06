/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

import {openConfirmModal} from 'frontend-js-web';

export default function propsTransformer({portletNamespace, ...otherProps}) {
	const deleteAssetDisplayPageEntry = (itemData) => {
		openConfirmModal({
			message: itemData.deleteAssetDisplayPageEntryMessage,
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					const form = document.getElementById(
						`${portletNamespace}fm`
					);

					if (form) {
						submitForm(
							form,
							itemData?.deleteAssetDisplayPageEntryURL
						);
					}
				}
			},
		});
	};
	const updateAssetDisplayPageEntry = (itemData) => {
		openConfirmModal({
			message: Liferay.Language.get(
				'are-you-sure-you-do-not-want-to-set-a-display-page-template-for-this'
			),
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					const form = document.getElementById(
						`${portletNamespace}fm`
					);

					if (form) {
						submitForm(
							form,
							itemData?.updateAssetDisplayPageEntryURL
						);
					}
				}
			},
		});
	};

	return {
		...otherProps,
		onActionButtonClick(event, {item}) {
			const data = item?.data;

			const action = data?.action;

			if (action === 'deleteAssetDisplayPageEntry') {
				deleteAssetDisplayPageEntry(data);
			}
			else if (action === 'updateAssetDisplayPageEntry') {
				updateAssetDisplayPageEntry(data);
			}
		},
	};
}
