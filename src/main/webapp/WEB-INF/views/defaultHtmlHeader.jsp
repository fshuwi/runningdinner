<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <%@ page session="false" %>
 
  <head>
    <title><tiles:insertAttribute name="pageTitle" ignore="true" defaultValue="Run Your Dinner"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <link rel="shortcut icon" href='<c:url value="/resources/images/favicon.ico"/>' type="image/x-icon" />

	<link href='<c:url value="/resources/css/dist/app.css" />' rel="stylesheet">

    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
  </head>