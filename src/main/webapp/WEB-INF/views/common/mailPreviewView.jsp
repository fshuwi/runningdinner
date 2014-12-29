<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:useAttribute name="previewModel" id="previewModel" classname="org.runningdinner.ui.dto.SendMailsPreviewModel" />

<div class="modal fade" id="previewDialog" tabindex="-1" role="dialog" aria-labelledby="#dialogLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg">
 		<div class="modal-content">
   			<div class="modal-header">
     				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
     				<h4 class="modal-title" id="dialogLabel"><spring:message code="label.preview"/></h4>
   			</div>
   			<div class="modal-body">
				<p>
					<c:choose>
						<c:when test="${previewModel.team != null}">
							<spring:message code="text.preview.mail.teams" />:<br/>
							<span class="help-block">Team ${previewModel.team.teamNumber}: ${previewModel.participantNames}</span>
						</c:when>
						<c:otherwise>
							<spring:message code="text.preview.mail.participants" />:<br/>
							<span class="help-block"><spring:message code="label.participant"/>: ${previewModel.participantNames}</span>
						</c:otherwise>
					</c:choose>
				</p>
				
				<c:forEach items="${previewModel.messages}" var="message" varStatus="counter">
					<c:if test="${not counter.first}">
						<hr/>
					</c:if>
					<h3>${previewModel.subject}</h3>
					<div class="well"><span class="word-wrap-break"><i>${message}</i></span></div>
				</c:forEach>						    				
   			</div>
   			<div class="modal-footer">
     			<button type="button" class="btn btn-primary" data-dismiss="modal"><spring:message code="label.close"/></button>
   			</div>
 		</div>
	</div>
</div>