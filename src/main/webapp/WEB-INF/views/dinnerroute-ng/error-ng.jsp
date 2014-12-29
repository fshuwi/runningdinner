<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page session="false" %>

<!DOCTYPE html>
<html>
  <head>
    <title>Route - <spring:message code="label.error"/></title>
    
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href='<c:url value="/resources/images/favicon.ico"/>' type="image/x-icon" />

	<link href='<c:url value="/resources/css/dist/app.css" />' rel="stylesheet">
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
  </head>
<body>
	  <div class="container">
	  	
		<div class="row">
			<div class="col-xs-12">
				<h3><spring:message code="label.error"/></h3>
			</div>
		</div>
		
		<div class="row">	
			<div class="col-xs-12">
				<p>${errorMessage}</p>
			</div>
		</div>
	  
	  </div>
				
	<c:set var="useCDN" value="false" />
	<spring:eval expression="@globalProperties['ui.useCDN']" var="useCDN" />
	<c:choose>
		<c:when test="${useCDN == 'true'}">
			<script src='https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.8.4/moment-with-locales.min.js'></script>
			
			<script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
			<script src="https://code.jquery.com/ui/1.10.4/jquery-ui.min.js"></script>
			
			<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js"></script>
			
			<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/i18n/jquery-ui-i18n.min.js"></script>
			
			<script src="https://cdnjs.cloudflare.com/ajax/libs/tooltipster/3.0.5/js/jquery.tooltipster.min.js"></script>
			
			<script src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/js/toastr.min.js"></script>
		</c:when>
		<c:otherwise>
			<script src='<c:url value="/resources/js/moment.min.js"/>'></script>
			<script src='<c:url value="/resources/js/dist/deps.js"/>'></script>
			<script src='<c:url value="/resources/js/dist/toastr_tooltip.js"/>'></script>
		</c:otherwise>
	</c:choose>

	<script src='<c:url value="/resources/js/common.js"/>'></script>
	
</body>
</html>