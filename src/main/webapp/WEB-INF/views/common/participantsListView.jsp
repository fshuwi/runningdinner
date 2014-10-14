<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@page import="org.runningdinner.core.Gender" %>

<% pageContext.setAttribute("MALE_ENUM", Gender.MALE); %>
<% pageContext.setAttribute("FEMALE_ENUM", Gender.FEMALE); %>

<table class="table well table-condensed">
	<thead>
		<tr>
			<th>#</th>
			<th><spring:message code="label.fullname" /></th>
			<th><spring:message code="label.street" /> + <spring:message code="label.streetnr" /></th>
			<th><spring:message code="label.zip" /> + <spring:message code="label.city" /></th>
			<th><spring:message code="label.numseats" /></th>
			<th><spring:message code="label.email" /></th>
			<th><spring:message code="label.mobilenr" /></th>
			<th><spring:message code="label.gender" /></th>
			<th><spring:message code="label.age" /></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${participants}" var="participant" varStatus="loopCounter">
			<tr <c:if test="${rd:contains(notAssignableParticipants,participant)}">class='warning'</c:if> >
				<td>${participant.participantNumber}</td>
				<td>${participant.name.fullnameFirstnameFirst}</td>
				<td>${participant.address.streetWithNr}</td>
				<td>${participant.address.zipWithCity}</td>
				<td>${participant.numSeats}</td>
				<td>${participant.email}</td>
				<td>${participant.mobileNumber}</td>
				<td>
					<c:choose>
						<c:when test="${participant.gender == MALE_ENUM}">
							<spring:message code="label.gender.male" />
						</c:when>
						<c:when test="${participant.gender == FEMALE_ENUM}">
							<spring:message code="label.gender.female" />
						</c:when>
						<c:otherwise>
							-
						</c:otherwise>
					</c:choose>
				</td>
				<td>${participant.age}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<div class="panel panel-${participantStatus}">
	<div class="panel-heading"><h4 class="panel-title"><spring:message code="text.participant.preview" /></h4></div>
	<div class="panel-body">${participantStatusMessage}</div>
</div>