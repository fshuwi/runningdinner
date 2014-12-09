<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@page import="org.runningdinner.service.email.FormatterUtil" %>
<%@ page session="false" %>

<!DOCTYPE html>
<html>
  <head>
    <title>Route</title>
    
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href='<c:url value="/resources/images/favicon.ico"/>' type="image/x-icon" />

	<link href='<c:url value="/resources/css/dist/app.css" />' rel="stylesheet">
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
  </head>
<body>

	<div class="row">
		<div class="col-xs-12">
			<h3>Route</h3>
			<h5>${route.teamMemberNames}</h5>
		</div>
		
		<c:forEach items="${route.teamRouteEntries}" var="teamRouteEntry">
			
			<c:choose>
				<c:when test="${teamRouteEntry.currentTeam == true}">
					<div class="alert alert-success col-xs-12 col-sm-6 col-md-4 col-lg-3">
						<h3 class="media-heading"><spring:message code="label.dinnerroutes.self.meal"/>: ${teamRouteEntry.meal.label}</h3>
						<spring:message code="label.dinnerroutes.self.host"/>: <strong>${teamRouteEntry.host.name}</strong><br/>
					<br/>
					<fmt:formatDate pattern="<%=FormatterUtil.DEFAULT_TIME_FORMAT%>" value="${teamRouteEntry.meal.time}" var="mealTimeSelf"/>
					<strong><spring:message code="label.time"/>: <spring:message code="time.display" arguments="${mealTimeSelf}" /></strong>
					</div>
				</c:when>
				<c:otherwise>
					<div class="alert alert-info col-xs-12 col-sm-6 col-md-4 col-lg-3">
						<h3 class="media-heading">${teamRouteEntry.meal.label}</h3>
						<address>
							<spring:message code="label.lastname" />: <strong>${teamRouteEntry.host.name}</strong><br>
							${teamRouteEntry.host.address.streetWithNr}<br>
							${teamRouteEntry.host.address.zipWithCity}<br>
							<br />
							<fmt:formatDate pattern="<%=FormatterUtil.DEFAULT_TIME_FORMAT%>" value="${teamRouteEntry.meal.time}" var="mealTimeHost" />
							<strong><spring:message code="label.time"/>: <spring:message code="time.display" arguments="${mealTimeHost}" /></strong>
						</address>
					</div>
				</c:otherwise>
			</c:choose>
		</c:forEach>
		
	</div>
	
	<div class="row">
		<div class="col-xs-12 col-md-8 col-md-offset-2" id="map" style="height:500px; margin-bottom:15px;">
		</div>
	</div>

	<script src='<c:url value="/resources/js/dist/deps.js"/>'></script>
	<script src='<c:url value="/resources/js/dist/toastr_tooltip.js"/>'></script>
			
	<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyB46NpYyjomUcb-N3_9XjMfSrLEbBbvCaQ&sensor=false"></script>
	
	<script src='<c:url value="/resources/js/common.js"/>'></script>
	
	<script>
		function setMarkersToMap(markers, map) {
			for (var i=0; i<markers.length; i++) {
			    markers[i].setMap(map);
			}    
		}
		
		function createMarker(latLngCoord, title, mapIcon) {
		    var result = null;
		    if (mapIcon) {
				result = new google.maps.Marker({
				      position: latLngCoord,
				      title: title,
				      icon : mapIcon
				});
		    } else {
				result = new google.maps.Marker({
				      position: latLngCoord,
				      title: title
				});
		    }
		    return result; 
		}
		
		function createMapIcon(number, isCurrentTeam) {
		    var color = '0000FF';
		    if (isCurrentTeam) {
				color = '00FF00';
		    }
		    
		    var result = 'https://chart.googleapis.com/chart?chst=d_map_pin_letter_withshadow&chld=' + number + '|' + color + '|000000';
		    return result;
		}
	
		var teamRouteList = JSON.parse('${routejson}');

		var currentTeamCoord = null;
		var markers = new Array();
		
		for (var i=0; i< teamRouteList.teamRouteEntries.length; i++) {
		    
		   	var teamRouteEntry = teamRouteList.teamRouteEntries[i];

		    var geocodes = teamRouteEntry.host.geocodes;
		    if (geocodes && geocodes.length >= 0) {
				var latLngCoord = new google.maps.LatLng( parseFloat(geocodes[0].lat),  parseFloat(geocodes[0].lng) );
				var title = teamRouteEntry.host.name; // TODO
				
				if (teamRouteEntry.currentTeam) {
				    currentTeamCoord = latLngCoord;
				}
				
				var mapIcon = createMapIcon(i+1, teamRouteEntry.currentTeam);
				
				markers.push(createMarker(latLngCoord, title, mapIcon));
		    }
		}

		// Fallback if current team could not be resolved:
		if (currentTeamCoord == null) {
		    currentTeamCoord = markers[0];
		}
		
		var mapOptions = {
		    center: currentTeamCoord,
		    zoom: 12,
		    mapTypeId: google.maps.MapTypeId.ROADMAP
		};
		
		
		$(document).ready(function() {
			var map = new google.maps.Map(document.getElementById("map"), mapOptions);
			setMarkersToMap(markers, map);
		});
	</script>	
	
</body>
</html>