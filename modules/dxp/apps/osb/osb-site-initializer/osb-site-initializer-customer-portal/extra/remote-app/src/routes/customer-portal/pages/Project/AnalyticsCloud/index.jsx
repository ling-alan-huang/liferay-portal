/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useState} from 'react';
import {useAppPropertiesContext} from '../../../../../common/contexts/AppPropertiesContext';
import {Liferay} from '../../../../../common/services/liferay';
import {getAnalyticsCloudWorkspace} from '../../../../../common/services/liferay/graphql/queries';
import ActivationStatus from '../../../components/ActivationStatus/index';
import {useCustomerPortal} from '../../../context';
import {PRODUCT_TYPES} from '../../../utils/constants';

const AnalyticsCloud = () => {
	const [analyticsCloudWorkspace, setAnalyticsCloudWorkspace] = useState();
	const [{project, subscriptionGroups, userAccount}] = useCustomerPortal();
	const {client} = useAppPropertiesContext();

	useEffect(() => {
		const getAnalyticsCloudData = async () => {
			const {data} = await client.query({
				fetchPolicy: 'network-only',
				query: getAnalyticsCloudWorkspace,
				variables: {
					filter: `accountKey eq '${project.accountKey}'`,
					scopeKey: Liferay.ThemeDisplay.getScopeGroupId(),
				},
			});

			if (data) {
				const items = data.c?.analyticsCloudWorkspaces?.items;

				if (items.length) {
					setAnalyticsCloudWorkspace(items[0]);
				}
			}
		};

		getAnalyticsCloudData();
	}, [client, project]);

	return (
		<div className="mr-4">
			<ActivationStatus.AnalyticsCloud
				analyticsCloudWorkspace={analyticsCloudWorkspace}
				project={project}
				subscriptionGroupAnalyticsCloud={subscriptionGroups.find(
					(subscriptionGroup) =>
						subscriptionGroup.name === PRODUCT_TYPES.analyticsCloud
				)}
				userAccount={userAccount}
			/>
		</div>
	);
};

export default AnalyticsCloud;
