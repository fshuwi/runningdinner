<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%><%

%><%@ attribute name="name" required="true" description="Name of corresponding property in bean object" %>
<%@ attribute name="label" required="true" description="Label appears in red color if input is considered as invalid after submission" %>
<%@ attribute name="items" required="true" type="java.util.Collection" %>
<%@ attribute name="itemLabel" required="true" description="Label appears in red color if input is considered as invalid after submission" %>
<%@ attribute name="itemValue" required="true" description="Label appears in red color if input is considered as invalid after submission" %><%

%><%@ attribute name="id" required="false" rtexprvalue="false" description="id for input field (if not set, the name is taken)" %>
<%@ attribute name="helpForLabelInline" required="false" description="inline label help text" %>
<%@ attribute name="helpForLabel" required="false" description="inline label help text" %>
<%@ attribute name="helpForInput" required="false" description="block input help text" %>
<%@ attribute name="inputTooltip" required="false" description="optional tooltip text" %>
<%@ attribute name="inputTooltipPosition" required="false" rtexprvalue="false" %>
<%@ attribute name="labelTooltip" required="false" description="optional tooltip text" %>
<%@ attribute name="labelTooltipPosition" required="false" rtexprvalue="false" %>
<%@ attribute name="inputColClass" required="false" description="Defines the bootstrap column width class, default is col-xs-4" %>
<%@ attribute name="inputErrorColClass" required="false" description="Defines the bootstrap column width class, default is col-xs-4" %>

<c:set var="theId" value="${name}" />
<c:if test="${not empty id}">
	<c:set var="theId" value="${id}" />
</c:if>
<c:set var="theInputColClass" value="col-xs-4" />
<c:if test="${not empty inputColClass}">
	<c:set var="theInputColClass" value="${inputColClass}" />
</c:if>
<c:set var="theInputErrorColClass" value="" />
<c:if test="${not empty inputErrorColClass}">
	<c:set var="theInputErrorColClass" value="${inputErrorColClass}" />
</c:if>
<c:set var="theInputTooltipPosition" value="right" />
<c:if test="${not empty inputTooltipPosition}">
	<c:set var="theInputTooltipPosition" value="${inputTooltipPosition}" />
</c:if>
<c:set var="theLabelTooltipPosition" value="right" />
<c:if test="${not empty labelTooltipPosition}">
	<c:set var="theLabelTooltipPosition" value="${labelTooltipPosition}" />
</c:if>

<spring:bind path="${name}">
	<div class="form-group <c:if test="${status.error}">has-error</c:if>" id="form-div-${theId}">
		<label class="control-label <c:if test="${not empty labelTooltip}">doTooltip</c:if>" for="${theId}" <c:if test="${not empty labelTooltip}">data-toggle="tooltip" data-original-title="${labelTooltip}" data-placement="${theLabelTooltipPosition}"</c:if>>
			${label}
			<c:if test="${not empty helpForLabelInline}">
				<span class="help-block" style="display:inline;"> ${helpForLabelInline}</span>
			</c:if>
		</label>
		<c:if test="${not empty helpForLabel}"><span class="help-block">${helpForLabel}</span></c:if>
		<div class="row">
			<div class="${theInputColClass}">
				<c:choose>
					<c:when test="${not empty inputTooltip}">
						<form:select path="${name}" class="form-control doTooltip" id="${theId}" items="${items}" itemLabel="${itemLabel}" itemValue="${itemValue}"
									data-toggle="tooltip" data-original-title="${inputTooltip}" data-placement="${theInputTooltipPosition}"/>
					</c:when>
					<c:otherwise>
						<form:select path="${name}" class="form-control" id="${theId}" items="${items}" itemLabel="${itemLabel}" itemValue="${itemValue}" />
					</c:otherwise>
				</c:choose>
				<c:if test="${not empty helpForInput}"><p class="help-block">${helpForInput}</p></c:if>
			</div>
			<c:if test="${status.error}">
				<div class="${theInputErrorColClass}">
					<span class="control-label">${status.errorMessage}</span>
				</div>
			</c:if>
		</div>
	</div>
</spring:bind>