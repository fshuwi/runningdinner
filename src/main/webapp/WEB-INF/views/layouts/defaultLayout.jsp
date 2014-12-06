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
  		  
  		<div class="my-fluid-container">
  		 	
  			<div class="row">
  				<div class="col-sm-3 col-md-2" style="margin-top:15px;">
  					<tiles:useAttribute name="currentView" id="currentView" classname="java.lang.String" ignore="true"/>
  					<tiles:insertDefinition name="view-admin-menu">
  						<tiles:putAttribute name="currentView" value="${currentView}" />
  					</tiles:insertDefinition>
  				</div>
				<div class="col-sm-9 col-md-10" style="margin-top:15px;">
  					<tiles:insertAttribute name="content" />
  				</div>
  			</div>
  			
  			<tiles:insertAttribute name="footer" />	
  		</div>
  	
  		<tiles:insertAttribute name="htmlFooter" />
  		
  		
  		<tiles:useAttribute name="customScripts" id="customScripts" ignore="true" classname="java.util.List"/>
  		<c:if test="${not empty customScripts}">
	  		<c:forEach var="customScript" items="${customScripts}">
	  			<tiles:insertAttribute value="${customScript}" flush="true" />
			</c:forEach>
		</c:if>
  		
  	</body>
  	
</html>