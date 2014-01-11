<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<spring:message code="label.adminlink.help" var="adminLinkHelp"/>

<h2><spring:message code="label.runningdinner.wizard.finished.headline" text="Fertig!"/></h2>

<div class="well">
	<form:form method="post" id="startForm5" onsubmit="return false;" role="form" htmlEscape="true" commandName="createWizardModel">
		<bs:inputField name="administrationUrl" label="Administrations-Link" readonly="true" 
					   cssStyle="cursor:default;" helpForInput="${adminLinkHelp}" inputColClass="col-xs-7"/>
		
		<div class="form-group">
			<a href="${createWizardModel.administrationUrl}">Goto Administration</a>
		</div>
	</form:form>
</div>