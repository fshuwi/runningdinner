<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<spring:url value="/event/{uuid}/admin/teams" var="teamsUrl" htmlEscape="true">
	<spring:param name="uuid" value="${uuid}" />
</spring:url>
<spring:url value="/event/{uuid}/admin/times" var="timesUrl" htmlEscape="true">
	<spring:param name="uuid" value="${uuid}" />
</spring:url>
<spring:url value="/event/{uuid}/admin/options" var="optionsUrl" htmlEscape="true">
	<spring:param name="uuid" value="${uuid}" />
</spring:url>

<spring:url value="/event/{uuid}/admin/participants" var="participantsUrl" htmlEscape="true">
	<spring:param name="uuid" value="${uuid}" />
</spring:url>
<spring:url value="/event/{uuid}/admin/participants/upload" var="participantsUploadUrl" htmlEscape="true">
	<spring:param name="uuid" value="${uuid}" />
</spring:url>
<spring:url value="/event/{uuid}/admin/teams/exchange" var="teamsExchangeUrl" htmlEscape="true">
	<spring:param name="uuid" value="${uuid}" />
</spring:url>

<spring:url value="/event/{uuid}/admin/participants/mail" var="participantsMailUrl" htmlEscape="true">
	<spring:param name="uuid" value="${uuid}" />
</spring:url>
<spring:url value="/event/{uuid}/admin/teams/mail" var="teamsMailUrl" htmlEscape="true">
	<spring:param name="uuid" value="${uuid}" />
</spring:url>
<spring:url value="/event/{uuid}/admin/dinnerroute/mail" var="dinnerRouteMailUrl" htmlEscape="true">
	<spring:param name="uuid" value="${uuid}" />
</spring:url>

<div class="col-xs-4">
	<h2>Dinner</h2>
	<a href="${teamsUrl}">Teameinteilung</a><br/>
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
	<a href="${teamsMailUrl}">Teameinteilungen verschicken</a><br />
	<a href="${dinnerRouteMailUrl}">Dinnerplaene verschicken#</a><br />
</div>

<div class="col-xs-12">
	<hr style="margin-bottom:15px;"/>
</div>

