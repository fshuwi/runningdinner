<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<spring:message code="label.adminlink.help" var="adminLinkHelp"/>

<h2><spring:message code="label.runningdinner.wizard.finish.headline"/></h2>

<div class="well">
	<form:form method="post" id="startForm5" commandName="createWizardModel" htmlEscape="true" onsubmit="return false;" role="form">
		<bs:inputField name="administrationUrl" label="" readonly="true" cssStyle="cursor:default;" helpForInput="${adminLinkHelp}" />
		
		<div class="form-group">
			<a href="#">Goto Administration</a>
		</div>
	</form:form>
</div>