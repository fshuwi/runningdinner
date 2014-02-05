<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>


<spring:url value="/event/{uuid}/admin/teams/mail" var="teamsFinalizeUrl" htmlEscape="true">
	<spring:param name="uuid" value="${uuid}" />
</spring:url>


<spring:message code="tooltip.teams.finalize.button" var="sendMessagesTooltip"/>
<c:set var="sendMessagesLabel" value="Teameinteilungen verschicken" />
<c:if test="${teamAdministration.teamsAlreadySaved}">
	<c:set var="sendMessagesLabel" value="Teameinteilungen verschicken" />
</c:if>

<h2>Team-Einteilung</h2>

<ul class="nav nav-tabs">
	<li class="active"><a href="#regular" data-toggle="tab">Regulaere Teams</a></li>
	<li><a href="#remainder" data-toggle="tab">Uebrig gebliebene Teilnehmer</a></li>
</ul>

<div class="tab-content" id="teamTabs">

	<div class="tab-pane fade in active" id="regular">
		<c:choose>
			<c:when test="${not empty regularTeams}">
				<div class="btn-toolbar" style="margin-top:30px;margin-bottom:15px;">
					<a class="btn btn-info btn-sm" href="#${uuid}">Export...</a>
				</div>
			
				<table class="table table-hover">
					<thead>
						<tr>
							<th>Team</th>
							<th>Team-Mitglieder</th>
							<th>Speise</th>
							<th>Besucht</th>
							<th>Empfängt</th>
							<th>Gastgeber</th>
							<th>&nbsp;</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${regularTeams}" var="team">
							<tr>
								<td><span id="teamNumber_${team.teamNumber}">${team.teamNumber}</span></td>
								<td>
									<div id="teaminfo_${team.naturalKey}">
										<c:forEach items="${team.teamMembers}" var="teamMember">
										
											<spring:url value="/event/{uuid}/admin/participant/{key}/edit" var="editParticipantUrl" htmlEscape="true">
												<spring:param name="uuid" value="${uuid}" />
												<spring:param name="key" value="${teamMember.naturalKey}" />
											</spring:url>
											
											<div class="draggableTeamMember droppableTeamMember" id="participant_${teamMember.naturalKey}">
												<h5 class="media-heading"><a href="${editParticipantUrl}">${teamMember.name.fullnameFirstnameFirst}</a></h5>
											</div>
											
										</c:forEach>
									</div>
								</td>
								<td><span class="text-success"><strong>${team.mealClass}</strong></span></td>
								<td>
									<div>
										<c:forEach items="${team.visitationPlan.hostTeams}" var="hostTeam">
											<h5 class="media-heading"><a href="#teamNumber_${hostTeam.teamNumber}">Team ${hostTeam.teamNumber}</a> - <span class="text-success"><strong>${hostTeam.mealClass}</strong></span></h5>
										</c:forEach>
									</div>
							   </td>
							   <td>
									<div>
										<c:forEach items="${team.visitationPlan.guestTeams}" var="guestTeam">
											<h5 class="media-heading"><a href="#teamNumber_${guestTeam.teamNumber}">Team ${guestTeam.teamNumber}</a></h5>
										</c:forEach>
									</div>
							   </td>
							   <td class="col-xs-2" style="text-align:center;">	
							   		<select class="form-control teamHoster" id="${team.naturalKey}" onchange="onTeamHosterChanged('${team.naturalKey}')">
							   			<c:forEach items="${team.teamMembers}" var="teamMember">
							   				<option value="${teamMember.naturalKey}" <c:if test="${teamMember.host eq true}">selected</c:if>>${teamMember.name.fullnameFirstnameFirst}</option>
							   			</c:forEach>
						 			</select>										
							   </td>
							   <td>
							   		<spring:url value="/team/{key}/route" var="teamRoutePreviewUrl" htmlEscape="true">
										<spring:param name="key" value="${team.naturalKey}" />
									</spring:url>
								   <div><a class='btn btn-info btn-sm' href="${teamRoutePreviewUrl}"><span class="glyphicon glyphicon-eye-open"></span> Vorschau</a></div>
							   </td>
							</tr>
						</c:forEach>
						<tr>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td><a class="btn btn-primary btn-sm" href="javascript:saveTeamHosts()"><span class="glyphicon glyphicon-save"></span> Gastgeber Speichern</a></td>
							<td>
								<a ${saveTeamsBtnStatus} class="btn btn-success btn-sm doTooltip" href="${teamsFinalizeUrl}" 
										data-placement="bottom" data-toggle="tooltip" data-original-title="${sendMessagesTooltip}"><span class="glyphicon glyphicon-play"></span> ${sendMessagesLabel}</a>
							</td>
						</tr>
					</tbody>
				</table>

				<div id="saveTeamHostsResponse" class="hidden col-xs-8 col-xs-offset-2"></div>
				
			</c:when>
			<c:otherwise>
				<h5>Es gibt leider nicht genügend Teilnehmer für eine Teameinteilung mit den gespeicherten Dinner-Optionen!</h5>
			</c:otherwise>
		</c:choose>	
	</div><%-- End regular teams pane --%>
	
	<div class="tab-pane fade in" id="remainder">
		<p>Noch nicht implementiert...
	</div>
	
</div>
