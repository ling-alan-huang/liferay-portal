/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import MDFClaimActivityDocument from '../mdfClaimActivityDocument';

export default interface MDFClaimActivityDocumentDTO
	extends MDFClaimActivityDocument {
	r_accToMDFClmActDocs_accountEntryId?: number;
	r_mdfClmActToMDFActDocs_c_mdfClaimActivityId?: number;
}
