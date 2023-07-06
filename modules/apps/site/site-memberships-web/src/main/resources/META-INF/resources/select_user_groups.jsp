<%--
/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SelectUserGroupsDisplayContext selectUserGroupsDisplayContext = new SelectUserGroupsDisplayContext(request, renderRequest, renderResponse);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new SelectUserGroupsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, selectUserGroupsDisplayContext) %>"
/>

<aui:form cssClass="container-fluid container-fluid-max-xl portlet-site-memberships-user-groups" name="fm">
	<liferay-ui:search-container
		id="userGroups"
		searchContainer="<%= selectUserGroupsDisplayContext.getUserGroupSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.UserGroup"
			escapedModel="<%= true %>"
			keyProperty="userGroupId"
			modelVar="userGroup"
		>

			<%
			String displayStyle = selectUserGroupsDisplayContext.getDisplayStyle();

			boolean selectUserGroup = true;
			%>

			<%@ include file="/user_group_columns.jspf" %>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= selectUserGroupsDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>