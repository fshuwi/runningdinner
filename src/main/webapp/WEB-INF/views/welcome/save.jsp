<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2><spring:message code="label.runningdinner.participantlist.headline"/></h2>
<div class="well">
	<form:form method="post" id="startForm4" commandName="createWizardModel" htmlEscape="true" onsubmit="" role="form">
		<div class="form-group">
			<div class="row">
				<div class="col-xs-3">
					<label class="control-label meal-label">Dateipfad</label>
					<form:input path="uploadedFilePath" class="form-control"/>
				</div>
			</div>
		</div>
			
		<input type="hidden" value="3" name="_page" />
		<input type="submit" class="btn btn-success" value="Fertigstellen" name="_finish" />
		<input type="submit" class="btn btn-danger" value="Abbrechen" name="_cancel" />				  
	</form:form>
</div>