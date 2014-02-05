<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.runningdinner.ui.RequestMappings" %>

<spring:url value="<%=RequestMappings.SHOW_TEAMS%>" var="teamsUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>
<spring:url value="<%= RequestMappings.ADMIN_OVERVIEW %>" var="overviewUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>
<spring:url value="<%= RequestMappings.EDIT_MEALTIMES %>" var="timesUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>
<spring:url value="/event/{uuid}/admin/options" var="optionsUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>

<spring:url value="<%=RequestMappings.SHOW_PARTICIPANTS%>" var="participantsUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>
<spring:url value="/event/{uuid}/admin/participants/upload" var="participantsUploadUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>
<spring:url value="/event/{uuid}/admin/teams/exchange" var="teamsExchangeUrl" htmlEscape="true">
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

<div class="col-xs-4">
	<h2>Dinner</h2>
	<a href="${teamsUrl}">Teameinteilung</a><br/>
	<a href="${overviewUrl}">Überblick</a><br/>
	<a href="${timesUrl}">Zeitplaene</a><br/>
	<a href="${optionsUrl}">Dinner-Optionen</a>
</div> 

<div class="col-xs-4">
	<h2>Teilnehmer</h2>
	<a href="${participantsUrl}">Teilnehmerliste anschauen</a><br/>
	<a href="${participantsUploadUrl}">Neue / Geaenderte Teilnehmerliste hochladen</a><br/>
	<a href="${teamsExchangeUrl}">Team austauschen</a><br/>
</div>

<div class="col-xs-4">
	<h2>Benachrichtigungen</h2>
	<a href="${participantsMailUrl}">Rundmail an alle Teilnehmer</a><br/>
	<a href="${teamsMailUrl}?selectAll=true">Teameinteilungen verschicken</a><br />
	<a href="${dinnerRouteMailUrl}">Dinnerplaene verschicken</a><br />
</div>

<div class="col-xs-12">
	<hr style="margin-bottom:15px;"/>
</div>

