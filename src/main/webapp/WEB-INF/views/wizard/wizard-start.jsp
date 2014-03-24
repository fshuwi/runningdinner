<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<spring:message code="label.title" text="Titel" var="titleLabel" />
<spring:message code="label.date" text="Datum" var="dateLabel" />
<spring:message code="label.city" text="Stadt" var="cityLabel" />
<spring:message code="label.teamsize" var="teamsizeLabel" />
<spring:message code="label.teamdistribution.host" var="teamDistributionText" />
<spring:message code="label.teamdistribution.gender.help" var="genderHelpText"/>
<spring:message code="label.teamdistribution" var="teamdistributionLabel" />

<spring:message code="tooltip.title" var="titleTooltip" />
<spring:message code="tooltip.date" var="dateTooltip" />
<spring:message code="tooltip.city" var="cityTooltip" />
<spring:message code="tooltip.teamsize" var="teamsizeTooltip" />
<spring:message code="tooltip.teamdistribution" var="teamdistributionTooltip" />
<spring:message code="tooltip.teamdistribution.gender" var="teamdistributionGenderTooltip" />

<h2><spring:message code="label.runningdinner.new"/></h2>

<div class="well">
	
	<form:form method="post" commandName="createWizardModel" htmlEscape="true" onsubmit="saveMealsToModel()" role="form"> 
			
		<bs:inputField name="title" label="${titleLabel}" placeholder="${titleLabel}" inputTooltip="${titleTooltip}" />
		
		<bs:inputField name="date" label="${dateLabel}" placeholder="${dateLabel}" inputColClass="col-xs-2" inputTooltip="${dateTooltip}" inputTooltipPosition="top" />
		
		<bs:inputField name="city" label="${cityLabel}" placeholder="${cityLabel}" helpForLabelInline="(optional)" inputTooltip="${cityTooltip}"/>
 	  
		<div class="panel panel-primary">
  			<div class="panel-heading"><h4 class="panel-title"><spring:message code="label.dinneroptions.advanced" /></h4></div>
  			<div class="panel-body">
  
  				<bs:inputField name="teamSize" label="${teamsizeLabel}" inputColClass="col-xs-1" inputTooltip="${teamsizeTooltip}" />

				<label><spring:message code="label.meals" /></label>
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
					<a href="javascript:addMeal()" class="btn btn-success"><span class="glyphicon glyphicon-plus"></span> <spring:message code="label.add"/></a>
				</div>
			
				<bs:singleCheckbox name="equalTeamDistribution" checkboxText="${teamDistributionText}" label="${teamdistributionLabel}" inputTooltip="${teamdistributionTooltip}"/>
				
				<bs:selectField name="genderTeamDistribution" itemLabel="label" itemValue="value" items="${genderAspects}" 
								label="Geschlechter-Aspekte" helpForLabel="${genderHelpText}" inputTooltip="${teamdistributionGenderTooltip}"/>					
	  		</div>
		</div><%-- End panel --%>
	
	<form:hidden path="meals" id="meals"/>
	
	<input type="hidden" value="0" name="_page" />
	<input type="submit" class="btn btn-primary" value="<spring:message code="label.next" />" name="_target1" />				  
	
  </form:form>

</div>