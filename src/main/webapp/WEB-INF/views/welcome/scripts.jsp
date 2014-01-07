<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script>
	$(document).ready(function() {
		$('.doTooltip').tooltip();		
		highlightErrors(['title','date','teamSize', 'meals', 'file']);
	});
	
	function addMeal() {
		var numMealInputs = $('.mealclass-input').length;
		if (numMealInputs >= 6) {
			// Max. 6 meals allowed
			alert('<spring:message code="error.meals.invalidsize"/>');
			return;
		}
		
		var seqNr = numMealInputs + 1;
		
		removeMealContainer = $('#removeMealContainer');
		if (removeMealContainer.length > 0) {
			// Remove 'remove'-button from last text input field (only one remove-btn allowed)
			$(removeMealContainer).remove();
		}
		
		var appendStr = "<div class='form-group mealclass-div' id='mealclass-div-" + seqNr + "'>";
		appendStr += "	<div class='input-group col-xs-4'>";
		appendStr += "  	<input type='text' class='form-control mealclass-input' value='' id='meal-" + seqNr + "' />";
		appendStr += getRemoveBtnStr('mealclass-div-'+seqNr);
		appendStr += "</div></div>";
		
		$(appendStr).appendTo($('#mealcontainer'));
		
		$('#meal-' + seqNr).focus();
	}
	
	function removeMeal(containerId) {
		var numMealInputs = $('.mealclass-input').length;
		if (numMealInputs <= 2) {
			// Should never happen actually
			alert('<spring:message code="error.meals.invalidsize"/>');
			return;
		}
		
		var containerToRemove = $('#'+containerId);
		
		if (numMealInputs > 3) {
			// The last input field must get the remove-button now:
			
			var previousContainer = $(containerToRemove).prev();
			var previousContainerId = $(previousContainer).attr('id');
			
			var divToAppend = $(previousContainer).children('div').eq(0);
			
			$(getRemoveBtnStr(previousContainerId)).appendTo($(divToAppend));
		}
		
		// Finally remove the current meal-container
		$(containerToRemove).remove();
	}
	
	function getRemoveBtnStr(containerId) {
		return "<span class='input-group-btn' id='removeMealContainer'><a href=\"javascript:removeMeal('" + containerId + "')\" class='btn btn-danger'>-</a></span>";
	}
	
	function saveMealsToModel() {
		var meals = [];	
	
		$('.mealclass-input').each(function() {
			meal = $(this).val();
			meals.push({"label" : meal});
		});
		
		$('#meals').val(JSON.stringify(meals));
	}
	
	function saveMealTimesToModel() {
		var meals = [];
		
		$('.meal-label').each(function() {
			var label = $(this).text();
			var time = $(this).next().val();
			meals.push({"label":label, "time":time});
		});
		
		$('#meals').val(JSON.stringify(meals));
	}
	
</script>