/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useMemo} from 'react';
import {useGetListTypeDefinitions} from '../../../../../../../../../../../../common/services/liferay/graphql/list-type-definitions';
import {LIST_TYPES} from '../../../../../../../../../../utils/constants';

const listTypePrimaryRegions = LIST_TYPES.lxcPrimaryRegion;

export default function useGetPrimaryRegionList() {
	const {data} = useGetListTypeDefinitions({
		filter: `name eq '${listTypePrimaryRegions}'`,
	});

	const items = data?.listTypeDefinitions?.items[0].listTypeEntries;

	const primaryRegionList = useMemo(
		() => items?.map(({name}) => ({label: name, value: name})) || [],
		[items]
	);

	return primaryRegionList;
}
