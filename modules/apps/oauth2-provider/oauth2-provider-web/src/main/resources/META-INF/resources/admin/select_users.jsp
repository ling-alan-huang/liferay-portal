<%--
/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
SelectUsersDisplayContext selectUsersDisplayContext = new SelectUsersDisplayContext(request, renderRequest, renderResponse);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new SelectUsersManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, selectUsersDisplayContext) %>"
/>

<aui:form cssClass="container-fluid container-fluid-max-xl" name="selectUser">
	<liferay-ui:search-container
		id="users"
		searchContainer="<%= selectUsersDisplayContext.getUserSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.User"
			escapedModel="<%= true %>"
			keyProperty="userId"
			modelVar="userRow"
			rowIdProperty="screenName"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand table-cell-minw-200 table-title"
				name="screen-name"
				orderable="<%= true %>"
			>
				<aui:a
					cssClass="selector-button"
					data='<%=
						HashMapBuilder.<String, Object>put(
							"screenname", userRow.getScreenName()
						).put(
							"userid", userRow.getUserId()
						).build()
					%>'
					href="javascript:void(0);"
				>
					<%= userRow.getScreenName() %>
				</aui:a>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand table-cell-minw-200"
				name="name"
				orderable="<%= true %>"
				property="fullName"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= selectUsersDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>