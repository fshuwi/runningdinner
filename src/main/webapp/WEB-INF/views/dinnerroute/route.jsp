<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<h1>Dinner-Plan</h1>
<h5>${participantNames}</h5>

<%-- TODO: Berechne spalten-breite anhand von anzahl teams! --%>

<c:forEach items="${teamDinnerRoute}" var="team">

		<c:choose>
		<c:when test="${team.naturalKey == currentTeamKey}">
			<div class="col-xs-4 alert alert-success">
				<h3 class="media-heading">Euer Gang: ${team.mealClass.label}</h3>
				Gastgeber ist: <strong>ASDF</strong>
				<br/>
				<strong>Uhrzeit: <fmt:formatDate type="time" value="${team.mealClass.time}" timeStyle="SHORT" /> Uhr</strong>
			</div>
		</c:when>
		<c:otherwise>
			<div class="col-xs-4 alert alert-info">
				<h3 class="media-heading">${team.mealClass.label}</h3>
				<address>FOO<br/>
					<%--
					Nachname: <strong>${participant.name.lastname}</strong><br>
					${participant.address.streetWithNr}<br>
					${participant.address.zipWithCity}<br>
					--%>
					<br>
					<strong>Uhrzeit: <fmt:formatDate type="time" value="${team.mealClass.time}" timeStyle="SHORT" /> Uhr</strong>
				</address>
			</div> 
		</c:otherwise>
	</c:choose>
</c:forEach>

<hr style="margin-top:15px;">