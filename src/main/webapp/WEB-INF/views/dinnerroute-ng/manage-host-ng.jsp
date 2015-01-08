<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ page import="org.runningdinner.ui.RequestMappings" %>
<%@ page session="false" %>

<spring:url value="<%=RequestMappings.TEAM_MANAGE_HOST%>" var="manageHostUrl" htmlEscape="true" />

<spring:message code="text.host.saved" var="hostSavedMessage" />

<!DOCTYPE html>
<html>
  <head>
    <title>Gastgeber festlegen</title>
    
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href='<c:url value="/resources/images/favicon.ico"/>' type="image/x-icon" />

	<link href='<c:url value="/resources/css/dist/app.css" />' rel="stylesheet">
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
  </head>
<body>

  <div class="container">
  	
	<div class="row">
		<div class="col-xs-12">
			<h3>Gastgeber festlegen</h3>
			<h5>Hier könnt ihr innerhalb eures Teams den Gastgeber selbstständig ändern.</h5>
		</div>
	</div>
	
	<div class="row">
		<div class="col-xs-12">
			<div class="well">
				<div class="form-group">
					<label class="control-label" for="teamHostSelect">LABEL</label>
					<span class="help-block">HELPLABEL</span>
					<div class="row">
						<div class="col-xs-4">
							<select id="teamHostSelect" class="form-control"></select>
							<p class="help-block">Aktueller Gastgeber ist <span id="currentHostName" style="font-weight:bold;"></span></p>
						</div>
					</div>
				</div>
						
				<div class="form-group">
					<label for="comment">Persönlicher Kommentar</label>
					<span class="help-block">Blabla</span>
					<textarea id="comment" rows="5" style="margin-bottom:5px;" class="form-control counted"></textarea>
					<h6 class="pull-right" id="comment_counter">3000 Zeichen &uml;brig</h6>
				</div>
			</div>
			
			<input type="button" class="btn btn-primary" value="Speichern" id="saveBtn" disabled />
			<img src="<c:url value="/resources/images/ajax-loader.gif" />" id="save-ajax-loader" style="display:none;" />
		</div>
	</div>
  
  </div>
				
	<c:set var="useCDN" value="false" />
	<spring:eval expression="@globalProperties['ui.useCDN']" var="useCDN" />
	<c:choose>
		<c:when test="${useCDN == 'true'}">
			<script src='https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.8.4/moment-with-locales.min.js'></script>
			
			<script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
			<script src="https://code.jquery.com/ui/1.10.4/jquery-ui.min.js"></script>
			
			<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js"></script>
			
			<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/i18n/jquery-ui-i18n.min.js"></script>
			
			<script src="https://cdnjs.cloudflare.com/ajax/libs/tooltipster/3.0.5/js/jquery.tooltipster.min.js"></script>
			
			<script src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/js/toastr.min.js"></script>
		</c:when>
		<c:otherwise>
			<script src='<c:url value="/resources/js/moment.min.js"/>'></script>
			<script src='<c:url value="/resources/js/dist/deps.js"/>'></script>
			<script src='<c:url value="/resources/js/dist/toastr_tooltip.js"/>'></script>
		</c:otherwise>
	</c:choose>
	
	<script src='<c:url value="/resources/js/common.js"/>'></script>
	
	<script>
		var currentTeam = JSON.parse('${teamjson}');
		var participantEditorKey = "${participantEditorKey}";
		
		function saveTeamHost(teamKey, hostKey, participantEditorKey, comment) {
		    
			var requestUrl = "${manageHostUrl}";
			requestUrl = requestUrl.replace("{teamKey}", teamKey);
			requestUrl = requestUrl.replace("{participantKey}", participantEditorKey);
			
			var data = {
				comment: comment,
				changedTeamHost: {
					teamKey: teamKey,
					participantKey: hostKey
				}
			};
			
			 $("#save-ajax-loader").show();
			jQuery.ajax({
			    type: "PUT",
			    url: requestUrl,
			    contentType: "application/json; charset=utf-8",
			    data: JSON.stringify(data),
			    dataType: "json"
			}).done(function (rslt) {
				currentTeam = rslt;
				popuplateControls(currentTeam);
				toastr.success('${hostSavedMessage}');
				toggleSaveBtn(false);
				$("#save-ajax-loader").hide();
			}).fail(function (jqXHR) {
				$("#save-ajax-loader").hide();
				toastr.error('Gastgeber konnte nicht gespeichert werden!'); 
			});
		}
		
		function popuplateControls(currentTeam) {
			var teamHostSelect = $("#teamHostSelect");
			teamHostSelect.empty();
			
			var currentHostName = '';
			var currentHostVal = '';
			
			$.each(currentTeam.teamMembers, function() {
				teamHostSelect.append($("<option />").val(this.naturalKey).text(this.name));
			    if (this.host) {
			    	currentHostName = this.name;
			    	currentHostVal = this.naturalKey;
			    }
			});
			
			$("#teamHostSelect").val(currentHostVal);
			$('#currentHostName').text(currentHostName);
		}
		
		function toggleSaveBtn(enable) {
			if (enable) {
				$('#saveBtn').removeAttr('disabled');
			} else {
				$('#saveBtn').attr('disabled', 'true');
			}
		}
		
		$(document).ready(function() {
			
			toastr.options.timeout = 4;
			toastr.options.closeButton = true;
			
			popuplateControls(currentTeam);
			
			$('#saveBtn').click(function() {
				var hostKey= $("#teamHostSelect").val();
				var comment = $('#comment').val();
				saveTeamHost(currentTeam.naturalKey, hostKey, participantEditorKey, comment);
			});
			
			
			$('#teamHostSelect').on('change', function() {
				var changedHostKey = this.value;
				
				var enableSaveBtn = true;
				$.each(currentTeam.teamMembers, function() {
				    if (this.host && this.naturalKey == changedHostKey) {
				    	enableSaveBtn = false;
				    }
				});
				
				toggleSaveBtn(enableSaveBtn);
			});
			
			$("#comment").charCounter(3000, {container: "#comment_counter"});
		});
	</script>
	
</body>
</html>