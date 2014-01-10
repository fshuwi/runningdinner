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
		<tiles:putAttribute name="pageTitle">Running Dinner - Error</tiles:putAttribute>
	</tiles:insertAttribute>
	
	<style>
		.error-template {padding: 40px 15px;text-align: center;}
		.error-actions {margin-top:15px;margin-bottom:15px;}
		.error-actions .btn { margin-right:10px; }
	</style>
	  
	<body>
		<div class="container">
  			<div class="row">
		        <div class="col-xs-12">
		            <div class="error-template">
		                <h1>Oops!</h1>
		                <h2>404 Not Found</h2>
		                <div class="error-details">Sorry, an error has occured, Requested page not found!</div>
		                <div class="error-actions">
		                    <a href="http://www.jquery2dotnet.com" class="btn btn-primary btn-lg"><span class="glyphicon glyphicon-home"></span>
		                        Take Me Home </a><a href="http://www.jquery2dotnet.com" class="btn btn-default btn-lg"><span class="glyphicon glyphicon-envelope"></span> Contact Support </a>
		                </div>
		            </div>
		        </div>
    		</div>
  		</div>
  	
  		<tiles:insertAttribute name="htmlFooter" />
  		
  		<tiles:insertAttribute name="customScripts" ignore="true" />
  	</body>
  	
</html>