<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@page import="org.runningdinner.core.util.CoreUtil" %>

<h3 class="contentheadline"><spring:message code="label.participants.sendmessage" /></h3>

<tiles:insertDefinition name="view-status-info" flush="true" />

<div class="alert alert-info alert-dismissable">
	<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
	<strong>Info</strong><br/><spring:message code="text.participants.sendmessage.info" /><br/>
	<strong><spring:message code="label.important"/></strong>: <spring:message code="text.sendmessage.info" />
</div>

<c:if test="${not empty sendMailsModel.lastMailReport}">
	<tiles:insertDefinition name="view-mailreport">
		<tiles:putAttribute name="lastMailReport" value="${sendMailsModel.lastMailReport}" />
		<tiles:putAttribute name="mailType" value="Participant Emails" />
	</tiles:insertDefinition>
</c:if>

<script>
	var charCounters = {};
	charCounters["message"] = 3000;
</script>

<div>	
	<form:form method="POST" commandName="sendMailsModel" htmlEscape="true" role="form" onsubmit="return isOneOrMoreEntitiesSelected()">
		<div class="well">
			<spring:message code="label.subject" var="subjectLabel" />
			<bs:inputField name="subject" label="${subjectLabel}" inputColClass="col-xs-6" placeholder="${subjectLabel}"/>
						
			<div class="form-group">
				<label for="message"><spring:message code="label.message" /></label>
				<span class="help-block"><spring:message code="label.message.template.help" />: {firstname}, {lastname}</span>
				<form:textarea path="message" id="message" rows="10" style="margin-bottom:5px;" class="form-control counted" />
				<h6 class="pull-right" id="message_counter">3000 characters remaining</h6>
				<form:errors path="message"/>
			</div>
		</div>
		
		<input type="submit" class="btn btn-primary" value="<spring:message code="label.participants.sendmessage"/>" name="sendPrincipalMessages" />
		<input type="submit" class='btn btn-info' value="<spring:message code="label.preview" />" name="preview" />
		
		<div style="margin-top:20px;">
			<h4><spring:message code="label.teams.selection" /></h4>
			<form:errors path="selectedEntities" />
			<span><input type="checkbox" id="allEntitiesSelectedBox" onchange="toggleEntitySelection()" /><label><spring:message code="label.teams.selection.all" /></label></span>
			<ul class="participantSelection">
				<form:checkboxes element="li" items="${sendMailsModel.entityDisplayMap}" path="selectedEntities" cssClass="entitySelectionBox"/>
			</ul>
		</div>
		
	</form:form>		
</div>

<c:if test="${not empty sendMailsPreviewModel}">
	<tiles:insertDefinition name="view-mailpreview" flush="true">
		<tiles:putAttribute name="previewModel" value="${sendMailsPreviewModel}" />
	</tiles:insertDefinition>
</c:if>
