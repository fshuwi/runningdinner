<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2><spring:message code="label.runningdinner.times.headline"/></h2>
<div class="well">
	<form:form method="post" id="startForm2" commandName="createWizardModel" htmlEscape="true" onsubmit="saveMealTimesToModel()" role="form">
		<c:forEach items="${createWizardModel.meals}" var="meal" varStatus="loopCounter">
			<div class="form-group">
				<div class="row">
					<div class="col-xs-3">
						<label id="meal-${loopCounter.count}" class="control-label meal-label">${meal.label}</label>
						<input type="text" class="form-control meal-time" value="<fmt:formatDate type="time" value="${meal.time}" timeStyle="SHORT" />" id="time-${loopCounter.count}"/>
					</div>
				</div>
			</div>
		</c:forEach>
			
		<form:hidden path="meals" id="meals"/>
			
		<input type="hidden" value="1" name="_page" />
		<input type="submit" class="btn btn-primary" value="Weiter" name="_target2" />
		<input type="submit" class="btn btn-danger" value="Abbrechen" name="_cancel" />				  
	</form:form>
</div>