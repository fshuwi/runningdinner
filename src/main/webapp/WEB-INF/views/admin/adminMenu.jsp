<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ page import="org.runningdinner.ui.RequestMappings" %>

<spring:url value="<%=RequestMappings.SHOW_TEAMS%>" var="teamsUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>
<spring:url value="<%= RequestMappings.ADMIN_OVERVIEW %>" var="overviewUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>
<spring:url value="<%= RequestMappings.EDIT_MEALTIMES %>" var="mealtimesUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>

<spring:url value="<%=RequestMappings.SHOW_PARTICIPANTS%>" var="participantsUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>
<spring:url value="/event/{uuid}/admin/participants/upload" var="participantsUploadUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>
<spring:url value="<%=RequestMappings.EXCHANGE_TEAM%>" var="teamsExchangeUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>

<spring:url value="<%=RequestMappings.SEND_PARTICIPANT_MAILS%>" var="participantsMailUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>
<spring:url value="<%=RequestMappings.SEND_TEAM_MAILS%>" var="teamsMailUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>
<spring:url value="<%=RequestMappings.SEND_DINNERROUTES_MAIL%>" var="dinnerRouteMailUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>


<%-- Use current view name to determine the current active menu link... not very nice, but sufficient for our simple scenario --%>
<tiles:useAttribute name="currentView" id="currentView" classname="java.lang.String" ignore="true"/>
<c:set var="overviewClass" value="" />
<c:set var="teamsClass" value="" />	
<c:set var="mealtimesClass" value="" />
<c:set var="participantsClass" value="" />
<c:set var="participantsMailClass" value="" />
<c:set var="teamsMailClass" value="" />
<c:set var="dinnerRoutesMailClass" value="" />
<c:choose>
	<c:when test="${currentView == 'overview'}">
		<c:set var="overviewClass" value="active" />
	</c:when>
	<c:when test="${currentView == 'teams'}">
		<c:set var="teamsClass" value="active" />
	</c:when>
	<c:when test="${currentView == 'editMealTimesForm'}">
		<c:set var="mealtimesClass" value="active" />
	</c:when>
	
	<c:when test="${currentView == 'participants'}">
		<c:set var="participantsClass" value="active" />
	</c:when>

	<c:when test="${currentView == 'sendParticipantsForm'}">
		<c:set var="participantsMailClass" value="active" />
	</c:when>	
	<c:when test="${currentView == 'sendTeamsForm'}">
		<c:set var="teamsMailClass" value="active" />
	</c:when>
	<c:when test="${currentView == 'sendDinnerRoutesForm'}">
		<c:set var="dinnerRoutesMailClass" value="active" />
	</c:when>
</c:choose>
<%-- End determine current menu entry --%>


<h3 class="contentheadline">Navigation</h3><hr />
<ul class="nav nav-pills nav-stacked">
    <li class="${overviewClass}"><a href="${overviewUrl}"><spring:message code="headline.overview" /></a></li>
    <li class="${teamsClass}"><a href="${teamsUrl}"><spring:message code="headline.teams"/></a></li>
    <li class="${mealtimesClass}"><a href="${mealtimesUrl}"><spring:message code="headline.mealtimes"/></a></li>
</ul>

<hr/>
<ul class="nav nav-pills nav-stacked">
    <li class="${participantsClass}"><a href="${participantsUrl}"><spring:message code="headline.participantlist"/></a></li>
    <li><a href="${teamsExchangeUrl}"><spring:message code="headline.teams.exchange"/></a></li>
</ul>

<hr/>
<ul class="nav nav-pills nav-stacked">
    <li class="${participantsMailClass}"><a href="${participantsMailUrl}"><spring:message code="label.participants.sendmessage"/></a></li>
    <li class="${teamsMailClass}"><a href="${teamsMailUrl}"><spring:message code="label.teams.sendmessage"/></a></li>
    <li class="${dinnerRoutesMailClass}"><a href="${dinnerRouteMailUrl}"><spring:message code="label.dinnerroutes.sendmessage"/></a></li>
</ul>
