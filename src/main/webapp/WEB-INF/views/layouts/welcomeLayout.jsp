<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ page pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>

	<tiles:insertAttribute name="htmlHeader" />
	  
	<body>
	  		
  		<div class="container">

			<tiles:insertTemplate template="/WEB-INF/views/wizard/carousel.jsp" />
  		
  			<div class="row">
				<div class="col-xs-12">
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

