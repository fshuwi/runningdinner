<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%><%

%><%@ attribute name="name" required="true" description="Name of corresponding property in bean object" %>
<%@ attribute name="label" required="true" description="Label appears in red color if input is considered as invalid after submission" %>
<%@ attribute name="id" required="false" rtexprvalue="false" description="id for input field (if not set, the name is taken)" %>
<%@ attribute name="checkboxText" required="true" rtexprvalue="true" description="Text that appears right from checkbox" %><%

%><%@ attribute name="helpForLabelInline" required="false" description="inline label help text" %>
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
<c:set var="theInputColClass" value="checkbox" />
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
	<label class="control-label <c:if test="${not empty labelTooltip}">doTooltip</c:if>" for="${theId}" <c:if test="${not empty labelTooltip}">data-toggle="tooltip" data-original-title="${labelTooltip}" data-placement="${theLabelTooltipPosition}"</c:if>>
		${label}
		<c:if test="${not empty helpForLabelInline}">
			<span class="help-block" style="display:inline;"> ${helpForLabelInline}</span>
		</c:if>
	</label>
	<c:if test="${not empty helpForLabel}"><span class="help-block">${helpForLabel}</span></c:if>
	<div class="form-group <c:if test="${status.error}">has-error</c:if>" id="form-div-${theId}">
		<div class="${theInputColClass}">
			<c:choose>
				<c:when test="${not empty inputTooltip}">
					<label class="doTooltip" data-placement="${theInputTooltipPosition}" data-toggle="tooltip" data-original-title="${inputTooltip}">
						<form:checkbox path="${name}" id="${theId}" class="doTooltip" />${checkboxText}
					</label>
				</c:when>
				<c:otherwise>
					<label><form:checkbox path="${name}" id="${theId}" />${checkboxText}</label>
				</c:otherwise>
			</c:choose>
		</div>
		<c:if test="${not empty helpForInput}"><p class="help-block">${helpForInput}</p></c:if>
		<c:if test="${status.error}">
			<div class="${theInputErrorColClass}">
				<span class="control-label">${status.errorMessage}</span>
			</div>
		</c:if>
	</div>
</spring:bind>