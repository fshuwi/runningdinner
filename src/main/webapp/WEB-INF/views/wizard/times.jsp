<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<h2><spring:message code="headline.mealtimes" /></h2>

<div class="well">
	<form:form method="post" commandName="createWizardModel" htmlEscape="true" onsubmit="saveMealTimesToModel()" role="form">
			
		<tiles:insertDefinition name="view-mealtimes">
			<tiles:putAttribute name="meals" value="${createWizardModel.meals}" />
		</tiles:insertDefinition>	
			
		<form:hidden path="meals" id="meals"/>
			
		<input type="hidden" value="1" name="_page" />
		<input type="submit" class="btn btn-primary" value="<spring:message code="label.next" />" name="_target2" />
		<input type="submit" class="btn btn-danger" value="<spring:message code="label.cancel" />" name="_cancel" />				  
	</form:form>
</div>