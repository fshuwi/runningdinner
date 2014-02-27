<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.runningdinner.ui.RequestMappings" %>

<footer style="margin-bottom:20px;">
	<div class="row">
	
		<hr/>
	 
	 	<spring:url value="<%=RequestMappings.PRIVACY%>" var="privacyUrl" htmlEscape="true" />
	 	<spring:url value="<%=RequestMappings.IMPRESSUM%>" var="impressumUrl" htmlEscape="true" />
	 	<spring:url value="<%=RequestMappings.ABOUT%>" var="aboutUrl" htmlEscape="true" />

		<div class="col-sm-4 col-sm-offset-1">
			<ul class="footer-list">
				<li><span class="muted">© 2014 Clemens Stich</span></li>
				<li><a data-toggle="modal" data-target="#impressumDialog" href="${impressumUrl}"><spring:message code="label.impressum"/></a></li>
				<li class="last"><a data-toggle="modal" data-target="#privacyDialog" href="${privacyUrl}"><spring:message code="label.privacy"/></a></li>
			</ul>
		</div>
		<div class="col-sm-2">&nbsp;</div>
		<div class="col-sm-4 col-sm-offset-1">
			<ul class="footer-list">
				<li><a data-toggle="modal" data-target="#aboutDialog" href="${aboutUrl}"><spring:message code="label.about"/></a></li>
				<li><a href="<spring:eval expression="@globalProperties['github.repo.url']" />" target="_blank"><spring:message code="label.source"/></a></li>
				<li class="last"><a href="mailto:<spring:eval expression="@globalProperties['contact.mail']" />"><spring:message code="label.contact"/></a></li>		
			</ul>
		</div>
	</div>
</footer>

<div class="modal fade" id="privacyDialog" tabindex="-1" role="dialog" aria-labelledby="#dialogLabel" aria-hidden="true"></div>
<div class="modal fade" id="impressumDialog" tabindex="-1" role="dialog" aria-labelledby="#dialogLabel" aria-hidden="true"></div>
<div class="modal fade" id="aboutDialog" tabindex="-1" role="dialog" aria-labelledby="#dialogLabel" aria-hidden="true"></div>