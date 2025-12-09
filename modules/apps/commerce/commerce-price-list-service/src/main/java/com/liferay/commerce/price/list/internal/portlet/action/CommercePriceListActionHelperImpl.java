/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.internal.portlet.action;

import com.liferay.commerce.price.list.constants.CommercePriceListWebKeys;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.portlet.action.CommercePriceListActionHelper;
import com.liferay.commerce.price.list.service.CommercePriceEntryService;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.price.list.service.CommerceTierPriceEntryService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(service = CommercePriceListActionHelper.class)
public class CommercePriceListActionHelperImpl
	implements CommercePriceListActionHelper {

	@Override
	public List<CommercePriceEntry> getCommercePriceEntries(
			PortletRequest portletRequest)
		throws PortalException {

		return TransformUtil.transformToList(
			ParamUtil.getLongValues(portletRequest, "rowIds"),
			commercePriceEntryId ->
				_commercePriceEntryService.fetchCommercePriceEntry(
					commercePriceEntryId));
	}

	@Override
	public CommercePriceEntry getCommercePriceEntry(RenderRequest renderRequest)
		throws PortalException {

		CommercePriceEntry commercePriceEntry =
			(CommercePriceEntry)renderRequest.getAttribute(
				CommercePriceListWebKeys.COMMERCE_PRICE_ENTRY);

		if (commercePriceEntry != null) {
			return commercePriceEntry;
		}

		long commercePriceEntryId = ParamUtil.getLong(
			renderRequest, "commercePriceEntryId");

		if (commercePriceEntryId > 0) {
			commercePriceEntry =
				_commercePriceEntryService.fetchCommercePriceEntry(
					commercePriceEntryId);
		}

		if (commercePriceEntry != null) {
			renderRequest.setAttribute(
				CommercePriceListWebKeys.COMMERCE_PRICE_ENTRY,
				commercePriceEntry);
		}

		return commercePriceEntry;
	}

	@Override
	public CommercePriceList getCommercePriceList(PortletRequest portletRequest)
		throws PortalException {

		CommercePriceList commercePriceList =
			(CommercePriceList)portletRequest.getAttribute(
				CommercePriceListWebKeys.COMMERCE_PRICE_LIST);

		if (commercePriceList != null) {
			return commercePriceList;
		}

		long commercePriceListId = ParamUtil.getLong(
			portletRequest, "commercePriceListId");

		if (commercePriceListId > 0) {
			commercePriceList =
				_commercePriceListService.fetchCommercePriceList(
					commercePriceListId);
		}

		if (commercePriceList != null) {
			portletRequest.setAttribute(
				CommercePriceListWebKeys.COMMERCE_PRICE_LIST,
				commercePriceList);
		}

		return commercePriceList;
	}

	@Override
	public List<CommercePriceList> getCommercePriceLists(
			PortletRequest portletRequest)
		throws PortalException {

		return TransformUtil.transformToList(
			ParamUtil.getLongValues(portletRequest, "rowIds"),
			commercePriceListId ->
				_commercePriceListService.fetchCommercePriceList(
					commercePriceListId));
	}

	@Override
	public List<CommerceTierPriceEntry> getCommerceTierPriceEntries(
			PortletRequest portletRequest)
		throws PortalException {

		return TransformUtil.transformToList(
			ParamUtil.getLongValues(portletRequest, "rowIds"),
			commerceTierPriceEntryId ->
				_commerceTierPriceEntryService.fetchCommerceTierPriceEntry(
					commerceTierPriceEntryId));
	}

	@Override
	public CommerceTierPriceEntry getCommerceTierPriceEntry(
			RenderRequest renderRequest)
		throws PortalException {

		long commerceTierPriceEntryId = ParamUtil.getLong(
			renderRequest, "commerceTierPriceEntryId");

		return _commerceTierPriceEntryService.fetchCommerceTierPriceEntry(
			commerceTierPriceEntryId);
	}

	@Reference
	private CommercePriceEntryService _commercePriceEntryService;

	@Reference
	private CommercePriceListService _commercePriceListService;

	@Reference
	private CommerceTierPriceEntryService _commerceTierPriceEntryService;

}