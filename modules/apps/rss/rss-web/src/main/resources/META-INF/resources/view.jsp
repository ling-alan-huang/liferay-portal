<%--
/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
List<RSSFeed> rssFeeds = rssDisplayContext.getRSSFeeds();

Map<String, Object> contextObjects = HashMapBuilder.<String, Object>put(
	"rssDisplayContext", rssDisplayContext
).build();

if (rssFeeds.isEmpty()) {
	renderRequest.setAttribute(WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
}
%>

<c:choose>
	<c:when test="<%= rssFeeds.isEmpty() %>">
		<div class="alert alert-info text-center">
			<div>
				<liferay-ui:message key="this-application-is-not-visible-to-users-yet" />
			</div>

			<c:if test="<%= rssDisplayContext.isShowConfigurationLink() %>">
				<div>
					<aui:a href="javascript:void(0);" onClick="<%= portletDisplay.getURLConfigurationJS() %>"><liferay-ui:message key="select-at-least-one-valid-rss-feed-to-make-it-visible" /></aui:a>
				</div>
			</c:if>
		</div>
	</c:when>
	<c:otherwise>
		<liferay-ddm:template-renderer
			className="<%= RSSFeed.class.getName() %>"
			contextObjects="<%= contextObjects %>"
			displayStyle="<%= rssPortletInstanceConfiguration.displayStyle() %>"
			displayStyleGroupId="<%= rssDisplayContext.getDisplayStyleGroupId() %>"
			entries="<%= rssFeeds %>"
		>

			<%
			for (int i = 0; i < rssFeeds.size(); i++) {
				RSSFeed rssFeed = rssFeeds.get(i);

				boolean last = false;

				if (i == (rssFeeds.size() - 1)) {
					last = true;
				}

				SyndFeed syndFeed = rssFeed.getSyndFeed();
			%>

				<%@ include file="/feed.jspf" %>

			<%
			}
			%>

		</liferay-ddm:template-renderer>
	</c:otherwise>
</c:choose>