<%@page import="org.runningdinner.ui.RequestMappings"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<spring:message code="error.teams.noselection" var="noselectionText"/>

<spring:url value="<%=RequestMappings.AJAX_CHECK_MAIL_CONNECTION%>" var="checkMailConnectionUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>
<spring:url value="<%=RequestMappings.AJAX_SAVE_MAIL_SETTINGS%>" var="saveMailSettingsUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>

<spring:message code="label.error" var="errorLabel"/>
<spring:message code="label.success" var="successLabel"/>
<spring:message code="text.mailserversettings.test.success" var="checkSuccessMessage"/>
<spring:message code="text.mailserversettings.save.cookie.success" var="saveSuccessMessage" />
<spring:message code="error.mailserversettings.submit" var="submitErrorMessage" />
<spring:message code="error.mailserversettings.validation.general" var="validationGeneralErrorMessage"/>

<script>
	
	var MAIL_SERVER_TYPE = {
		STANDARD : "standard",
		CUSTOM : "custom"
	};
	
	var mailServerSettings = {
		mailServerType : MAIL_SERVER_TYPE.STANDARD,
		mailServer : "",
		mailServerPort : 25,
		username : "",
		password : "",
		useTls : true,
		from : "",
	
		checkEmailAddress : ""
	};

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
		
		$("button[data-content]").popover();
		
		// Init mail server settings:		
		var useCustomMailServer = $("#useCustomMailServer").val().toLowerCase() === "true";
		if (useCustomMailServer) {
		    mailServerSettings.mailServerType = MAIL_SERVER_TYPE.CUSTOM;
		} else {
		    mailServerSettings.mailServerType = MAIL_SERVER_TYPE.STANDARD;
		}
		
		mailServerSettings.mailServer = $("#mailServer").val();
		mailServerSettings.mailServerPort = $("#mailServerPort").val();
		mailServerSettings.username = $("#username").val();
		mailServerSettings.password = $("#password").val();
		mailServerSettings.from = $("#from").val();
		mailServerSettings.useTls = $("#useTls").val().toLowerCase() === "true";
		
		writeMailSettingsToDialog();
		
		toggleMailServer(mailServerSettings.mailServerType);
		
		toastr.options.timeout = 4;
		toastr.options.closeButton = true;
		// toastr.options.positionClass = 'toast-top-center';
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
	
	function toggleMailServer(mailTypeServer) {
	    var primaryClass = "btn-success";
	    
	    standardButton = $("#mailStandardBtn");
	    customButton = $("#mailCustomBtn");
	    
	  	standardButton.removeClass(primaryClass + " btn-default");
	  	customButton.removeClass(primaryClass + " btn-default");
	  	
	  	if (mailTypeServer == MAIL_SERVER_TYPE.CUSTOM) {
	  		standardButton.addClass("btn-default");
	  		customButton.addClass(primaryClass);
	  		mailServerSettings.mailServerType = MAIL_SERVER_TYPE.CUSTOM;
	  		$("#mailConfigurationLink").show();
	  	}
	  	else {
	  		standardButton.addClass(primaryClass);
	  		customButton.addClass("btn-default");
	  		$("#mailConfigurationLink").hide();
	  		mailServerSettings.mailServerType = MAIL_SERVER_TYPE.STANDARD;
	  	}
	}
	
	
	function saveMailSettings() {
	    var readMailServerSettings = readMailSettingsFromDialog();
	   
	    var jsonDataStr = JSON.stringify(readMailServerSettings);
	    jQuery.ajax({
		    type: "PUT",
		    url: "${saveMailSettingsUrl}",
		    contentType: "application/json; charset=utf-8",
		    data: jsonDataStr,
		    dataType: "json"
		}).done(function (data) {
		    resetValidationErrors();
		    
		    // Set values back to form for transfer to model of server 
		    $("#mailServer").val(readMailServerSettings.mailServer); 
		    $("#mailServerPort").val(readMailServerSettings.mailServerPort);
		    $("#username").val(readMailServerSettings.username);
		    $("#password").val(readMailServerSettings.password);
		    $("#from").val(readMailServerSettings.from);
		    $("#useTls").val(readMailServerSettings.useTls);
		    
			if (data.success) {
		    	$("#mailServerSettings").modal('hide');
		    	toastr.success('${saveSuccessMessage}'); 
			}
			else { // This happens only if mail server settings could not be stored into browser:
				var responseMessage = getErrorBox("${errorLabel}", data.errorMessage);
				$(responseMessage).appendTo($("#mailSettingsResult"));		    
			}
		}).fail(function (jqXHR) {
		    resetValidationErrors();
		    handleValidationErrors(jqXHR);
		});   
	}
	
	function cancelMailSettings() {
	    resetValidationErrors();
	    writeMailSettingsToDialog(); // Reset input fields to last saved values
	}
	
	function checkMailConnection() {
	    var readMailServerSettings = readMailSettingsFromDialog();
	    var checkEmailAddress = $("#testEmailAddress").val();
	    
	    var jsonDataStr = JSON.stringify(readMailServerSettings);
	    
	    $("#check-ajax-loader").show();
		jQuery.ajax({
		    type: "POST",
		    url: "${checkMailConnectionUrl}?email="+checkEmailAddress,
		    contentType: "application/json; charset=utf-8",
		    data: jsonDataStr,
		    dataType: "json"
		}).done(function (data) {
		    $("#check-ajax-loader").hide();
		    resetValidationErrors();
		    if (data.success) {
				var responseMessage = getSuccessBox("${successLabel}", "${checkSuccessMessage}");
				$(responseMessage).appendTo($("#mailSettingsResult"));
		    } else {
				var responseMessage = getErrorBox("${errorLabel}", data.errorMessage);
				$(responseMessage).appendTo($("#mailSettingsResult"));
		    }
		    $("#mailSettingsResult").show();
		}).fail(function (jqXHR) {
		    $("#check-ajax-loader").hide();
		    resetValidationErrors();
		    handleValidationErrors(jqXHR);
	    });
	}
	
	function handleValidationErrors(jqXHR) {
	    var errorMessage = "";
	    if (400 == jqXHR.status) {
			data = jqXHR.responseJSON;
			if (data && data.fieldErrors.length > 0) {
				for (var i=0; i<data.fieldErrors.length; i++) {
					errorMessage += "<br/>";
				    errorMessage += data.fieldErrors[i].message;
				    
				    field = data.fieldErrors[i].field;
				    $("#"+field+"_modal").parent().parent().addClass("has-error");
				}
			} else {
			    errorMessage = "${validationGeneralErrorMessage}";
			}
	    }
	    
	    var responseMessage = getErrorBox("${errorLabel}", errorMessage);
	    $(responseMessage).appendTo($("#mailSettingsResult"));
	    $("#mailSettingsResult").show();
	}
	
	function resetValidationErrors() {
	    $("#mailSettingsResult").empty();
	    $("#mailServerSettings .form-group").removeClass("has-error");
	}
	
	function readMailSettingsFromDialog() {
	    var result = $.extend({}, mailServerSettings);
	  
	    result.mailServer = $("#mailServer_modal").val();
	    result.mailServerPort = $("#mailServerPort_modal").val();
	    result.username = $("#username_modal").val();
	    result.password = $("#password_modal").val();
	    result.from = $("#from_modal").val();
	    result.useTls = $("#useTls_modal").prop('checked');
	    
	    return result;
	}
	
	function onSendMailsSubmit() {
	    var useCustomMailServer = false;
	    
	    if (mailServerSettings.mailServerType == MAIL_SERVER_TYPE.STANDARD) {
	    	$("#useCustomMailServer").val('false');
	    	useCustomMailServer = false;
	    } else {
			$("#useCustomMailServer").val('true');
			useCustomMailServer = true;
	    }
	    
	    // Check for valid configuration:
		if (useCustomMailServer) {
		    var mailServer = $("#mailServer").val();
		    var from = $("#from").val();
		    var port = $("#mailServerPort").val();
		    if (mailServer === "" || from === "" || port === "" || !isNormalInteger(port)) {
			 	toastr.options.positionClass = "toast-bottom-right";
				toastr.error('${submitErrorMessage}');
				return false;
		    }
		}
	    
	    return true;
	}
	
	function isNormalInteger(str) {
	    return /^\+?(0|[1-9]\d*)$/.test(str);
	}
	
	function writeMailSettingsToDialog() {
	    $("#mailServer_modal").val(mailServerSettings.mailServer);
	    $("#mailServerPort_modal").val(mailServerSettings.mailServerPort);
	    $("#username_modal").val(mailServerSettings.username);
	    $("#password_modal").val(mailServerSettings.password);
	    $("#from_modal").val(mailServerSettings.from);
	    $("#useTls_modal").prop('checked',mailServerSettings.useTls);
	}
</script>