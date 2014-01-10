<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ page pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>

	<tiles:insertAttribute name="htmlHeader">
		<tiles:putAttribute name="pageTitle">Running Dinner - Neue Berechnung</tiles:putAttribute>
	</tiles:insertAttribute>
	  
	<body>
  		  
  		<tiles:insertAttribute name="menu" />
  		
  		<div class="container">  
  		
  			
  			<div class="row">
  				<div class="col-xs-12">
		  			<ol class="breadcrumb">
		  				<li class="active">1. Allgemein</li>
		  				<li><a href="javascript:void();" style="cursor:default;text-decoration:none;">2. Zeiten festlegen</a></li>
		  				<li class="active">3. Teilnehmerliste hochladen</li>
		  				<li class="active">4. Fertigstellen</li>
					</ol>
				</div>
			</div>
  				
  			<div class="row">
				<div class="col-xs-12">		
  					<tiles:insertAttribute name="content" />
  				</div>
  			</div>
  			
  			<tiles:insertAttribute name="footer" />	
  		</div>
  	
  		<tiles:insertAttribute name="htmlFooter" />
  		
  		<tiles:insertAttribute name="customScripts" />
  		
  	</body>
  	
</html>