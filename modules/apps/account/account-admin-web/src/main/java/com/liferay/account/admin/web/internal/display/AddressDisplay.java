/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.account.admin.web.internal.display;

import com.liferay.account.model.AccountEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.service.AddressLocalServiceUtil;
import com.liferay.portal.kernel.service.ListTypeLocalServiceUtil;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Pei-Jung Lan
 */
public class AddressDisplay {

	public static AddressDisplay of(Address address) {
		if (address != null) {
			return new AddressDisplay(address);
		}

		return _EMPTY_INSTANCE;
	}

	public static AddressDisplay of(long addressId) {
		return of(AddressLocalServiceUtil.fetchAddress(addressId));
	}

	public long getAddressId() {
		return _addressId;
	}

	public String getCity() {
		return _city;
	}

	public long getListTypeId() {
		return _listTypeId;
	}

	public String getName() {
		return _name;
	}

	public String getRegionName() {
		return _region.getName();
	}

	public String getStreet() {
		return _street;
	}

	public String getType(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return LanguageUtil.get(
			new AggregateResourceBundle(
				resourceBundle, PortalUtil.getResourceBundle(locale)),
			_listTypeName);
	}

	public String getValidDomainsString() {
		List<Set<String>> accountEntryDomains = Stream.of(
			_getAccountEntryUserRels(getUserId())
		).flatMap(
			List::stream
		).map(
			AccountEntryUserRelModel::getAccountEntryId
		).map(
			AccountEntryLocalServiceUtil::fetchAccountEntry
		).filter(
			Objects::nonNull
		).map(
			AccountEntry::getDomains
		).map(
			StringUtil::split
		).map(
			SetUtil::fromArray
		).collect(
			Collectors.toList()
		);

		if (ListUtil.isEmpty(accountEntryDomains)) {
			return StringPool.BLANK;
		}

		Set<String> commonDomains = accountEntryDomains.remove(0);

		for (Set<String> domains : accountEntryDomains) {
			commonDomains = SetUtil.intersect(commonDomains, domains);

			if (SetUtil.isEmpty(commonDomains)) {
				return StringPool.BLANK;
			}
		}

		return StringUtil.merge(commonDomains, StringPool.COMMA);
	}

	public String getZip() {
		return _zip;
	}

	private AddressDisplay() {
		_addressId = 0;
		_city = StringPool.BLANK;

		ListType listType = ListTypeLocalServiceUtil.getListType(
			"billing-and-shipping",
			AccountEntry.class.getName() + ListTypeConstants.ADDRESS);

		_listTypeId = listType.getListTypeId();

		_listTypeName = StringPool.BLANK;
		_name = StringPool.BLANK;
		_region = null;
		_street = StringPool.BLANK;
		_zip = StringPool.BLANK;
	}

	private AddressDisplay(Address address) {
		_addressId = address.getAddressId();
		_city = address.getCity();
		_listTypeId = address.getListTypeId();
		_listTypeName = _getListTypeName(address);
		_name = address.getName();
		_region = address.getRegion();
		_street = address.getStreet1();
		_zip = address.getZip();
	}

	private String _getListTypeName(Address address) {
		ListType listType = address.getListType();

		return listType.getName();
	}

	private static final AddressDisplay _EMPTY_INSTANCE = new AddressDisplay();

	private final long _addressId;
	private final String _city;
	private final long _listTypeId;
	private final String _listTypeName;
	private final String _name;
	private final Region _region;
	private final String _street;
	private final String _zip;

}