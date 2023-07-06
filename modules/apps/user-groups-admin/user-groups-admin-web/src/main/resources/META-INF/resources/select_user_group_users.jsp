<%--
/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String displayStyle = ParamUtil.getString(request, "displayStyle");

if (Validator.isNull(displayStyle)) {
	displayStyle = portalPreferences.getValue(UserGroupsAdminPortletKeys.USER_GROUPS_ADMIN, "users-display-style", "list");
}
else {
	portalPreferences.setValue(UserGroupsAdminPortletKeys.USER_GROUPS_ADMIN, "users-display-style", displayStyle);

	request.setAttribute(WebKeys.SINGLE_PAGE_APPLICATION_CLEAR_CACHE, Boolean.TRUE);
}

String eventName = ParamUtil.getString(request, "eventName", liferayPortletResponse.getNamespace() + "selectUsers");

EditUserGroupAssignmentsManagementToolbarDisplayContext editUserGroupAssignmentsManagementToolbarDisplayContext = new EditUserGroupAssignmentsManagementToolbarDisplayContext(request, renderRequest, renderResponse, displayStyle, "/select_user_group_users.jsp");

LinkedHashMap<String, Object> userParams = new LinkedHashMap<String, Object>();

if (filterManageableOrganizations) {
	userParams.put("usersOrgsTree", user.getOrganizations());
}

SearchContainer<User> searchContainer = editUserGroupAssignmentsManagementToolbarDisplayContext.getSearchContainer(userParams);
%>

<clay:management-toolbar
	clearResultsURL="<%= editUserGroupAssignmentsManagementToolbarDisplayContext.getClearResultsURL() %>"
	filterDropdownItems="<%= editUserGroupAssignmentsManagementToolbarDisplayContext.getFilterDropdownItems() %>"
	itemsTotal="<%= searchContainer.getTotal() %>"
	searchActionURL="<%= editUserGroupAssignmentsManagementToolbarDisplayContext.getSearchActionURL() %>"
	searchContainerId="selectUsers"
	searchFormName="searchFm"
	selectable="<%= true %>"
	showSearch="<%= true %>"
	sortingOrder="<%= searchContainer.getOrderByType() %>"
	sortingURL="<%= editUserGroupAssignmentsManagementToolbarDisplayContext.getSortingURL() %>"
	viewTypeItems="<%= editUserGroupAssignmentsManagementToolbarDisplayContext.getViewTypeItems() %>"
/>

<aui:form cssClass="container-fluid container-fluid-max-xl" method="post" name="fm">
	<liferay-ui:search-container
		id="selectUsers"
		searchContainer="<%= searchContainer %>"
		var="userSearchContainer"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.User"
			escapedModel="<%= true %>"
			keyProperty="userId"
			modelVar="user2"
			rowIdProperty="screenName"
		>

			<%
			boolean showActions = false;
			%>

			<%@ include file="/user_search_columns.jspf" %>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= displayStyle %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>

<aui:script use="liferay-search-container">
	var searchContainer = Liferay.SearchContainer.get(
		'<portlet:namespace />selectUsers'
	);

	searchContainer.on('rowToggled', (event) => {
		var selectedItems = event.elements.allSelectedElements;

		var data = [];

		if (selectedItems.size() > 0) {
			data = selectedItems.attr('value');
		}

		Liferay.Util.getOpener().Liferay.fire(
			'<%= HtmlUtil.escapeJS(eventName) %>',
			{
				data: data.join(','),
			}
		);
	});
</aui:script>