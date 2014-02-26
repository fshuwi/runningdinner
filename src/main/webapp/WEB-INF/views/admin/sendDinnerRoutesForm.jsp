<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<h3 class="contentheadline"><spring:message code="label.dinnerroutes.sendmessage" /></h3>

<tiles:insertDefinition name="view-status-info" />

<div>
	<div class="alert alert-info"><strong>Info</strong><br/>
		<spring:message code="text.dinnerroutes.message.info" />
	</div>
	
	<script>
		var charCounters = {};
		charCounters["message"] = 3000;
		charCounters["selfTemplate"] = 300;
		charCounters["hostsTemplate"] = 300;
	</script>
	
	<form:form method="POST" commandName="sendDinnerRoutesModel" htmlEscape="true" role="form" onsubmit="return isOneOrMoreTeamsSelected()">
		<div class="well">
			<spring:message code="label.subject" var="subjectLabel" />
			<bs:inputField name="subject" label="${subjectLabel}" inputColClass="col-xs-6" placeholder="${subjectLabel}"/>
			
			<div class="form-group">
				<label for="message"><spring:message code="label.message" /></label>
				<span class="help-block"><spring:message code="label.message.template.help" />: {firstname}, {lastname}, {route}</span>
				<form:textarea path="message" id="message" rows="10" style="margin-bottom:5px;" class="form-control counted" />
				<h6 class="pull-right" id="message_counter">3000 characters remaining</h6>
				<form:errors path="message"/>
			</div>
			
			<div class="form-group">
				<label for="selfTemplate"><spring:message code="label.message.dinnerroute.selftemplate" /></label>
				<span class="help-block"><spring:message code="label.message.template.replacement" arguments="{route}" />. <spring:message code="label.message.template.help" />: {firstname}, {lastname}, {meal}, {mealtime}</span>
				<form:textarea path="selfTemplate" id="selfTemplate" rows="3" style="margin-bottom:5px;" class="form-control counted" />
				<h6 class="pull-right" id="selfTemplate_counter">300 characters remaining</h6>
				<form:errors path="selfTemplate"/>
			</div>
			
			<div class="form-group">
				<label for="hostsTemplate"><spring:message code="label.message.dinnerroute.hoststemplate" /></label>
				<span class="help-block"><spring:message code="label.message.template.replacement" arguments="{route}" />. <spring:message code="label.message.template.help" />: {firstname}, {lastname}, {meal}, {mealtime}, {hostaddress}</span>
				<form:textarea path="hostsTemplate" id="hostsTemplate" rows="4" style="margin-bottom:5px;" class="form-control counted" />
				<h6 class="pull-right" id="hostsTemplate_counter">300 characters remaining</h6>
				<form:errors path="hostsTemplate"/>
			</div>
		</div>
		
		<input type="submit" class="btn btn-primary" value="<spring:message code="label.dinnerroutes.sendmessage" />!" name="sendDinnerRoutes" />

		<div style="margin-top:20px;">
			<h4><spring:message code="label.teams.selection" /></h4>
			<span><input type="checkbox" id="allTeamsSelectedBox" onchange="toggleTeamSelection()" /><label><spring:message code="label.teams.selection.all" /></label></span>
			<ul class="teamSelection">
				<form:checkboxes element="li" items="${sendDinnerRoutesModel.teamDisplayMap}" path="selectedTeams" cssClass="teamSelectionBox"/>
			</ul>
		</div>
			
	</form:form>		
</div>