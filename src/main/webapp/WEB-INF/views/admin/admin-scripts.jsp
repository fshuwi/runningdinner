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
   		        	
   		        	$(responseContainer).removeClass("hidden");
   		        	$(responseContainer).empty();
   		        	$(responseContainer).addClass("show");
   		        	
   		        	if (response.success) {
   		        		changedHostingFields = {}; // Reset changed fields
 
   		        		var responseMessage = "<div class='alert alert-success alert-dismissable'>";
   		        		responseMessage += "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>";
   		        		responseMessage += "<strong>Success:</strong> All team hosts have been successfully saved!";
   		        		responseMessage += "</div>";
   		        		$(responseMessage).appendTo($(responseContainer));
   		        	}
   		        	else {
   		        		var responseMessage = "<div class='alert alert-danger'>";
   		        		responseMessage += '<strong>Error:</strong> ' + response.errorMessage;
   		        		responseMessage += "</div>";
   		        		$(responseMessage).appendTo($(responseContainer));
   		        	}
   		        }
   		    });
		}
	}
</script>
