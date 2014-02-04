<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>


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
	<h2>Mail-Benachrichtigung</h2>
	
	<form:form method="POST" commandName="sendTeamsModel" htmlEscape="true" role="form">
		<div class="well">
			<bs:inputField name="subject" label="Subjekt" inputColClass="col-xs-6" placeholder="Titel der Mail"/>
			
			<div class="form-group">
				<label for="message">Nachricht</label>
				<span class="help-block">Benutze folgende Templates: {firstname}, {lastname}, {meal}, {mealtime}, {host}, {partner}</span>
				<form:textarea path="message" id="message" rows="10" style="margin-bottom:5px;" class="form-control counted" />
				<h6 class="pull-right" id="counter">3000 characters remaining</h6>
				<form:errors path="message"/>
			</div>
			
			<div class="form-group">
				<label for="hostMessagePartTemplate">Nachricht für Gastgeber</label>
				<span class="help-block">Dieser Block wird weiter oben unter {host} eingefügt</span>
				<form:textarea path="hostMessagePartTemplate" id="hostMessagePartTemplate" rows="2" style="margin-bottom:5px;" class="form-control" />
			</div>
			
			<div class="form-group">
				<label for="nonHostMessagePartTemplate">Nachricht für Nicht-Gastgeber</label>
				<span class="help-block">Dieser Block wird weiter oben unter {host} eingefügt</span>
				<form:textarea path="nonHostMessagePartTemplate" id="nonHostMessagePartTemplate" rows="2" style="margin-bottom:5px;" class="form-control" />
			</div>
		</div>
		
		
		<div>
			<h4>Team-Auswahl für Mail-Versand</h4>
			<span><input type="checkbox" id="allTeamsSelectedBox" onchange="toggleTeamSelection()" /><label>Alle selektieren/deselektieren</label></span>
			<ul>
				<form:checkboxes element="li" items="${sendTeamsModel.teamDisplayMap}" path="selectedTeams" cssClass="teamSelectionBox"/>
			</ul>
		</div>
		
		<input type="submit" class="btn btn-primary" value="Teameinteilungen verschicken" name="sendTeamMessages" />
	
	</form:form>		
</div>