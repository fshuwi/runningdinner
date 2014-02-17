<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<h3 class="contentheadline"><spring:message code="label.dinnerroutes.sendmessage" /></h3>

<div>

	<div class="alert alert-info"><strong>Info</strong><br/>
		<spring:message code="text.dinnerroutes.message.info" />
	</div>
	
	<form:form method="POST" commandName="sendDinnerRoutesModel" htmlEscape="true" role="form">
		<div class="well">
			<spring:message code="label.subject" var="subjectLabel" />
			<bs:inputField name="subject" label="${subjectLabel}" inputColClass="col-xs-6" placeholder="${subjectLabel}"/>
			
			<div class="form-group">
				<label for="message"><spring:message code="label.message" /></label>
				<span class="help-block">Benutze folgende Templates: {firstname}, {lastname}, {route}</span>
				<form:textarea path="message" id="message" rows="10" style="margin-bottom:5px;" class="form-control counted" />
				<h6 class="pull-right" id="messageCounter">3000 characters remaining</h6>
				<form:errors path="message"/>
			</div>
			
			<div class="form-group">
				<label for="selfTemplate">Eigene Speisen-Beschreibung in der Route</label>
				<span class="help-block">Dieser Block wird weiter oben als Teil von {route} eingefügt. Benutze hier folgende Templates: {firstname}, {lastname}, {meal}, {mealtime}</span>
				<form:textarea path="selfTemplate" id="selfTemplate" rows="2" style="margin-bottom:5px;" class="form-control counted" />
				<h6 class="pull-right" id="selfMessageCounter">200 characters remaining</h6>
				<form:errors path="selfTemplate"/>
			</div>
			
			<div class="form-group">
				<label for="hostsTemplate">Speisen-Beschreibung der zu besuchenden Gastgeber in der Route</label>
				<span class="help-block">Dieser Block wird weiter oben als Teil von {route} eingefügt. Benutze hier folgende Templates: {firstname}, {lastname}, {meal}, {mealtime}, {hostaddress}</span>
				<form:textarea path="hostsTemplate" id="hostsTemplate" rows="2" style="margin-bottom:5px;" class="form-control counted" />
				<h6 class="pull-right" id="hostsTemplateCounter">200 characters remaining</h6>
				<form:errors path="hostsTemplate"/>
			</div>
			
		</div>
		
		<div>
			<h4><spring:message code="label.teams.selection" /></h4>
			<span><input type="checkbox" id="allTeamsSelectedBox" onchange="toggleTeamSelection()" /><label><spring:message code="label.teams.selection.all" /></label></span>
			<ul>
				<form:checkboxes element="li" items="${sendDinnerRoutesModel.teamDisplayMap}" path="selectedTeams" cssClass="teamSelectionBox"/>
			</ul>
		</div>
		
		<input type="submit" class="btn btn-primary" value="<spring:message code="label.dinnerroutes.sendmessage" />!" name="sendDinnerRoutes" />
		<input type="submit" class="btn btn-danger" value="<spring:message code="label.cancel" />" name="cancel" />
	
	</form:form>		
</div>