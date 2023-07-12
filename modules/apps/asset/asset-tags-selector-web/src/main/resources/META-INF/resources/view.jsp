<%--
/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new AssetTagsSelectorManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, assetTagsSelectorDisplayContext) %>"
/>

<clay:container-fluid>
	<liferay-ui:search-container
		id="tags"
		searchContainer="<%= assetTagsSelectorDisplayContext.getTagsSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.asset.kernel.model.AssetTag"
			keyProperty="name"
			modelVar="tag"
		>
			<liferay-ui:search-container-column-text
				cssClass="content-column name-column title-column"
				name="name"
				truncate="<%= true %>"
				value="<%= tag.getName() %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="site"
				value="<%= HtmlUtil.escape(assetTagsSelectorDisplayContext.getAssetTagGroupName(tag, locale)) %>"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="list"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>