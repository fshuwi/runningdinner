<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@page import="org.runningdinner.core.util.CoreUtil" %>

<tiles:useAttribute name="lastMailReport" id="lastMailReport" classname="org.runningdinner.model.BaseMailReport" />
<tiles:useAttribute name="mailType" id="mailType" classname="java.lang.String" ignore="true" />

<c:set value="<%=CoreUtil.DEFAULT_DATEFORMAT_PATTERN%>" var="datePattern" />
<fmt:formatDate pattern="${datePattern} HH:mm:ss" value="${lastMailReport.sendingStartDate}" var="startDate"/>

<spring:message code="text.teams.sendmessage.sendingactive" arguments="${startDate}" var="sendingActiveLabel"/>
<spring:message code="label.teams.mails" var="mailTypeLabel"/>
<c:choose>
	<c:when test="${mailType == 'participant'}">
		<spring:message code="text.participants.sendmessage.sendingactive" arguments="${startDate}" var="sendingActiveLabel"/>
		<spring:message code="label.participants.mails" var="mailTypeLabel"/>
	</c:when>
	<c:when test="${mailType == 'route'}">
		<spring:message code="text.routes.sendmessage.sendingactive" arguments="${startDate}" var="sendingActiveLabel"/>
		<spring:message code="label.routes.mails" var="mailTypeLabel"/>
	</c:when>
</c:choose>

<c:choose>
	<c:when test="${lastMailReport.sending}">
		<div class="alert alert-info"><strong>Mail-Report</strong><br/>
		${sendingActiveLabel}</div>
	</c:when>
	<c:otherwise>
		<div class="alert alert-info  alert-dismissable">
			<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
			
			<c:set var="succeededMailsSize" value="${fn:length(lastMailReport.succeededMails)}" />
			<c:set var="failedMailsSize" value="${fn:length(lastMailReport.failedMails)}" />
			
			<strong>Mail-Report</strong><br/>
			<spring:message code="text.sendmessage.alreadysent" arguments="${startDate},${mailTypeLabel}"/>:
			<p>
				<spring:message code="text.sendmessage.success" arguments="${succeededMailsSize}"/>.
				<c:if test="${failedMailsSize gt 0}">
					<br/><strong><spring:message code="text.sendmessage.failed"/></strong>: <span class="label label-danger">${lastMailReport.failedMailsAsString}</span>	
				</c:if>
			</p>
		</div>
	</c:otherwise>
</c:choose>