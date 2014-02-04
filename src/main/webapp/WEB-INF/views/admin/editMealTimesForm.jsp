<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>Edit Meal Times</h2>

<div class="well">
	<form:form method="post" id="editMealTimesForm" commandName="editMealTimesModel" htmlEscape="true" onsubmit="saveEditedMealTimesToModel()" role="form">
		<c:forEach items="${editMealTimesModel.meals}" var="meal" varStatus="loopCounter">
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
			
		<input type="submit" class="btn btn-primary" value="Speichern" name="save" />
		<input type="submit" class="btn btn-danger" value="Abbrechen" name="cancel" />
				  
	</form:form>
</div>