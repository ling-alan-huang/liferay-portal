/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import mdfClaimDTO from '../../../common/interfaces/dto/mdfClaimDTO';
import MDFRequestDTO from '../../../common/interfaces/dto/mdfRequestDTO';
import LiferayFile from '../../../common/interfaces/liferayFile';
import MDFClaim from '../../../common/interfaces/mdfClaim';
import {ResourceName} from '../../../common/services/liferay/object/enum/resourceName';
import createMDFClaim from '../../../common/services/liferay/object/mdf-claim/createMDFClaim';
import updateMDFClaim from '../../../common/services/liferay/object/mdf-claim/updateMDFClaim';
import updateMDFClaimSF from '../../../common/services/liferay/object/mdf-claim/updateMDFClaimSF';

export default async function createMDFClaimProxyAPI(
	mdfClaim: MDFClaim,
	mdfRequest: MDFRequestDTO
) {
	let dtoMDFClaimSFResponse: mdfClaimDTO | undefined = undefined;

	if (
		mdfClaim.externalReferenceCode &&
		mdfClaim.externalReferenceCodeSF &&
		mdfClaim.externalReferenceCode === mdfClaim.externalReferenceCodeSF
	) {
		dtoMDFClaimSFResponse = await updateMDFClaimSF(
			ResourceName.MDF_CLAIM_SALESFORCE,
			mdfClaim,
			mdfRequest,
			mdfClaim.reimbursementInvoice?.id as LiferayFile & number,
			mdfClaim.externalReferenceCode
		);
	}
	else {
		dtoMDFClaimSFResponse = await createMDFClaim(
			ResourceName.MDF_CLAIM_SALESFORCE,
			mdfClaim,
			mdfRequest
		);
	}

	let dtoMDFClaimResponse: mdfClaimDTO | undefined = undefined;

	if (dtoMDFClaimSFResponse.externalReferenceCode) {
		if (mdfClaim.id) {
			dtoMDFClaimResponse = await updateMDFClaim(
				ResourceName.MDF_CLAIM_DXP,
				mdfClaim,
				mdfRequest,
				mdfClaim.id,
				mdfClaim.reimbursementInvoice?.id as LiferayFile & number,
				dtoMDFClaimSFResponse.externalReferenceCode,
				dtoMDFClaimSFResponse.externalReferenceCode
			);
		}
		else {
			dtoMDFClaimResponse = await createMDFClaim(
				ResourceName.MDF_CLAIM_DXP,
				mdfClaim,
				mdfRequest,
				dtoMDFClaimSFResponse.externalReferenceCode,
				dtoMDFClaimSFResponse.externalReferenceCode
			);
		}
	}

	return dtoMDFClaimResponse;
}
