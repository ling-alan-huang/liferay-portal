/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import MDFClaimActivityDTO from '../../../interfaces/dto/mdfClaimActivityDTO';
import LiferayFile from '../../../interfaces/liferayFile';
import MDFClaimActivity from '../../../interfaces/mdfClaimActivity';

export default function getDTOFromMDFClaimActivity(
	mdfClaimActivity: MDFClaimActivity,
	mdfClaimId?: number,
	listOfQualifiedLeadsDocumentId?: LiferayFile & number,
	companyId?: number
): MDFClaimActivityDTO {
	return {
		currency: mdfClaimActivity.currency,
		listOfQualifiedLeads: listOfQualifiedLeadsDocumentId,
		metrics: mdfClaimActivity.metrics,
		name: mdfClaimActivity.name,
		r_accToMDFClmActs_accountEntryId: companyId,
		r_actToMDFClmActs_c_activityId:
			mdfClaimActivity.r_actToMDFClmActs_c_activityId,
		r_mdfClmToMDFClmActs_c_mdfClaimId: mdfClaimId,
		selected: mdfClaimActivity.selected,
		totalCost: mdfClaimActivity.totalCost,
	};
}
