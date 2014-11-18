<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script>
	$(document).ready(function() {
		// Set boxes equally heighted
		// ('.self').height($('.hoster').first().height());
		
		var mapOptions = {
		    center: new google.maps.LatLng(-34.397, 150.644),
		    zoom: 8,
		    mapTypeId: google.maps.MapTypeId.ROADMAP
		};
		var map = new google.maps.Map(document.getElementById("map"), mapOptions);
	});
</script>