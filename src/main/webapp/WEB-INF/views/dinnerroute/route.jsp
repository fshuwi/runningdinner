<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@page import="org.runningdinner.service.email.FormatterUtil" %>

<div class="row" style="text-align:center">
	<h3>Dinner Route</h3>
	<h5>${participantNames}</h5>
</div>

<c:forEach items="${teamDinnerRoute}" var="team">

	<c:set var="participant" value="${team.hostTeamMember}" />
	<div class="row">
		<c:choose>
			<c:when test="${team.naturalKey == currentTeamKey}">
				<div class="alert alert-success self col-xs-12 col-md-4 col-md-offset-4 col-sm-8 col-sm-offset-2">
					<h3 class="media-heading"><spring:message code="label.dinnerroutes.self.meal"/>: ${team.mealClass.label}</h3>
					<spring:message code="label.dinnerroutes.self.host"/>: <strong>${participant.name.fullnameFirstnameFirst}</strong><br/>
					<br/>
					<fmt:formatDate pattern="<%=FormatterUtil.DEFAULT_TIME_FORMAT%>" value="${team.mealClass.time}" var="mealTimeSelf"/>
					<strong><spring:message code="label.time"/>: <spring:message code="time.display" arguments="${mealTimeSelf}" /></strong>
				</div>
			</c:when>
			<c:otherwise>
				<div class="alert alert-info hoster col-xs-12 col-md-4 col-md-offset-4 col-sm-8 col-sm-offset-2">
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
	</div>
</c:forEach>