<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<spring:message code="label.title" text="Titel" var="titleLabel" />
<spring:message code="label.date" text="Datum" var="dateLabel" />
<spring:message code="label.city" text="Stadt" var="cityLabel" />

<h2><spring:message code="label.runningdinner.new"/></h2>

<div class="well">
	
	<form:form method="post" id="startForm1" commandName="createWizardModel" htmlEscape="true" onsubmit="saveMealsToModel()" role="form"> 
			
 		<div class="form-group" id="title.form.div">
			<label class="control-label" for="title">${titleLabel}</label>
			<div class="row">
				<div class="col-xs-4">
					<form:input path="title" class="form-control" id="title" placeholder="${titleLabel}" />
				</div>
				<div class="col-xs-4">
					<form:errors path="title" cssClass="control-label"/>
				</div>
			</div>
 		</div>
 			
 		<div class="form-group" id="date.form.div">
			<label class="control-label" for="date">${dateLabel}</label>
			<div class="row">
				<div class="col-xs-4">
					<form:input path="date" class="form-control" id="date" placeholder="${dateLabel}" />
				</div>
				<div class="col-xs-4">
					<form:errors path="date" cssClass="control-label" />
				</div>
			</div>
 		</div> 
 
 		<div class="form-group">
			<label for="city">${cityLabel} <span class="help-block" style="display:inline;">(optional)</span></label>
			<div class="row"><div class="col-xs-4">
				<form:input path="city" class="form-control" id="city" placeholder="${cityLabel}" />
			</div></div>
 			</div>
 				  
		<div class="panel panel-primary">
  			<div class="panel-heading"><h4 class="panel-title">Erweiterte Dinner Optionen</h4></div>
  			<div class="panel-body">
  
			<div class="form-group">
				<label for="teamSize"><spring:message code="label.teamsize" text="Teamgröße" /></label>
				<div class="row">
					<div class="col-xs-2">
						<form:input path="teamSize" class="form-control" id="teamSize" />
					</div>
					<div clasS="col-xs-4">
						<form:errors path="teamSize" cssClass="control-label" />
					</div>
				</div>
			</div>
		
			<label>Speisen</label>
			<span class="help-block"><spring:message code="label.mealsinfo" /></span>
			<div id="mealcontainer">
				<c:forEach items="${createWizardModel.meals}" var="meal" varStatus="loopCounter">
					<div class="form-group mealclass-div" id="mealclass-div-${loopCounter.count}">
						<div class="input-group col-xs-4">
							<input type="text" class="form-control mealclass-input" value="${meal.label}" id="meal-${loopCounter.count}" />
							<c:if test="${loopCounter.last}">
								<span class="input-group-btn" id="removeMealContainer"><a href="javascript:removeMeal('mealclass-div-${loopCounter.count}')" class="btn btn-danger">-</a></span>
							</c:if>
						</div>
					</div>				
				</c:forEach>
			</div>
			<div class="form-group" id="meals.form.div">
				<form:errors path="meals" cssClass="control-label" cssStyle="font-weight:bold;display:block;margin-bottom:5px;"/>
				<a href="javascript:addMeal()" class="btn btn-success"><span class="glyphicon glyphicon-plus"></span> Hinzufügen</a>
			</div>
			
			<label for="equalTeamDistribution">Aufteilung der Teams</label>
			<div class="form-group">
				<div class="checkbox">
					<label class="doTooltip" data-placement="right" data-toggle="tooltip" data-original-title="<spring:message code="tooltip.teamdistribution.hosting" />">
						<form:checkbox path="equalTeamDistribution" id="equalTeamDistribution" class="doTooltip" /><spring:message code="label.teamdistribution.host" />
					</label>
				</div>
			</div>

			<label>Geschlechter-Aspekte</label>
			<span class="help-block"><spring:message code="label.teamdistribution.gender.help" /></span>
			<div class="form-group col-xs-4">
				<div class="row">
					<spring:message code="tooltip.teamdistribution.gender" var="genderTooltip"/>
					<form:select path="genderTeamDistribution" id="genderTeamDistribution"  items="${genderAspects}" itemLabel="label" itemValue="value" 
								class="form-control doTooltip" data-placement="right" 
								data-toggle="tooltip" data-original-title="${genderTooltip}">
					</form:select>
				</div>
			</div>
				
	  	</div>
	</div><%-- End panel --%>
	
	<form:hidden path="meals" id="meals"/>
	
	<input type="hidden" value="0" name="_page" />
	<input type="submit" class="btn btn-primary" value="Weiter" name="_target1" />				  
	
  </form:form>

</div>