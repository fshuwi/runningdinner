<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@page import="org.runningdinner.service.email.FormatterUtil" %>
<%@page import="org.runningdinner.core.CoreUtil" %>

<h3 class="contentheadline"><spring:message code="headline.overview" /></h3>

<tiles:insertDefinition name="view-status-info" />

<div class="jumbotron" style="padding-top:17px;padding-bottom:20px;">
	<h2>${runningDinner.title}</h2>
	<c:if test="${not empty runningDinner.city}">
		<p>Wo: <span class="text-success"><strong>${runningDinner.city}</strong></span></p>
	</c:if>
	<p>Wann: <span class="text-success"><strong><fmt:formatDate pattern="<%=CoreUtil.DEFAULT_DATEFORMAT_PATTERN%>" value="${runningDinner.date}" /></strong></span></p>
	<div class="row">
		<div class="col-xs-4">
			<c:forEach items="${runningDinner.configuration.mealClasses}" var="meal">
				<span class="label label-primary"><fmt:formatDate value="${meal.time}" pattern="<%=FormatterUtil.DEFAULT_TIME_FORMAT%>" /> Uhr</span> <span class="badge">${meal.label}</span><br/>
			</c:forEach>
		</div>
	</div>
</div>

<div class="row">
	<div class="col-xs-12">
		<div class="well">
			<p>TODO: Aktivitšten</p>
		</div>
	</div>
</div>





