<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${statusMessage != null}">
	<div class="alert alert-${statusMessage.status}">
		${statusMessage.message}
	</div>
</c:if>