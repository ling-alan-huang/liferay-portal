/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Liferay} from '../..';
import LiferayFile from '../../../../interfaces/liferayFile';
import MDFClaimBudget from '../../../../interfaces/mdfClaimBudget';
import getDTOFromMDFClaimBudget from '../../../../utils/dto/mdf-claim-budget/getDTOFromMDFClaimBudget';
import {LiferayAPIs} from '../../common/enums/apis';
import liferayFetcher from '../../common/utils/fetcher';

export default async function updateMDFClaimActivityBudget(
	mdfClaimBudget: MDFClaimBudget,
	mdfClaimActivityId?: number,
	mdfClaimBudgetId?: number,
	companyId?: number,
	budgetInvoiceId?: LiferayFile & number
) {
	return await liferayFetcher.put(
		`/o/${LiferayAPIs.OBJECT}/mdfclaimbudgets/${mdfClaimBudgetId}`,
		Liferay.authToken,
		getDTOFromMDFClaimBudget(
			mdfClaimBudget,
			mdfClaimActivityId,
			companyId,
			budgetInvoiceId
		)
	);
}
