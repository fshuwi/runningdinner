<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.runningdinner.ui.RequestMappings" %>

<footer style="margin-bottom:20px;">
	<div class="row">
	
		<hr/>
	 
	 	<spring:url value="<%=RequestMappings.PRIVACY%>" var="privacyUrl" htmlEscape="true" />
	 	<spring:url value="<%=RequestMappings.IMPRESSUM%>" var="impressumUrl" htmlEscape="true" />
	 	<spring:url value="<%=RequestMappings.ABOUT%>" var="aboutUrl" htmlEscape="true" />
	 	
	 	<spring:message code="label.impressum" var="impressumLabel" />
	 	<spring:message code="label.privacy" var="privacyLabel" />
	 	<spring:message code="label.about" var="aboutLabel" />

		<div class="col-sm-4 col-sm-offset-1">
			<ul class="footer-list">
				<li><span class="muted">© 2014 Clemens Stich</span></li>
				<li><a href="javascript:toggleModalFooter('modalFooterDialog', '${impressumUrl}', '${impressumLabel}')">${impressumLabel}</a></li>
				<li class="last"><a href="javascript:toggleModalFooter('modalFooterDialog', '${privacyUrl}', '${privacyLabel}')">${privacyLabel}</a></li>
			</ul>
		</div>
		<div class="col-sm-2">&nbsp;</div>
		<div class="col-sm-4 col-sm-offset-1">
			<ul class="footer-list">
				<li><a href="javascript:toggleModalFooter('modalFooterDialog', '${aboutUrl}', '${aboutLabel}')">${aboutLabel}</a></li>
				<li><a href="<spring:eval expression="@globalProperties['github.repo.url']" />" target="_blank"><spring:message code="label.source"/></a></li>
				<li class="last"><a href="mailto:<spring:eval expression="@globalProperties['contact.mail']" />"><spring:message code="label.contact"/></a></li>		
			</ul>
		</div>
	</div>
</footer>

<div class="modal fade" id="modalFooterDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-dialog modal-sm">
    <div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h4 class="modal-title" id="footer-modal-title"></h4>
		</div>
        <div class="modal-body" id="footer-modal-body">
        </div>
        <div class="modal-footer">
        	<button type="button" class="btn btn-primary" data-dismiss="modal"><spring:message code="label.close"/></button>
        </div>
    </div> <!-- End Modal content -->
  </div> <!-- End modal-dialog -->        
</div>