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
			<h3>Route <span style="display:none;" id="routeinfo"></span></h3>
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
			<div id="map" style="height:550px;"></div>
			<div id="maperrors" style="display:none;"></div>
		</div>		
	</div>
  
  </div>
				
	<script src='https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.8.4/moment-with-locales.min.js'></script>				
				
	<script src='<c:url value="/resources/js/dist/deps.js"/>'></script>
	<script src='<c:url value="/resources/js/dist/toastr_tooltip.js"/>'></script>

	<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyB46NpYyjomUcb-N3_9XjMfSrLEbBbvCaQ&sensor=false"></script>
	<script src='<c:url value="/resources/js/geolocationmarker.js"/>'></script>
	
	<script src='<c:url value="/resources/js/common.js"/>'></script>
	
	<script>
		function setMarkersToMap(teamMarkers, map) {
			for (var i=0; i<teamMarkers.length; i++) {
				if (teamMarkers[i].enabled == false) {
					continue;
				}
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
			if (teamMarker.enabled == false) {
				return;
			}
			
			google.maps.event.addListener(teamMarker.marker, 'click', function() {
			    var teamInfoString = getTeamInfoString(teamMarker.teamRouteEntry);
				var infoWindow = new google.maps.InfoWindow({
			      content: teamInfoString
				});
		    
		    	infoWindow.open(map, teamMarker.marker);
		  	});
		}
		
		function getTeamInfoString(teamRouteEntry) {
		    
			var date = new Date(teamRouteEntry.meal.time);
			var formattedTime = getFormattedTime(date);
			
			var addressStr = '<p>Dieser Gang wird bei euch eingenommen</p><p>Uhrzeit: ' + formattedTime + '</p>';
		    
		    if (!teamRouteEntry.currentTeam) {
				addressStr = '<p>Bei: ' + teamRouteEntry.host.name + '</p>';
				addressStr += '<p>Anschrift: ' + getAddressString(teamRouteEntry.host.address) + '</p>';
				addressStr += '<p>Uhrzeit: ' + formattedTime + '</p>';
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
		
		var map = null;
		var currentTeamCoord = null;
		var teamMarkers = new Array();
		var currentPositionMarker = null;
		var showOnlyLastnames = false;
		
		var unresolvedTeamRouteEntries = new Array();
		
		var isMobile = ${mobile};
		
		for (var i=0; i< teamRouteList.teamRouteEntries.length; i++) {
		    
		   	var teamRouteEntry = teamRouteList.teamRouteEntries[i];
		   	
			if (!teamRouteEntry.currentTeam && teamRouteEntry.host.onlyLastname == true) {
				showOnlyLastnames = true;
			}

		    var geocodes = teamRouteEntry.host.geocodes;
		    if (geocodes && geocodes.length > 0) {
				// Typically we have exactly one geocode:
		    	for (var j=0; j<geocodes.length; j++) {
			    	var latLngCoord = new google.maps.LatLng( parseFloat(geocodes[j].lat),  parseFloat(geocodes[j].lng) );
					
					if (teamRouteEntry.currentTeam && j==0) {
					    currentTeamCoord = latLngCoord;
					}
					
					var mapIcon = createMapIcon(i+1, teamRouteEntry.currentTeam);
					
					var teamMarker = createMarker(latLngCoord, teamRouteEntry, mapIcon);
					teamMarkers.push(teamMarker);
					
					if (j==0) {
						// First one is enabled by default
						teamMarker.enabled = true;
					} else {
						teamMarker.enabled = false;
					}
					
					teamMarker.exact = geocodes[j].exact;
					teamMarker.formattedAddress = geocodes[j].formattedAddress;
		    	}
		    }
		    else {
		    	unresolvedTeamRouteEntries.push(teamRouteEntry);
		    }
		}

		// Fallback if current team could not be resolved:
		if (currentTeamCoord == null && teamMarkers.length > 0) {
		    currentTeamCoord = teamMarkers[0].marker.position;
		}
		
		var mapOptions = {
		    center: currentTeamCoord,
		    zoom: isMobile ? 12 : 13,
		    mapTypeId: google.maps.MapTypeId.ROADMAP
		};
		
		
		function addConnectionLine(teamMarkers, map) {
			var positions = new Array();
		    for (var i=0; i<teamMarkers.length; i++) {
		    	if (teamMarkers[i].enabled == false) {
		    		continue;
		    	}
				positions.push(teamMarkers[i].marker.position);
		    }
		    
			var connectionLine = new google.maps.Polyline({
			    path: positions,
			    geodesic: true,
			    strokeColor: '#0000FF',
			    strokeOpacity: 1.0,
			    strokeWeight: 2
			});
			
			connectionLine.setMap(map);
			
			return connectionLine;
		}
		
		function handleUnresolvedTeamRouteEntries(unresolvedTeamRouteEntries) {
			var resultHtml = '<p><u>Nicht gefundene Adresse(n):</u></p><ul>';
			for (var i=0; i<unresolvedTeamRouteEntries.length; i++) {
				var teamRouteEntry = unresolvedTeamRouteEntries[i];
				var singleResultHtml = 'Die Adresse von <b>' + teamRouteEntry.host.name + ' (' + teamRouteEntry.meal.label + ')</b> kann nicht angezeigt werden. Ist folgende Adresse gültig?<br/>';
				singleResultHtml += "<i>" + getAddressString(teamRouteEntry.host.address) + "</i>";
				resultHtml += '<li>' + singleResultHtml + '</li>';
			}
			resultHtml += '</ul>';
			
			$('#maperrors').append($(resultHtml));
			$('#maperrors').show();
		}
		
		function handleNonAccurateTeamRouteEntries(teamMarkers) {
			
			var multipleGeocodes = {};
			var nonAccurateGeocodes = new Array();
			
			var showMapErrors = false;
			
		    for (var i=0; i<teamMarkers.length; i++) {
		    	
		    	if (teamMarkers[i].enabled == false) {
		    		var teamNumber = teamMarkers[i].teamRouteEntry.teamNumber;
		    		var entry = multipleGeocodes[teamNumber];
		    		if (!entry) {
		    			entry = new Array();
		    			multipleGeocodes[teamNumber] = entry;
		    		}
		    		entry.push(teamMarkers[i]);
			    	
		    		continue;
		    	}
		    	else if (teamMarkers[i].exact == false) {
					nonAccurateGeocodes.push(teamMarkers[i]);		    		
		    	}
		    }
		    
		    if (nonAccurateGeocodes.length > 0) {
		    	var nonAccurateGeocodesHtml = '<p><u>Ungenaue Adresse(n):</u></p><ul>';
		    	for (var i=0; i<nonAccurateGeocodes.length; i++) {
		    		var teamRouteEntry = nonAccurateGeocodes[i].teamRouteEntry;
					
		    		var singleResultHtml = 'Die Adresse von <b>' + teamRouteEntry.host.name + ' (' + teamRouteEntry.meal.label + ')</b> wird nur ungenau auf der Karte dargestellt. Angegebene Adresse:<br/>';
					singleResultHtml += "<i>" + getAddressString(teamRouteEntry.host.address) + "</i>";
					
					nonAccurateGeocodesHtml += '<li>' + singleResultHtml + '</li>';
		    	}
		    	nonAccurateGeocodesHtml += '</ul>';
		    	$('#maperrors').append($(nonAccurateGeocodesHtml));	
		    	showMapErrors = true;
		    }
		    
		    if (!$.isEmptyObject(multipleGeocodes)) {
		    	var multipleGeocodesHtml = '<ul>';
		    	$.each( multipleGeocodes, function( key, value ) {
		    		
		    		var teamRouteEntry = value[0].teamRouteEntry;
		    		multipleGeocodesHtml += '<li>Für die Adresse von <b>' + teamRouteEntry.host.name + ' (' + teamRouteEntry.meal.label + ')</b> gibt es noch weitere alternative Treffer:</li>';
		    		multipleGeocodesHtml += '<ul>';
		    		for (var i=0; i<value.length; i++) {
		    			multipleGeocodesHtml += ("<li>" + value[i].formattedAddress + ": <a href='#'>Auf Karte anzeigen</a></li>");
		    		}
		    		multipleGeocodesHtml += '</ul>';
		    	});
		    	multipleGeocodesHtml += '</ul>';
		    	$('#maperrors').append($(multipleGeocodesHtml));	
		    	showMapErrors = true;
		    }
		    
		    if (showMapErrors) {
				$('#maperrors').show();
		    }
		}
		
		$(document).ready(function() {
			
			if (isMobile) {
				$('map').height('400px');
			}
			
			map = new google.maps.Map(document.getElementById("map"), mapOptions);
			setMarkersToMap(teamMarkers, map);
			
			for (var i=0; i<teamMarkers.length; i++) {
				addInfoWindow(map, teamMarkers[i]);
			}
			
			addConnectionLine(teamMarkers, map);
			
		   	if (showOnlyLastnames) {
		   	    $('#routeinfo').append($('<small>(Es werden nur die Nachnamen eurer Gastgeber angezeigt!)</small>'));
		   	    $('#routeinfo').show();
		   	}
			
			var geoMarker = new GeolocationMarker(map);
			google.maps.event.addListener(geoMarker, 'geolocation_error', function(e) {
			 if (console) { 
				 console.error('There was an error obtaining your position. Message: ' + e.message);
			 }
			});
			
			if (unresolvedTeamRouteEntries.length > 0) {
				handleUnresolvedTeamRouteEntries(unresolvedTeamRouteEntries);
			}
			
			handleNonAccurateTeamRouteEntries(teamMarkers);
		});
	</script>
	
</body>
</html>