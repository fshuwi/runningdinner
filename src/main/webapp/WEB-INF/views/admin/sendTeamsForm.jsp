<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>


<h3 class="contentheadline">Mail-Benachrichtigung über Teameinteilungen</h3>

<div class="row well">
	<div class="col-xs-12">
		<c:choose>
			<c:when test="${teamAdministration.teamsAlreadySaved}">
				Teams wurden bereits gespeichert, es koennen nur noch Mails versandt werden
			</c:when>
			<c:otherwise>
				Die Teameinteilung kann im Nachhinein nicht mehr veraendert werden (es koennen nur einzelne Teilnehmer innerhalb eines Teams ausgetauscht werden bzw. das gastgebende Mitglied kann getauscht werden).
				Wenn gewuenscht, wird (durch Ausfuellen des Formulars) jedem Teilnehmer eine Mail geschickt, in welcher er erfaehrt wer sein(e) Teampartner ist/sind und welche Speise zubereitet werden soll.
			</c:otherwise>
		</c:choose>
	</div>
</div>

<c:if test="${teamAdministration.mailsAlreadySent}">
	<div class="alert alert-info"><strong>Info</strong><br/>
		Du hast schon einmal Benachrichtigungen an alle Teilnehmer versandt.
	</div>
</c:if>

<div>	
	<form:form method="POST" commandName="sendTeamsModel" htmlEscape="true" role="form">
		<div class="well">
			<spring:message code="label.subject" var="subjectLabel" />
			<bs:inputField name="subject" label="${subjectLabel}" inputColClass="col-xs-6" placeholder="${subjectLabel}"/>
			
			<div class="form-group">
				<label for="message"><spring:message code="label.message" /></label>
				<span class="help-block">Benutze folgende Templates: {firstname}, {lastname}, {meal}, {mealtime}, {host}, {partner}</span>
				<form:textarea path="message" id="message" rows="10" style="margin-bottom:5px;" class="form-control counted" />
				<h6 class="pull-right" id="counter">3000 characters remaining</h6>
				<form:errors path="message"/>
			</div>
			
			<div class="form-group">
				<label for="hostMessagePartTemplate">Nachricht für Gastgeber</label>
				<span class="help-block">Dieser Block wird weiter oben unter {host} eingefügt</span>
				<form:textarea path="hostMessagePartTemplate" id="hostMessagePartTemplate" rows="2" style="margin-bottom:5px;" class="form-control" />
				<h6 class="pull-right" id="hostMessageTemplateCounter">200 characters remaining</h6>
				<form:errors path="hostMessagePartTemplate"/>
			</div>
			
			<div class="form-group">
				<label for="nonHostMessagePartTemplate">Nachricht für Nicht-Gastgeber</label>
				<span class="help-block">Dieser Block wird weiter oben unter {host} eingefügt</span>
				<form:textarea path="nonHostMessagePartTemplate" id="nonHostMessagePartTemplate" rows="2" style="margin-bottom:5px;" class="form-control" />
				<h6 class="pull-right" id="nonHostMessageTemplateCounter">200 characters remaining</h6>
				<form:errors path="nonHostMessagePartTemplate"/>
			</div>
		</div>
		
		
		<div>
			<h4><spring:message code="label.teams.selection" /></h4>
			<span><input type="checkbox" id="allTeamsSelectedBox" onchange="toggleTeamSelection()" /><label><spring:message code="label.teams.selection.all" /></label></span>
			<ul>
				<form:checkboxes element="li" items="${sendTeamsModel.teamDisplayMap}" path="selectedTeams" cssClass="teamSelectionBox"/>
			</ul>
		</div>
		
		<input type="submit" class="btn btn-primary" value="Teameinteilungen verschicken" name="sendTeamMessages" />
	
	</form:form>		
</div>