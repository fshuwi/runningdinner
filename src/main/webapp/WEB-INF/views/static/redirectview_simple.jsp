<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${simpleMessage != null}">
	<div class="alert alert-danger">
		${simpleMessage}
	</div>
</c:if>