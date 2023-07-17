/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import MDFClaimDTO from '../../../interfaces/dto/mdfClaimDTO';
import LiferayFile from '../../../interfaces/liferayFile';
import MDFClaim from '../../../interfaces/mdfClaim';

export function getMDFClaimFromDTO(mdfClaim: MDFClaimDTO): MDFClaim {
	return {
		...mdfClaim,

		activities:
			mdfClaim?.mdfClmToMDFClmActs?.map((activityItem) => {
				const {
					currency,
					id,
					listOfQualifiedLeads,
					metrics,
					r_actToMDFClmActs_c_activityId,
					r_mdfClmToMDFClmActs_c_mdfClaimId,
					selected,
					totalCost,
				} = activityItem;

				return {
					allContents: activityItem.mdfClmActToMDFActDocs?.map(
						(allContentItem) =>
							allContentItem.allContents &&
							({
								...(allContentItem.allContents as Object),
								name: allContentItem.allContents.name
									.split('#')
									.reverse()
									.splice(1)
									.join(''),
							} as LiferayFile & number)
					) as LiferayFile[],
					budgets: activityItem.mdfClmActToMDFClmBgts?.map(
						(budgetItem) => {
							const {
								expenseName,
								id,
								invoice,
								invoiceAmount,
								r_bgtToMDFClmBgts_c_budgetId,
								selected,
							} = budgetItem;

							return {
								expenseName,
								id,
								invoice:
									invoice &&
									({
										...(invoice as Object),
										name: invoice.name
											.split('#')
											.reverse()
											.splice(1)
											.join(''),
									} as LiferayFile & number),
								invoiceAmount,
								r_bgtToMDFClmBgts_c_budgetId,
								selected,
							};
						}
					),
					currency,
					id,
					listOfQualifiedLeads:
						listOfQualifiedLeads &&
						({
							...(listOfQualifiedLeads as Object),
							name: listOfQualifiedLeads.name
								.split('#')
								.reverse()
								.splice(1)
								.join(''),
						} as LiferayFile & number),
					metrics,

					r_actToMDFClmActs_c_activityId,
					r_mdfClmToMDFClmActs_c_mdfClaimId,
					selected,
					totalCost,
				};
			}) || [],
		reimbursementInvoice:
			mdfClaim.reimbursementInvoice &&
			({
				...(mdfClaim.reimbursementInvoice as Object),
				name: mdfClaim.reimbursementInvoice.name
					.split('#')
					.reverse()
					.splice(1)
					.join(''),
			} as LiferayFile & number),
	};
}
