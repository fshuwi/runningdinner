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


<h3 class="contentheadline">Navigation</h3><hr />
<ul class="nav nav-pills nav-stacked">
    <li class="active"><a href="${overviewUrl}">Überblick</a></li>
    <li><a href="${teamsUrl}">Teameinteilung</a></li>
    <li><a href="${timesUrl}">Zeitpläne</a></li>
</ul>


<hr/> <!-- <h3>Teilnehmer</h3> -->
<ul class="nav nav-pills nav-stacked">
    <li><a href="${participantsUrl}">Teilnehmerliste</a></li>
    <li><a href="${teamsExchangeUrl}">Team austauschen</a></li>
</ul>


<!-- <h3>Benachrichtigungen</h3><hr style="margin:0 ! important;"/>-->
<hr/>
<ul class="nav nav-pills nav-stacked">
    <li><a href="${participantsMailUrl}">Rundmail an alle Teilnehmer</a></li>
    <li><a href="${teamsMailUrl}">Teameinteilungen verschicken</a></li>
    <li><a href="${dinnerRouteMailUrl}">Dinner-Routen verschicken</a></li>
</ul>
