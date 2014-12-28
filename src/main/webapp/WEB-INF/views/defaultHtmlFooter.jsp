<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>

<%--
<script src='<c:url value="/resources/js/jquery-1.11.1.min.js"/>'></script>
<script src='<c:url value="/resources/js/bootstrap.min.js"/>'></script>
<script src='<c:url value="/resources/js/jquery-ui-1.10.4.custom.min.js"/>'></script>
<script src='<c:url value="/resources/js/jquery.tooltipster.min.js"/>'></script>
<script src='<c:url value="/resources/js/toastr.min.js"/>'></script> 
--%>

<c:set var="useCDN" value="false" />
<spring:eval expression="@globalProperties['ui.useCDN']" var="useCDN" />

<c:choose>
	<c:when test="${useCDN == 'true'}">
		<script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
		<script src="https://code.jquery.com/ui/1.10.4/jquery-ui.min.js"></script>
		
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js"></script>
		
		<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/i18n/jquery-ui-i18n.min.js"></script>
		
		<script src="https://cdnjs.cloudflare.com/ajax/libs/tooltipster/3.0.5/js/jquery.tooltipster.min.js"></script>
		
		<script src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/js/toastr.min.js"></script>
	</c:when>
	<c:otherwise>
		<script src='<c:url value="/resources/js/dist/deps.js"/>'></script>
		<script src='<c:url value="/resources/js/dist/toastr_tooltip.js"/>'></script>
	</c:otherwise>
</c:choose>

<script src='<c:url value="/resources/js/common.js"/>'></script>