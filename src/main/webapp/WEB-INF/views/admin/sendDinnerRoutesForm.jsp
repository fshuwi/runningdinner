<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<h3 class="contentheadline"><spring:message code="label.dinnerroutes.sendmessage" /></h3>

<tiles:insertDefinition name="view-status-info" flush="true" />


<div class="modal fade" tabindex="-1" id="mailServerSettings" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-dialog modal-sm">
    <div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only"><spring:message code="label.close"/></span></button>
			<h4 class="modal-title" id="myModalLabel"><spring:message code="label.mailserver.settings"/></h4>
		</div>
        <div class="modal-body">
        	<form class="form-horizontal" autocomplete="off">
				<div class="form-group">
					<label for="mailServer_modal" class="control-label col-xs-4"><spring:message code="label.mailserver.host"/></label>
					<div class="col-xs-8">
						<input type="text" class="form-control" id="mailServer_modal" placeholder="<spring:message code="label.mailserver.host"/>" autocomplete="off">
					</div>
				</div>
				<div class="form-group">
					<label for="mailServerPort_modal" class="control-label col-xs-4"><spring:message code="label.mailserver.port"/></label>
					<div class="col-xs-4">
						<input type="text" class="form-control" id="mailServerPort_modal" placeholder="<spring:message code="label.mailserver.port"/>" autocomplete="off">
					</div>
				</div>
				<div class="form-group">
					<label for="username_modal" class="control-label col-xs-4"><spring:message code="label.username"/></label>
					<div class="col-xs-8">
						<input type="text" class="form-control" id="username_modal" placeholder="<spring:message code="label.username"/>" autocomplete="off">
					</div>
				</div>
				<div class="form-group">
					<label for="password_modal" class="control-label col-xs-4"><spring:message code="label.password"/></label>
					<div class="col-xs-8">
						<input type="password" class="form-control" id="password_modal" placeholder="<spring:message code="label.password"/>">
					</div>
				</div>
				<div class="form-group">
					<label for="from_modal" class="control-label col-xs-4"><spring:message code="label.mailserver.from"/></label>
					<div class="col-xs-8">
						<input type="email" class="form-control" id="from_modal" placeholder="<spring:message code="label.mailserver.from"/>" autocomplete="off">
					</div>
				</div>
				
				<div class="form-group">
					<div class="col-xs-offset-4 col-xs-8">
						<div class="checkbox">
							<label><input type="checkbox" id="useTls_modal"> <spring:message code="label.mailserver.tls"/></label>
						</div>
					</div>
				</div>
			
				<hr/>
				
				<div class="form-group">
					<label for="testEmailAddress" class="control-label col-xs-4"><spring:message code="label.mailserver.addresstest"/></label>
					<div class="col-xs-8">
						<input type="email" class="form-control" id="testEmailAddress" placeholder="<spring:message code="label.mailserver.addresstest"/>" />
					</div>
				</div>
				<div class="form-group">
					<div class="col-xs-offset-4 col-xs-8">
						<button class="btn btn-success" onclick="checkMailConnection();return false"><spring:message code="label.mailserver.connection.test"/></button>
						<img src="<c:url value="/resources/images/ajax-loader.gif" />" id="check-ajax-loader" style="display:none;" />
					</div>
					<div class="col-xs-12" id="mailSettingsResult" style="display:none;margin-top:5px;">
					</div>
				</div>
				
			</form>
        </div>
        <div class="modal-footer">
        	<button type="button" class="btn btn-danger" onclick="cancelMailSettings()" data-dismiss="modal"><spring:message code="label.cancel"/></button>
        	<button type="button" class="btn btn-primary" onclick="saveMailSettings()"><spring:message code="label.save"/></button>
        </div>
    </div> <!-- End Modal content -->
  </div> <!-- End modal-dialog -->
</div> <!--  end modal-fade -->


<div>
	<div class="alert alert-info alert-dismissable">
		<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
		<strong>Info</strong><br/><spring:message code="text.dinnerroutes.message.info" />
		<br/><strong><spring:message code="label.important"/></strong>: <spring:message code="text.sendmessage.info" />
	</div>
	
	<c:if test="${not empty sendMailsModel.lastMailReport}">
		<tiles:insertDefinition name="view-mailreport">
			<tiles:putAttribute name="lastMailReport" value="${sendMailsModel.lastMailReport}" />
			<tiles:putAttribute name="mailType" value="Dinner-Route Emails" />
		</tiles:insertDefinition>
	</c:if>
	
	<script>
		var charCounters = {};
		charCounters["message"] = 3000;
		charCounters["selfTemplate"] = 300;
		charCounters["hostsTemplate"] = 300;
	</script>
	
	<form:form method="POST" commandName="sendMailsModel" htmlEscape="true" role="form" onsubmit="return isOneOrMoreEntitiesSelected()">
		<div class="well">
			<spring:message code="label.subject" var="subjectLabel" />
			<bs:inputField name="subject" label="${subjectLabel}" inputColClass="col-xs-6" placeholder="${subjectLabel}"/>
			
			<div class="form-group">
				<label for="message"><spring:message code="label.message" /></label>
				<span class="help-block"><spring:message code="label.message.template.help" />: {firstname}, {lastname}, {route}</span>
				<form:textarea path="message" id="message" rows="10" style="margin-bottom:5px;" class="form-control counted" />
				<h6 class="pull-right" id="message_counter">3000 characters remaining</h6>
				<form:errors path="message"/>
			</div>
			
			<div class="form-group">
				<label for="selfTemplate"><spring:message code="label.message.dinnerroute.selftemplate" /></label>
				<span class="help-block"><spring:message code="label.message.template.replacement" arguments="{route}" />. <spring:message code="label.message.template.help" />: {firstname}, {lastname}, {meal}, {mealtime}</span>
				<form:textarea path="selfTemplate" id="selfTemplate" rows="3" style="margin-bottom:5px;" class="form-control counted" />
				<h6 class="pull-right" id="selfTemplate_counter">300 characters remaining</h6>
				<form:errors path="selfTemplate"/>
			</div>
			
			<div class="form-group">
				<label for="hostsTemplate"><spring:message code="label.message.dinnerroute.hoststemplate" /></label>
				<span class="help-block"><spring:message code="label.message.template.replacement" arguments="{route}" />. <spring:message code="label.message.template.help" />: {firstname}, {lastname}, {meal}, {mealtime}, {hostaddress}</span>
				<form:textarea path="hostsTemplate" id="hostsTemplate" rows="4" style="margin-bottom:5px;" class="form-control counted" />
				<h6 class="pull-right" id="hostsTemplate_counter">300 characters remaining</h6>
				<form:errors path="hostsTemplate"/>
			</div>
		</div>
		
		<spring:eval expression="@globalProperties['mail.from']" var="standardMailSender"/>
		<spring:message code="tooltip.mailserver.standard" var="standardMailServerTooltip" arguments="standardMailSender"/>
		<spring:message code="tooltip.mailserver.custom" var="customMailServerTooltip"/>
		<div class="well">
			<div class="form-group">
				<label><spring:message code="label.mailserver.settings"/></label><br/>
				<div class="btn-group">
  					<button type="button" class="btn btn-success" id="mailStandardBtn" onclick="toggleMailServer('standard')"
  							data-toggle="popover" data-container="body" data-trigger="focus" 
  							data-content='${standardMailServerTooltip}' data-placement="left">
  							<spring:message code="text.mailserver.standard"/>
  					</button>
  					<button type="button" class="btn btn-default" id="mailCustomBtn" onclick="toggleMailServer('custom')"
  							data-toggle="popover" data-container="body" data-trigger="focus" data-content='${customMailServerTooltip}' data-placement="right">
  							<spring:message code="text.mailserver.custom"/>
  					</button>
				</div>
			</div>
			<div id="mailConfigurationLink" style="display:none;">
				<a href="#mailServerSettings" data-toggle="modal" data-target="#mailServerSettings">Bearbeite Mail-Server Einstellungen</a>
			</div>			
		</div>
		
		<form:hidden path="mailServer" id="mailServer" />
		<form:hidden path="mailServerPort" id="mailServerPort" />
		<form:hidden path="username" id="username" />
		<form:hidden path="password" id="password" />
		<form:hidden path="useTls" id="useTls" />
		<form:hidden path="from" id="from" />
		<form:hidden path="useCustomMailServer" id="useCustomMailServer" />
		
		<input onclick="return onSendMailsSubmit()" type="submit" class="btn btn-primary" value="<spring:message code="label.dinnerroutes.sendmessage" />!" name="sendDinnerRoutes" />
		<input type="submit" class='btn btn-info' value="<spring:message code="label.preview" />" name="preview" />

		<div style="margin-top:20px;">
			<h4><spring:message code="label.teams.selection" /></h4>
			<form:errors path="selectedEntities" />
			<span><input type="checkbox" id="allEntitiesSelectedBox" onchange="toggleEntitySelection()" /><label><spring:message code="label.teams.selection.all" /></label></span>
			<ul class="teamSelection">
				<form:checkboxes element="li" items="${sendMailsModel.entityDisplayMap}" path="selectedEntities" cssClass="entitySelectionBox"/>
			</ul>
		</div>
			
	</form:form>		
</div>

<c:if test="${not empty sendMailsPreviewModel}">
	<tiles:insertDefinition name="view-mailpreview" flush="true">
		<tiles:putAttribute name="previewModel" value="${sendMailsPreviewModel}" />
	</tiles:insertDefinition>
</c:if>
