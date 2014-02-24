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
</script>