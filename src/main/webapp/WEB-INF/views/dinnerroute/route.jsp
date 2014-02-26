<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@page import="org.runningdinner.service.email.FormatterUtil" %>

<h1>Dinner Route</h1>
<h5>${participantNames}</h5>

<%-- TODO: Berechne spalten-breite anhand von anzahl teams! --%>

<c:forEach items="${teamDinnerRoute}" var="team">

	<c:set var="participant" value="${team.hostTeamMember}" />
	
	<c:choose>
		<c:when test="${team.naturalKey == currentTeamKey}">
			<div class="col-xs-3 alert alert-success self">
				<h3 class="media-heading"><spring:message code="label.dinnerroutes.self.meal"/>: ${team.mealClass.label}</h3>
				<spring:message code="label.dinnerroutes.self.host"/>: <strong>${participant.name.fullnameFirstnameFirst}</strong><br/>
				<br/>
				<fmt:formatDate pattern="<%=FormatterUtil.DEFAULT_TIME_FORMAT%>" value="${team.mealClass.time}" var="mealTimeSelf"/>
				<strong><spring:message code="label.time"/>: <spring:message code="time.display" arguments="${mealTimeSelf}" /></strong>
			</div>
		</c:when>
		<c:otherwise>
			<div class="col-xs-3 alert alert-info hoster">
				<h3 class="media-heading">${team.mealClass.label}</h3>
				<address>
					<spring:message code="label.lastname" />: <strong>${participant.name.lastname}</strong><br>
					${participant.address.streetWithNr}<br>
					${participant.address.zipWithCity}<br>
					<br />
					<fmt:formatDate pattern="<%=FormatterUtil.DEFAULT_TIME_FORMAT%>" value="${team.mealClass.time}" var="mealTimeHost" />
					<strong><spring:message code="label.time"/>: <spring:message code="time.display" arguments="${mealTimeHost}" /></strong>
				</address>
			</div> 
		</c:otherwise>
	</c:choose>
	
</c:forEach>

<hr style="margin-top:15px;">