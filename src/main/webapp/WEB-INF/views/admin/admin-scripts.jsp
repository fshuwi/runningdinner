<script>
(function($) {
	/**
	 * attaches a character counter to each textarea element in the jQuery object
	 * usage: $("#myTextArea").charCounter(max, settings);
	 */
	
	$.fn.charCounter = function (max, settings) {
		max = max || 100;
		settings = $.extend({
			container: "<span></span>",
			classname: "charcounter",
			format: "(%1 characters remaining)",
			pulse: true,
			delay: 0
		}, settings);
		var p, timeout;
		
		function count(el, container) {
			el = $(el);
			if (el.val().length > max) {
				el.val(el.val().substring(0, max));
				if (settings.pulse && !p) {
					pulse(container, true);
				};
			};
			if (settings.delay > 0) {
				if (timeout) {
					window.clearTimeout(timeout);
				}
				timeout = window.setTimeout(function () {
					container.html(settings.format.replace(/%1/, (max - el.val().length)));
				}, settings.delay);
			} else {
				container.html(settings.format.replace(/%1/, (max - el.val().length)));
			}
		};
		
		function pulse(el, again) {
			if (p) {
				window.clearTimeout(p);
				p = null;
			};
			el.animate({ opacity: 0.1 }, 100, function () {
				$(this).animate({ opacity: 1.0 }, 100);
			});
			if (again) {
				p = window.setTimeout(function () { pulse(el) }, 200);
			};
		};
		
		return this.each(function () {
			var container;
			if (!settings.container.match(/^<.+>$/)) {
				// use existing element to hold counter message
				container = $(settings.container);
			} else {
				// append element to hold counter message (clean up old element first)
				$(this).next("." + settings.classname).remove();
				container = $(settings.container)
								.insertAfter(this)
								.addClass(settings.classname);
			}
			$(this)
				.unbind(".charCounter")
				.bind("keydown.charCounter", function () { count(this, container); })
				.bind("keypress.charCounter", function () { count(this, container); })
				.bind("keyup.charCounter", function () { count(this, container); })
				.bind("focus.charCounter", function () { count(this, container); })
				.bind("mouseover.charCounter", function () { count(this, container); })
				.bind("mouseout.charCounter", function () { count(this, container); })
				.bind("paste.charCounter", function () { 
					var me = this;
					setTimeout(function () { count(me, container); }, 10);
				});
			if (this.addEventListener) {
				this.addEventListener('input', function () { count(this, container); }, false);
			};
			count(this, container);
		});
	};

})(jQuery);





	var changedHostingFields = {};
	
	var checkedTeamMembers = [];

	$(document).ready(function() {
		$('.doTooltip').tooltip();
		
		// TODO: Swap this to extra file as it is only interesting for team overview
		$('#teamTabs a:first').tab('show');		
		
		// TODO: Check server workflow (Enable drag drop or not!))
		
		setUpDragDrop();
	
		$(".counted").charCounter(3000,{container: "#counter"});
	});
	
	function toggleTeamSelection() {
		checked = $('#allTeamsSelectedBox').is(":checked");
		$('.teamSelectionBox').prop("checked", checked);
	}
	
	
	// TODO: Copied
	function saveEditedMealTimesToModel() {
		var meals = [];
		
		$('.meal-label').each(function() {
			var label = $(this).text();
			var time = $(this).next().val();
			meals.push({"label":label, "time":time});
		});
		
		$('#meals').val(JSON.stringify(meals));
	}
	
	
	function setUpDragDrop() {
		 $(".draggableTeamMember").draggable({ revert: "invalid" });
		 $(".droppableTeamMember").droppable({
			accept: function(incoming) {
				if ($(this).attr("id") == $(incoming).attr("id")) {
					return false;
				}
				if ( $(this).parent().attr("id") == $(incoming).parent().attr("id") ) {
					return false;
				}
				return true;
			},
		 	drop: function( event, ui ) {
		 		firstParticipant = getParticipantKey($(this));
		 		secondParticipant = getParticipantKey(ui.draggable);
		 		
		 		// TODO: Provide fallback array (to reset in AJAX request)
		 		
		 		switchTeamMembers(firstParticipant, secondParticipant);
		 	}
		 });	
	}
	
	function getParticipantKey(element) {
		id = $(element).attr("id");
		rslt = id.split("_")[1];
		return rslt;
	}
	
	function onTeamHosterChanged(id) {
		changedHostingFields[id] = true;
	}
	
	function saveTeamHosts() {
		var jsonRequest = [];
		
		for (affectedTeam in changedHostingFields) {
			 var newHostingParticipant = $('#'+affectedTeam).val();
			 
			 var singleJson = {};
			 singleJson.teamKey = affectedTeam;
			 singleJson.participantKey = newHostingParticipant;
			 
			 jsonRequest.push(singleJson);
		}
		
		if (jsonRequest.length > 0) {
			
			var backendUrl = '${pageContext.request.contextPath}/event/${uuid}/admin/teams/savehosts';
			
		    $.ajax({
   		        url: backendUrl,
   		        data: JSON.stringify(jsonRequest),
   		        type: "POST",
   		     	dataType: 'json',
   		  		contentType: 'application/json',   	

   		        success: function(response) {
   		        	// TODO: Make own function!
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
	
	function switchTeamMembers(firstParticipant, secondParticipant) {
		jsonRequest = new Array();
		//jsonRequest.push({"participantKey" : firstParticipant});
		//jsonRequest.push({"participantKey" : secondParticipant});
		
		// TODO: Zu umstaendlich
		
		var singleJson = {};
	 	singleJson.teamKey = '-';
		singleJson.participantKey = firstParticipant;
		jsonRequest.push(singleJson);
		
		singleJson = {};
	 	singleJson.teamKey = '-';
		singleJson.participantKey = secondParticipant;
		jsonRequest.push(singleJson);
	
		
		var backendUrl = '${pageContext.request.contextPath}/event/${uuid}/admin/teams/switchmembers';

	    $.ajax({
	        url: backendUrl,
	        data: JSON.stringify(jsonRequest),
	        type: "POST",
	        async: false,
		    dataType: 'json',
   		  	contentType: 'application/json',
   		  		
	        success: function(response) {
	        	if (response.success) {
	        		changedTeams = response.changedTeams;
	        		firstTeam = changedTeams[0];
	        		secondTeam = changedTeams[1];

	        		updateSwitchedTeam(firstTeam);
	        		updateSwitchedTeam(secondTeam);
	        		
	        		setUpDragDrop();
	        	}
	        	else {
	        		// TODO: How to show error?
	        		// TODO #2: Set teams back!
	        		alert("Error: " + response.errorMessage);
	        	}
	        },
	        error: function (xhr, ajaxOptions, thrownError) {
	        	alert("AJAX Error: " + xhr.responseText);
	        	// TODO: Set teams back!
	        }
		});	
	}
	
	
	function updateSwitchedTeam(switchedTeam) {
		teamKey = switchedTeam.naturalKey;
		teamMembers = switchedTeam.teamMembers;
		
		teamInfoContainer = $("#teaminfo_"+teamKey);
		teamHostSelect = $("#"+teamKey);
		
		// Update team member labels:
		$(teamInfoContainer).empty();
		for (var i = 0; i < teamMembers.length; i++) {
			teamMember = teamMembers[i];
			var newTeamMemberDiv = "<div class=\"draggableTeamMember droppableTeamMember\" id=\"participant_" + teamMember.naturalKey + "\">";
			newTeamMemberDiv += "<h5 class=\"media-heading\"><a href=\"#\">" + teamMember.fullname + "</a></h5>";
			newTeamMemberDiv += "</div>";
			
			// $(newTeamMemberDiv).draggable({ revert: "invalid" });
			
			$(teamInfoContainer).append($(newTeamMemberDiv));
		}
		
		// Select control for hosts
		$(teamHostSelect).empty();
		for (var i = 0; i < teamMembers.length; i++) {
			teamMember = teamMembers[i];
			var selectOption = $('<option></option>').attr("value", teamMember.naturalKey).text(teamMember.fullname);
			if (teamMember.host) {
				$(selectOption).attr("selected", "selected"); // TODO: Funktioniert wohl noch nicht ganz!
			}
			$(teamHostSelect).append(selectOption);
		}
	}
		
</script>
