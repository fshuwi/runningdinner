<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@page import="org.runningdinner.core.CoreUtil" %>

<tiles:useAttribute name="lastMailReport" id="lastMailReport" classname="org.runningdinner.model.BaseMailReport" />
<tiles:useAttribute name="mailType" id="mailType" classname="java.lang.String" ignore="true" />

<c:set value="<%=CoreUtil.DEFAULT_DATEFORMAT_PATTERN%>" var="datePattern" />
<fmt:formatDate pattern="${datePattern} HH:mm:ss" value="${lastMailReport.sendingStartDate}" var="startDate"/>
<c:choose>
	<c:when test="${lastMailReport.sending}">
		<div class="alert alert-info"><strong>Mail-Report</strong><br/><spring:message code="text.teams.sendmessage.sendingactive" arguments="${startDate}"/></div>
	</c:when>
	<c:otherwise>
		<div class="alert alert-info  alert-dismissable">
			<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
			
			<c:set var="succeededMailsSize" value="${fn:length(lastMailReport.succeededMails)}" />
			<c:set var="failedMailsSize" value="${fn:length(lastMailReport.failedMails)}" />
			
			<strong>Mail-Report</strong><br/>
			<spring:message code="text.sendmessage.alreadysent" arguments="${startDate},${mailType}"/>:
			<p>
				<spring:message code="text.sendmessage.success" arguments="${succeededMailsSize}"/>.
				<c:if test="${failedMailsSize gt 0}">
					<br/><strong><spring:message code="text.sendmessage.failed"/></strong>: <span class="label label-danger">${lastMailReport.failedMailsAsString}</span>	
				</c:if>
			</p>
		</div>
	</c:otherwise>
</c:choose>