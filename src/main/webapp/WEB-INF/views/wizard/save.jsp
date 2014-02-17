<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<%-- Render participant preview table --%>
<h2><spring:message code="headline.participantlist"/></h2>
<tiles:insertDefinition name="view-participants" />

<spring:message code="label.email" var="emailLabel"/>
<spring:message code="label.adminlink.email.help" var="labelEmailHelp" />

<form:form class="well" method="post" action="wizard" commandName="createWizardModel" htmlEscape="true" role="form">

	<bs:inputField name="email" label="${emailLabel}" placeholder="${emailLabel}" type="email" helpForInput="${labelEmailHelp}" inputColClass="col-xs-6" />
	
	<input type="hidden" value="3" name="_page" />
	<input type="submit" class="btn btn-success" value="<spring:message code="label.finalize" />" name="_finish" data-placement="bottom" data-toggle="tooltip" data-original-title="tooltip.createwizared.finish" />
	<input type="submit" class="btn btn-danger" value="<spring:message code="label.cancel" />" name="_cancel" />
</form:form>