<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spring:message code="error.teams.noselection" var="noselectionText"/>

<script>
	$(document).ready(function() {
		if (typeof charCounters != 'undefined') {
			for (var counterId in charCounters) {
				var maxAllowedChars = charCounters[counterId];
				$("#"+counterId).charCounter(maxAllowedChars,{container: "#"+counterId+"_counter"});
			}
		}
	});
	
	function toggleTeamSelection() {
		checked = $('#allTeamsSelectedBox').is(":checked");
		$('.teamSelectionBox').prop("checked", checked);
	}
	
	function isOneOrMoreTeamsSelected() {
		result = false;
		$('.teamSelectionBox').each(function() {
			if ($(this).is(":checked")) {
				result = true;
				return;
			}
		});
		
		if (!result) {
			alert('${noselectionText}');
		}
		return result;
	}
</script>