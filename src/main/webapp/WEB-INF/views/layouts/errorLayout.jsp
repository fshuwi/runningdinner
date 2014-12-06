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
	
	<style>
		.error-template {padding: 40px 15px;text-align: center;}
		.error-actions {margin-top:15px;margin-bottom:15px;}
		.error-actions .btn { margin-right:10px; }
		body { background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABoAAAAaCAYAAACpSkzOAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEgAACxIB0t1+/AAAABZ0RVh0Q3JlYXRpb24gVGltZQAxMC8yOS8xMiKqq3kAAAAcdEVYdFNvZnR3YXJlAEFkb2JlIEZpcmV3b3JrcyBDUzVxteM2AAABHklEQVRIib2Vyw6EIAxFW5idr///Qx9sfG3pLEyJ3tAwi5EmBqRo7vHawiEEERHS6x7MTMxMVv6+z3tPMUYSkfTM/R0fEaG2bbMv+Gc4nZzn+dN4HAcREa3r+hi3bcuu68jLskhVIlW073tWaYlQ9+F9IpqmSfq+fwskhdO/AwmUTJXrOuaRQNeRkOd5lq7rXmS5InmERKoER/QMvUAPlZDHcZRhGN4CSeGY+aHMqgcks5RrHv/eeh455x5KrMq2yHQdibDO6ncG/KZWL7M8xDyS1/MIO0NJqdULLS81X6/X6aR0nqBSJcPeZnlZrzN477NKURn2Nus8sjzmEII0TfMiyxUuxphVWjpJkbx0btUnshRihVv70Bv8ItXq6Asoi/ZiCbU6YgAAAABJRU5ErkJggg==);}

		.errorTextInvisible { display: none; }
		.errorTextVisible {	display: block;	}
	</style>
	  
	<body>
		<div class="container">
  			<div class="row">
		        <div class="col-xs-12">
		            <div class="error-template">
		                
		                <tiles:insertAttribute name="content" />
		                		                
		                <div class="error-actions">
		                    <a href="${startUrl}" class="btn btn-primary btn-lg"><span class="glyphicon glyphicon-home"></span> Home</a>
		                    <a href="mailto:${contactMail}" class="btn btn-default btn-lg"><span class="glyphicon glyphicon-envelope"></span> <spring:message code="label.contact"/></a>
		                </div>
		                
		                <div class="technicalInfo">
		                	<spring:message code="label.show.technical.error" var="showExceptionLabel" />
		                	<spring:message code="label.hide.technical.error" var="hideExceptionLabel" />
		                
		                	<p><spring:message code="label.time"/>: <b><fmt:formatDate type="both" value="${timestamp}" timeStyle="FULL" /></b></p>
		                	<c:if test="${showException}">
		                		<c:if test="${ex != null}">
	                				<a href="javascript:toggleErrorView('errortext', '${showExceptionLabel}', '${hideExceptionLabel}')" hidefocus="hidefocus" id="errortext_label" 
	                					style="font-weight:bold;">${showExceptionLabel}</a><br/>
	                				<div id="errortext" class="errorTextInvisible" style="text-align:left;">
	                					<b>${ex.message}</b><br/>
	                					<c:forEach items="${ex.stackTrace}" var="ste">${ste}<br/></c:forEach>
	                				</div>
		                		</c:if>
		                	</c:if> 
		                </div>
		                
		            </div>
		        </div>
    		</div>
  		</div>
  	
  		<tiles:insertAttribute name="htmlFooter" />
  		
  		<tiles:insertAttribute name="customScripts" ignore="true" />
  	</body>
  	
</html>