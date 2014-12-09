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
  <div class="container">
  	
	<div class="row">
		<div class="col-xs-12">
			<h3>Route</h3>
			<h5>${route.teamMemberNames}</h5>
		</div>
	</div>
	<div class="row">	
		
		<div class="col-md-4 col-xs-12">
			<c:forEach items="${route.teamRouteEntries}" var="teamRouteEntry" varStatus="loopStatus">
				<c:choose>
					<c:when test="${teamRouteEntry.currentTeam == true}">
							<div class="alert alert-success">
								<h3 class="media-heading">
									${loopStatus.index + 1}) <spring:message code="label.dinnerroutes.self.meal"/>: ${teamRouteEntry.meal.label}
								</h3>
								<spring:message code="label.dinnerroutes.self.host"/>: <strong>${teamRouteEntry.host.name}</strong><br/>
							<br/>
							<fmt:formatDate pattern="<%=FormatterUtil.DEFAULT_TIME_FORMAT%>" value="${teamRouteEntry.meal.time}" var="mealTimeSelf"/>
							<strong><spring:message code="label.time"/>: <spring:message code="time.display" arguments="${mealTimeSelf}" /></strong>
						</div>
					</c:when>
					<c:otherwise>
						<div class="alert alert-info">
							<h3 class="media-heading">
								${loopStatus.index + 1}) ${teamRouteEntry.meal.label}
							</h3>
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
	
		<div class="col-md-8 col-xs-12">
			<div id="map" style="height:450px;"></div>
		</div>		
	</div>
	
	<div class="row">
		<div class="col-xs-12" style="display:none;" id="routeinfo">
		</div>
	</div>
  
  </div>

	<script src='<c:url value="/resources/js/dist/deps.js"/>'></script>
	<script src='<c:url value="/resources/js/dist/toastr_tooltip.js"/>'></script>
			
	<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyB46NpYyjomUcb-N3_9XjMfSrLEbBbvCaQ&sensor=false"></script>
	
	<script src='<c:url value="/resources/js/common.js"/>'></script>
	
	<script>
		function setMarkersToMap(teamMarkers, map) {
			for (var i=0; i<teamMarkers.length; i++) {
			    teamMarkers[i].marker.setMap(map);
			}    
		}
		
		function createMarker(latLngCoord, teamRouteEntry, mapIcon) {
		    
			var title = teamRouteEntry.meal.label + ': ' + teamRouteEntry.host.name;
		    
		    var result = {};
		    result.teamRouteEntry = teamRouteEntry;
		    
		    if (mapIcon) {
				result.marker = new google.maps.Marker({
				      position: latLngCoord,
				      title: title,
				      icon : mapIcon
				});
		    } else {
				result.marker = new google.maps.Marker({
				      position: latLngCoord,
				      title: title
				});
		    }
		    return result; 
		}
		
		function addInfoWindow(map, teamMarker) {
			google.maps.event.addListener(teamMarker.marker, 'click', function() {
			    var teamInfoString = getTeamInfoString(teamMarker.teamRouteEntry);
				var infoWindow = new google.maps.InfoWindow({
			      content: teamInfoString
				});
		    
		    	infoWindow.open(map, teamMarker.marker);
		  	});
		}
		
		function getTeamInfoString(teamRouteEntry) {
		    
		    var addressStr = '<p>Dieser Gang wird bei euch eingenommen</p><p>Uhrzeit: ' + teamRouteEntry.meal.time + '</p>';
		    
		    if (!teamRouteEntry.currentTeam) {
				addressStr = '<p>Bei: ' + teamRouteEntry.host.name + '</p>';
				addressStr += '<p>Anschrift: ' + getAddressString(teamRouteEntry.host.address) + '</p>';
				addressStr += '<p>Uhrzeit: ' + teamRouteEntry.meal.time + '</p>';
		    }
		    
		    return '<div id="content">' +
		      '<div id="siteNotice"></div>' +
		      '<h2 id="firstHeading" class="firstHeading">' + teamRouteEntry.meal.label + '</h2>'+
		      '<div id="bodyContent">'+
		      addressStr +
		      '</div>'+
		      '</div>';
		}
		
		function getAddressString(address) {
		    var result = address.street + ' ' + address.streetNr + ', ' + address.zip;
		    if (address.cityName) {
				result += ' ' + address.cityName; 
		    }
		    return result;
		}
		
		function createMapIcon(number, isCurrentTeam) {
		    var color = '9acfea';
		    if (isCurrentTeam) {
				color = '00FF00';
		    }
		    
		    var result = 'https://chart.googleapis.com/chart?chst=d_map_pin_letter_withshadow&chld=' + number + '|' + color + '|000000';
		    return result;
		}
		
		// Start Logic
		var teamRouteList = JSON.parse('${routejson}');
		
		var currentTeamCoord = null;
		var teamMarkers = new Array();
		
		for (var i=0; i< teamRouteList.teamRouteEntries.length; i++) {
		    
		   	var teamRouteEntry = teamRouteList.teamRouteEntries[i];

		    var geocodes = teamRouteEntry.host.geocodes;
		    if (geocodes && geocodes.length >= 0) {
				var latLngCoord = new google.maps.LatLng( parseFloat(geocodes[0].lat),  parseFloat(geocodes[0].lng) );
					
				if (teamRouteEntry.currentTeam) {
				    currentTeamCoord = latLngCoord;
				}
				
				var mapIcon = createMapIcon(i+1, teamRouteEntry.currentTeam);
				
				var teamMarker = createMarker(latLngCoord, teamRouteEntry, mapIcon);
				teamMarkers.push(teamMarker);
		    }
		}

		// Fallback if current team could not be resolved:
		if (currentTeamCoord == null) {
		    currentTeamCoord = teamMarkers[0].marker.position;
		}
		
		var mapOptions = {
		    center: currentTeamCoord,
		    zoom: 12,
		    mapTypeId: google.maps.MapTypeId.ROADMAP
		};
		
		
		$(document).ready(function() {
			var map = new google.maps.Map(document.getElementById("map"), mapOptions);
			setMarkersToMap(teamMarkers, map);
			
			for (var i=0; i<teamMarkers.length; i++) {
				addInfoWindow(map, teamMarkers[i]);
			}
			
			for (var i=0; i<teamMarkers.length; i++) {
			   	if (!teamMarkers[i].teamRouteEntry.currentTeam && teamMarkers[i].teamRouteEntry.host.onlyLastname) {
			   	    $('#routeinfo').append($('<span>Es werden nur die Nachnamen eurer Gastgeber angezeigt!</span>'));
			   	    $('#routeinfo').show();
			   	    break;
			   	}
			}
			
		});
	</script>	
	
</body>
</html>