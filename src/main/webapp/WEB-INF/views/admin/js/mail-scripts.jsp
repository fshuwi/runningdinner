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
		
		if ($('#previewDialog').length) {
			$('#previewDialog').modal();
		}
	});
	
	function toggleEntitySelection() {
		checked = $('#allEntitiesSelectedBox').is(":checked");
		$('.entitySelectionBox').prop("checked", checked);
	}
	
	function isOneOrMoreEntitiesSelected() {
		result = false;
		$('.entitySelectionBox').each(function() {
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