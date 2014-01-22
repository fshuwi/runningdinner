<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>


<c:set var="saveTeamsBtnStatus" value=""/>
<c:if test="${teamArrangementsFinalized eq true}">
	<c:set var="saveTeamsBtnStatus" value="disabled" />
</c:if>

<h2>Team-Einteilung</h2>

<ul class="nav nav-tabs">
	<li><a href="#regular" data-toggle="tab">Reguläre Teams</a></li>
	<li><a href="#remainder" data-toggle="tab">Übrig gebliebene Teilnehmer</a></li>
</ul>

<div class="tab-content" id="teamTabs">

	<div class="tab-pane active" id="regular">
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
								<td>${team.teamNumber}</td>
								<td>
									<div>
										<c:forEach items="${team.teamMembers}" var="teamMember">
											<h5 class="media-heading"><a href="#">${teamMember.name.fullnameFirstnameFirst}</a></h5>
										</c:forEach>
									</div>
								</td>
								<td><span class="text-success"><strong>${team.mealClass}</strong></span></td>
								<td>
									<div>
										<c:forEach items="${team.visitationPlan.hostTeams}" var="hostTeam">
											<h5 class="media-heading"><a href="#">Team ${hostTeam.teamNumber}</a> - <span class="text-success"><strong>${hostTeam.mealClass}</strong></span></h5>
										</c:forEach>
									</div>
							   </td>
							   <td>
									<div>
										<c:forEach items="${team.visitationPlan.guestTeams}" var="guestTeam">
											<h5 class="media-heading"><a href="#">Team ${guestTeam.teamNumber}</a></h5>
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
								   <div><a class='btn btn-info btn-sm' href="#"><span class="glyphicon glyphicon-eye-open"></span> Vorschau</a></div>
								   <div style="margin-top:3px;"><a class='btn btn-info btn-sm' href="#"><span class="glyphicon glyphicon-phone-alt"></span> Email</a></div>
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
							<td><a ${saveTeamsBtnStatus} class="btn btn-success btn-sm doTooltip" href="#" data-placement="bottom" data-toggle="tooltip" data-original-title="Legt die Teams endgueltig fest. Alle Teilnehmer bekommen eine Mail mit der Info ueber Ihren Team-Partner"><span class="glyphicon glyphicon-play"></span> Teameinteilung finalisieren...</a></td>
						</tr>
					</tbody>
				</table>
				
				<p>TODO: Das muss noch besser werden (eigenes ueber div etc.)</p>
				<div id="saveTeamHostsResponse" class="alert hidden col-xs-4"></div>
				
			</c:when>
			<c:otherwise>
				<h5>Es gibt leider nicht genügend Teilnehmer für eine Teameinteilung mit den gespeicherten Dinner-Optionen!</h5>
			</c:otherwise>
		</c:choose>	
	</div><%-- End regular teams pane --%>
	
	<div class="tab-pane" id="remainder">
		<p>Noch nicht implementiert...
	</div>
	
</div>
