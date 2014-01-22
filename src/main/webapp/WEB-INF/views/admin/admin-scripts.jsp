<script>
	var changedHostingFields = {};

	$(document).ready(function() {
		$('.doTooltip').tooltip();
		
		// TODO: Swap this to extra file as it is only interesting for team overview
		$('#teamTabs a:first').tab('show');		
	});
	
	function onTeamHosterChanged(id) {
		changedHostingFields[id] = true;
	}
	
	function saveTeamHosts() {
		var jsonRequest = [];
		
		for (affectedTeam in changedHostingFields) {
			 var newHostingParticipant = $('#'+affectedTeam).val();
			 
			 var singleJson = {};
			 singleJson.teamKey = affectedTeam;
			 singleJson.newHostParticipantKey = newHostingParticipant;
			 
			 jsonRequest.push(singleJson);
		}
		
		if (jsonRequest.length > 0) {
			var backendUrl = '${pageContext.request.contextPath}/event/${uuid}/admin/teams/savehosts';
			
		    $.ajax({
   		        url: backendUrl,
   		        data: JSON.stringify(jsonRequest),
   		        type: "POST",
   	
   		        beforeSend: function(xhr) {
   		            xhr.setRequestHeader("Accept", "application/json");
   		            xhr.setRequestHeader("Content-Type", "application/json");
   	        	},
   		        success: function(response) {
   		        	responseContainer = $('#saveTeamHostsResponse');
   		        	$(responseContainer).removeClass("alert-danger alert-success hidden");
   		        	$(responseContainer).empty();
   		        	
   		        	if (response.success) {
   		        		$(responseContainer).addClass("alert-success alert-dismissable show");
   		        		content = "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>";
   		        		content = content + "<strong>Success:</strong> All team hosts have been successfully saved!";
   		        		alert(content);
   		        		$(content).appendTo($(responseContainer));
   		        	}
   		        	else {
   		        		$(responseContainer).addClass("alert-danger show");
   		        		$('<strong>Error:</strong> ' + response.errorMessage).appendTo($(responseContainer));
   		        	}
   		        }
   		    });
		}
	}
</script>
