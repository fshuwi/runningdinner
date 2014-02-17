<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ page import="org.runningdinner.ui.RequestMappings" %>

<img src="<c:url value="/resources/images/ajax-loader.gif" />" id="dragdrop-ajax-loader" style="display:none;position:absolute;left:-10px;top:-10px;" />

<spring:url value="<%=RequestMappings.SEND_TEAM_MAILS%>" var="teamsFinalizeUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>
<spring:url value="<%=RequestMappings.EXPORT_TEAMS%>" var="teamsExportUrl" htmlEscape="true">
	<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
</spring:url>


<h3 class="contentheadline"><spring:message code="headline.teams"/></h3>

<ul class="nav nav-tabs">
	<li class="active"><a href="#regular" data-toggle="tab"><spring:message code="label.teams.regular"/></a></li>
	<li><a href="#remainder" data-toggle="tab"><spring:message code="label.teams.remainder" /></a></li>
</ul>

<div class="tab-content" id="teamTabs">

	<div class="tab-pane fade in active" id="regular">
		<c:choose>
			<c:when test="${not empty regularTeams}">
				<div class="btn-toolbar" style="margin-top:30px;margin-bottom:15px;">
					<a class="btn btn-info btn-sm" href="${teamsExportUrl}" target="_blank"><spring:message code="label.export" /></a>
				</div>
			
				<table class="table table-hover">
					<thead>
						<tr>
							<th><spring:message code="label.team" /></th>
							<th><spring:message code="label.teammembers" /></th>
							<th><spring:message code="label.meal" /></th>
							<th><spring:message code="label.visits" /></th>
							<th><spring:message code="label.receives" /></th>
							<th><spring:message code="label.host" /></th>
							<th>&nbsp;</th>
							<th>&nbsp;</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${regularTeams}" var="team">
							<tr>
								<td><span id="teamNumber_${team.teamNumber}">${team.teamNumber}</span></td>
								
								<td>
									<div teamKey="${team.naturalKey}">
										<c:forEach items="${team.teamMembers}" var="teamMember">
										
											<spring:url value="<%=RequestMappings.EDIT_PARTICIPANT%>" var="editParticipantUrl" htmlEscape="true">
												<spring:param name="<%=RequestMappings.ADMIN_URL_UUID_MARKER%>" value="${uuid}" />
												<spring:param name="key" value="${teamMember.naturalKey}" />
											</spring:url>
											
											<div class="draggableTeamMember droppableTeamMember" participantKey="${teamMember.naturalKey}">
												<h5 class="media-heading">
													<a class="teamMember" href="${editParticipantUrl}">${teamMember.name.fullnameFirstnameFirst}</a>
												</h5>
											</div>
											
										</c:forEach>
									</div>
								</td>
								
								<td><span class="text-success"><strong>${team.mealClass}</strong></span></td>
								
								<td>
									<div>
										<c:forEach items="${team.visitationPlan.guestTeams}" var="guestTeam">
											<h5 class="media-heading"><a href="#teamNumber_${guestTeam.teamNumber}">Team ${guestTeam.teamNumber}</a></h5>
										</c:forEach>
									</div>
							   </td>
								
								<td>
									<div>
										<c:forEach items="${team.visitationPlan.hostTeams}" var="hostTeam">
											<h5 class="media-heading"><a href="#teamNumber_${hostTeam.teamNumber}">Team ${hostTeam.teamNumber}</a> - <span class="text-success"><strong>${hostTeam.mealClass}</strong></span></h5>
										</c:forEach>
									</div>
							   </td>
							   
							   <td class="col-xs-2" style="text-align:center;">	
							   		<select class="form-control teamHoster" teamKey="${team.naturalKey}" onchange="onTeamHosterChanged('${team.naturalKey}')">
							   			<c:forEach items="${team.teamMembers}" var="teamMember">
							   				<option value="${teamMember.naturalKey}" <c:if test="${teamMember.host eq true}">selected</c:if>>${teamMember.name.fullnameFirstnameFirst}</option>
							   			</c:forEach>
						 			</select>					
							   </td>
							  							   
							   <td><span class="label label-primary" teamKeyZip="${team.naturalKey}">${team.hostTeamMember.address.zip}</span></td>
							   
							   <td>
							   		<spring:url value="<%=RequestMappings.TEAM_DINNER_ROUTE%>" var="teamRoutePreviewUrl" htmlEscape="true">
										<spring:param name="key" value="${team.naturalKey}" />
									</spring:url>
								   <div><a class='btn btn-info btn-sm' href="${teamRoutePreviewUrl}" target="_blank"><span class="glyphicon glyphicon-eye-open"></span> <spring:message code="label.preview" /></a></div>
							   </td>
							</tr>
						</c:forEach>
						<tr>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td>
								<a class="btn btn-primary btn-sm" href="javascript:saveTeamHosts()"><span class="glyphicon glyphicon-save"></span> <spring:message code="label.teams.savehosts" /></a>
								<img src="<c:url value="/resources/images/ajax-loader.gif" />" id="savehosts-ajax-loader" style="display:none;" />
							</td>
							<td colspan="2">
								<spring:message code="tooltip.teams.sendmessage" var="sendMessagesTooltip"/>
								<spring:message code="label.teams.sendmessage" var="sendMessagesLabel"/>
								<a ${saveTeamsBtnStatus} class="btn btn-success btn-sm doTooltip" href="${teamsFinalizeUrl}" 
									data-placement="bottom" data-toggle="tooltip" data-original-title="${sendMessagesTooltip}">
									<span class="glyphicon glyphicon-play"></span> ${sendMessagesLabel}</a>
							</td>
						</tr>
					</tbody>
				</table>
								

				<div id="saveTeamHostsResponse" class="hidden col-xs-12 col-xs-offset-0"></div>
				
			</c:when>
			<c:otherwise>
				<h5><spring:message code="text.teams.invalidsize" /></h5>
			</c:otherwise>
		</c:choose>	
	</div><%-- End regular teams pane --%>
	
	<div class="tab-pane fade in" id="remainder">
		<p>... Noch nicht implementiert ...</p>
	</div>
	
</div>
