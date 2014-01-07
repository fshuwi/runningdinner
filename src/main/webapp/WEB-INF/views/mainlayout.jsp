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
  				<div class="col-md-12">
		  			<ol class="breadcrumb">
		  				<li><a href="javascript:void();" style="cursor:default;text-decoration:none;">1. Allgemein</a></li>
		  				<li class="active">2. Zeiten festlegen</li>
		  				<li><a href="javascript:void();" style="cursor:default;text-decoration:none;">3. Teilnehmerliste hochladen</a></li>
		  				<li><a href="javascript:void();" style="cursor:default;text-decoration:none;">4. Fertigstellen</a></li>
					</ol>
				</div>
			</div>
  				
  			<div class="row">
				<div class="col-md-12">		
  					<tiles:insertAttribute name="content" />
  				</div>
  			</div>
  			
  			<tiles:insertAttribute name="footer" />	
  		</div>
  	
  		<tiles:insertAttribute name="htmlFooter" />
  		
  		<tiles:insertAttribute name="customScripts" />
  		
  	</body>
  	
</html>