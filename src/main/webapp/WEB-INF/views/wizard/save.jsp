<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<spring:message code="label.adminlink.email.help" var="labelEmailHelp" />

<%-- Render participant preview table --%>
<h2><spring:message code="label.runningdinner.participantlist.headline"/></h2>
<tiles:insertDefinition name="view-participants" />

<form:form class="well" method="post" id="startForm4" action="wizard" commandName="createWizardModel" htmlEscape="true" role="form">

	<bs:inputField name="email" label="EMail Adresse" placeholder="EMail Adresse" type="email" helpForInput="${labelEmailHelp}" inputColClass="col-xs-6"/>
	
	<input type="hidden" value="3" name="_page" />
	<input type="submit" class="btn btn-success" value="Fertigstellen" name="_finish" data-placement="bottom" data-toggle="tooltip" data-original-title="Speichert die Liste und erstellt einen Dinner-Plan" />
	<input type="submit" class="btn btn-danger" value="Abbrechen" name="_cancel" />
</form:form>