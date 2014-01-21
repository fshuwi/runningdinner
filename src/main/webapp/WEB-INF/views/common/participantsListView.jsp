<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<table class="table well table-condensed">
	<thead>
		<tr>
			<th>#</th>
			<th>Name</th>
			<th>Strasse + HausNr</th>
			<th>PLZ + Stadt</th>
			<th># Plätze</th>
			<th>EMail</th>
			<th>Handy-Nr</th>
			<th>Geschlecht</th>
			<th>Alter</th>
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
				<td>${participant.gender}</td>
				<td>${participant.age}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<div class="panel panel-${participantStatus}">
	<div class="panel-heading"><h4 class="panel-title">Vorschau-Information</h4></div>
	<div class="panel-body">${participantStatusMessage}</div>
</div>