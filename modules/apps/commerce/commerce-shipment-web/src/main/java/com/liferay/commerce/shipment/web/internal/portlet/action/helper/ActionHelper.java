/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipment.web.internal.portlet.action.helper;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.service.CommerceShipmentItemLocalService;
import com.liferay.commerce.service.CommerceShipmentLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.ResourceRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = ActionHelper.class)
public class ActionHelper {

	public CommerceShipment getCommerceShipment(RenderRequest renderRequest)
		throws PortalException {

		CommerceShipment commerceShipment =
			(CommerceShipment)renderRequest.getAttribute(
				CommerceWebKeys.COMMERCE_SHIPMENT);

		if (commerceShipment != null) {
			return commerceShipment;
		}

		long commerceShipmentId = ParamUtil.getLong(
			renderRequest, "commerceShipmentId");

		if (commerceShipmentId > 0) {
			commerceShipment =
				_commerceShipmentLocalService.fetchCommerceShipment(
					commerceShipmentId);
		}

		if (commerceShipment != null) {
			renderRequest.setAttribute(
				CommerceWebKeys.COMMERCE_SHIPMENT, commerceShipment);
		}

		return commerceShipment;
	}

	public CommerceShipmentItem getCommerceShipmentItem(
			RenderRequest renderRequest)
		throws PortalException {

		CommerceShipmentItem commerceShipmentItem =
			(CommerceShipmentItem)renderRequest.getAttribute(
				CommerceWebKeys.COMMERCE_SHIPMENT_ITEM);

		if (commerceShipmentItem != null) {
			return commerceShipmentItem;
		}

		long commerceShipmentItemId = ParamUtil.getLong(
			renderRequest, "commerceShipmentItemId");

		if (commerceShipmentItemId > 0) {
			commerceShipmentItem =
				_commerceShipmentItemLocalService.fetchCommerceShipmentItem(
					commerceShipmentItemId);
		}

		if (commerceShipmentItem != null) {
			renderRequest.setAttribute(
				CommerceWebKeys.COMMERCE_SHIPMENT_ITEM, commerceShipmentItem);
		}

		return commerceShipmentItem;
	}

	public List<CommerceShipmentItem> getCommerceShipmentItems(
			ResourceRequest resourceRequest)
		throws PortalException {

		return TransformUtil.transformToList(
			ParamUtil.getLongValues(resourceRequest, RowChecker.ROW_IDS),
			commerceShipmentItemId ->
				_commerceShipmentItemLocalService.getCommerceShipmentItem(
					commerceShipmentItemId));
	}

	public List<CommerceShipment> getCommerceShipments(
			ResourceRequest resourceRequest)
		throws PortalException {

		return TransformUtil.transformToList(
			ParamUtil.getLongValues(resourceRequest, RowChecker.ROW_IDS),
			commerceShipmentId ->
				_commerceShipmentLocalService.getCommerceShipment(
					commerceShipmentId));
	}

	@Reference
	private CommerceShipmentItemLocalService _commerceShipmentItemLocalService;

	@Reference
	private CommerceShipmentLocalService _commerceShipmentLocalService;

}