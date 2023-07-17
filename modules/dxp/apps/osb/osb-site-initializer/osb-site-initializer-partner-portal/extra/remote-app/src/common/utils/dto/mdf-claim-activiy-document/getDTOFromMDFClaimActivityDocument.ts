/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import MDFClaimActivityDocumentDTO from '../../../interfaces/dto/mdfClaimActivityDocumentDTO';
import LiferayFile from '../../../interfaces/liferayFile';

export function getDTOFromMDFClaimActivityDocument(
	allContentId: LiferayFile & number,
	mdfClaimActivityId?: number,
	companyId?: number
): MDFClaimActivityDocumentDTO {
	return {
		allContents: allContentId,
		r_accToMDFClmActDocs_accountEntryId: companyId,
		r_mdfClmActToMDFActDocs_c_mdfClaimActivityId: mdfClaimActivityId,
	};
}
