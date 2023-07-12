/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {createContext, useContext} from 'react';

export const AppPropertiesContext = createContext({
	articleAccountSupportURL: '',
	articleDeployingActivationKeysURL: '',
	articleGettingStartedWithLiferayEnterpriseSearchURL: '',
	articleWhatIsMyInstanceSizingValueURL: '',
	client: null,
	featureFlags: [],
	gravatarAPI: '',
	importDate: null,
	oktaSessionAPI: '',
	provisioningServerAPI: '',
	submitSupportTicketURL: '',
});

export function useAppPropertiesContext() {
	return useContext(AppPropertiesContext);
}
