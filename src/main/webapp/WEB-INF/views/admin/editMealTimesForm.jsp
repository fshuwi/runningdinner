<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<h3 class="contentheadline">Edit Meal Times</h3>

<div class="well">
	<form:form method="post" id="editMealTimesForm" commandName="editMealTimesModel" htmlEscape="true" onsubmit="saveMealTimesToModel()" role="form">
		
		<tiles:insertDefinition name="view-mealtimes">
			<tiles:putAttribute name="meals" value="${editMealTimesModel.meals}" />
		</tiles:insertDefinition>
					
		<form:hidden path="meals" id="meals"/>
			
		<input type="submit" class="btn btn-primary" value="Speichern" name="save" />
		<input type="submit" class="btn btn-danger" value="Abbrechen" name="cancel" />
				  
	</form:form>
</div>