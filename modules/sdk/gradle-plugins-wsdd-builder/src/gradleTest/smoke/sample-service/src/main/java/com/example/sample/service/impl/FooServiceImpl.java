/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.example.sample.service.impl;

import com.example.sample.service.base.FooServiceBaseImpl;

/**
 * The implementation of the foo remote service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the {@link
 * com.example.sample.service.FooService} interface. <p> This is a remote
 * service. Methods of this service are expected to have security checks based
 * on the propagated JAAS credentials because this service can be accessed
 * remotely.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see    FooServiceBaseImpl
 * @see    com.example.sample.service.FooServiceUtil
 */
public class FooServiceImpl extends FooServiceBaseImpl {

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Always use {@link com.example.sample.service.FooServiceUtil} to access the foo remote service.
	 */

}