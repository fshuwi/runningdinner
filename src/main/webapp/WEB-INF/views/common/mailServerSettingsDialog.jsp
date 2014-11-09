<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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