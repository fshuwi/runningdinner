<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<spring:message code="label.adminlink.email.help" var="labelEmailHelp" />

<h2><spring:message code="label.runningdinner.participantlist.headline"/></h2>
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

<form:form class="well" method="post" id="startForm4" action="wizard" commandName="createWizardModel" htmlEscape="true" role="form">
	<%--
	<div class="form-group">
		<label for="adminLink">Administrations-Link</label>
		<div class="row">
			<div class="col-xs-7">
				<form:input path="administrationUrl" class="form-control" id="adminLink" readonly="true" cssStyle="cursor:default;" />
			</div>
		</div>
		<p class="help-block"><spring:message code="label.adminlink.help" /></p>
	</div>
	--%>
	<bs:inputField name="email" label="EMail Adresse" placeholder="EMail Adresse" type="email" helpForInput="${labelEmailHelp}" inputColClass="col-xs-6"/>
	
	<input type="hidden" value="3" name="_page" />
	<input type="submit" class="btn btn-success" value="Fertigstellen" name="_finish" data-placement="bottom" data-toggle="tooltip" data-original-title="Speichert die Liste und erstellt einen Dinner-Plan" />
	<input type="submit" class="btn btn-danger" value="Abbrechen" name="_cancel" />
</form:form>