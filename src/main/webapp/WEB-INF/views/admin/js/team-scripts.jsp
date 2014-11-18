<%@page import="org.runningdinner.ui.RequestMappings"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<spring:url value="<%=RequestMappings.AJAX_SWITCH_TEAMMEMBERS%>" var="teamsSwitchMembersUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>
<spring:url value="<%=RequestMappings.AJAX_SAVE_HOSTS%>" var="teamsSaveHostUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>
<spring:url value="<%=RequestMappings.AJAX_GET_CROSSING_TEAMS_PREFIX%>" var="crossedTeamPrefixUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>

<spring:message code="error.general" var="errorText"/>
<spring:message code="label.error" var="errorLabel"/>
<spring:message code="label.success" var="successLabel"/>
<spring:message code="text.hosts.saved" var="hostsSavedLabel"/> 
<spring:message code="text.hosts.noaction" var="hostsNoAction"/>

<script>
	var changedHostingFields = {};
	var checkedTeamMembers = [];

	$(document).ready(function() {
		setUpDragDrop();		
		setUpTeamTooltips();
	});
	
	
	function setUpTeamTooltips() {
	    
		$('.tooltip').tooltipster({
			content: $("<span>Laden...</span>" + "<img src='<c:url value="/resources/images/ajax-loader.gif" />' class='tooltip-loader-img' />"),
			theme: 'tooltipster-shadow',
			position: 'right',
			delay: 100,
			animation: 'fade',
			trigger: 'click',
			functionBefore: function(origin, continueTooltip) {
			    continueTooltip();
			    
		       // if (origin.data('ajax') !== 'cached') {
			   		var teamKey = origin.attr("teamKey");
			   		var teamNumber = origin.attr("teamNumber");
			   		
			   		var url = '${crossedTeamPrefixUrl}/' +  teamKey + "/crossingteams";
		            $.ajax({
		        		dataType: 'json',
		                type: 'GET',
		                url: url,
		                success: function(result) {
		                    // update our tooltip content with our returned data and cache it
		                    
		                    var tooltipContent = "<div class='row'><div class='col-xs-12'>"
		                    tooltipContent += "<h3>Team " + teamNumber + " trifft folgende Teams&nbsp;&nbsp;";
		                    tooltipContent += "<button type='button' class='close' style='cursor:pointer ! important;'><span aria-hidden='true' style='cursor:pointer ! important;'>&times;</span><span class='sr-only'>Close</span></button></h3>";
		                    tooltipContent += "<hr/></div></div>";
		                    
		                    var len = result.length;
		                    for (var i=0; i<len; i+=2) {
		                		var team = result[i];
		                		var teamMembers = team.teamMembers;
		                		var teamMembersLen = teamMembers.length;
		                		
		                		var teamStr = "<div class='row'>";
		                		
		                		teamStr += renderSingleTeam(team);
		                		if (i+1 < len) {
		                		    team = result[i+1];
		                		}
		                		teamStr += renderSingleTeam(team);
	
		                		teamStr += "</div>";
		                		
		                		tooltipContent += teamStr;
		                    }
		                    
		                    origin.tooltipster('content', $(tooltipContent)).data('ajax', 'cached');
		                    
		                    $(".tooltip-loader-img").hide();
		                }
		            });
		       // }
		       
		       function renderSingleTeam(team) {
					var teamMembers = team.teamMembers;
					var teamMembersLen = teamMembers.length;
					
					var result = "<div class='col-xs-6'><b>Team " + team.teamNumber + "</b><br/>";
					
					for (var j=0; j<teamMembersLen;j++) {
					    result += teamMembers[j].fullname + "<br/>";
					}
					
					result += "<br/></div>";
					return result;
		       }
			    
			}
		});    
	}
	
	function setUpDragDrop() {
		 $(".draggableTeamMember").draggable({ revert: "invalid" });
		 $(".droppableTeamMember").droppable({
			accept: function(incoming) {
				if ($(this).attr("participantKey") == $(incoming).attr("participantKey")) {
					return false;
				}
				if ( $(this).parent().attr("teamKey") == $(incoming).parent().attr("teamKey") ) {
					return false;
				}
				return true;
			},
		 	drop: function( event, ui ) {
		 		firstParticipant = getParticipantKey($(this));
		 		secondParticipant = getParticipantKey(ui.draggable);
		 		
		 		// Generate fallback for resetting ui state when AJAX request fails:
		 		var switchTeamsFallback = new Array();
		 		switchTeamsFallback.push( getTeamFallback($(this).parent()) );
		 		switchTeamsFallback.push( getTeamFallback($(ui.draggable).parent()) );
		 		
		 		switchTeamMembers(firstParticipant, secondParticipant, switchTeamsFallback);
		 	}
		 });
	}
	
	
	// Generate JSON structure for team (same as for successful AJAX request) that is used for re-setting UI context in case of failed AJAX requests
	function getTeamFallback(teamContainer) {
		// Construct JSON Result (if server-side structure changes, this must also be changed):
		var rslt = {};
		
		var teamKey = $(teamContainer).attr('teamKey');
		rslt.naturalKey = teamKey;
		rslt.teamMembers = new Array();
		
		// Iterate through team members of the current team:
	 	$(teamContainer).children(".draggableTeamMember").each(function() {
	 		var teamMember = {};
	 		teamMember.naturalKey = $(this).attr('participantKey');
	 		teamMember.fullname = $(this).find('.teamMember').text();
	 		
	 		teamMember.host = false;
	 		var hostingSelectValue = $("select[teamKey='" + teamKey + "']").val();
	 		if (teamMember.naturalKey == hostingSelectValue) {
	 			teamMember.host = true;
	 		}

	 		teamMember.editLink = $(this).find('.teamMember').attr("href");
	 		
	 		rslt.teamMembers.push(teamMember);
	 	});
		
		return rslt;
	}
	
	function getParticipantKey(element) {
		rslt = $(element).attr("participantKey");
		return rslt;
	}
	
	// Track changed team hosts
	function onTeamHosterChanged(teamKey) {
		changedHostingFields[teamKey] = true;
	}
	
	function saveTeamHosts() {
		var jsonRequest = [];
		
		for (affectedTeamKey in changedHostingFields) {
			 var newHostingParticipant =  $("select[teamKey='" + affectedTeamKey + "']").val();
			 
			 var singleJson = {};
			 singleJson.teamKey = affectedTeamKey;
			 singleJson.participantKey = newHostingParticipant;
			 
			 jsonRequest.push(singleJson);
		}
		
		if (jsonRequest.length > 0) {
			$("#savehosts-ajax-loader").show();
		    $.ajax({
   		        url: '${teamsSaveHostUrl}',
   		        data: JSON.stringify(jsonRequest),
   		        type: "POST",
   		     	dataType: 'json',
   		  		contentType: 'application/json',   	

   		        success: function(response) {
   		        	$("#savehosts-ajax-loader").hide();
   		        	updateSavedTeamHosts(response);
   		        },
		        error: function (xhr, ajaxOptions, thrownError) {
		        	$("#savehosts-ajax-loader").hide();
		        	alert('${errorText}');
		        }
   		    });
		}
		else {
			updateNoTeamHostChange();
		}
	}
	
	function switchTeamMembers(firstParticipant, secondParticipant, switchTeamsFallback) {
		
		jsonRequest = new Array();
		jsonRequest.push({"participantKey" : firstParticipant});
		jsonRequest.push({"participantKey" : secondParticipant});
	    
	    //show the loading image directly over the second participant
	    showDragDropLoadingImage(secondParticipant);
		
	    $.ajax({
	        url: '${teamsSwitchMembersUrl}',
	        data: JSON.stringify(jsonRequest),
	        type: "POST",
	        async: false,
		    dataType: 'json',
   		  	contentType: 'application/json',
   		  		
	        success: function(response) {
	        	$("#dragdrop-ajax-loader").hide();
	        	if (response.success) {
	        		changedTeams = response.changedTeams;
	        		firstTeam = changedTeams[0];
	        		secondTeam = changedTeams[1];

	        		updateSwitchedTeam(firstTeam);
	        		updateSwitchedTeam(secondTeam);
	        	}
	        	else {
	        		restoreOldTeamMembersState(switchTeamsFallback);
	        		alert(response.errorMessage);
	        	}
	        	
	        	setUpDragDrop(); // Some DOM elements may have changed => re-setup the drag'n'drop functionality
	        },
	        error: function (xhr, ajaxOptions, thrownError) {
	        	$("#dragdrop-ajax-loader").hide();
	        	restoreOldTeamMembersState(switchTeamsFallback);
	        	alert('${errorText}');
	        	setUpDragDrop(); // Some DOM elements may have changed => re-setup the drag'n'drop functionality
	        }
		});	
	}
	
	function showDragDropLoadingImage(participantKey) {
	    participantDiv = $("div[participantKey='" + participantKey + "']");
    	var pos = $(participantDiv).position();
    	var width = $(participantDiv).outerWidth();
	    $("#dragdrop-ajax-loader").css({
	        position: "absolute",
	        top: pos.top + "px",
	        left: (pos.left + width - 30) + "px"
	    }).show();	
	}
	
	// Restored UI context of teams from previous failed AJAX request
	function restoreOldTeamMembersState(switchTeamsFallback) {
		for (var i = 0; i < switchTeamsFallback.length; i++) {
			updateSwitchedTeam(switchTeamsFallback[i]);
		}
	}
	
	// Refrehs UI context for switched teams from previous AJAX request
	function updateSwitchedTeam(switchedTeam) {
		teamKey = switchedTeam.naturalKey;
		teamMembers = switchedTeam.teamMembers;
		teamNumber = switchedTeam.teamNumber;
		
		teamInfoContainer = $("div[teamKey='" + teamKey + "']"); 
		teamHostSelect = $("select[teamKey='" + teamKey + "']");
		
		// Update team member labels:
		$(teamInfoContainer).empty();
		for (var i = 0; i < teamMembers.length; i++) {
			teamMember = teamMembers[i];
			var newTeamMemberDiv = "<div class=\"draggableTeamMember droppableTeamMember\" participantKey=\"" + teamMember.naturalKey + "\">";
			var editLink = '${pageContext.request.contextPath}'+teamMember.editLink;
			newTeamMemberDiv += "<h5 class=\"media-heading\"><a class='teamMember' href=\"" + editLink + "\">" + teamMember.fullname + "</a></h5>";
			newTeamMemberDiv += "</div>";
			
			$(teamInfoContainer).append($(newTeamMemberDiv));
		}
		
		// Update team number labels (hosting distribution)
		$('#teamNumber_'+teamNumber).removeClass(); // Remove all classes
		$('#teamNumber_'+teamNumber).addClass(mapDistributionToCssClass(switchedTeam));
		
		// Select control for hosts
		$(teamHostSelect).empty();
		for (var i = 0; i < teamMembers.length; i++) {
			teamMember = teamMembers[i];
			var selectOption = $('<option></option>').attr("value", teamMember.naturalKey).text(teamMember.fullname);
			if (teamMember.host) {
				$(selectOption).attr("selected", "selected");
			}
			$(teamHostSelect).append(selectOption);
		}
		
		// Update (potentially) changed zip:
		$("span[teamKeyZip='" + teamKey + "']").text(switchedTeam.hostZip);
	}
	
	function mapDistributionToCssClass(team) {
	    var result = "label label-success";
	    if (team.unbalancedDistribution) {
			result = "label label-danger";
	    }
	    else if (team.overbalancedDistribution) {
			result = "label label-warning";
	    }
	    return result;
	}
	
	// Refrehs UI context from successful AJAX request after some team hosts may have been changed
	function updateSavedTeamHosts(response) {
       	responseContainer = $('#saveTeamHostsResponse');
       	
       	$(responseContainer).removeClass("hidden");
       	$(responseContainer).empty();
       	$(responseContainer).addClass("show");
       	
       	if (response.success) {
       		changedHostingFields = {}; // Reset changed fields

       		// Update (potentially) changed zip fields of saved teams:
       		var savedTeams = response.savedTeams;
       		for (var i=0; i<savedTeams.length;i++) {
       			var savedTeam = savedTeams[i];
       			$("span[teamKeyZip='" + savedTeam.naturalKey + "']").text(savedTeam.hostZip);
       		}
       		
       		var responseMessage = getSuccessBox("${successLabel}", "${hostsSavedLabel}");
       		$(responseMessage).appendTo($(responseContainer));
       	}
       	else {
       		var responseMessage = "<div class='alert alert-danger'>";
       		responseMessage += '<strong>${errorLabel}</strong> ' + response.errorMessage;
       		responseMessage += "</div>";
       		$(responseMessage).appendTo($(responseContainer));
       	}
	}
	
	// Indicate user that nothing has changed after trying to update team hosts
	function updateNoTeamHostChange() {
       	responseContainer = $('#saveTeamHostsResponse');
       	$(responseContainer).removeClass("hidden");
       	$(responseContainer).empty();
       	$(responseContainer).addClass("show");
       	
   		var responseMessage = "<div class='alert alert-info alert-dismissable'>";
   		responseMessage += "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>";
   		responseMessage += '<strong>Info:</strong> ${hostsNoAction}';
   		responseMessage += "</div>";
   		$(responseMessage).appendTo($(responseContainer));
	}
</script>