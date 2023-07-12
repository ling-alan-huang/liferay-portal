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

<aui:form cssClass="container-fluid container-fluid-max-xl" name="selectUserGroupFm">
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
			int usersCount = UserLocalServiceUtil.searchCount(
				company.getCompanyId(), StringPool.BLANK, WorkflowConstants.STATUS_ANY,
				LinkedHashMapBuilder.<String, Object>put(
					"usersUserGroups", Long.valueOf(userGroup.getUserGroupId())
				).build());
			%>

			<c:choose>
				<c:when test='<%= Objects.equals(selectUserGroupsDisplayContext.getDisplayStyle(), "descriptive") %>'>
					<liferay-ui:search-container-column-icon
						icon="users"
						toggleRowChecker="<%= true %>"
					/>

					<liferay-ui:search-container-column-text
						colspan="<%= 2 %>"
					>
						<h5><%= userGroup.getName() %></h5>

						<h6 class="text-default">
							<span><%= userGroup.getDescription() %></span>
						</h6>

						<h6 class="text-default">
							<span><liferay-ui:message arguments="<%= usersCount %>" key="x-users" /></span>
						</h6>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:otherwise>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand"
						name="name"
						orderable="<%= true %>"
						property="name"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand"
						name="description"
						orderable="<%= true %>"
						property="description"
					/>

					<liferay-ui:search-container-column-text
						name="users"
						value="<%= String.valueOf(usersCount) %>"
					/>
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= selectUserGroupsDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>