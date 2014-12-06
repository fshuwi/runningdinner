<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ page pageEncoding="UTF-8" %>
<%@ page session="false" %>


<!DOCTYPE html>
<html>
	<tiles:insertAttribute name="htmlHeader" />
	  
	<body>  			
  		<div class="container">		 	
   					
  			<div class="row">
				<div class="col-xs-12">		
  					<tiles:insertAttribute name="content" />
  				</div>
  			</div>
  			
  			<tiles:insertAttribute name="footer" />	
  		</div>
  	
  		<tiles:insertAttribute name="htmlFooter" />
  		
    	<!-- <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script> -->
  		
  		<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyB46NpYyjomUcb-N3_9XjMfSrLEbBbvCaQ&sensor=false"></script>
  		
  		<tiles:insertAttribute name="customScripts" />
  		
  	</body>
  	
</html>