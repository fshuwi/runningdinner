<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:eval expression="@globalProperties['mail.from']" var="standardMailSender"/>
<spring:message code="tooltip.mailserver.standard" var="standardMailServerTooltip" arguments="standardMailSender"/>
<spring:message code="tooltip.mailserver.custom" var="customMailServerTooltip"/>
<div class="well">
	<div class="form-group">
		<label><spring:message code="label.mailserver.settings"/></label><br/>
		<div class="btn-group">
			<button type="button" class="btn btn-success" id="mailStandardBtn" onclick="toggleMailServer('standard')"
				data-toggle="popover" data-container="body" data-trigger="focus" 
				data-content='${standardMailServerTooltip}' data-placement="left">
				<spring:message code="text.mailserver.standard"/>
			</button>
			<button type="button" class="btn btn-default" id="mailCustomBtn" onclick="toggleMailServer('custom')"
					data-toggle="popover" data-container="body" data-trigger="focus" data-content='${customMailServerTooltip}' data-placement="right">
					<spring:message code="text.mailserver.custom"/>
			</button>
		</div>
	</div>
	<div id="mailConfigurationLink" style="display:none;">
		<a href="#mailServerSettings" data-toggle="modal" data-target="#mailServerSettings"><spring:message code="label.mailserver.settings.edit" /></a>
	</div>			
</div>

<form:hidden path="mailServer" id="mailServer" />
<form:hidden path="mailServerPort" id="mailServerPort" />
<form:hidden path="username" id="username" />
<form:hidden path="password" id="password" />
<form:hidden path="useTls" id="useTls" />
<form:hidden path="from" id="from" />
<form:hidden path="useCustomMailServer" id="useCustomMailServer" />