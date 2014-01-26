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
		<tiles:putAttribute name="pageTitle">Running Dinner</tiles:putAttribute>
	</tiles:insertAttribute>
	  
	<body>
  		  
  		<tiles:insertAttribute name="menu" />
  		
  		<div class="container">
  		
  		  	<div class="row">
  				<div clasS="col-xs-12">
  					<tiles:insertDefinition name="view-admin-menu" />
  				</div>
  			</div>
  		
  		
  			<div class="jumbotron" style="padding-top:17px;padding-bottom:20px;">
  				<tiles:insertAttribute name="info" ignore="true"/>
			</div>
			
			<div class="row">
				<div class="col-xs-12">
					<tiles:insertAttribute name="activities" ignore="true"/>
				</div>
			</div>
  		
  			<%--
  			<div class="row">
				<div class="col-xs-12">
  					<tiles:insertAttribute name="content" />
  				</div>
  			</div>
  			--%>
  			
  			<tiles:insertAttribute name="footer" />	
  		</div>
  	
  		<tiles:insertAttribute name="htmlFooter" />
  		
  		<tiles:insertAttribute name="customScripts" />
  		
  	</body>
  	
</html>

