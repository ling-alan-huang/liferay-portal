<%--
/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
assetTagsSelectorDisplayContext = new AssetTagsSelectorDisplayContext(request, renderRequest, renderResponse, false);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new AssetTagsSelectorManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, assetTagsSelectorDisplayContext) %>"
/>

<aui:form action="<%= assetTagsSelectorDisplayContext.getPortletURL() %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="selectAssetTagFm">
	<liferay-ui:search-container
		searchContainer="<%= assetTagsSelectorDisplayContext.getTagsSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.asset.kernel.model.AssetTag"
			escapedModel="<%= true %>"
			keyProperty="name"
			modelVar="tag"
			rowIdProperty="friendlyURL"
			rowVar="row"
		>
			<liferay-ui:search-container-column-text
				name="name"
				truncate="<%= true %>"
			>
				<aui:a
					cssClass="selector-button"
					data='<%=
						HashMapBuilder.<String, Object>put(
							"entityid", tag.getTagId()
						).put(
							"entityname", tag.getName()
						).build()
					%>'
					href="javascript:void(0);"
				>
					<%= HtmlUtil.escape(tag.getName()) %>
				</aui:a>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="list"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>