<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@page import="org.runningdinner.core.CoreUtil" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<h3 class="contentheadline"><spring:message code="headline.teams.sendmessage" /></h3>

<tiles:insertDefinition name="view-status-info" />

<div class="alert alert-info"><strong>Info</strong><br/><spring:message code="text.teams.sendmessage.info" /></div>

<c:choose>
	<c:when test="${not empty sendTeamsModel.lastMailSendingStatus}">
		<%--HH:mm:ss --%>
		<fmt:formatDate pattern="<%=CoreUtil.DEFAULT_DATEFORMAT_PATTERN%>" value="${sendTeamsModel.lastMailSendingStatus.sendingStartDate}" var="startDate"/>
		<c:choose>
			<c:when test="${sendTeamsModel.lastMailSendingStatus.sending}">
				<div class="alert alert-info"><strong>Mail-Report</strong><br/><spring:message code="text.teams.sendmessage.sendingactive" arguments="${startDate}"/></div>
			</c:when>
			<c:otherwise>
				<div class="alert alert-info"><strong>Mail-Report</strong><br/>
					<c:set var="succeededMailsSize" value="${fn:length(sendTeamsModel.lastMailSendingStatus.succeededMails)}" />
					<c:set var="failedMailsSize" value="${fn:length(sendTeamsModel.lastMailSendingStatus.failedMails)}" />
					<spring:message code="text.teams.sendmessage.alreadysent" arguments="${startDate}"/>:
					<p>${succeededMailsSize} Mail(s) wurden erfolgreich versendet.
						<c:if test="${failedMailsSize gt 0}">
							<br/><strong>Folgende Mail-Adressen schlugen fehl</strong>: <span class="label label-danger">${sendTeamsModel.lastMailSendingStatus.failedMailsAsString}</span>	
						</c:if>
					</p>
				</div>
			</c:otherwise>
		</c:choose>
	</c:when>
</c:choose>

<script>
	var charCounters = {};
	charCounters["message"] = 3000;
	charCounters["hostMessagePartTemplate"] = 300;
	charCounters["nonHostMessagePartTemplate"] = 300;
</script>

<div>	
	<form:form method="POST" commandName="sendTeamsModel" htmlEscape="true" role="form" onsubmit="return isOneOrMoreTeamsSelected()">
		<div class="well">
			<spring:message code="label.subject" var="subjectLabel" />
			<bs:inputField name="subject" label="${subjectLabel}" inputColClass="col-xs-6" placeholder="${subjectLabel}"/>
						
			<div class="form-group">
				<label for="message"><spring:message code="label.message" /></label>
				<span class="help-block"><spring:message code="label.message.template.help" />: {firstname}, {lastname}, {meal}, {mealtime}, {host}, {partner}</span>
				<form:textarea path="message" id="message" rows="10" style="margin-bottom:5px;" class="form-control counted" />
				<h6 class="pull-right" id="message_counter">3000 characters remaining</h6>
				<form:errors path="message"/>
			</div>
			
			<div class="form-group">
				<label for="hostMessagePartTemplate"><spring:message code="label.message.sendteams.host" /></label>
				<span class="help-block"><spring:message code="label.message.template.replacement" arguments="{host}" /></span>
				<form:textarea path="hostMessagePartTemplate" id="hostMessagePartTemplate" rows="2" style="margin-bottom:5px;" class="form-control counted" />
				<h6 class="pull-right" id="hostMessagePartTemplate_counter">300 characters remaining</h6>
				<form:errors path="hostMessagePartTemplate"/>
			</div>
			
			<div class="form-group">
				<label for="nonHostMessagePartTemplate"><spring:message code="label.message.sendteams.nonhost" /></label>
				<span class="help-block"><spring:message code="label.message.template.replacement" arguments="{host}" /></span>
				<form:textarea path="nonHostMessagePartTemplate" id="nonHostMessagePartTemplate" rows="2" style="margin-bottom:5px;" class="form-control counted" />
				<h6 class="pull-right" id="nonHostMessagePartTemplate_counter">300 characters remaining</h6>
				<form:errors path="nonHostMessagePartTemplate"/>
			</div>
		</div>
		
		<input type="submit" class="btn btn-primary" value="<spring:message code="label.teams.sendmessage"/>" name="sendTeamMessages" />
		
		<div style="margin-top:20px;">
			<h4><spring:message code="label.teams.selection" /></h4>
			<span><input type="checkbox" id="allTeamsSelectedBox" onchange="toggleTeamSelection()" /><label><spring:message code="label.teams.selection.all" /></label></span>
			<ul class="teamSelection">
				<form:checkboxes element="li" items="${sendTeamsModel.teamDisplayMap}" path="selectedTeams" cssClass="teamSelectionBox"/>
			</ul>
		</div>
		
	</form:form>		
</div>