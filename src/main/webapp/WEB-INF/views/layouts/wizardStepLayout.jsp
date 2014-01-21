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
  		
  		<%-- Use current view name for determining the current highlighted breadcrumb element --%>	
  		<tiles:useAttribute name="currentView" id="currentStep" classname="java.lang.String" />
  		<c:set var="timesClass" value="inactiveWizardStep" />
  		<c:set var="uploadClass" value="inactiveWizardStep" />	
  		<c:set var="finishClass" value="inactiveWizardStep" />
  		<c:choose>
  			<c:when test="${currentStep == 'times'}">
  				<c:set var="timesClass" value="activeWizardStep" />
  			</c:when>
  			<c:when test="${currentStep == 'upload'}">
  				<c:set var="uploadClass" value="activeWizardStep" />
  			</c:when>
  			<c:when test="${currentStep == 'save'}">
  				<c:set var="finishClass" value="activeWizardStep" />
  			</c:when>  			
  		</c:choose>		
  			
  		<div class="container">
  				
  			<c:if test="${currentStep != 'finish'}">	
	  			<div class="row">
	  				<div class="col-xs-12">
			  			<ol class="breadcrumb">
			  				<li class="inactiveWizardStep">1. Allgemein</li>
			  				<li class="${timesClass}">2. Zeiten festlegen</li>
			  				<li class="${uploadClass}">3. Teilnehmerliste hochladen</li>
			  				<li class="${finishClass}">4. Fertigstellen</li>
						</ol>
					</div>
				</div>
			</c:if>
  				
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